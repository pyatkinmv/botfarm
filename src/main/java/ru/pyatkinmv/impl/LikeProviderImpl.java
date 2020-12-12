package ru.pyatkinmv.impl;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.queries.likes.LikesType;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ru.pyatkinmv.api.LikeProvider;

@RequiredArgsConstructor
public class LikeProviderImpl implements LikeProvider {
    private final VkApiClient vk;
    private final UserActor actor;

    @SneakyThrows
    @Override
    public void likePhoto(Integer photoId, Integer ownerId) {
        vk.likes()
                .add(actor, LikesType.PHOTO, photoId)
                .ownerId(ownerId)
                .execute();
    }

    @SneakyThrows
    @Override
    public void likePost(Integer postId, Integer ownerId) {
        vk.likes()
                .add(actor, LikesType.POST, postId)
                .ownerId(ownerId)
                .execute();
    }
}
