package ru.pyatkinmv.vk.provide;

import com.google.gson.Gson;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.queries.newsfeed.NewsfeedGetFilter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import ru.pyatkinmv.vk.dto.NewsFeedPostGetQuery;
import ru.pyatkinmv.vk.dto.NewsFeedRepostsItem;
import ru.pyatkinmv.vk.dto.PostReferenceDto;
import ru.pyatkinmv.exception.ItemNotFoundException;

import static java.util.Comparator.comparing;

@Component
@RequiredArgsConstructor
public class RepostContentProvider {
    private final VkApiClient vk;

    @SneakyThrows
    public PostReferenceDto getPostForRepost(UserActor actor) {
        return new Gson().fromJson(
                vk.newsfeed()
                        .get(actor)
                        .filters(NewsfeedGetFilter.POST)
                        .count(100)
                        .executeAsString(),
                NewsFeedPostGetQuery.class)
                .getResponse()
                .getItems()
                .stream()
                .filter(it -> it.getReposts() != null && isNotReposted(it.getReposts()))
                .filter(it -> it.getLikes() != null && it.getLikes().getCount() != null)
                .max(comparing(it -> it.getLikes().getCount()))
                .map(it -> new PostReferenceDto(it.getPostId(), it.getSourceId(), it.getDate()))
                .orElseThrow(ItemNotFoundException::new);
    }

    private static boolean isNotReposted(NewsFeedRepostsItem reposts) {
        if (reposts.getUserReposted() == 1) {
            return false;
        }

        if (reposts.getUserReposted() == 0) {
            return true;
        }

        throw new IllegalStateException("userReposted should be 0 or 1");
    }
}
