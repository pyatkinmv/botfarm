package ru.pyatkinmv.vk.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class NewsFeedRepostsItem {
    private Integer count;
    @SerializedName("user_reposted")
    private Integer userReposted;
}
