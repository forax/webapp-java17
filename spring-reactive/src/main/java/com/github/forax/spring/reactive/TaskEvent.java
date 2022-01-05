package com.github.forax.spring.reactive;

import org.springframework.data.annotation.Id;

public record TaskEvent(@Id long id, TaskEventKind kind, String content) { }
