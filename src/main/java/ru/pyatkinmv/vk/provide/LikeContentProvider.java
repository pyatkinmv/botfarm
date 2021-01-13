package ru.pyatkinmv.vk.provide;

import com.google.gson.Gson;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.queries.newsfeed.NewsfeedGetFilter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.pyatkinmv.exception.ItemNotFoundException;
import ru.pyatkinmv.vk.dto.*;

import java.util.List;

import static com.vk.api.sdk.objects.newsfeed.NewsfeedItemType.PHOTO;
import static java.util.stream.Collectors.toList;

@Component
@RequiredArgsConstructor
@Slf4j
public class LikeContentProvider {
    private static final int MAX_COUNT = 100;
    private static final Gson GSON = new Gson();

    private final VkApiClient vk;

    private static boolean isUser(Integer sourceId) {
        return sourceId > 0;
    }

    @SneakyThrows
    public PhotoReferenceDto getPhoto(UserActor actor) {
        NewsFeedPhotoGetQuery newsFeedPhotoGetQuery = GSON.fromJson(
                vk.newsfeed()
                        .get(actor)
                        .filters(NewsfeedGetFilter.WALL_PHOTO, NewsfeedGetFilter.PHOTO)
                        .count(MAX_COUNT)
                        .executeAsString(),
                NewsFeedPhotoGetQuery.class);

        return newsFeedPhotoGetQuery.getResponse()
                .getItems()
                .stream()
                .filter(it -> it.getPhotos() != null && it.getPhotos().getItems() != null)
                .flatMap(it -> convertAndFilterNotLiked(it).stream())
                .filter(it -> isUser(it.getOwnerId()))
                .findFirst()
                .orElseGet(() -> {
                    log.info("Can not find photo in newsfeed. Searching in recommended...");
                    return getRecommendedPhoto(actor);
                });
    }

    @SneakyThrows
    public PhotoReferenceDto getRecommendedPhoto(UserActor actor) {
        return GSON.fromJson(
                vk.newsfeed()
                        .getRecommended(actor)
                        .count(MAX_COUNT)
                        .executeAsString(),
                NewsFeedPostGetQuery.class
        )
                .getResponse()
                .getItems()
                .stream()
                .flatMap(it -> it.getAttachments().stream())
                .filter(it -> PHOTO.getValue().equals(it.getType()))
                .filter(it -> isUser(it.getPhoto().getOwnerId()))
                .findFirst()
                .map(NewsFeedPostGetQuery.NewsFeedAttachment::getPhoto)
                .map(
                        it -> PhotoReferenceDto.builder()
                                .id(it.getId())
                                .ownerId(it.getOwnerId())
                                .date(it.getDate())
                                .build()
                )
                .orElseThrow(ItemNotFoundException::new);
    }

    @SneakyThrows
    public PostReferenceDto getRecommendedPost(UserActor actor) {
        return GSON.fromJson(
                vk.newsfeed()
                        .getRecommended(actor)
                        .count(MAX_COUNT)
                        .executeAsString(),
                NewsFeedPostGetQuery.class
        )
                .getResponse()
                .getItems()
                .stream()
                .filter(it -> isUser(it.getSourceId()))
                .findFirst()
                .map(
                        it -> PostReferenceDto.builder()
                                .id(it.getPostId())
                                .ownerId(it.getSourceId())
                                .date(it.getDate())
                                .build()
                )
                .orElseThrow(ItemNotFoundException::new);
    }

    private static List<PhotoReferenceDto> convertAndFilterNotLiked(NewsFeedPhotoGetQuery.NewsFeedItem newsFeedItem) {
        return newsFeedItem.getPhotos()
                .getItems()
                .stream()
                .filter(it -> it.getLikes() != null && isNotLiked(it.getLikes()))
                .map(it -> PhotoReferenceDto
                        .builder()
                        .id(it.getId())
                        .ownerId(newsFeedItem.getSourceId())
                        .date(it.getDate())
                        .build())
                .collect(toList());
    }

    private static boolean isNotLiked(NewsFeedLikesItem likesItem) {
        if (likesItem.getUserLikes() == 1) {
            return false;
        }

        if (likesItem.getUserLikes() == 0) {
            return true;
        }

        throw new IllegalStateException("userLikes should be 0 or 1");
    }

    @SneakyThrows
    public PostReferenceDto getPost(UserActor actor) {
        NewsFeedPostGetQuery newsFeedPostGetQuery = GSON.fromJson(
                vk.newsfeed()
                        .get(actor)
                        .filters(NewsfeedGetFilter.WALL_PHOTO, NewsfeedGetFilter.PHOTO)
                        .count(MAX_COUNT)
                        .executeAsString(),
                NewsFeedPostGetQuery.class);

        return newsFeedPostGetQuery.getResponse().getItems().stream()
                .filter(it -> it.getLikes() != null && isNotLiked(it.getLikes()))
                .map(LikeContentProvider::convert)
                .filter(it -> isUser(it.getOwnerId()))
                .findFirst()
                .orElseGet(() -> {
                    log.info("Can not find post in newsfeed. Searching in recommended...");
                    return getRecommendedPost(actor);
                });
    }

    private static PostReferenceDto convert(NewsFeedPostGetQuery.NewsFeedItem newsFeedItem) {
        return PostReferenceDto
                .builder()
                .id(newsFeedItem.getPostId())
                .ownerId(newsFeedItem.getSourceId())
                .date(newsFeedItem.getDate())
                .build();
    }
}


