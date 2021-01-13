package ru.pyatkinmv.service;

import com.vk.api.sdk.client.actors.UserActor;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.pyatkinmv.dao.entities.Profile;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ModelToDtoMapper {
    public static UserActor convert(Profile profile) {
        return new UserActor(profile.getUserId(), profile.getToken());
    }
}
