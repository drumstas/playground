package com.example.tasks.rest.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.RepresentationModel;

import java.time.Duration;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
public class TaskResponse extends RepresentationModel<TaskResponse> {

    private final long id;
    private final String name;
    private final String description;
    private final String type;
    private final String status;
    private final Duration timeSpent;
    private final String group;
    private final String assignee;

    @JsonCreator
    public TaskResponse(@JsonProperty("id") long id,
                        @JsonProperty("name") String name,
                        @JsonProperty("description") String description,
                        @JsonProperty("type") String type,
                        @JsonProperty("status") String status,
                        @JsonProperty("timeSpent") Duration timeSpent,
                        @JsonProperty("group") String group,
                        @JsonProperty("assignee") String assignee) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.status = status;
        this.timeSpent = timeSpent;
        this.group = group;
        this.assignee = assignee;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public Duration getTimeSpent() {
        return timeSpent;
    }

    public String getGroup() {
        return group;
    }

    public String getAssignee() {
        return assignee;
    }
}
