package com.github.forax.quarkus17;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
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
public record TaskController() {
  public record JsonTask(long id, String content) {}
  public record JsonTaskWithoutId(String content) {}
  public record JsonTaskEvent(TaskEventKind kind, String content) {}

  @GET
  @Produces(APPLICATION_JSON)
  @Operation(summary = "get all tasks")
  public Multi<JsonTask> getAllTasks() {
    return Task.<Task>streamAll()
        .map(task -> new JsonTask(task.id, task.content));
  }

  @POST
  @Produces(APPLICATION_JSON)
  @Consumes(APPLICATION_JSON)
  @Operation(summary = "create a new task from a content")
  public Uni<JsonTask> createTask(JsonTaskWithoutId task) {
    var newTask = new Task(task.content);
    return Panache.withTransaction(() -> newTask.<Task>persist()
        .flatMap(t -> {
          var newTaskEvent = new TaskEvent(TaskEventKind.CREATED, task.content);
          return newTaskEvent.<TaskEvent>persist()
              .map(__ -> new JsonTask(t.id, t.content));
        }));
  }

  @DELETE
  @Path("/{taskId}")
  @Operation(summary = "delete a task from an id")
  public Uni<Void> deleteTask(@PathParam("taskId") long taskId) {
    return Panache.withTransaction(() -> Task.<Task>findById(taskId)
        .flatMap(t -> {
          var newTaskEvent = new TaskEvent(TaskEventKind.DELETED, t.content);
          return Task.deleteById(taskId)
              .chain(newTaskEvent::persist)
              .replaceWithVoid();
        }));
  }

  @GET
  @Path("/events")
  @Produces(APPLICATION_JSON)
  @Operation(summary = "get all task events")
  public Multi<JsonTaskEvent> getAllTaskEvents() {
    return TaskEvent.<TaskEvent>streamAll()
      .map(event -> new JsonTaskEvent(event.kind, event.content));
  }
}
