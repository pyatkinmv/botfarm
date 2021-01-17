package ru.pyatkinmv.service;

import org.springframework.stereotype.Component;
import ru.pyatkinmv.dao.entities.ProfileType;

import java.util.Map;
import java.util.Set;

import static ru.pyatkinmv.dao.entities.ProfileType.DEFAULT;

@Component
public class ProfileMethodsProvider {
    private static final Map<ProfileType, Set<MethodInfo>> PROFILE_TYPE_TO_METHODS_MAP = Map.of(
            DEFAULT, Set.of(MethodInfo.values())
    );

    public Set<MethodInfo> getMethods(ProfileType profileType) {
        return PROFILE_TYPE_TO_METHODS_MAP.get(profileType);
    }
}
