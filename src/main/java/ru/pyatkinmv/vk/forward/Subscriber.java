package ru.pyatkinmv.vk.forward;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class Subscriber {
    private final VkApiClient vk;

    @SneakyThrows
    public void subscribeOnUser(UserActor actor, Integer userId) {
        vk.friends()
                .add(actor, userId)
                .execute();
    }

    @SneakyThrows
    public void subscribeOnGroup(UserActor actor, Integer groupId) {
        vk.groups()
                .join(actor)
                .groupId(groupId)
                .execute();
    }
}
