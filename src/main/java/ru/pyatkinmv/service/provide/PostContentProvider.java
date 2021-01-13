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
import ru.pyatkinmv.dao.entities.PostType;
import ru.pyatkinmv.exception.ItemNotFoundException;
import ru.pyatkinmv.vk.dto.WallPostDto;

import java.io.File;

@Component
@RequiredArgsConstructor
public class PostContentProvider {
    private final PostRepository postRepository;
    private final FileRepository fileRepository;

    @SneakyThrows
    private File convert(FileInfo file) {
        File destination = new File(file.getName());
        final byte[] fileData = fileRepository.getFileData(file.getId());
        FileUtils.writeByteArrayToFile(destination, fileData);

        return destination;
    }

    @SneakyThrows
    @Transactional
    public WallPostDto getWallPost(Integer profileId) {
        Post post = postRepository.findByProfileIdAndType(profileId, PostType.WALL.name())
                .orElseThrow(ItemNotFoundException::new);

        post.setIsPosted(true);
        postRepository.save(post);

        return new WallPostDto(
                post.getMessage(),
                convert(post.getFile())
        );
    }

    @Transactional
    public String getWallMessage(Integer profileId) {
        final Post post = postRepository.findByProfileIdAndType(profileId, PostType.WALL_MESSAGE.name())
                .orElseThrow(ItemNotFoundException::new);

        post.setIsPosted(true);
        postRepository.save(post);

        return post.getMessage();
    }

    @Transactional
    public File getMainPhoto(Integer profileId) {
        Post post = postRepository.findByProfileIdAndType(profileId, PostType.MAIN_PHOTO.name())
                .orElseThrow(ItemNotFoundException::new);

        post.setIsPosted(true);
        postRepository.save(post);

        return convert(post.getFile());
    }
}
