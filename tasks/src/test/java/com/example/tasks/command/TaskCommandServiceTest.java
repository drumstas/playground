package com.example.tasks.command;

import com.example.tasks.domain.Tasks;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

public class TaskCommandServiceTest {

    private Tasks tasks = mock(Tasks.class);

    // subject
    TaskCommandService commandService = new TaskCommandService(tasks);

    @Test
    public void shouldExecuteTestCommand() {
        var result = commandService.execute(new TestCommand());
        assertThat(result.tasks(), is(tasks));
    }

    record TestResult(Tasks tasks) {
    }

    class TestCommand implements TaskCommand<TestResult> {
        @Override
        public TestResult execute(Tasks tasks) {
            return new TestResult(tasks);
        }
    }
}
