package ru.pyatkinmv.consume.impl;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.objects.photos.Photo;
import com.vk.api.sdk.objects.photos.responses.OwnerUploadResponse;
import com.vk.api.sdk.queries.wall.WallPostQuery;
import com.vk.api.sdk.queries.wall.WallRepostQuery;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import ru.pyatkinmv.consume.api.PostConsumer;
import ru.pyatkinmv.dto.WallPostDto;
import ru.pyatkinmv.dto.PostReferenceDto;

import java.io.File;

import static org.apache.commons.lang3.StringUtils.isBlank;


@RequiredArgsConstructor
public class PostConsumerImpl implements PostConsumer {
    private final VkApiClient vk;
    private final UserActor actor;

    private WallPostQuery buildWallPhotoPostQuery(File file) {
        Photo photo = uploadWallPhoto(file);
        String attachments = String.format("photo%s_%s", photo.getOwnerId(), photo.getId());

        return vk.wall()
                .post(actor)
                .attachments(attachments);
    }

    @SneakyThrows
    private Photo uploadWallPhoto(File file) {
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
    @Override
    public void postWallPost(WallPostDto wallPost) {
        WallPostQuery wallPostQuery = buildWallPhotoPostQuery(wallPost.getFile());

        if (isBlank(wallPost.getMessage())) {
            wallPostQuery.execute();
        } else {
            wallPostQuery.message(wallPost.getMessage())
                    .execute();
        }
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
    public void repost(PostReferenceDto post) {
        buildWallRepostQuery(post).execute();
    }

    // NOTE: If owner is a group than ownerId must be prefixed with minus.
    private WallRepostQuery buildWallRepostQuery(PostReferenceDto post) {
        return vk.wall()
                .repost(actor, String.format("wall%s_%s", post.getOwnerId(), post.getId()));
    }
}
