package ru.pyatkinmv.service;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pyatkinmv.dao.FileRepository;
import ru.pyatkinmv.dao.PostRepository;
import ru.pyatkinmv.dao.ProfileRepository;
import ru.pyatkinmv.dao.entities.FileInfo;
import ru.pyatkinmv.dao.entities.Post;
import ru.pyatkinmv.dao.entities.Profile;
import ru.pyatkinmv.dao.entities.ProfileType;
import ru.pyatkinmv.inst.ImageProvider;
import ru.pyatkinmv.inst.dto.PostInfo;

import java.math.BigDecimal;
import java.net.URL;
import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostProfileService {
    private final ImageProvider imageProvider;
    private final ProfileRepository profileRepository;
    private final FileRepository fileRepository;
    private final PostRepository postRepository;

    @Transactional
    public void post(ProfileDto profileDto) {
        Profile profile = convert(profileDto);
        profileRepository.save(profile);

        Collection<PostInfo> posts = imageProvider.getImages(profile.getReference());

        posts.forEach(it -> downloadAndSave(it, profile));

        log.info("Successfully migrated instagram content for vk user {}", profileDto.getUserId());
    }

    private static void validate(BigDecimal activityRate) {
        if (activityRate.doubleValue() < 0.0 || activityRate.doubleValue() > 1.0) {
            throw new IllegalArgumentException("Activity rate should be in (0,1] interval");
        }
    }

    @SneakyThrows
    private void downloadAndSave(PostInfo postInfo, Profile profile) {
        Post post = postRepository.save(
                Post.builder()
                        .posted(false)
                        .message(postInfo.getMessage())
                        .profile(profile)
                        .sourceDate(postInfo.getCreatedTime())
                        .likesCount(postInfo.getLikesCount())
                        .build()
        );

        postInfo.getImages()
                .forEach(it -> downloadAndSave(it, post));
    }

    private void downloadAndSave(PostInfo.ImageInfo image, Post post) {
        FileInfo fileInfo = fileRepository.save(FileInfo.builder()
                .name(image.getName())
                .post(post)
                .build());

        byte[] imageBytes = download(image);
        fileRepository.saveFileData(fileInfo.getId(), imageBytes);
    }


    @SneakyThrows
    private byte[] download(PostInfo.ImageInfo imageInfo) {
        log.info("Downloading photo {} from url {}", imageInfo.getName(), imageInfo.getUrl());
        byte[] imageBytes = IOUtils.toByteArray((new URL(imageInfo.getUrl())).openStream());
        log.info("Successfully downloaded");

        return imageBytes;
    }

    private static Profile convert(ProfileDto profileDto) {
        if (profileDto.activityRate != null) {
            validate(profileDto.getActivityRate());
        }

        return Profile.builder()
                .userId(profileDto.getUserId())
                .reference(profileDto.getReference())
                .token(profileDto.getToken())
                .activityRate(
                        Optional.ofNullable(profileDto.getActivityRate())
                                .orElse(BigDecimal.ONE)
                )
                .type(
                        Optional.ofNullable(profileDto.getType())
                                .orElse(ProfileType.DEFAULT)
                )
                .build();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileDto {
        private Integer userId;
        private String token;
        private BigDecimal activityRate;
        private String reference;
        private ProfileType type;
    }
}
