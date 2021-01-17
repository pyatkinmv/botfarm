package ru.pyatkinmv.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.pyatkinmv.task.Task;
import ru.pyatkinmv.task.TaskGenerator;

import java.util.Collection;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;

@Component
@RequiredArgsConstructor
@Slf4j
public class TaskService {
    private final TaskGenerator taskGenerator;
    // TODO: What if tasks too much?
    private final ScheduledThreadPoolExecutor taskExecutor = new ScheduledThreadPoolExecutor(1);

    @Scheduled(cron = "${cron.expression}")
    public void plan() {
        taskGenerator.generate()
                .entrySet()
                .stream()
                .flatMap(it -> it.getValue().stream())
                .forEach(this::plan);
    }

    public void plan(Integer userId, Long durationMillis) {
        if (durationMillis < 60 * 1000) {
            throw new IllegalArgumentException("Duration should be at least one minute");
        }

        taskGenerator.generate(userId, durationMillis)
                .forEach(this::plan);
    }

    public TaskQueueDto getTaskQueue() {
        return new TaskQueueDto(
                taskExecutor.getQueue()
                        .stream()
                        .map(it -> extractTaskData(it.toString()))
                        .collect(toList())
        );
    }

    // NOTE: BICYCLE. Don't know how to extract data from queue in a better way
    private String extractTaskData(String taskString) {
        Matcher statusMatcher = Pattern.compile("\\[(.*?), task").matcher(taskString);
        if (!statusMatcher.find()) {
            throw new IllegalStateException("Can not extract task status from string");
        }

        String rawStatus = statusMatcher.group();
        String status = rawStatus.substring(1, rawStatus.length() - 6);

        Matcher taskMatcher = Pattern.compile("Task\\((.*?)\\)").matcher(taskString);
        if (!taskMatcher.find()) {
            throw new IllegalStateException("Can not extract task data from string");
        }
        String task = taskMatcher.group();

        return String.format("status = %s, task = %s", status, task);
    }

    public TaskQueueDto getTaskQueue(Integer userId) {
        return new TaskQueueDto(
                getTaskQueue().getQueue()
                        .stream()
                        .filter(it -> it.contains(String.valueOf(userId)))
                        .collect(toList())
        );
    }

    private void plan(Task task) {
        taskExecutor.schedule(task, millisToDate(task.getDate()), TimeUnit.MILLISECONDS);
    }

    private static Long millisToDate(Long date) {
        return date - System.currentTimeMillis();
    }

    @Data
    public static class TaskQueueDto {
        private int size;
        private Collection<String> queue;

        public TaskQueueDto(Collection<String> queue) {
            this.size = queue.size();
            this.queue = queue;
        }
    }
}
