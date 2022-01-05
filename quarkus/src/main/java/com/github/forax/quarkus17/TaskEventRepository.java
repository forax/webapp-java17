package com.github.forax.quarkus17;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TaskEventRepository implements PanacheRepository<TaskEvent> { }