package com.example.tasks.command;

import com.example.tasks.domain.Task;
import com.example.tasks.rest.data.TaskIdResponse;

public record TaskIdResult(long id) {

    public static TaskIdResult from(Task task) {
        return new TaskIdResult(task.getId());
    }

    public TaskIdResponse toResponse() {
        return new TaskIdResponse(id());
    }
}
