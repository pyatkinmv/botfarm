package ru.pyatkinmv.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.pyatkinmv.BotfarmApplication;
import ru.pyatkinmv.consume.api.SubscribeConsumer;
import ru.pyatkinmv.dto.PostReferenceDto;
import ru.pyatkinmv.supply.api.SubscribeSupplier;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = BotfarmApplication.class
)
class SubscribeServiceTest {
    @Autowired
//    private LikeService likeService;
            SubscribeSupplier subscribeSupplier;
    @Autowired
    SubscribeConsumer subscribeConsumer;

    @Test
    void test() {
//        final PhotoReferenceDto photoReferenceDto = likeSupplier.supplyPhoto();
//        System.out.println(photoReferenceDto);
//        likeConsumer.likePhoto(photoReferenceDto);
        final Integer userId = subscribeSupplier.supplyUser();
        System.out.println(userId);
        subscribeConsumer.subscribeOnUser(userId);
    }
}