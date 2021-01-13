package ru.pyatkinmv.inst.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImageInfo {
    private String url;
    private String message;
    private Long date;
    private Integer likesCount;
    private String name;
}
