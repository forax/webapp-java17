package com.github.forax.quarkus17;

import org.eclipse.microprofile.openapi.annotations.Operation;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@ApplicationScoped
@Path("/tasks")
public record TaskController(TaskEventRepository taskEventRepository) {
  public record JsonTask(long id, String content) {}
  public record JsonTaskWithoutId(String content) {}
  public record JsonTaskEvent(TaskEventKind kind, String content) {}

  @GET
  @Produces(APPLICATION_JSON)
  @Operation(summary = "get all tasks")
  public List<JsonTask> getAllTasks() {
    return Task.<Task>streamAll()
        .map(task -> new JsonTask(task.id, task.content))
        .toList();
  }

  @POST
  @Produces(APPLICATION_JSON)
  @Consumes(APPLICATION_JSON)
  @Transactional
  @Operation(summary = "create a new task from a content")
  public JsonTask createTask(JsonTaskWithoutId task) {
    var newTask = new Task(task.content);
    newTask.persist();
    var newTaskEvent = new TaskEvent(TaskEventKind.CREATED, task.content);
    newTaskEvent.persist();
    return new JsonTask(newTask.id, newTask.content);
  }

  @DELETE
  @Path("/{taskId}")
  @Transactional
  @Operation(summary = "delete a task from an id")
  public void deleteTask(@PathParam("taskId") long taskId) {
    var optionalTask = Task.<Task>findByIdOptional(taskId);
    optionalTask.ifPresent(task -> {
      Task.deleteById(taskId);
      var newTaskEvent = new TaskEvent(TaskEventKind.DELETED, task.content);
      newTaskEvent.persist();
    });
  }

  @GET
  @Path("/events")
  @Produces(APPLICATION_JSON)
  @Operation(summary = "get all task events")
  public List<JsonTaskEvent> getAllTaskEvents() {
    return taskEventRepository.streamAll()
      .map(event -> new JsonTaskEvent(event.kind, event.content))
      .toList();
  }
}
