package com.example.tasks.command;

import com.example.tasks.domain.Task;
import com.example.tasks.domain.TaskNotFoundException;
import com.example.tasks.domain.Tasks;
import org.junit.jupiter.api.Test;

import static com.example.tasks.domain.Task.Type.TASK;
import static java.lang.String.format;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetTaskCommandTest {

    private Tasks tasks = mock(Tasks.class);

    @Test
    public void shouldGetTask() {
        var id = 1L;

        var task = new Task(TASK);
        task.setId(id);
        task.setName("Easy task");

        when(tasks.findById(id)).thenReturn(of(task));

        var result = new GetTaskCommand(id).execute(tasks);
        assertThat(result.id(), is(task.getId()));
        assertThat(result.name(), is(task.getName()));
    }

    @Test
    public void shouldFailToFindTask() {
        var id = 1L;

        when(tasks.findById(id)).thenReturn(empty());

        var thrown = assertThrows(TaskNotFoundException.class, () -> new GetTaskCommand(id).execute(tasks));
        assertThat(thrown.getMessage(), is(format("Task %d cannot be found.", id)));
    }
}
