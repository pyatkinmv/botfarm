package ru.pyatkinmv.impl;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.objects.photos.Photo;
import com.vk.api.sdk.objects.photos.responses.OwnerUploadResponse;
import com.vk.api.sdk.queries.wall.WallPostQuery;
import com.vk.api.sdk.queries.wall.WallRepostQuery;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import ru.pyatkinmv.api.ContentProvider;

import java.io.File;


@RequiredArgsConstructor
public class ContentProviderImpl implements ContentProvider {
    private final VkApiClient vk;
    private final UserActor actor;

    @SneakyThrows
    private WallPostQuery buildWallPhotoPostQuery(File file) {
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

        Photo photo = photos.get(0);

        String attachments = String.format("photo%s_%s", photo.getOwnerId(), photo.getId());

        return vk.wall()
                .post(actor)
                .attachments(attachments);
    }

    @SneakyThrows
    @Override
    public void postWallPhoto(File file) {
        buildWallPhotoPostQuery(file).execute();
    }

    @SneakyThrows
    @Override
    public void postWallPhoto(File file, String message) {
        buildWallPhotoPostQuery(file)
                .message(message)
                .execute();
    }

    @SneakyThrows
    @Override
    public void postWallMessage(String message) {
        vk.wall()
                .post(actor)
                .message(message)
                .execute();
    }

    @SneakyThrows
    @Override
    public void postMainPhoto(File file) {
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
    @Override
    public void repost(Integer ownerId, Integer postId) {
        buildWallRepostQuery(ownerId, postId).execute();
    }


    @SneakyThrows
    @Override
    public void repost(Integer ownerId, Integer postId, String message) {
        buildWallRepostQuery(ownerId, postId)
                .message(message)
                .execute();
    }

    // NOTE: If owner is a group than ownerId must be prefixed with minus.
    private WallRepostQuery buildWallRepostQuery(Integer ownerId, Integer postId) {
        return vk.wall()
                .repost(actor, String.format("wall%s_%s", ownerId, postId));
    }
}
