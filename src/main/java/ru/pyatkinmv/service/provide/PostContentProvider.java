package ru.pyatkinmv.service.provide;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.pyatkinmv.dao.FileRepository;
import ru.pyatkinmv.dao.PostRepository;
import ru.pyatkinmv.dao.entities.FileInfo;
import ru.pyatkinmv.dao.entities.Post;
import ru.pyatkinmv.exception.ItemNotFoundException;
import ru.pyatkinmv.vk.dto.WallPostDto;

import java.io.File;
import java.util.Collection;

import static java.util.stream.Collectors.toList;

@Component
@RequiredArgsConstructor
public class PostContentProvider {
    private final PostRepository postRepository;
    private final FileRepository fileRepository;

    @SneakyThrows
    private File toFile(FileInfo file) {
        File destination = new File(file.getName());
        final byte[] fileData = fileRepository.getFileData(file.getId());
        FileUtils.writeByteArrayToFile(destination, fileData);

        return destination;
    }

    @SneakyThrows
    @Transactional
    public WallPostDto getWallPhotoPost(Integer profileId) {
        Post post = postRepository.findOldestNotPostedById(profileId)
                .orElseThrow(ItemNotFoundException::new);

        post.setPosted(true);
        postRepository.save(post);

        Collection<File> images = post.getImages()
                .stream()
                .map(this::toFile)
                .collect(toList());

        return new WallPostDto(images, post.getMessage());
    }

    @Transactional
    public String getWallMessage(Integer profileId) {
        Post post = postRepository.findOldestNotPostedById(profileId)
                .orElseThrow(ItemNotFoundException::new);

        post.setPosted(true);
        postRepository.save(post);

        return post.getMessage();
    }

    @Transactional
    public File getMainPhoto(Integer profileId) {
        Post post = postRepository.findMostLikedNotPostedById(profileId)
                .orElseThrow(ItemNotFoundException::new);

        post.setPosted(true);
        postRepository.save(post);

        return post.getImages()
                .stream().findFirst()
                .map(this::toFile)
                .orElseThrow();
    }
}
