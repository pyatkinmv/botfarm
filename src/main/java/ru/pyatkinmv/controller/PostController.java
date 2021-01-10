package ru.pyatkinmv.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pyatkinmv.consume.api.LikeConsumer;
import ru.pyatkinmv.consume.api.PostConsumer;
import ru.pyatkinmv.consume.api.SubscribeConsumer;
import ru.pyatkinmv.supply.api.LikeSupplier;
import ru.pyatkinmv.supply.api.PostSupplier;
import ru.pyatkinmv.supply.api.SubscribeSupplier;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {
    private final PostConsumer postConsumer;
    private final PostSupplier postSupplier;
    private final LikeConsumer likeConsumer;
    private final LikeSupplier likeSupplier;
    private final SubscribeConsumer subscribeConsumer;
    private final SubscribeSupplier subscribeSupplier;

    @GetMapping
    public void post() {
//        final PostReferenceDto postReferenceDto = postSupplier.supplyPostToRepost();
//        postService.repost(postReferenceDto);
//
//        final WallPostDto wallPostDto = postSupplier.supplyWallPost();
//        postService.postWallPost(wallPostDto);
//
//        final String message = postSupplier.supplyWallMessage();
//        postService.postWallMessage(message);
//
//        final File mainPhoto = postSupplier.supplyMainPhoto();
//        postService.postMainPhoto(mainPhoto);

//        likeSupplier.supplyPhoto()
//                .stream()
//                .limit(1)
//                .forEach(likeConsumer::likePhoto);
//
//        likeSupplier.supplyPost()
//                .stream()
//                .limit(1)
//                .forEach(likeConsumer::likePost);

//        subscribeSupplier.supplyGroup()
//                .stream()
//                .limit(2)
//                .forEach(subscribeConsumer::subscribeOnGroup);
//
//        subscribeSupplier.supplyUser()
//                .stream()
//                .limit(2)
//                .forEach(subscribeConsumer::subscribeOnUser);
    }
}
