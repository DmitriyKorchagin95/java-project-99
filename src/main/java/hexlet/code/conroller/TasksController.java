package hexlet.code.conroller;

import hexlet.code.dto.TaskCreateDTO;
import hexlet.code.dto.TaskParamsDTO;
import hexlet.code.dto.TaskUpdateDTO;
import hexlet.code.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
    public ResponseEntity<?> index(
            TaskParamsDTO params,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        var tasks = taskService.findAll(params, page, limit);

        var headers = new HttpHeaders();
        headers.add(
                "X-Total-Count",
                String.valueOf(tasks.getTotalElements())
        );

        return new ResponseEntity<>(
                tasks.getContent(),
                headers,
                HttpStatus.OK
        );
    }
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody TaskCreateDTO taskData) {
        var task = taskService.create(taskData);

        return new ResponseEntity<>(
                task,
                HttpStatus.CREATED
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        var task = taskService.findById(id);

        return new ResponseEntity<>(
                task,
                HttpStatus.OK
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> update(@Valid @RequestBody TaskUpdateDTO taskData, @PathVariable Long id) {
        var task = taskService.update(id, taskData);

        return new ResponseEntity<>(
                task,
                HttpStatus.OK
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        taskService.delete(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
