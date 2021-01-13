package ru.pyatkinmv.vk.forward;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.queries.likes.LikesType;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import ru.pyatkinmv.vk.dto.PhotoReferenceDto;
import ru.pyatkinmv.vk.dto.PostReferenceDto;

@Component
@RequiredArgsConstructor
public class Liker {
    private final VkApiClient vk;

    @SneakyThrows
    public void likePhoto(UserActor actor, PhotoReferenceDto photo) {
        vk.likes()
                .add(actor, LikesType.PHOTO, photo.getId())
                .ownerId(photo.getOwnerId())
                .execute();
    }

    @SneakyThrows
    public void likePost(UserActor actor, PostReferenceDto post) {
        vk.likes()
                .add(actor, LikesType.POST, post.getId())
                .ownerId(post.getOwnerId())
                .execute();
    }
}
