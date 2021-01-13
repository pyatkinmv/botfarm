package ru.pyatkinmv.service;

import com.vk.api.sdk.client.actors.UserActor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pyatkinmv.dao.entities.Profile;
import ru.pyatkinmv.vk.dto.PhotoReferenceDto;
import ru.pyatkinmv.vk.dto.PostReferenceDto;
import ru.pyatkinmv.vk.forward.Liker;
import ru.pyatkinmv.vk.provide.LikeContentProvider;

import static ru.pyatkinmv.service.ModelToDtoMapper.convert;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final Liker liker;
    private final LikeContentProvider likeContentProvider;

    public void likePhoto(Profile profile) {
        UserActor actor = convert(profile);
        PhotoReferenceDto photo = likeContentProvider.getPhoto(actor);
        liker.likePhoto(actor, photo);
    }

    public void likePost(Profile profile) {
        UserActor actor = convert(profile);
        PostReferenceDto post = likeContentProvider.getPost(actor);
        liker.likePost(actor, post);
    }
}
