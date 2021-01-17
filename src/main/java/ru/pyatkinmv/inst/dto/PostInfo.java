package ru.pyatkinmv.inst.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PostInfo {
    private List<ImageInfo> images;
    private String message;
    private Long createdTime;
    private Integer likesCount;

    @Data
    @Builder
    public static class ImageInfo {
        private String url;
        private String name;
    }
}
