package ru.pyatkinmv.consume.impl;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.queries.likes.LikesType;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ru.pyatkinmv.consume.api.LikeConsumer;
import ru.pyatkinmv.dto.PhotoReferenceDto;
import ru.pyatkinmv.dto.PostReferenceDto;

@RequiredArgsConstructor
public class LikeConsumerImpl implements LikeConsumer {
    private final VkApiClient vk;
    private final UserActor actor;

    @SneakyThrows
    @Override
    public void likePhoto(PhotoReferenceDto photo) {
        vk.likes()
                .add(actor, LikesType.PHOTO, photo.getId())
                .ownerId(photo.getOwnerId())
                .execute();
    }

    @SneakyThrows
    @Override
    public void likePost(PostReferenceDto post) {
        vk.likes()
                .add(actor, LikesType.POST, post.getId())
                .ownerId(post.getOwnerId())
                .execute();
    }
}
