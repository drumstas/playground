package com.example.tasks.command;

import com.example.tasks.domain.Task;
import com.example.tasks.domain.TaskNotFoundException;
import com.example.tasks.domain.Tasks;
import com.example.tasks.rest.data.TaskRequest;
import org.springframework.transaction.annotation.Transactional;

import static com.example.tasks.domain.Task.Type.SUB_TASK;

public class CreateSubTaskCommand implements TaskCommand<TaskIdResult> {

    private final long taskId;
    private final TaskRequest request;

    public CreateSubTaskCommand(long taskId, TaskRequest request) {
        this.taskId = taskId;
        this.request = request;
    }

    @Override
    @Transactional
    public TaskIdResult execute(Tasks tasks) {
        var task = tasks.findById(taskId).orElseThrow(() -> new TaskNotFoundException(taskId));
        var subTask = tasks.save(subTask(request));

        task.addSubTask(subTask);
        tasks.save(task);

        return TaskIdResult.from(subTask);
    }

    private Task subTask(TaskRequest request) {
        var task = new Task(SUB_TASK);

        request.getName().ifPresent(task::setName);
        request.getDescription().ifPresent(task::setDescription);
        request.getGroup().ifPresent(task::setTaskGroup);
        request.getAssignee().ifPresent(task::setAssignee);

        return task;
    }

}
