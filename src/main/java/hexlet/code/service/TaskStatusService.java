package hexlet.code.service;

import hexlet.code.dto.status.TaskStatusCreateDTO;
import hexlet.code.dto.status.TaskStatusDTO;
import hexlet.code.dto.status.TaskStatusUpdateDTO;
import hexlet.code.exception.ResourceAlreadyExistsException;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskStatusService {
    private final TaskStatusRepository taskStatusRepository;
    private final TaskStatusMapper taskStatusMapper;
    private final TaskRepository taskRepository;

    @Transactional
    public TaskStatusDTO create(TaskStatusCreateDTO dto) {
        log.info("Creating task status with name={}", dto.getName());

        if (taskStatusRepository.existsByName(dto.getName())) {
            throw new ResourceAlreadyExistsException(
                    String.format("Task status with name %s already exists", dto.getName())
            );
        }

        var taskStatus = taskStatusMapper.map(dto);
        var savedTaskStatus = taskStatusRepository.save(taskStatus);
        return taskStatusMapper.map(savedTaskStatus);
    }

    @Transactional(readOnly = true)
    public TaskStatusDTO findById(Long id) {
        log.debug("Finding task status by id={}", id);

        return taskStatusRepository.findById(id)
                .map(taskStatusMapper::map)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Task status with id %d not found", id)
                ));
    }

    @Transactional(readOnly = true)
    public List<TaskStatusDTO> findAll() {
        log.debug("Finding all task statuses");

        return taskStatusRepository.findAll().stream()
                .map(taskStatusMapper::map)
                .toList();
    }

    @Transactional
    public TaskStatusDTO update(Long id, TaskStatusUpdateDTO dto) {
        log.info("Updating task status id={}", id);

        var taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Task status with id %d not found", id)
                ));

        if (dto.getName().isPresent() && !taskStatus.getName().equals(dto.getName().get())
                && taskStatusRepository.existsByName(dto.getName().get())) {
            throw new ResourceAlreadyExistsException(
                    String.format("Task status with name %s already exists", dto.getName().get())
            );
        }

        taskStatusMapper.update(dto, taskStatus);
        var updatedTaskStatus = taskStatusRepository.save(taskStatus);

        return taskStatusMapper.map(updatedTaskStatus);
    }

    @Transactional
    public void delete(Long id) {
        log.info("Deleting task status id={}", id);

        var taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Task status with id %d not found", id)
                ));

        if (taskRepository.existsByTaskStatus(taskStatus)) {
            throw new ResourceAlreadyExistsException("Cannot delete status: it is used by tasks");
        }

        taskStatusRepository.delete(taskStatus);
    }
}

