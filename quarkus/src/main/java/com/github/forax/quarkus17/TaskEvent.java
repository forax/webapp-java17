package com.github.forax.quarkus17;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Objects;

@Entity
public class TaskEvent extends PanacheEntity {
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
