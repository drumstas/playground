package com.example.tasks.rest;

import com.example.tasks.domain.TaskNotFoundException;
import com.example.tasks.rest.data.Error;
import com.example.tasks.rest.data.ValidationError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
class TaskExceptionHandler {

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public List<ValidationError> validation(MethodArgumentNotValidException e) {
        return e.getFieldErrors().stream()
                .map(error -> new ValidationError(error.getField(), error.getDefaultMessage()))
                .toList();
    }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(TaskNotFoundException.class)
    public Error notFound(TaskNotFoundException e) {
        return new Error(e.getMessage());
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public Error illegal(IllegalArgumentException e) {
        return new Error(e.getMessage());
    }

}