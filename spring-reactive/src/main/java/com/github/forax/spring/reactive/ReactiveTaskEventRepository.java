package com.github.forax.spring.reactive;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ReactiveTaskEventRepository extends ReactiveCrudRepository<TaskEvent, Long> { }