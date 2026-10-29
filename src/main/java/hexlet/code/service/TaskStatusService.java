package hexlet.code.service;

import hexlet.code.dto.status.TaskStatusCreateDTO;
import hexlet.code.dto.status.TaskStatusDTO;
import hexlet.code.dto.status.TaskStatusUpdateDTO;

import java.util.List;

public interface TaskStatusService {
    TaskStatusDTO create(TaskStatusCreateDTO dto);

    TaskStatusDTO findById(Long id);

    List<TaskStatusDTO> findAll();

    TaskStatusDTO update(Long id, TaskStatusUpdateDTO dto);

    void delete(Long id);
}
