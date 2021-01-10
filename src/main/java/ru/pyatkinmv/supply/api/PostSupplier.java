package ru.pyatkinmv.supply.api;


import ru.pyatkinmv.dto.WallPostDto;
import ru.pyatkinmv.dto.PostReferenceDto;

import java.io.File;

public interface PostSupplier {
    WallPostDto supplyWallPost();

    String supplyWallMessage();

    File supplyMainPhoto();

    PostReferenceDto supplyPostToRepost();
}
