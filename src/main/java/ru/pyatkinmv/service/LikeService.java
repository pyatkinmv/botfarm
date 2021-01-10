package ru.pyatkinmv.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pyatkinmv.consume.api.LikeConsumer;
import ru.pyatkinmv.supply.api.LikeSupplier;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeConsumer likeConsumer;
    private final LikeSupplier likeSupplier;

//    public void likePhotos(int count) {
//        likeSupplier.supplyPhotos();
//    }
}
