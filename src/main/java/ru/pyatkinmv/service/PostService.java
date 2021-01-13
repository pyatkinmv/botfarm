package ru.pyatkinmv.service;

import com.vk.api.sdk.client.actors.UserActor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pyatkinmv.dao.entities.Profile;
import ru.pyatkinmv.service.provide.PostContentProvider;
import ru.pyatkinmv.vk.dto.PostReferenceDto;
import ru.pyatkinmv.vk.forward.Poster;
import ru.pyatkinmv.vk.provide.RepostContentProvider;

import static ru.pyatkinmv.service.ModelToDtoMapper.convert;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostContentProvider postContentProvider;
    private final RepostContentProvider repostContentProvider;
    private final Poster poster;

    public void postMainPhoto(Profile profile) {
        poster.postMainPhoto(
                new UserActor(profile.getUserId(), profile.getToken()),
                postContentProvider.getMainPhoto(profile.getId())
        );
    }

    public void postWallMessage(Profile profile) {
        poster.postWallMessage(
                new UserActor(profile.getUserId(), profile.getToken()),
                postContentProvider.getWallMessage(profile.getId())
        );
    }

    public void postWallPost(Profile profile) {
        poster.postWallPost(
                new UserActor(profile.getUserId(), profile.getToken()),
                postContentProvider.getWallPost(profile.getId())
        );
    }

    public void repost(Profile profile) {
        UserActor actor = convert(profile);
        PostReferenceDto postForRepost = repostContentProvider.getPostForRepost(actor);
        poster.repost(actor, postForRepost);
    }
}
