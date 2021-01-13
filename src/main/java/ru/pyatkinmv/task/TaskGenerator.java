package ru.pyatkinmv.task;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.pyatkinmv.dao.ProfileRepository;
import ru.pyatkinmv.dao.entities.Profile;
import ru.pyatkinmv.service.LikeService;
import ru.pyatkinmv.service.PostService;
import ru.pyatkinmv.service.SubscribeService;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Component
@Slf4j
public class TaskGenerator {
    private static final long ACTIVITY_PERIOD_MILLIS = (long) 16 * 60 * 60 * 1000;

    private final ProfileRepository profileRepository;
    private final List<MethodInfo> profileConsumerToRateMap;

    public TaskGenerator(LikeService likeService, SubscribeService subscribeService, PostService postService, ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
        this.profileConsumerToRateMap = List.of(
                new MethodInfo("LIKE_PHOTO", likeService::likePhoto, BigDecimal.valueOf(20.0)),
                new MethodInfo("LIKE_POST", likeService::likePost, BigDecimal.valueOf(20.0)),
                new MethodInfo("SUBSCRIBE_ON_USER", subscribeService::subscribeOnUser, BigDecimal.valueOf(50.0)),
                new MethodInfo("SUBSCRIBE_ON_GROUP", subscribeService::subscribeOnGroup, BigDecimal.valueOf(0.01)),
                new MethodInfo("POST_MAIN_PHOTO", postService::postMainPhoto, BigDecimal.valueOf(0.04)),
                new MethodInfo("POST_WALL_POST", postService::postWallPost, BigDecimal.valueOf(0.3)),
                new MethodInfo("REPOST", postService::repost, BigDecimal.valueOf(0.5))
        );
    }

    @Data
    @RequiredArgsConstructor
    private static class MethodInfo {
        private final String methodName;
        private final Consumer<Profile> method;
        private final BigDecimal rate;
    }

    public Map<Profile, Collection<Task>> generate() {
        return profileRepository.findAll()
                .stream()
                .collect(toMap(Function.identity(), this::generate));
    }

    private Collection<Task> generate(Profile profile) {
        log.info("Generating tasks for user {}", profile.getUserId());
        List<Task> tasks = profileConsumerToRateMap.stream()
                .map(it -> Map.entry(it, count(it.getRate().multiply(profile.getActivityRate()))))
                .map(it -> distribute(it.getKey(), profile, it.getValue()))
                .flatMap(Collection::stream)
                .collect(toList());
        log.info("Generated {} tasks for user {}", tasks.size(), profile.getUserId());

        return tasks;
    }

    private int count(BigDecimal rate) {
        int integerPart = rate.intValue();
        BigDecimal fractionalPart = rate.subtract(BigDecimal.valueOf(integerPart));

        if (!fractionalPart.equals(BigDecimal.ZERO)) {
            int multiplier = 100;
            int probability = fractionalPart.multiply(BigDecimal.valueOf(multiplier)).intValue();

            int randomInt = ThreadLocalRandom.current().nextInt(100);

            if (randomInt <= probability) {
                integerPart++;
            }
        }

        return integerPart;
    }

    private Collection<Task> distribute(MethodInfo methodInfo, Profile profile, int count) {
        long now = System.currentTimeMillis();

        return IntStream.rangeClosed(0, count)
                .mapToObj(
                        it -> Task.builder()
                                .runnable(() -> methodInfo.getMethod().accept(profile))
                                .date(randomDateBetween(now, now + ACTIVITY_PERIOD_MILLIS))
                                .methodName(methodInfo.getMethodName())
                                .userId(profile.getUserId())
                                .build()
                )
                .collect(toList());
    }

    private long randomDateBetween(long beginDate, long endDate) {
        return ThreadLocalRandom.current().nextLong(endDate - beginDate) + beginDate;
    }
}
