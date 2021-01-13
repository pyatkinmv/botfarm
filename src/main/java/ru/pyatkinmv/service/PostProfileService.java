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
import ru.pyatkinmv.dao.entities.PostType;
import ru.pyatkinmv.dao.entities.Profile;
import ru.pyatkinmv.inst.ImageProvider;
import ru.pyatkinmv.inst.dto.ImageInfo;
import ru.pyatkinmv.plan.TaskPlanner;

import java.math.BigDecimal;
import java.net.URL;
import java.util.Collection;
import java.util.List;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostProfileService {
    private final ImageProvider imageProvider;
    private final ProfileRepository profileRepository;
    private final FileRepository fileRepository;
    private final PostRepository postRepository;

    private final TaskPlanner taskPlanner;

    private static final double PERCENT_MAIN_PHOTOS_OF_ALL_PHOTOS = 10.0;

    @Transactional
    public void post(ProfileDto profileDto) {
        if (isNotValid(profileDto.getActivityRate())) {
            throw new IllegalArgumentException("Activity rate should be in (0,1] interval");
        }

        Profile profile = convert(profileDto);
        profileRepository.save(profile);

        Collection<ImageInfo> images = imageProvider.getImages(profile.getReference());

        int mainPhotosCount = (int) ((PERCENT_MAIN_PHOTOS_OF_ALL_PHOTOS / 100.0) * images.size());

        List<ImageInfo> sorted = images.stream()
                .sorted(comparing(ImageInfo::getLikesCount))
                .collect(toList());

        List<ImageInfo> mainPhotos = sorted.subList(sorted.size() - mainPhotosCount, sorted.size());
        List<ImageInfo> wallPhotos = sorted.subList(0, sorted.size() - mainPhotosCount - 1);

        mainPhotos.forEach(it -> downloadAndSave(it, profile, PostType.MAIN_PHOTO));
        wallPhotos.forEach(it -> downloadAndSave(it, profile, PostType.WALL));

        log.info("Successfully migrated instagram content for vk user {}", profileDto.getUserId());

        taskPlanner.plan();
    }

    private static boolean isNotValid(BigDecimal activityRate) {
        return activityRate.doubleValue() < 0.0 || activityRate.doubleValue() > 1.0;
    }

    @SneakyThrows
    private void downloadAndSave(ImageInfo imageInfo, Profile profile, PostType type) {
        FileInfo file = fileRepository.save(
                FileInfo.builder()
                        .name(imageInfo.getName())
                        .build()
        );

        log.info("Downloading photo {} from url {}", imageInfo.getName(), imageInfo.getUrl());
        byte[] imageBytes = IOUtils.toByteArray((new URL(imageInfo.getUrl())).openStream());
        log.info("Successfully downloaded");

        fileRepository.saveFileData(file.getId(), imageBytes);

        postRepository.save(
                Post.builder()
                        .isPosted(false)
                        .message(imageInfo.getMessage())
                        .profile(profile)
                        .sourceDate(imageInfo.getDate())
                        .type(type)
                        .file(file)
                        .build()
        );
    }

    private static Profile convert(ProfileDto profileDto) {
        return Profile.builder()
                .userId(profileDto.getUserId())
                .reference(profileDto.getReference())
                .token(profileDto.getToken())
                .activityRate(profileDto.getActivityRate())
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
        private String email;
    }
}
