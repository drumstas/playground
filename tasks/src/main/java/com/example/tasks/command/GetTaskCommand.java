package com.example.tasks.command;

import com.example.tasks.domain.TaskNotFoundException;
import com.example.tasks.domain.Tasks;

public class GetTaskCommand implements TaskCommand<TaskResult> {

    private final long id;

    public GetTaskCommand(long id) {
        this.id = id;
    }

    @Override
    public TaskResult execute(Tasks tasks) {
        var task = tasks.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
        return TaskResult.from(task);
    }

}
