package com.example.tasks.command;

import com.example.tasks.domain.Tasks;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class DeleteTaskCommandTest {

    private Tasks tasks = mock(Tasks.class);

    @Test
    public void shouldDelegateDeletionToRepository() {
        var id = 1L;

        new DeleteTaskCommand(id).execute(tasks);

        verify(tasks).deleteById(id);
    }

}
