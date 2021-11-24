package com.example.tasks.command;

import com.example.tasks.domain.Task;
import com.example.tasks.domain.Task.Status;
import com.example.tasks.domain.Task.Type;
import com.example.tasks.domain.Tasks;
import com.example.tasks.rest.data.TaskRequest;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.jpa.domain.Specification.where;

public class FilterTasksCommand implements TaskCommand<List<TaskResult>> {

    private final TaskRequest request;

    public FilterTasksCommand(TaskRequest request) {
        this.request = request;
    }

    @Override
    public List<TaskResult> execute(Tasks tasks) {
        var specs = specificationFromRequest();
        if (specs.isEmpty()) {
            return toResults(tasks.findAll());
        }

        var querySpec = querySpecificationFrom(specs);
        return toResults(tasks.findAll(querySpec));
    }

    private List<TaskResult> toResults(List<Task> tasks) {
        return tasks.stream()
                .map(TaskResult::from)
                .toList();
    }

    private List<Specification<Task>> specificationFromRequest() {
        var specs = new ArrayList<Specification<Task>>();

        request.getName().ifPresent(name -> specs.add(byField("name", name)));
        request.getDescription().ifPresent(desc -> specs.add(byField("description", desc)));
        request.getGroup().ifPresent(group -> specs.add(byField("taskGroup", group)));
        request.getAssignee().ifPresent(assignee -> specs.add(byField("assignee", assignee)));
        request.getStatus().ifPresent(status -> specs.add((root, query, cb) -> cb.equal(root.get("status"), Status.valueOf(status))));
        request.getType().ifPresent(type -> specs.add((root, query, cb) -> cb.equal(root.get("type"), Type.valueOf(type))));

        return specs;
    }

    private static Specification<Task> byField(String field, String value) {
        return (root, query, cb) -> cb.equal(root.get(field), value);
    }

    private Specification<Task> querySpecificationFrom(List<Specification<Task>> specs) {
        Specification<Task> taskSpecification = where(null);
        var it = specs.iterator();
        if (it.hasNext()) {
            taskSpecification = where(it.next());
            while (it.hasNext()) {
                taskSpecification = taskSpecification.and(where(it.next()));
            }
        }
        return taskSpecification;
    }

}
