package ru.pyatkinmv.vk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.File;
import java.util.Collection;

@Data
@AllArgsConstructor
public class WallPostDto {
    private Collection<File> images;
    private String message;
}
