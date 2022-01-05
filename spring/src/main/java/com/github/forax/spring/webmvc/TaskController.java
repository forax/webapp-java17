package com.github.forax.spring.webmvc;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/tasks")
public class TaskController {
  public record JsonTask(long id, String content) {}
  public record JsonTaskWithoutId(String content) {}
  public record JsonTaskEvent(TaskEventKind kind, String content) {}

  private final TaskRepository taskRepository;
  private final TaskEventRepository eventRepository;

  public TaskController(TaskRepository taskRepository, TaskEventRepository eventRepository) {
    this.taskRepository = taskRepository;
    this.eventRepository = eventRepository;
  }

  @GetMapping(produces = APPLICATION_JSON_VALUE)
  @Operation(summary = "get all tasks")
  public List<JsonTask> getAllTasks() {
    return taskRepository.findAll().stream()
        .map(data -> new JsonTask(data.id, data.content))
        .toList();
  }

  @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  @Transactional
  @Operation(summary = "create a new task from a content")
  public JsonTask createTask(@RequestBody JsonTaskWithoutId task) {
    var data = new Task(task.content);
    taskRepository.save(data);
    eventRepository.save(new TaskEvent(TaskEventKind.CREATED, task.content));
    return new JsonTask(data.id, data.content);
  }

  @Transactional
  @DeleteMapping("/{taskId}")
  @Operation(summary = "delete a task from an id")
  public void deleteTask(@PathVariable("taskId") long taskId) {
    var taskOptional = taskRepository.findById(taskId);
    taskOptional.ifPresent(task -> {
      taskRepository.deleteById(taskId);
      eventRepository.save(new TaskEvent(TaskEventKind.DELETED, task.content));
    });
  }

  @GetMapping(path = "/events", produces = APPLICATION_JSON_VALUE)
  @Operation(summary = "get all task events")
  public List<JsonTaskEvent> getAllTaskEvents() {
    return eventRepository.findAll().stream()
        .map(data -> new JsonTaskEvent(data.kind, data.content))
        .toList();
  }
}
