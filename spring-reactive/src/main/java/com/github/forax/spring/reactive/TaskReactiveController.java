package com.github.forax.spring.reactive;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/tasks")
public class TaskReactiveController {
  public record JsonTaskWithoutId(String content) {}

  private final ReactiveTaskRepository taskRepository;
  private final ReactiveTaskEventRepository eventRepository;

  public TaskReactiveController(ReactiveTaskRepository taskRepository, ReactiveTaskEventRepository eventRepository) {
    this.taskRepository = taskRepository;
    this.eventRepository = eventRepository;
  }

  @GetMapping(produces = APPLICATION_JSON_VALUE)
  @Operation(summary = "get all tasks")
  public Flux<Task> getAllTasks() {
    return taskRepository.findAll();
  }

  @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  @Transactional
  @Operation(summary = "create a new task from a content")
  public Mono<Task> createTask(@RequestBody JsonTaskWithoutId task) {
    var data = new Task(0, task.content);
    return taskRepository.save(data)
        .flatMap(t ->
            eventRepository.save(new TaskEvent(0, TaskEventKind.CREATED, task.content()))
              .then(Mono.just(t)));
  }

  @DeleteMapping("/{taskId}")
  @Transactional
  @Operation(summary = "delete a task from an id")
  public Mono<Void> deleteTask(@PathVariable("taskId") long taskId) {
    return taskRepository.findById(taskId)
        .flatMap(t ->
            taskRepository.deleteById(taskId)
              .then(eventRepository.save(new TaskEvent(0, TaskEventKind.DELETED, t.content())))
              .then());
  }

  @GetMapping(path = "/events", produces = APPLICATION_JSON_VALUE)
  @Operation(summary = "Get all task events")
  public Flux<TaskEvent> getAllTaskEvents() {
    return eventRepository.findAll();
  }
}