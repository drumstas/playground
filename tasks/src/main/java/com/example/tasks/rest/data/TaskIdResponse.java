package com.example.tasks.rest.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.RepresentationModel;

public class TaskIdResponse extends RepresentationModel<TaskIdResponse> {

    private final long id;

    @JsonCreator
    public TaskIdResponse(@JsonProperty("id") long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

}
