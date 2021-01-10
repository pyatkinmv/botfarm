package ru.pyatkinmv.supply.impl;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.objects.users.UserMin;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ru.pyatkinmv.supply.api.SubscribeSupplier;

@RequiredArgsConstructor
public class SubscribeSupplierImpl implements SubscribeSupplier {
    public static final int MAX_COUNT = 500;
    public static final int MAX_SUGGESTED_SOURCES_COUNT = 1000;
    public static final String PAGE = "page";
    public static final String TYPE = "type";

    private final VkApiClient vk;
    private final UserActor actor;

    @Override
    @SneakyThrows
    public Integer supplyUser() {
        return vk.friends()
                .getSuggestions(actor)
                .count(MAX_COUNT)
                .execute()
                .getItems()
                .stream()
                .map(UserMin::getId)
                .findFirst()
                .orElseThrow();
    }

    @SneakyThrows
    @Override
    public Integer supplyGroup() {
        return vk.newsfeed()
                .getSuggestedSources(actor)
                .count(MAX_SUGGESTED_SOURCES_COUNT)
                .execute()
                .getItems()
                .stream()
                .filter(it -> PAGE.equals(it.get(TYPE).getAsString()))
                .map(it -> it.get("id").getAsInt())
                .findFirst()
                .orElseThrow();
    }
}
