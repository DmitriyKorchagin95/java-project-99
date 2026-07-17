package hexlet.code.conroller;

import hexlet.code.dto.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatusUpdateDTO;
import hexlet.code.service.TaskStatusesService;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/task_statuses")
@RequiredArgsConstructor
public class TaskStatusesController {

    private final TaskStatusesService taskStatusesService;

    @GetMapping
    public ResponseEntity<?> index() {
        var taskStatuses = taskStatusesService.findAll();
        var totalCount = taskStatuses.size();
        var headers = new HttpHeaders();
        headers.add("X-Total-Count", String.valueOf(totalCount));

        return new ResponseEntity<>(taskStatuses, headers, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody TaskStatusCreateDTO taskStatusData) {
        var taskStatus = taskStatusesService.create(taskStatusData);

        return new ResponseEntity<>(
                taskStatus,
                HttpStatus.CREATED
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        var taskStatus = taskStatusesService.findById(id);

        return new ResponseEntity<>(
                taskStatus,
                HttpStatus.OK
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> update(@Valid @RequestBody TaskStatusUpdateDTO taskStatusData, @PathVariable Long id) {
        var taskStatus = taskStatusesService.update(id, taskStatusData);

        return new ResponseEntity<>(
                taskStatus,
                HttpStatus.OK
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        taskStatusesService.delete(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
