package ru.pyatkinmv.supply.api;


import ru.pyatkinmv.dto.PhotoReferenceDto;
import ru.pyatkinmv.dto.PostReferenceDto;

public interface LikeSupplier {
    PhotoReferenceDto supplyPhoto();

    PostReferenceDto supplyPost();
}
