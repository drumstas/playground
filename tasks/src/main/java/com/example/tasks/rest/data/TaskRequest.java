package com.example.tasks.rest.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Optional;

import static java.util.Optional.ofNullable;

public class TaskRequest {

    @Size(max = 50, message = "Task name cannot exceed 50 characters")
    private final String name;

    @Size(max = 50, message = "Task description cannot exceed 50 characters")
    private final String description;

    @Size(max = 10, message = "Task group name cannot exceed 10 characters")
    private final String group;

    @Size(max = 20, message = "Task assignee name cannot exceed 20 characters")
    private final String assignee;

    @Pattern(regexp = "NEW|IN_PROGRESS|DONE", message = "Task status must be one of - NEW, IN_PROGRESS, DONE")
    private final String status;

    @Pattern(regexp = "TASK|SUB_TASK", message = "Task type must be one of - TASK, SUB_TASK")
    private final String type;

    private final Long timeSpentSeconds;

    @JsonCreator
    public TaskRequest(@JsonProperty("name") String name,
                       @JsonProperty("description") String description,
                       @JsonProperty("group") String group,
                       @JsonProperty("assignee") String assignee,
                       @JsonProperty("status") String status,
                       @JsonProperty("type") String type,
                       @JsonProperty("timeSpentSeconds") Long timeSpentSeconds) {
        this.name = name;
        this.description = description;
        this.group = group;
        this.assignee = assignee;
        this.status = status;
        this.type = type;
        this.timeSpentSeconds = timeSpentSeconds;
    }

    public Optional<String> getName() {
        return ofNullable(name);
    }

    public Optional<String> getDescription() {
        return ofNullable(description);
    }

    public Optional<String> getGroup() {
        return ofNullable(group);
    }

    public Optional<String> getAssignee() {
        return ofNullable(assignee);
    }

    public Optional<String> getStatus() {
        return ofNullable(status);
    }

    public Optional<String> getType() {
        return ofNullable(type);
    }

    public Optional<Long> getTimeSpentSeconds() {
        return ofNullable(timeSpentSeconds);
    }

}
