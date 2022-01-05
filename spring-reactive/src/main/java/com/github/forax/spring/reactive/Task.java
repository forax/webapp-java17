package com.github.forax.spring.reactive;

import org.springframework.data.annotation.Id;

public record Task(@Id long id, String content) {}