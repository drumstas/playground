package com.example.tasks.command;

import com.example.tasks.domain.Task;
import com.example.tasks.domain.Tasks;
import com.example.tasks.rest.data.TaskRequest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.example.tasks.domain.Task.Type.TASK;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FilterTasksCommandTest {

    private Tasks tasks = mock(Tasks.class);

    @Test
    public void shouldFindAllWhenNothingSpecified() {
        var emptyRequest = new TaskRequest(null, null, null, null, null, null, null);

        var task = new Task(TASK);
        task.setId(1L);

        when(tasks.findAll()).thenReturn(List.of(task));

        var results = new FilterTasksCommand(emptyRequest).execute(tasks);
        assertThat(results.size(), is(1));
        assertThat(results.get(0).id(), is(task.getId()));
    }

}
