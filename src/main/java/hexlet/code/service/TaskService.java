package hexlet.code.service;

import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskParamsDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.specification.TaskSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskService {
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;
    private final TaskStatusRepository taskStatusRepository;
    private final TaskRepository taskRepository;
    private final LabelRepository labelRepository;
    private final TaskSpecification taskSpecification;

    @Transactional
    public TaskDTO create(TaskCreateDTO dto) {
        log.info(
                "Creating task: name='{}', status='{}'",
                dto.getName(),
                dto.getTaskStatus()
        );

        var task = taskMapper.map(dto);

        task.setTaskStatus(getTaskStatus(dto.getTaskStatus()));

        if (dto.getAssigneeId() != null) {
            task.setAssignee(getUser(dto.getAssigneeId()));
        }

        if (dto.getTaskLabelIds() != null) {
            task.setLabels(getLabels(dto.getTaskLabelIds()));
        }

        var savedTask = taskRepository.save(task);

        log.info("Task created: id={}", savedTask.getId());

        return taskMapper.map(savedTask);
    }

    @Transactional(readOnly = true)
    public TaskDTO findById(Long id) {
        log.debug("Finding task: id={}", id);

        return taskRepository.findById(id)
                .map(taskMapper::map)
                .orElseThrow(() -> {
                    log.warn("Task not found: id={}", id);

                    return new ResourceNotFoundException(
                            String.format(
                                    "Task with id %d not found",
                                    id
                            )
                    );
                });
    }

    @Transactional(readOnly = true)
    public Page<TaskDTO> findAll(
            TaskParamsDTO params,
            int page,
            int limit
    ) {
        log.debug(
                "Finding tasks: page={}, limit={}, params={}",
                page,
                limit,
                params
        );

        var specification = taskSpecification.build(params);
        var pageable = PageRequest.of(page - 1, limit);

        var result = taskRepository
                .findAll(specification, pageable)
                .map(taskMapper::map);

        log.debug(
                "Found tasks: page={}, count={}, total={}",
                page,
                result.getNumberOfElements(),
                result.getTotalElements()
        );

        return result;
    }

    @Transactional
    public TaskDTO update(Long id, TaskUpdateDTO dto) {
        log.info("Updating task: id={}", id);

        var task = taskRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Task not found for update: id={}", id);

                    return new ResourceNotFoundException(
                            String.format(
                                    "Task with id %d not found",
                                    id
                            )
                    );
                });

        taskMapper.update(dto, task);

        updateStatus(dto, task);
        updateAssignee(dto, task);
        updateLabels(dto, task);

        log.info("Task updated: id={}", id);

        return taskMapper.map(task);
    }

    @Transactional
    public void delete(Long id) {
        log.info("Deleting task: id={}", id);

        var task = taskRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Task not found for deletion: id={}", id);

                    return new ResourceNotFoundException(
                            String.format(
                                    "Task with id %d not found",
                                    id
                            )
                    );
                });

        taskRepository.delete(task);

        log.info("Task deleted: id={}", id);
    }

    private void updateStatus(TaskUpdateDTO dto, Task task) {
        if (dto.getTaskStatus().isPresent()) {
            task.setTaskStatus(
                    getTaskStatus(dto.getTaskStatus().get())
            );
        }
    }

    private void updateAssignee(TaskUpdateDTO dto, Task task) {
        if (!dto.getAssigneeId().isPresent()) {
            return;
        }

        var assigneeId = dto.getAssigneeId().orElse(null);

        if (assigneeId == null) {
            task.setAssignee(null);
            return;
        }

        task.setAssignee(getUser(assigneeId));
    }

    private void updateLabels(TaskUpdateDTO dto, Task task) {
        if (!dto.getTaskLabelIds().isPresent()) {
            return;
        }

        task.setLabels(
                getLabels(dto.getTaskLabelIds().get())
        );
    }

    private HashSet<Label> getLabels(Set<Long> ids) {
        var labels = new HashSet<>(
                labelRepository.findAllById(ids)
        );

        if (labels.size() != ids.size()) {
            throw new ResourceNotFoundException(
                    "One or more labels not found"
            );
        }

        return labels;
    }

    private TaskStatus getTaskStatus(String slug) {
        return taskStatusRepository.findBySlug(slug)
                .orElseThrow(() -> {
                    log.warn(
                            "Task status not found: slug={}",
                            slug
                    );

                    return new ResourceNotFoundException(
                            String.format(
                                    "Task status with slug '%s' not found",
                                    slug
                            )
                    );
                });
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn(
                            "User not found: id={}",
                            id
                    );

                    return new ResourceNotFoundException(
                            String.format(
                                    "User with id %d not found",
                                    id
                            )
                    );
                });
    }
}
