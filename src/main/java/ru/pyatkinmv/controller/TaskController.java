package ru.pyatkinmv.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pyatkinmv.service.TaskService;
import ru.pyatkinmv.service.TaskService.TaskQueueDto;

@RestController
@RequestMapping("/task")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @PostMapping(value = "/plan/{userId}")
    @ResponseStatus(code = HttpStatus.OK)
    public void plan(@PathVariable("userId") Integer userId, @RequestParam Long durationMillis) {
        taskService.plan(userId, durationMillis);
    }

    @GetMapping(value = "/queue", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TaskQueueDto> getQueue() {
        return ResponseEntity.ok(taskService.getTaskQueue());
    }

    @GetMapping(value = "/queue/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TaskQueueDto> getQueue(@PathVariable("userId") Integer userId) {
        return ResponseEntity.ok(taskService.getTaskQueue(userId));
    }
}
