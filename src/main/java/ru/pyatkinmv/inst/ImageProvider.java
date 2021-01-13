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
import ru.pyatkinmv.inst.dto.ImageInfo;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;

@Component
public class ImageProvider {
    @Value("${instagram.username}")
    private String clientUsername;
    @Value("${instagram.password}")
    private String clientPassword;

    @SneakyThrows
    public Collection<ImageInfo> getImages(String username) {
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
    private static List<ImageInfo> getImages(IGClient client, Long pk) {
        String maxId = "";
        List<TimelineMedia> items = new ArrayList<>();

        while (maxId != null) {
            FeedUserResponse feedUserResponse = client.sendRequest(new FeedUserRequest(pk, maxId)).get();
            maxId = feedUserResponse.getNext_max_id();
            items.addAll(feedUserResponse.getItems());
        }

        return items.stream()
                .flatMap(it -> mediaToImages(it).stream())
                .collect(Collectors.toList());
    }

    private static List<ImageInfo> mediaToImages(TimelineMedia media) {
        if (media.getUsertags() != null) {
            return Collections.emptyList();
        }

        if (media instanceof TimelineImageMedia) {
            TimelineImageMedia imageMedia = (TimelineImageMedia) media;

            ImageVersionsMeta imageVersionsMeta = selectLargest(
                    imageMedia.getImage_versions2().getCandidates()
            );

            return singletonList(toImage(imageVersionsMeta, imageMedia));

        } else if (media instanceof TimelineCarouselMedia) {
            TimelineCarouselMedia carouselMedia = (TimelineCarouselMedia) media;

            return carouselMedia.getCarousel_media()
                    .stream()
                    .filter(it -> it instanceof ImageCaraouselItem)
                    .map(it -> (ImageCaraouselItem) it)
                    .map(it -> selectLargest(it.getImage_versions2().getCandidates()))
                    .map(it -> toImage(it, carouselMedia))
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    private static ImageInfo toImage(ImageVersionsMeta imageVersionsMeta, TimelineMedia media) {
        ImageInfo imageInfo = ImageInfo.builder()
                .url(imageVersionsMeta.getUrl())
                .name(urlToName(imageVersionsMeta.getUrl()))
                .likesCount(media.getLike_count())
                .build();

        Comment.Caption caption = media.getCaption();

        if (caption != null) {
            imageInfo.setDate(caption.getCreated_at() * 1000);
            imageInfo.setMessage(caption.getText());
        } else {
            imageInfo.setDate(new Date().getTime());
        }

        return imageInfo;
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
