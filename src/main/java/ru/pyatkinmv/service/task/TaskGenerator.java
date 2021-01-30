package ru.pyatkinmv.service.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.pyatkinmv.dao.ProfileRepository;
import ru.pyatkinmv.dao.entities.Profile;
import ru.pyatkinmv.service.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static ru.pyatkinmv.service.MethodInfo.*;

@Component
@Slf4j
public class TaskGenerator {
    private static final long DEFAULT_DURATION_MILLIS = (long) 16 * 60 * 60 * 1000;

    private final ProfileRepository profileRepository;
    private final ProfileMethodsProvider profileMethodsProvider;
    private final Map<MethodInfo, Consumer<Profile>> infoToMethodMap;

    public TaskGenerator(LikeService likeService,
                         SubscribeService subscribeService,
                         PostService postService,
                         ProfileRepository profileRepository,
                         ProfileMethodsProvider profileMethodsProvider) {
        this.profileRepository = profileRepository;
        this.profileMethodsProvider = profileMethodsProvider;
        this.infoToMethodMap = Map.of(
                LIKE_PHOTO, likeService::likePhoto,
                LIKE_POST, likeService::likePost,
                SUBSCRIBE_ON_USER, subscribeService::subscribeOnUser,
                SUBSCRIBE_ON_GROUP, subscribeService::subscribeOnGroup,
                POST_MAIN_PHOTO, postService::postMainPhoto,
                POST_WALL_PHOTO, postService::postWallPhotoPost,
                REPOST, postService::repost
        );
    }

    public Collection<Task> generate(Integer userId, Long durationMillis) {
        return profileRepository.findByUserId(userId)
                .map(it -> generate(it, durationMillis))
                .orElseThrow();
    }

    public Map<Profile, Collection<Task>> generate() {
        return profileRepository.findAll()
                .stream()
                .collect(toMap(Function.identity(), it -> generate(it, DEFAULT_DURATION_MILLIS)));
    }

    private Collection<Task> generate(Profile profile, Long durationMillis) {
        log.info("Generating tasks for user {}", profile.getUserId());
        Set<MethodInfo> availableMethods = profileMethodsProvider.getMethods(profile.getType());

        List<Task> tasks = infoToMethodMap.entrySet()
                .stream()
                .filter(it -> availableMethods.contains(it.getKey()))
                .map(it -> generate(it.getKey(), it.getValue(), profile, durationMillis))
                .flatMap(Collection::stream)
                .collect(toList());
        log.info("Generated {} tasks for user {}", tasks.size(), profile.getUserId());

        return tasks;
    }

    private int count(BigDecimal rate) {
        int integerPart = rate.intValue();
        BigDecimal fractionalPart = rate.subtract(BigDecimal.valueOf(integerPart));

        if (!fractionalPart.equals(BigDecimal.ZERO)) {
            int multiplier = 1000;
            int probability = fractionalPart.multiply(BigDecimal.valueOf(multiplier)).intValue();

            int randomInt = ThreadLocalRandom.current().nextInt(1000);

            if (randomInt <= probability) {
                integerPart++;
            }
        }

        return integerPart;
    }

    private Collection<Task> generate(MethodInfo methodInfo,
                                      Consumer<Profile> method,
                                      Profile profile,
                                      Long durationMillis) {
        int count = count(methodInfo.getRate().multiply(profile.getActivityRate()));

        long now = System.currentTimeMillis();

        return IntStream.rangeClosed(0, count)
                .mapToObj(
                        it -> Task.builder()
                                .runnable(() -> method.accept(profile))
                                .date(new Date(randomDateBetween(now, now + durationMillis)))
                                .methodName(methodInfo.name())
                                .userId(profile.getUserId())
                                .build()
                )
                .collect(toList());
    }

    private static long randomDateBetween(long beginDate, long endDate) {
        return ThreadLocalRandom.current().nextLong(endDate - beginDate) + beginDate;
    }
}
