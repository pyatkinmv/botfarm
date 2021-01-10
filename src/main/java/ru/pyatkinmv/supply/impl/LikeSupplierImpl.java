package ru.pyatkinmv.supply.impl;

import com.google.gson.Gson;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.queries.likes.LikesType;
import com.vk.api.sdk.queries.newsfeed.NewsfeedGetFilter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ru.pyatkinmv.dto.PhotoReferenceDto;
import ru.pyatkinmv.dto.PostReferenceDto;
import ru.pyatkinmv.supply.api.LikeSupplier;
import ru.pyatkinmv.supply.dto.NewsFeedPhotoGetQuery;
import ru.pyatkinmv.supply.dto.NewsFeedPostGetQuery;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static ru.pyatkinmv.Utils.supplyAndWait;

@RequiredArgsConstructor
public class LikeSupplierImpl implements LikeSupplier {
    private static final int MAX_COUNT = 100;

    private final VkApiClient vk;
    private final UserActor actor;

    private static boolean isUser(Integer sourceId) {
        return sourceId > 0;
    }

    @SneakyThrows
    @Override
    public PhotoReferenceDto supplyPhoto() {
        Optional<PhotoReferenceDto> photoReference = Optional.empty();
        int endTime = (int) (new Date().getTime() / 1000);

        while (photoReference.isEmpty()) {
            NewsFeedPhotoGetQuery newsFeedPhotoGetQuery = new Gson().fromJson(
                    vk.newsfeed()
                            .get(actor)
                            .filters(NewsfeedGetFilter.WALL_PHOTO, NewsfeedGetFilter.PHOTO)
                            .count(MAX_COUNT)
                            .endTime(endTime)
                            .executeAsString(),
                    NewsFeedPhotoGetQuery.class);

            List<PhotoReferenceDto> photoReferences = newsFeedPhotoGetQuery.getResponse()
                    .getItems()
                    .stream()
                    .flatMap(
                            newsFeedItem -> newsFeedItem.getPhotos()
                                    .getItems()
                                    .stream()
                                    .map(
                                            it -> PhotoReferenceDto
                                                    .builder()
                                                    .id(it.getId())
                                                    .ownerId(newsFeedItem.getSourceId())
                                                    .date(it.getDate())
                                                    .build()
                                    )
                    )
                    .collect(Collectors.toList());

            photoReference = photoReferences.stream()
                    .filter(it -> isUser(it.getOwnerId()))
                    .filter(it ->
                            supplyAndWait(
                                    () -> isNotLiked(it.getId(), it.getOwnerId(), LikesType.PHOTO)
                            )
                    )
                    .findFirst();

            if (photoReference.isEmpty()) {
                endTime = photoReferences.stream()
                        .min(comparing(PhotoReferenceDto::getDate))
                        .orElseThrow()
                        .getDate();
            }
        }

        return photoReference.get();
    }

    @SneakyThrows
    private boolean isNotLiked(Integer itemId, Integer ownerId, LikesType type) {
        return !vk.likes()
                .isLiked(actor, type, itemId)
                .userId(actor.getId())
                .ownerId(ownerId)
                .execute()
                .isLiked();
    }

    @SneakyThrows
    @Override
    public PostReferenceDto supplyPost() {
        Optional<PostReferenceDto> postReference = Optional.empty();
        int endTime = (int) (new Date().getTime() / 1000);

        while (postReference.isEmpty()) {
            NewsFeedPostGetQuery newsFeedPostGetQuery = new Gson().fromJson(
                    vk.newsfeed()
                            .get(actor)
                            .filters(NewsfeedGetFilter.POST)
                            .count(MAX_COUNT)
                            .endTime(endTime)
                            .executeAsString(),
                    NewsFeedPostGetQuery.class);

            List<PostReferenceDto> postReferences = newsFeedPostGetQuery.getResponse()
                    .getItems()
                    .stream()
                    .map(
                            it -> PostReferenceDto
                                    .builder()
                                    .id(it.getPostId())
                                    .ownerId(it.getSourceId())
                                    .date(it.getDate())
                                    .build()
                    )
                    .collect(Collectors.toList());

            postReference = postReferences.stream()
                    .filter(it -> isUser(it.getOwnerId()))
                    .filter(it ->
                            supplyAndWait(
                                    () -> isNotLiked(it.getId(), it.getOwnerId(), LikesType.POST)
                            )
                    )
                    .findFirst();

            if (postReference.isEmpty()) {
                endTime = postReferences.stream()
                        .min(comparing(PostReferenceDto::getDate))
                        .orElseThrow()
                        .getDate();
            }
        }

        return postReference.get();
    }
}
