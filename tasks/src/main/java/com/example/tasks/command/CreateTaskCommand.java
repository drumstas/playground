package com.example.tasks.command;

import com.example.tasks.domain.Task;
import com.example.tasks.domain.Tasks;
import com.example.tasks.rest.data.TaskRequest;

import static com.example.tasks.domain.Task.Type.TASK;

public class CreateTaskCommand implements TaskCommand<TaskIdResult> {

    private final TaskRequest request;

    public CreateTaskCommand(TaskRequest request) {
        this.request = request;
    }

    @Override
    public TaskIdResult execute(Tasks tasks) {
        var task = new Task(TASK);
        request.getName().ifPresent(task::setName);
        request.getDescription().ifPresent(task::setDescription);
        request.getGroup().ifPresent(task::setTaskGroup);
        request.getAssignee().ifPresent(task::setAssignee);

        return TaskIdResult.from(tasks.save(task));
    }

}
