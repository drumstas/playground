package com.example.tasks.command;

import com.example.tasks.domain.Task;
import com.example.tasks.rest.data.TaskResponse;

import java.time.Duration;
import java.util.Optional;

public record TaskResult(long id,
                         String name,
                         String description,
                         Task.Type type,
                         Task.Status status,
                         Duration timeSpent,
                         String taskGroup,
                         String assignee,
                         Optional<Long> mainTaskId) {

    public static TaskResult from(Task task) {
        return new TaskResult(
                task.getId(),
                task.getName(),
                task.getDescription(),
                task.getType(),
                task.getStatus(),
                task.getTimeSpent(),
                task.getTaskGroup(),
                task.getAssignee(),
                task.getMainTask().map(Task::getId)
        );
    }

    public TaskResponse toResponse() {
        return new TaskResponse(
                id(),
                name(),
                description(),
                type().name(),
                status().name(),
                timeSpent(),
                taskGroup(),
                assignee()
        );
    }
}
