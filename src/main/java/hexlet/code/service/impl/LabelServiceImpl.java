package hexlet.code.service.impl;

import hexlet.code.dto.label.LabelCreateDTO;
import hexlet.code.dto.label.LabelDTO;
import hexlet.code.dto.label.LabelUpdateDTO;
import hexlet.code.exception.ResourceAlreadyExistsException;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.repository.LabelRepository;
import hexlet.code.service.LabelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class LabelServiceImpl implements LabelService {
    private static final String NOT_FOUND_MSG_TEMPLATE = "Label with id %d not found";
    private final LabelRepository labelRepository;
    private final LabelMapper labelMapper;

    @Transactional
    @Override
    public LabelDTO create(LabelCreateDTO dto) {
        log.info("Creating label with name={}", dto.getName());

        if (labelRepository.existsByName(dto.getName())) {
            throw new ResourceAlreadyExistsException(
                    String.format("Label with name %s already exists", dto.getName())
            );
        }

        var label = labelMapper.map(dto);
        var savedLabel = labelRepository.save(label);

        return labelMapper.map(savedLabel);
    }

    @Transactional(readOnly = true)
    @Override
    public LabelDTO findById(Long id) {
        log.debug("Finding label id={}", id);

        return labelRepository.findById(id)
                .map(labelMapper::map)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(NOT_FOUND_MSG_TEMPLATE, id)
                ));
    }

    @Transactional(readOnly = true)
    @Override
    public List<LabelDTO> findAll() {
        log.debug("Finding all labels");

        return labelRepository.findAll().stream()
                .map(labelMapper::map)
                .toList();
    }

    @Transactional
    @Override
    public LabelDTO update(Long id, LabelUpdateDTO dto) {
        log.info("Updating label id={}", id);

        var label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(NOT_FOUND_MSG_TEMPLATE, id)
                ));

        if (dto.getName().isPresent() && !label.getName().equals(dto.getName().get())
                && labelRepository.existsByName(dto.getName().get())) {
            throw new ResourceAlreadyExistsException(
                    String.format("Label with name %s already exists", dto.getName().get())
            );
        }

        labelMapper.update(dto, label);
        var updatedLabel = labelRepository.save(label);

        return labelMapper.map(updatedLabel);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        log.info("Deleting label id={}", id);

        var label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(NOT_FOUND_MSG_TEMPLATE, id)
                ));

        if (label.getTasks() != null && !label.getTasks().isEmpty()) {
            throw new ResourceAlreadyExistsException(
                    String.format("Label with id %d is used by tasks", id)
            );
        }

        labelRepository.delete(label);
    }
}
