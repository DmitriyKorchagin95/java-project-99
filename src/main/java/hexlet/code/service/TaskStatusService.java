package hexlet.code.service;

import hexlet.code.dto.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatusDTO;
import hexlet.code.dto.TaskStatusUpdateDTO;
import hexlet.code.exception.ConflictException;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskStatusService {

    private final TaskStatusRepository taskStatusRepository;
    private final TaskStatusMapper taskStatusMapper;
    private final TaskRepository taskRepository;

    @Transactional
    public TaskStatusDTO create(TaskStatusCreateDTO dto) {
        var taskStatus = taskStatusMapper.map(dto);
        var savedTaskStatus = taskStatusRepository.save(taskStatus);
        return taskStatusMapper.map(savedTaskStatus);
    }

    @Transactional(readOnly = true)
    public TaskStatusDTO findById(Long id) {

        return taskStatusRepository.findById(id)
                .map(taskStatusMapper::map)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                String.format("Task status with id %d not found", id)
                        ));
    }

    @Transactional(readOnly = true)
    public List<TaskStatusDTO> findAll() {
        log.debug("Finding all task statuses");

        return taskStatusRepository.findAll()
                .stream()
                .map(taskStatusMapper::map)
                .toList();
    }

    @Transactional
    public TaskStatusDTO update(Long id, TaskStatusUpdateDTO dto) {
        log.info("Updating task status id={}", id);

        var taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                String.format("Task status with id %d not found", id)
                        ));

        taskStatusMapper.update(dto, taskStatus);
        var updatedTaskStatus = taskStatusRepository.save(taskStatus);

        return taskStatusMapper.map(updatedTaskStatus);
    }

    @Transactional
    public void delete(Long id) {
        log.info("Deleting task status id={}", id);

        var taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                String.format("Task status with id %d not found", id)
                        ));

        if (taskRepository.existsByTaskStatus(taskStatus)) {
            throw new ConflictException("Task status is used by tasks");
        }

        taskStatusRepository.delete(taskStatus);
    }

    @Transactional(readOnly = true)
    public Optional<TaskStatus> getById(Long id) {
        return taskStatusRepository.findById(id);
    }
}
