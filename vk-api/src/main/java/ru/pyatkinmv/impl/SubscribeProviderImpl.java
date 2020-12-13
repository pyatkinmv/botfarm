package ru.pyatkinmv.impl;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ru.pyatkinmv.api.SubscribeProvider;

@RequiredArgsConstructor
public class SubscribeProviderImpl implements SubscribeProvider {
    private final VkApiClient vk;
    private final UserActor actor;

    @SneakyThrows
    @Override
    public void subscribeOnUser(Integer userId) {
        vk.friends()
                .add(actor, userId)
                .execute();
    }

    @SneakyThrows
    @Override
    public void subscribeOnGroup(Integer groupId) {
        vk.groups()
                .join(actor)
                .groupId(groupId)
                .execute();
    }
}
