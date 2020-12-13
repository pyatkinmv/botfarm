package ru.pyatkinmv.api;

public interface LikeProvider {
    void likePhoto(Integer photoId, Integer ownerId);

    void likePost(Integer postId, Integer ownerId);
}
