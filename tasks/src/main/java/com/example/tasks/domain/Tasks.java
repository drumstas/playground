package com.example.tasks.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface Tasks extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {
}
