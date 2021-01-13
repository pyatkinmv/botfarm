package ru.pyatkinmv.plan;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.pyatkinmv.task.Task;
import ru.pyatkinmv.task.TaskGenerator;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class TaskPlanner {
    private final TaskGenerator taskGenerator;
    // TODO: What if tasks too much?
    private final ScheduledExecutorService taskExecutor = Executors.newSingleThreadScheduledExecutor();

    @Scheduled(cron = "${cron.expression}")
    public void plan() {
        taskGenerator.generate()
                .entrySet()
                .stream()
                .flatMap(it -> it.getValue().stream())
                .forEach(this::plan);
    }

    private void plan(Task task) {
        taskExecutor.schedule(task, millisToDate(task.getDate()), TimeUnit.MILLISECONDS);
    }

    private static Long millisToDate(Long date) {
        return date - System.currentTimeMillis();
    }
}
