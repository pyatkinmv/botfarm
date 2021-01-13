package ru.pyatkinmv.service;

import com.vk.api.sdk.client.actors.UserActor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pyatkinmv.dao.entities.Profile;
import ru.pyatkinmv.vk.forward.Subscriber;
import ru.pyatkinmv.vk.provide.SubscribeContentProvider;

import static ru.pyatkinmv.service.ModelToDtoMapper.convert;

@Service
@RequiredArgsConstructor
public class SubscribeService {
    private final SubscribeContentProvider subscribeContentProvider;
    private final Subscriber subscriber;

    public void subscribeOnGroup(Profile profile) {
        UserActor actor = convert(profile);
        Integer groupId = subscribeContentProvider.getGroup(actor);
        subscriber.subscribeOnGroup(actor, groupId);
    }

    public void subscribeOnUser(Profile profile) {
        UserActor actor = convert(profile);
        Integer userId = subscribeContentProvider.getUser(actor);
        subscriber.subscribeOnUser(actor, userId);
    }
}
