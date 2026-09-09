package hexlet.code.service;

import hexlet.code.dto.LabelCreateDTO;
import hexlet.code.dto.LabelDTO;
import hexlet.code.dto.LabelUpdateDTO;
import hexlet.code.exception.ConflictException;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.repository.LabelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class LabelService {

    private final LabelRepository labelRepository;
    private final LabelMapper labelMapper;

    @Transactional
    public LabelDTO create(LabelCreateDTO dto) {
        log.info("Creating label");

        var label = labelMapper.map(dto);
        var savedLabel = labelRepository.save(label);

        return labelMapper.map(savedLabel);
    }

    @Transactional(readOnly = true)
    public LabelDTO findById(Long id) {
        log.debug("Finding label id={}", id);

        return labelRepository.findById(id)
                .map(labelMapper::map)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                String.format(
                                        "Label with id %d not found",
                                        id
                                )
                        ));
    }

    @Transactional(readOnly = true)
    public List<LabelDTO> findAll() {
        log.debug("Finding all labels");

        return labelRepository.findAll()
                .stream()
                .map(labelMapper::map)
                .toList();
    }

    @Transactional
    public LabelDTO update(Long id, LabelUpdateDTO dto) {
        log.info("Updating label id={}", id);

        var label = labelRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                String.format(
                                        "Label with id %d not found",
                                        id
                                )
                        ));

        labelMapper.update(dto, label);

        var updatedLabel = labelRepository.save(label);

        return labelMapper.map(updatedLabel);
    }

    @Transactional
    public void delete(Long id) {
        log.info("Deleting label id={}", id);

        var label = labelRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                String.format(
                                        "Label with id %d not found",
                                        id
                                )
                        ));

        if (!label.getTasks().isEmpty()) {
            throw new ConflictException(
                    String.format(
                            "Label with id %d is used by tasks",
                            id
                    )
            );
        }

        labelRepository.delete(label);
    }
}
