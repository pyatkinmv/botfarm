package ru.pyatkinmv.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pyatkinmv.consume.api.PostConsumer;
import ru.pyatkinmv.supply.api.PostSupplier;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostSupplier postSupplier;
    private final PostConsumer postConsumer;

    public void postMainPhoto() {
        postConsumer.postMainPhoto(postSupplier.supplyMainPhoto());
    }

    public void postWallMessage() {
        postConsumer.postWallMessage(postSupplier.supplyWallMessage());
    }

    public void postWallPost() {
        postConsumer.postWallPost(postSupplier.supplyWallPost());
    }

    public void repost() {
        postConsumer.repost(postSupplier.supplyPostToRepost());
    }
}
