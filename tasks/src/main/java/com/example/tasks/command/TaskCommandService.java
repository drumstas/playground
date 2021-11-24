package com.example.tasks.command;

import com.example.tasks.domain.Tasks;
import org.springframework.stereotype.Component;

@Component
public class TaskCommandService {

    private final Tasks tasks;

    TaskCommandService(Tasks tasks) {
        this.tasks = tasks;
    }

    public <T> T execute(TaskCommand<T> command) {
        return command.execute(tasks);
    }

}
