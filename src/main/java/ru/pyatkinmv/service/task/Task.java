package ru.pyatkinmv.service.task;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

@Data
@Builder
@Slf4j
public class Task implements Runnable {
    private final Integer id = ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE);
    @JsonIgnore
    private final Runnable runnable;
    private final String methodName;
    private final Integer userId;
    private final Date date;

    @Override
    public void run() {
        log.info("Executing task {}", this);
        runnable.run();
        log.info("Successfully executed Task(id={})", id);
    }

    public String toString() {
        return "Task(id=" + this.getId()
                + ", methodName=" + this.getMethodName()
                + ", userId=" + this.getUserId()
                + ", date=" + this.getDate()
                + ")";
    }
}
