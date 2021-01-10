package ru.pyatkinmv.consume.api;

import ru.pyatkinmv.dto.PhotoReferenceDto;
import ru.pyatkinmv.dto.PostReferenceDto;

public interface LikeConsumer {
    void likePhoto(PhotoReferenceDto photo);

    void likePost(PostReferenceDto post);
}
