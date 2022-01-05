package com.github.forax.spring.webmvc;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class TaskEvent {
  @Id @GeneratedValue Long id;

  @Column(nullable = false)
  TaskEventKind kind;

  @Column(nullable = false)
  String content;

  protected TaskEvent() {}  // called by hibernate

  public TaskEvent(TaskEventKind kind, String content) {
    this.kind = Objects.requireNonNull(kind);
    this.content = Objects.requireNonNull(content);
  }
}
