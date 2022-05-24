package com.example.tasks;

import com.example.tasks.rest.data.Error;
import com.example.tasks.rest.data.TaskIdResponse;
import com.example.tasks.rest.data.TaskRequest;
import com.example.tasks.rest.data.TaskResponse;
import com.example.tasks.rest.data.ValidationError;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static java.lang.String.format;
import static java.time.Duration.ZERO;
import static java.util.Arrays.stream;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpMethod.PUT;

@Testcontainers
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = RANDOM_PORT)
class TasksIntegrationTests {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate rest;

    @Test
    void shouldCreateAndGetATask() {
        var request = taskRequest();

        var taskId = createTask(request);
        var task = retrieveTask(taskId.getId());

        assertThat(task.getId(), is(taskId.getId()));
        assertThat(task.getName(), is(request.getName().get()));
        assertThat(task.getDescription(), is(request.getDescription().get()));
        assertThat(task.getType(), is("TASK"));
        assertThat(task.getStatus(), is("NEW"));
        assertThat(task.getTimeSpent(), is(ZERO));
        assertThat(task.getGroup(), is(request.getGroup().get()));
        assertThat(task.getAssignee(), is(request.getAssignee().get()));
    }

    @Test
    void shouldFailToCreateTaskWithATooLongName() {
        var request = new TaskRequest(
                "namenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamenamename",
                "My description",
                "My group",
                "Important assignee",
                "NEW",
                "TASK",
                0L
        );

        var error = createTaskValidationError(request).get(0);

        assertThat(error.field(), is("name"));
        assertThat(error.message(), is("Task name cannot exceed 50 characters"));
    }

    @Test
    void shouldASubtasksForATaskAndRetrieveThem() {
        var taskId = createTask(taskRequest());
        var subtask1 = createSubtaskFor(taskId.getId(), taskRequest());
        var subtask2 = createSubtaskFor(taskId.getId(), taskRequest());

        var subtaskIds = retrieveSubtasksFor(taskId.getId()).stream()
                .map(TaskResponse::getId)
                .toList();

        assertThat(subtaskIds, hasItem(subtask1.getId()));
        assertThat(subtaskIds, hasItem(subtask2.getId()));
        assertThat(retrieveTask(subtask1.getId()).getType(), is("SUB_TASK"));
        assertThat(retrieveTask(subtask2.getId()).getType(), is("SUB_TASK"));
    }

    @Test
    void shouldDeleteTaskAndAllSubtasks() {
        var taskId = createTask(taskRequest());
        var subtask1 = createSubtaskFor(taskId.getId(), taskRequest());
        var subtask2 = createSubtaskFor(taskId.getId(), taskRequest());

        deleteTask(taskId.getId());

        var errorMsg = "Task %d cannot be found.";
        assertThat(retrieveError(taskId.getId()).message(), is(format(errorMsg, taskId.getId())));
        assertThat(retrieveError(subtask1.getId()).message(), is(format(errorMsg, subtask1.getId())));
        assertThat(retrieveError(subtask2.getId()).message(), is(format(errorMsg, subtask2.getId())));
    }

    @Test
    void shouldFilterTasksByGroup() {
        var alphaGroup = "alpha";
        var gammaGroup = "gamma";

        var taskAlpha = createTask(taskRequest(alphaGroup));
        var subtaskAlpha = createSubtaskFor(taskAlpha.getId(), taskRequest(alphaGroup));
        var subtaskGamma = createSubtaskFor(taskAlpha.getId(), taskRequest(gammaGroup));
        var taskGamma = createTask(taskRequest(gammaGroup));

        var filtered = filterTasks(new TaskRequest(null, null, gammaGroup, null, null, null, null)).stream()
                .map(TaskResponse::getId)
                .toList();

        assertThat(filtered, not(hasItem(taskAlpha.getId())));
        assertThat(filtered, not(hasItem(subtaskAlpha.getId())));
        assertThat(filtered, hasItem(subtaskGamma.getId()));
        assertThat(filtered, hasItem(taskGamma.getId()));
    }

    @Test
    void shouldUpdateATask() {
        var taskId = createTask(taskRequest());
        var task = retrieveTask(taskId.getId());

        assertThat(task.getStatus(), is("NEW"));

        updateTask(taskId.getId(), taskRequest());
        var updatedTask = retrieveTask(taskId.getId());

        assertThat(updatedTask.getStatus(), is("DONE"));
    }


    private TaskIdResponse createTask(TaskRequest request) {
        return rest.postForObject(format("http://localhost:%d/tasks", port), request, TaskIdResponse.class);
    }

    private List<ValidationError> createTaskValidationError(TaskRequest request) {
        return stream(rest.postForObject(format("http://localhost:%d/tasks", port), request, ValidationError[].class)).toList();
    }

    private TaskResponse retrieveTask(long id) {
        return rest.getForObject(format("http://localhost:%d/tasks/%d", port, id), TaskResponse.class);
    }

    private TaskIdResponse createSubtaskFor(long id, TaskRequest request) {
        return rest.postForObject(format("http://localhost:%d/tasks/%d/sub-tasks", port, id), request, TaskIdResponse.class);
    }

    private List<TaskResponse> retrieveSubtasksFor(long id) {
        return stream(rest.getForObject(format("http://localhost:%d/tasks/%d/sub-tasks", port, id), TaskResponse[].class)).toList();
    }

    private void deleteTask(long id) {
        rest.delete(format("http://localhost:%d/tasks/%d", port, id));
    }

    private List<TaskResponse> filterTasks(TaskRequest request) {
        return stream(rest.postForObject(format("http://localhost:%d/tasks/filter", port), request, TaskResponse[].class)).toList();
    }

    private TaskIdResponse updateTask(long id, TaskRequest request) {
        return rest.exchange(format("http://localhost:%d/tasks/%d", port, id), PUT, new HttpEntity<>(request), TaskIdResponse.class).getBody();
    }

    private Error retrieveError(long id) {
        return rest.getForObject(format("http://localhost:%d/tasks/%d", port, id), Error.class);
    }

    private TaskRequest taskRequest() {
        return new TaskRequest(
                "My task",
                "My description",
                "My group",
                "Important assignee",
                "DONE",
                "TASK",
                0L
        );
    }

    private TaskRequest taskRequest(String group) {
        return new TaskRequest(
                "Task",
                "description",
                group,
                "Assignee",
                "DONE",
                "SUB_TASK",
                0L
        );
    }

}
