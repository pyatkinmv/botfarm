package ru.pyatkinmv.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.io.File;

@Data
@AllArgsConstructor
public class WallPostDto {
    private String message;
    private File file;
}
