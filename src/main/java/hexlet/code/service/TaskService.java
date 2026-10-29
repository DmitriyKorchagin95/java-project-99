package hexlet.code.service;

import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskParamsDTO;
import hexlet.code.dto.task.TaskUpdateDTO;

import java.util.List;

public interface TaskService {
    TaskDTO create(TaskCreateDTO dto);

    TaskDTO findById(Long id);

    List<TaskDTO> findAll(TaskParamsDTO params);

    TaskDTO update(Long id, TaskUpdateDTO dto);

    void delete(Long id);
}
