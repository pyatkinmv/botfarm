package ru.pyatkinmv.supply.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class NewsFeedPhotoGetQuery {
    private NewsFeedPhotoGetResponse response;

    @Data
    public static class NewsFeedPhotoGetResponse {
        private List<NewsFeedItem> items;
    }

    @Data
    public static class NewsFeedItem {
        @SerializedName("source_id")
        private Integer sourceId;
        private Integer date;
        private NewsFeedPhotos photos;
    }

    @Data
    public static class NewsFeedPhotos {
        private List<NewsFeedPhotoItem> items;
    }

    @Data
    public static class NewsFeedPhotoItem {
        private Integer id;
        private Integer date;
    }
}
