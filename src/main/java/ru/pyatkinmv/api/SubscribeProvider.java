package ru.pyatkinmv.api;

public interface SubscribeProvider {
    void subscribeOnUser(Integer userId);

    void subscribeOnGroup(Integer groupId);
}
