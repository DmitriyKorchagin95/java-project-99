package hexlet.code.service;

import hexlet.code.dto.label.LabelCreateDTO;
import hexlet.code.dto.label.LabelDTO;
import hexlet.code.dto.label.LabelUpdateDTO;

import java.util.List;

public interface LabelService {
    LabelDTO create(LabelCreateDTO dto);

    LabelDTO findById(Long id);

    List<LabelDTO> findAll();

    LabelDTO update(Long id, LabelUpdateDTO dto);

    void delete(Long id);
}
