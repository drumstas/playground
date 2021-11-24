package com.example.tasks.command;

import com.example.tasks.command.DeleteTaskCommand.EmptyResult;
import com.example.tasks.domain.Tasks;

public class DeleteTaskCommand implements TaskCommand<EmptyResult> {

    private final long id;

    public DeleteTaskCommand(long id) {
        this.id = id;
    }

    @Override
    public EmptyResult execute(Tasks tasks) {
        tasks.deleteById(id);
        return new EmptyResult();
    }

    public static class EmptyResult { }
}
