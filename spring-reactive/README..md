## Spring with Java 17

One reactive REST Controller
```java
@RestController
@RequestMapping("/tasks")
public class TaskReactiveController {
  ...

  private final ReactiveTaskRepository taskRepository;
  private final ReactiveTaskEventRepository eventRepository;

  public TaskReactiveController(ReactiveTaskRepository taskRepository, ReactiveTaskEventRepository eventRepository) {
    this.taskRepository = taskRepository;
    this.eventRepository = eventRepository;
  }

  @GetMapping(produces = APPLICATION_JSON_VALUE)
  @Operation(summary = "get all tasks")
  public Flux<Task> getAllTasks() {
    ...
  }

  @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  @Transactional
  @Operation(summary = "create a new task from a content")
  public Mono<Task> createTask(@RequestBody JsonTaskWithoutId task) {
    ...
  }

  @DeleteMapping("/{taskId}")
  @Transactional
  @Operation(summary = "delete a task from an id")
  public Mono<Void> deleteTask(@PathVariable("taskId") long taskId) {
    ...
  }

  @GetMapping(path = "/events", produces = APPLICATION_JSON_VALUE)
  @Operation(summary = "Get all task events")
  public Flux<TaskEvent> getAllTaskEvents() {
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

Two reactive CRUD repositories
```java
  public interface ReactiveTaskRepository extends ReactiveCrudRepository<Task, Long> { }
  public interface ReactiveTaskEventRepository extends ReactiveCrudRepository<TaskEvent, Long> { }
```

There is no need to have JPA POJOs because we use R2DBC to access the database and R2DBC works natively
with Java 17 records.
