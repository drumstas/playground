package com.example.tasks.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.cookingfox.guava_preconditions.Preconditions.checkArgument;
import static com.example.tasks.domain.Task.Status.NEW;
import static com.example.tasks.domain.Task.Type.SUB_TASK;
import static java.util.Optional.ofNullable;
import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.SEQUENCE;

@Entity
@Table(name = "tasks")
public class Task {

    public enum Type {
        TASK, SUB_TASK
    }

    public enum Status {
        NEW, IN_PROGRESS, DONE
    }

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    @GeneratedValue(strategy = SEQUENCE, generator = "task_id_generator")
    @SequenceGenerator(name = "task_id_generator", sequenceName = "seq_tasks_id", allocationSize = 1)
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "task_type", nullable = false)
    private Type type;

    @Column(name = "status", nullable = false)
    @Enumerated(STRING)
    private Status status = NEW;

    @Column(name = "seconds_spent", nullable = false)
    private long secondsSpent = 0;

    @Column(name = "task_group")
    private String taskGroup;

    @Column(name = "assignee")
    private String assignee;

    @ManyToOne
    @JoinColumn(name = "main_task_id")
    private Task mainTask;

    @OneToMany(mappedBy = "mainTask", cascade = REMOVE)
    private List<Task> subTasks = new ArrayList<>();

    Task() {
        // for hibernate
    }

    public Task(Type type) {
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getTaskGroup() {
        return taskGroup;
    }

    public void setTaskGroup(String taskGroup) {
        this.taskGroup = taskGroup;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public Optional<Task> getMainTask() {
        return ofNullable(mainTask);
    }

    public List<Task> getSubTasks() {
        return subTasks;
    }

    public Duration getTimeSpent() {
        return Duration.ofSeconds(secondsSpent);
    }

    public void setTimeSpent(Duration timeSpent) {
        this.secondsSpent = timeSpent.toSeconds();
    }

    public void addSubTask(Task task) {
        checkArgument(task.type == SUB_TASK, "Cannot add a task as a subtask to another task.");

        task.mainTask = this;
        subTasks.add(task);
    }

}
