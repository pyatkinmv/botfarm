package ru.pyatkinmv.vk.provide;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.objects.users.UserMin;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import ru.pyatkinmv.exception.ItemNotFoundException;

@Component
@RequiredArgsConstructor
public class SubscribeContentProvider {
    public static final int MAX_COUNT = 500;
    public static final int MAX_SUGGESTED_SOURCES_COUNT = 1000;
    public static final String PAGE = "page";
    public static final String TYPE = "type";

    private final VkApiClient vk;

    @SneakyThrows
    public Integer getUser(UserActor actor) {
        return vk.friends()
                .getSuggestions(actor)
                .count(MAX_COUNT)
                .execute()
                .getItems()
                .stream()
                .findFirst()
                .map(UserMin::getId)
                .orElseThrow(ItemNotFoundException::new);
    }

    @SneakyThrows
    public Integer getGroup(UserActor actor) {
        return vk.newsfeed()
                .getSuggestedSources(actor)
                .count(MAX_SUGGESTED_SOURCES_COUNT)
                .execute()
                .getItems()
                .stream()
                .filter(it -> PAGE.equals(it.get(TYPE).getAsString()))
                .map(it -> it.get("id").getAsInt())
                .findFirst()
                .orElseThrow(ItemNotFoundException::new);
    }
}
