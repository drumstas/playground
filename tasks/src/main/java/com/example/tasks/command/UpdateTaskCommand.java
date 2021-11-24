package com.example.tasks.command;

import com.example.tasks.domain.TaskNotFoundException;
import com.example.tasks.domain.Tasks;
import com.example.tasks.rest.data.TaskRequest;

import java.time.Duration;

import static com.example.tasks.domain.Task.Status.valueOf;

public class UpdateTaskCommand implements TaskCommand<TaskIdResult> {

    private final long taskId;
    private final TaskRequest request;

    public UpdateTaskCommand(long taskId, TaskRequest request) {
        this.taskId = taskId;
        this.request = request;
    }

    @Override
    public TaskIdResult execute(Tasks tasks) {
        var task = tasks.findById(taskId).orElseThrow(() -> new TaskNotFoundException(taskId));
        request.getName().ifPresent(task::setName);
        request.getDescription().ifPresent(task::setDescription);
        request.getGroup().ifPresent(task::setTaskGroup);
        request.getAssignee().ifPresent(task::setAssignee);
        request.getTimeSpentSeconds().ifPresent(time -> task.setTimeSpent(Duration.ofSeconds(time)));
        request.getStatus().ifPresent(status -> task.setStatus(valueOf(status)));

        return TaskIdResult.from(tasks.save(task));
    }

}
