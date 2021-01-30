package ru.pyatkinmv.service.task;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static java.util.Comparator.comparing;
import static java.util.Objects.requireNonNull;
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
        // TODO: enable planning only if there is no tasks already in queue
        if (durationMillis < 60 * 1000) {
            throw new IllegalArgumentException("Duration should be at least one minute");
        }

        taskGenerator.generate(userId, durationMillis)
                .forEach(this::plan);
    }

    public TaskQueueDto getTaskQueue() {
        return new TaskQueueDto(getTasks());
    }

    private Collection<Task> getTasks() {
        return taskExecutor.getQueue()
                .stream()
                .map(this::extractTask)
                .sorted(comparing(Task::getDate))
                .collect(toList());
    }

    // NOTE:  Don't know how to extract task from queue in a better way
    @SneakyThrows
    private Task extractTask(Object scheduledTask) {
        Field callableField = requireNonNull(ReflectionUtils.findField(FutureTask.class, "callable"));

        ReflectionUtils.makeAccessible(callableField);
        Object callable = callableField.get(scheduledTask);

        Field taskField = requireNonNull(ReflectionUtils.findField(callable.getClass(), "task"));
        ReflectionUtils.makeAccessible(taskField);

        return (Task) taskField.get(callable);
    }

    public TaskQueueDto getTaskQueue(Integer userId) {
        return new TaskQueueDto(
                getTasks().stream()
                        .filter(it -> it.getUserId().equals(userId))
                        .collect(toList())
        );
    }


    private void plan(Task task) {
        taskExecutor.schedule(task, millisToDate(task.getDate().getTime()), TimeUnit.MILLISECONDS);
    }

    private static Long millisToDate(Long date) {
        return date - System.currentTimeMillis();
    }

    @Data
    public static class TaskQueueDto {
        private int size;
        private Collection<Task> queue;

        public TaskQueueDto(Collection<Task> queue) {
            this.size = queue.size();
            this.queue = queue;
        }
    }
}
