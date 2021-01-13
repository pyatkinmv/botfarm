package ru.pyatkinmv.vk.forward;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.objects.photos.Photo;
import com.vk.api.sdk.objects.photos.responses.OwnerUploadResponse;
import com.vk.api.sdk.queries.wall.WallPostQuery;
import com.vk.api.sdk.queries.wall.WallRepostQuery;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.springframework.stereotype.Component;
import ru.pyatkinmv.vk.dto.PostReferenceDto;
import ru.pyatkinmv.vk.dto.WallPostDto;

import java.io.File;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
@RequiredArgsConstructor
public class Poster {
    private final VkApiClient vk;

    private WallPostQuery buildWallPhotoPostQuery(UserActor actor, File file) {
        Photo photo = uploadWallPhoto(actor, file);
        String attachments = String.format("photo%s_%s", photo.getOwnerId(), photo.getId());

        return vk.wall()
                .post(actor)
                .attachments(attachments);
    }

    @SneakyThrows
    private Photo uploadWallPhoto(UserActor actor, File file) {
        val uploadUrl = vk.photos()
                .getWallUploadServer(actor)
                .execute()
                .getUploadUrl();

        val response = vk.upload()
                .photoWall(uploadUrl, file)
                .execute();

        val photos = vk.photos()
                .saveWallPhoto(actor, response.getPhoto())
                .server(response.getServer())
                .hash(response.getHash())
                .execute();

        return photos.get(0);
    }

    @SneakyThrows
    public void postWallPost(UserActor actor, WallPostDto wallPost) {
        WallPostQuery wallPostQuery = buildWallPhotoPostQuery(actor, wallPost.getFile());

        if (isBlank(wallPost.getMessage())) {
            wallPostQuery.execute();
        } else {
            wallPostQuery.message(wallPost.getMessage())
                    .execute();
        }
    }

    @SneakyThrows
    public void postWallMessage(UserActor actor, String message) {
        vk.wall()
                .post(actor)
                .message(message)
                .execute();
    }

    @SneakyThrows
    public void postMainPhoto(UserActor actor, File file) {
        String uploadUrl = vk.photos()
                .getOwnerPhotoUploadServer(actor)
                .ownerId(actor.getId())
                .execute()
                .getUploadUrl();

        OwnerUploadResponse response = vk.upload()
                .photoOwner(uploadUrl, file)
                .execute();

        vk.photos()
                .saveOwnerPhoto(actor)
                .photo(response.getPhoto())
                .server(response.getServer().toString())
                .hash(response.getHash())
                .execute();
    }

    @SneakyThrows
    public void repost(UserActor actor, PostReferenceDto post) {
        buildWallRepostQuery(actor, post).execute();
    }

    // NOTE: If owner is a group than ownerId must be prefixed with minus.
    private WallRepostQuery buildWallRepostQuery(UserActor actor, PostReferenceDto post) {
        return vk.wall()
                .repost(actor, String.format("wall%s_%s", post.getOwnerId(), post.getId()));
    }
}
