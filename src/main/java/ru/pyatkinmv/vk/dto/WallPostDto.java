package ru.pyatkinmv.vk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.File;

@Data
@AllArgsConstructor
public class WallPostDto {
    private String message;
    private File file;
}
