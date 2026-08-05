package hexlet.code.service;

import hexlet.code.dto.TaskCreateDTO;
import hexlet.code.dto.TaskDTO;
import hexlet.code.dto.TaskUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskService {

    private final UserRepository userRepository;
    private final TaskMapper taskMapper;
    private final TaskStatusRepository taskStatusRepository;
    private final TaskRepository taskRepository;

    @Transactional
    public TaskDTO create(TaskCreateDTO dto) {
        log.info("Creating task");

        var task = taskMapper.map(dto);

        task.setTaskStatus(getTaskStatus(dto.getStatus()));

        if (dto.getAssigneeId() != null) {
            task.setAssignee(getUser(dto.getAssigneeId()));
        }

        var savedTask = taskRepository.save(task);

        return taskMapper.map(savedTask);
    }

    @Transactional(readOnly = true)
    public TaskDTO findById(Long id) {
        log.debug("Finding task id={}", id);

        return taskRepository.findById(id)
                .map(taskMapper::map)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                String.format("Task with id %d not found", id)
                        ));
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> findAll() {
        log.debug("Finding all tasks");

        return taskRepository.findAll()
                .stream()
                .map(taskMapper::map)
                .toList();
    }

    @Transactional
    public TaskDTO update(Long id, TaskUpdateDTO dto) {
        log.info("Updating task id={}", id);

        var task = taskRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                String.format("Task with id %d not found", id)
                        ));

        taskMapper.update(dto, task);

        if (dto.getStatus().isPresent()) {
            task.setTaskStatus(getTaskStatus(dto.getStatus().get()));
        }

        if (dto.getAssigneeId().isPresent()) {
            var assigneeId = dto.getAssigneeId().orElse(null);

            if (assigneeId == null) {
                task.setAssignee(null);
            } else {
                task.setAssignee(getUser(assigneeId));
            }
        }

        return taskMapper.map(task);
    }

    @Transactional
    public void delete(Long id) {
        log.info("Deleting task id={}", id);

        var task = taskRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                String.format("Task with id %d not found", id)
                        ));

        taskRepository.delete(task);
    }

    private TaskStatus getTaskStatus(String slug) {
        return taskStatusRepository.findBySlug(slug)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                String.format(
                                        "Task status with slug '%s' not found",
                                        slug
                                )
                        ));
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                String.format(
                                        "User with id %d not found",
                                        id
                                )
                        ));
    }
}
