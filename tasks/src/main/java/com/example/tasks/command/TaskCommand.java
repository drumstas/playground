package com.example.tasks.command;

import com.example.tasks.domain.Tasks;

public interface TaskCommand<T> {

    T execute(Tasks tasks);

}
