package ru.pyatkinmv.inst;

import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.actions.users.UserAction;
import com.github.instagram4j.instagram4j.models.media.ImageVersionsMeta;
import com.github.instagram4j.instagram4j.models.media.timeline.*;
import com.github.instagram4j.instagram4j.requests.feed.FeedUserRequest;
import com.github.instagram4j.instagram4j.responses.feed.FeedUserResponse;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.pyatkinmv.inst.dto.PostInfo;

import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Component
public class ImageProvider {
    @Value("${instagram.username}")
    private String clientUsername;
    @Value("${instagram.password}")
    private String clientPassword;

    @SneakyThrows
    public Collection<PostInfo> getImages(String username) {
        IGClient client = IGClient.builder()
                .username(clientUsername)
                .password(clientPassword)
                .simulatedLogin();

        UserAction userAction = client.actions()
                .users()
                .findByUsername(username)
                .get();

        return getImages(client, userAction.getUser().getPk());
    }

    @SneakyThrows
    private static List<PostInfo> getImages(IGClient client, Long pk) {
        String maxId = "";
        List<TimelineMedia> items = new ArrayList<>();

        while (maxId != null) {
            FeedUserResponse feedUserResponse = client.sendRequest(new FeedUserRequest(pk, maxId)).get();
            maxId = feedUserResponse.getNext_max_id();
            items.addAll(feedUserResponse.getItems());
        }

        return items.stream()
                .map(ImageProvider::mediaToPost)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toList());
    }

    private static Optional<PostInfo> mediaToPost(TimelineMedia media) {
        if (media instanceof TimelineImageMedia) {
            TimelineImageMedia postMedia = (TimelineImageMedia) media;

            ImageVersionsMeta image = selectLargest(
                    postMedia.getImage_versions2().getCandidates()
            );

            return Optional.of(toPostInfo(List.of(image), postMedia));

        } else if (media instanceof TimelineCarouselMedia) {
            TimelineCarouselMedia postMedia = (TimelineCarouselMedia) media;

            Collection<ImageVersionsMeta> postImages = postMedia.getCarousel_media()
                    .stream()
                    .filter(it -> it instanceof ImageCaraouselItem)
                    .map(it -> (ImageCaraouselItem) it)
                    .map(it -> selectLargest(it.getImage_versions2().getCandidates()))
                    .collect(toList());

            return Optional.of(toPostInfo(postImages, postMedia));
        }

        return Optional.empty();
    }

    private static PostInfo toPostInfo(Collection<ImageVersionsMeta> images, TimelineMedia post) {
        return PostInfo.builder()
                .images(
                        images.stream()
                                .map(
                                        it -> PostInfo.ImageInfo.builder()
                                                .url(it.getUrl())
                                                .name(urlToName(it.getUrl()))
                                                .build()
                                )
                                .collect(toList())
                )
                .likesCount(post.getLike_count())
                .createdTime(post.getTaken_at() * 1000L)
                .message(
                        Optional.ofNullable(post.getCaption())
                                .map(Comment::getText)
                                .orElse(null)
                )
                .build();
    }

    private static String urlToName(String url) {
        return Stream.of(url.split("/"))
                .flatMap(it -> Stream.of(it.split("\\?")))
                .filter(it -> it.contains(".jpg") || it.contains("jpeg") || it.contains("png"))
                .findFirst()
                .orElseThrow();
    }

    private static ImageVersionsMeta selectLargest(List<ImageVersionsMeta> candidates) {
        return candidates.stream()
                .max(Comparator.comparing(it -> it.getHeight() * it.getWidth()))
                .orElseThrow();
    }
}
