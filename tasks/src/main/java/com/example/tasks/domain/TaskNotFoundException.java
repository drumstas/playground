package com.example.tasks.domain;

import static java.lang.String.format;

public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(long id) {
        super(format("Task %d cannot be found.", id));
    }

}
