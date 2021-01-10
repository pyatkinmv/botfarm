package ru.pyatkinmv.consume.api;


import ru.pyatkinmv.dto.WallPostDto;
import ru.pyatkinmv.dto.PostReferenceDto;

import java.io.File;

public interface PostConsumer {
    void postWallPost(WallPostDto wallPost);

    void postWallMessage(String message);

    void postMainPhoto(File file);

    void repost(PostReferenceDto post);
}
