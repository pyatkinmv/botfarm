package ru.pyatkinmv.vk.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class NewsFeedLikesItem {
    private Integer count;
    @SerializedName("user_likes")
    private Integer userLikes;
}
