package com.example.tasks.command;

import com.example.tasks.domain.TaskNotFoundException;
import com.example.tasks.domain.Tasks;

import java.util.List;

public class GetSubTasksCommand implements TaskCommand<List<TaskResult>> {

    private final long taskId;

    public GetSubTasksCommand(long taskId) {
        this.taskId = taskId;
    }

    @Override
    public List<TaskResult> execute(Tasks tasks) {
        var task = tasks.findById(taskId).orElseThrow(() -> new TaskNotFoundException(taskId));
        return task.getSubTasks().stream()
                .map(TaskResult::from)
                .toList();

    }
}
