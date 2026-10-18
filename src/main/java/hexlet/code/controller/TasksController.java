package hexlet.code.controller;

import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskParamsDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TasksController {

    private final TaskService taskService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<TaskDTO>> index(
            TaskParamsDTO params,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        var taskPage = taskService.findAll(params, page, limit);

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(taskPage.getTotalElements()))
                .body(taskPage);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TaskDTO> create(@Valid @RequestBody TaskCreateDTO taskData) {
        var task = taskService.create(taskData);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(task);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TaskDTO> show(@PathVariable Long id) {
        var task = taskService.findById(id);
        return ResponseEntity.ok(task);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TaskDTO> update(@Valid @RequestBody TaskUpdateDTO taskData, @PathVariable Long id) {
        var task = taskService.update(id, taskData);
        return ResponseEntity.ok(task);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@taskRepository.findById(#id).orElse(null)?.assignee?.email == authentication.name")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
