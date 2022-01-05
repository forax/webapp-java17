package com.github.forax.quarkus17;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Objects;

@Entity
public class Task extends PanacheEntity {
  @Column(nullable = false)
  String content;

  protected Task() {}  // called by hibernate

  public Task(String content) {
    this.content = Objects.requireNonNull(content);
  }
}
