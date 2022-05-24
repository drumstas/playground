package com.example.tasks.command;

import com.example.tasks.domain.Task;
import com.example.tasks.domain.TaskNotFoundException;
import com.example.tasks.domain.Tasks;
import com.example.tasks.rest.data.TaskRequest;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;

import java.util.Optional;

import static com.example.tasks.domain.Task.Type.TASK;
import static java.lang.String.format;
import static java.util.Optional.empty;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UpdateTaskCommandTest {

    private Tasks tasks = mock(Tasks.class);

    @Test
    public void shouldFailToFindTask() {
        var id = 1L;

        when(tasks.findById(id)).thenReturn(empty());

        var thrown = assertThrows(TaskNotFoundException.class, () -> new GetSubTasksCommand(id).execute(tasks));
        assertThat(thrown.getMessage(), is(format("Task %d cannot be found.", id)));
    }

    @Test
    public void shouldUpdateTaskDescription() {
        var newDescription = "newDescription";

        var task = new Task(TASK);
        task.setId(1);

        when(tasks.findById(task.getId())).thenReturn(Optional.of(task));

        ArgumentMatcher<Task> taskMatcher = updatedTask -> {
            assertThat(updatedTask.getId(), is(task.getId()));
            assertThat(task.getDescription(), is(newDescription));
            return true;
        };

        var updated = new Task(TASK);
        updated.setId(1);
        when(tasks.save(argThat(taskMatcher))).thenReturn(updated);

        var result = new UpdateTaskCommand(task.getId(), request(newDescription)).execute(tasks);
        assertThat(result.id(), is(updated.getId()));
    }

    TaskRequest request(String description) {
        return new TaskRequest(null, description, null, null, null, null, null);
    }

}
