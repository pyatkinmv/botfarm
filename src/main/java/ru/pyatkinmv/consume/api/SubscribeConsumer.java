package ru.pyatkinmv.consume.api;

public interface SubscribeConsumer {
    void subscribeOnUser(Integer userId);

    void subscribeOnGroup(Integer groupId);
}
