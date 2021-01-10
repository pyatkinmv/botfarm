package ru.pyatkinmv.consume.impl;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiFriendsAddInEnemyException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ru.pyatkinmv.consume.api.SubscribeConsumer;

@Slf4j
@RequiredArgsConstructor
public class SubscribeConsumerImpl implements SubscribeConsumer {
    private final VkApiClient vk;
    private final UserActor actor;

    @SneakyThrows
    @Override
    public void subscribeOnUser(Integer userId) {
        try {
            vk.friends()
                    .add(actor, userId)
                    .execute();
        } catch (ApiFriendsAddInEnemyException ex) {
            log.info("Ignore exception: ", ex);
        }
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
