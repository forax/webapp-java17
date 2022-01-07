## Spring with Java 17

One REST Controller
```java
@RestController
@RequestMapping("/tasks")
public class TaskController {
  ...
  
  private final TaskRepository taskRepository;
  private final TaskEventRepository eventRepository;

  public TaskController(TaskRepository taskRepository, TaskEventRepository eventRepository) {
    this.taskRepository = taskRepository;
    this.eventRepository = eventRepository;
  }

  @GetMapping(produces = APPLICATION_JSON_VALUE)
  @Operation(summary = "get all tasks")
  public List<JsonTask> getAllTasks() {
    ...
  }

  @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  @Transactional
  @Operation(summary = "create a new task from a content")
  public JsonTask createTask(@RequestBody JsonTaskWithoutId task) {
    ...
  }

  @Transactional
  @DeleteMapping("/{taskId}")
  @Operation(summary = "delete a task from an id")
  public void deleteTask(@PathVariable("taskId") long taskId) {
    ...
  }

  @GetMapping(path = "/events", produces = APPLICATION_JSON_VALUE)
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

Two JPA repositories
```java
  public interface TaskRepository extends JpaRepository<Task, Long> { }
  public interface TaskEventRepository extends JpaRepository<TaskEvent, Long> { }
```

Two JPA POJOs
```java
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
```