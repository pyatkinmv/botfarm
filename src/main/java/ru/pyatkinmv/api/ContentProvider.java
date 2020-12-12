package ru.pyatkinmv.api;

import java.io.File;

public interface ContentProvider {
    void postWallPhoto(File file);

    void postWallPhoto(File file, String message);

    void postWallMessage(String message);

    void postMainPhoto(File file);

    void repost(Integer ownerId, Integer postId);

    void repost(Integer ownerId, Integer postId, String comment);
}
