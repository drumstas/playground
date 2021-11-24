package com.example.tasks.rest;

import com.example.tasks.command.CreateSubTaskCommand;
import com.example.tasks.command.CreateTaskCommand;
import com.example.tasks.command.DeleteTaskCommand;
import com.example.tasks.command.FilterTasksCommand;
import com.example.tasks.command.GetSubTasksCommand;
import com.example.tasks.command.GetTaskCommand;
import com.example.tasks.command.TaskCommandService;
import com.example.tasks.command.TaskIdResult;
import com.example.tasks.command.TaskResult;
import com.example.tasks.command.UpdateTaskCommand;
import com.example.tasks.rest.data.TaskIdResponse;
import com.example.tasks.rest.data.TaskRequest;
import com.example.tasks.rest.data.TaskResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/tasks")
class TaskController {

    private final TaskCommandService commandService;

    TaskController(TaskCommandService commandService) {
        this.commandService = commandService;
    }

    @ResponseStatus(CREATED)
    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @Operation(summary = "Create a task")
    TaskIdResponse createTask(@Valid @RequestBody TaskRequest request) {
        return decorated(commandService.execute(new CreateTaskCommand(request)));
    }

    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    @Operation(summary = "Get a task")
    TaskResponse getTask(@PathVariable("id") long id) {
        return decorated(commandService.execute(new GetTaskCommand(id)));
    }

    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Delete a task")
    void deleteTask(@PathVariable("id") long id) {
        commandService.execute(new DeleteTaskCommand(id));
    }

    @PutMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @Operation(summary = "Update a task")
    TaskIdResponse updateTask(@PathVariable("id") long id, @Valid @RequestBody TaskRequest request) {
        return decorated(commandService.execute(new UpdateTaskCommand(id, request)));
    }

    @PostMapping(value = "/filter", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @Operation(summary = "Filter tasks by provided criteria")
    List<TaskResponse> getByFilter(@Valid @RequestBody TaskRequest request) {
        return commandService.execute(new FilterTasksCommand(request)).stream()
                .map(this::decorated)
                .toList();
    }

    @GetMapping(value = "/{id}/sub-tasks", produces = APPLICATION_JSON_VALUE)
    @Operation(summary = "Get subtasks for a task")
    List<TaskResponse> getSubTasks(@PathVariable("id") long id) {
        return commandService.execute(new GetSubTasksCommand(id)).stream().map(result -> {
            var response = result.toResponse();
            response.add(linkTo(TaskController.class).slash(response.getId()).withSelfRel());
            result.mainTaskId().ifPresent(mainTaskId -> response.add(linkTo(TaskController.class).slash(mainTaskId).withRel("mainTask")));
            return response;
        }).toList();
    }

    @ResponseStatus(CREATED)
    @PostMapping(value = "/{id}/sub-tasks", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @Operation(summary = "Create a subtask for a task")
    TaskIdResponse createSubTask(@PathVariable("id") long id, @Valid @RequestBody TaskRequest request) {
        return decorated(commandService.execute(new CreateSubTaskCommand(id, request)));
    }

    private TaskIdResponse decorated(TaskIdResult result) {
        var response = result.toResponse();
        response.add(linkTo(TaskController.class).slash(response.getId()).withSelfRel());
        return response;
    }

    private TaskResponse decorated(TaskResult result) {
        var response = result.toResponse();
        response.add(linkTo(TaskController.class).slash(response.getId()).withSelfRel());
        response.add(linkTo(methodOn(TaskController.class).getSubTasks(response.getId())).withRel("subTasks"));
        result.mainTaskId().ifPresent(mainTaskId -> response.add(linkTo(TaskController.class).slash(mainTaskId).withRel("mainTask")));
        return response;
    }

}
