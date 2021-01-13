package ru.pyatkinmv.vk.dto;

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
        private NewsFeedLikesItem likes;
        private NewsFeedRepostsItem reposts;
        private List<NewsFeedAttachment> attachments;
    }

    @Data
    public static class NewsFeedAttachment {
        private String type;
        private NewsFeedPhotoItem photo;
    }

    @Data
    public static class NewsFeedPhotoItem {
        private Integer id;
        @SerializedName("owner_id")
        private Integer ownerId;
        private Integer date;
    }
}
