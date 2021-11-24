package com.example.tasks.command;

import com.example.tasks.domain.Task;
import com.example.tasks.domain.Tasks;
import com.example.tasks.rest.data.TaskRequest;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;

import static com.example.tasks.domain.Task.Status.NEW;
import static com.example.tasks.domain.Task.Type.TASK;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CreateTaskCommandTest {

    private Tasks tasks = mock(Tasks.class);

    @Test
    public void shouldSaveTask() {
        var name = "this is a name";
        var description = "some description";
        var group = "grp";
        var assignee = "must assign to";

        ArgumentMatcher<Task> taskMatcher = task -> {
            assertThat(task.getName(), is(name));
            assertThat(task.getDescription(), is(description));
            assertThat(task.getTaskGroup(), is(group));
            assertThat(task.getAssignee(), is(assignee));
            assertThat(task.getType(), is(TASK));
            assertThat(task.getStatus(), is(NEW));
            assertThat(task.getMainTask().isEmpty(), is(true));
            assertThat(task.getSubTasks().isEmpty(), is(true));
            assertThat(task.getTimeSpent().toSeconds(), is(0L));
            return true;
        };

        var taskId = 123L;
        when(tasks.save(argThat(taskMatcher))).thenReturn(taskWithId(taskId));

        var result = new CreateTaskCommand(request(name, description, group, assignee)).execute(tasks);
        assertThat(result.id(), is(taskId));
    }

    TaskRequest request(String name, String description, String group, String assignee) {
        return new TaskRequest(
                name,
                description,
                group,
                assignee,
                "RANDOM",
                "RANDOM",
                12345L
        );
    }

    Task taskWithId(long id) {
        var task = new Task(TASK);
        task.setId(id);
        return task;
    }

}
