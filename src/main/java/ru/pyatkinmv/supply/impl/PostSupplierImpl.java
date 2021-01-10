package ru.pyatkinmv.supply.impl;

import com.google.gson.Gson;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.objects.wall.WallPostFull;
import com.vk.api.sdk.queries.newsfeed.NewsfeedGetFilter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import ru.pyatkinmv.dao.PostRepository;
import ru.pyatkinmv.dao.entities.FileInfo;
import ru.pyatkinmv.dao.entities.Post;
import ru.pyatkinmv.dao.entities.PostType;
import ru.pyatkinmv.dto.PostReferenceDto;
import ru.pyatkinmv.dto.WallPostDto;
import ru.pyatkinmv.supply.api.PostSupplier;
import ru.pyatkinmv.supply.dto.NewsFeedPostGetQuery;

import java.io.File;
import java.net.URL;
import java.util.Set;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toSet;

@RequiredArgsConstructor
public class PostSupplierImpl implements PostSupplier {
    private final VkApiClient vk;
    private final UserActor actor;
    private final PostRepository postRepository;

    private static boolean isRepost(WallPostFull wallPost) {
        return wallPost.getCopyHistory() != null && !wallPost.getCopyHistory().isEmpty();
    }

    @SneakyThrows
    @Override
    public PostReferenceDto supplyPostToRepost() {
        Set<Integer> reposted = vk.wall()
                .get(actor)
                .count(100)
                .execute()
                .getItems()
                .stream()
                .filter(PostSupplierImpl::isRepost)
                .map(it -> it.getCopyHistory().get(0).getId())
                .collect(toSet());

        return new Gson().fromJson(
                vk.newsfeed()
                        .get(actor)
                        .filters(NewsfeedGetFilter.POST)
                        .count(100)
                        .executeAsString(),
                NewsFeedPostGetQuery.class)
                .getResponse()
                .getItems()
                .stream()
                .filter(it -> !reposted.contains(it.getPostId()))
                .filter(it -> it.getLikes() != null && it.getLikes().getCount() != null)
                .max(comparing(it -> it.getLikes().getCount()))
                .map(it -> new PostReferenceDto(it.getPostId(), it.getSourceId(), it.getDate()))
                .orElseThrow();
    }

    //TODO: mark as supplied
    @SneakyThrows
    @Override
    public WallPostDto supplyWallPost() {
        return postRepository.findPostByType(PostType.WALL.name())
                .map(
                        it -> new WallPostDto(
                                it.getMessage(),
                                convert(it.getFile())
                        )
                )
                .orElseThrow();
    }

    @SneakyThrows
    private static File convert(FileInfo file) {
        File destination = new File(file.getName());
        FileUtils.copyURLToFile(new URL(file.getReference()), destination);

        return destination;
    }

    @Override
    public String supplyWallMessage() {
        return postRepository.findPostByType(PostType.WALL_MESSAGE.name())
                .map(Post::getMessage)
                .orElseThrow();
    }

    @Override
    public File supplyMainPhoto() {
        return postRepository.findPostByType(PostType.MAIN_PHOTO.name())
                .map(it -> convert(it.getFile()))
                .orElseThrow();
    }
}
