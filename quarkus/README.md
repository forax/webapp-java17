## Quarkus with Java 17

One REST Controller (a record)
```java
@ApplicationScoped
@Path("/tasks")
public record TaskController(TaskEventRepository taskEventRepository) {
  ...
  
  @GET
  @Produces(APPLICATION_JSON)
  @Operation(summary = "get all tasks")
  public List<JsonTask> getAllTasks() {
    ...
  }

  @POST
  @Produces(APPLICATION_JSON)
  @Consumes(APPLICATION_JSON)
  @Transactional
  @Operation(summary = "create a new task from a content")
  public JsonTask createTask(JsonTaskWithoutId task) {
    ...
  }

  @DELETE
  @Path("/{taskId}")
  @Transactional
  @Operation(summary = "delete a task from an id")
  public void deleteTask(@PathParam("taskId") long taskId) {
    ...
  }

  @GET
  @Path("/events")
  @Produces(APPLICATION_JSON)
  @Operation(summary = "get all task events")
  public List<JsonTaskEvent> getAllTaskEvents() {
    ...
  }
}
```

Three JSON records
```java
  public record JsonTask(long id, String content) {}
  public record JsonTaskWithoutId(String content) {}
  public record JsonTaskEvent(TaskEventKind kind, String content) {}
```


One Hibernate with Panache repository (see below why)
```java
  public interface TaskEventRepository extends JpaRepository<TaskEvent, Long> { }
```

Two JPA Panache entities
```java
@Entity
public class Task extends PanacheEntity {
  @Column(nullable = false)
  String content;

  protected Task() {}  // called by hibernate

  public Task(String content) {
    this.content = Objects.requireNonNull(content);
  }
}

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
```

A panache entity also provide static methods that act as instance method of
a repository but they have to be typed explicitly.

By example to get all task events using a repository
```java
taskEventRepository.streamAll()
    .map(...)
```

And to get all tasks using the panache static methods
```java
Task.<Task>streamAll()
    .map(...)
```
the type (under < and >) as to be specified explicitly.
