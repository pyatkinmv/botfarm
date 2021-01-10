package ru.pyatkinmv.supply.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class NewsFeedPostGetQuery {
    private NewsFeedPostGetResponse response;

    @Data
    public static class NewsFeedPostGetResponse {
        private List<NewsFeedItem> items;
    }

    @Data
    public static class NewsFeedItem {
        @SerializedName("source_id")
        private Integer sourceId;
        @SerializedName("post_id")
        private Integer postId;
        private Integer date;
        private LikesItem likes;
    }

    @Data
    public static class LikesItem {
        private Integer count;
    }
}
