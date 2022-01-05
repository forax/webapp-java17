package com.github.forax.spring.webmvc;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Task {
  @Id @GeneratedValue Long id;

  @Column(nullable = false)
  String content;

  protected Task() {}  // called by hibernate

  public Task(String content) {
    this.content = Objects.requireNonNull(content);
  }
}
