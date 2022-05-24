package com.example.tasks.command;

import com.example.tasks.domain.Task;
import com.example.tasks.domain.TaskNotFoundException;
import com.example.tasks.domain.Tasks;
import org.junit.jupiter.api.Test;

import static com.example.tasks.domain.Task.Type.SUB_TASK;
import static com.example.tasks.domain.Task.Type.TASK;
import static java.lang.String.format;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetSubTasksCommandTest {

    private Tasks tasks = mock(Tasks.class);

    @Test
    public void shouldGetTask() {
        var task = new Task(TASK);
        task.setId(1L);

        var firstSubtask = new Task(SUB_TASK);
        firstSubtask.setId(2);
        task.addSubTask(firstSubtask);

        var secondSubtask = new Task(SUB_TASK);
        secondSubtask.setId(3);
        task.addSubTask(secondSubtask);

        when(tasks.findById(task.getId())).thenReturn(of(task));

        var result = new GetSubTasksCommand(task.getId()).execute(tasks);

        assertThat(result.size(), is(2));
        assertThat(result.get(0).id(), is(firstSubtask.getId()));
        assertThat(result.get(1).id(), is(secondSubtask.getId()));
    }

    @Test
    public void shouldFailToFindTask() {
        var id = 1L;

        when(tasks.findById(id)).thenReturn(empty());

        var thrown = assertThrows(TaskNotFoundException.class, () -> new GetSubTasksCommand(id).execute(tasks));
        assertThat(thrown.getMessage(), is(format("Task %d cannot be found.", id)));
    }
}
