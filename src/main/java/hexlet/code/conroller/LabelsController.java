package hexlet.code.controller;

import hexlet.code.dto.label.LabelCreateDTO;
import hexlet.code.dto.label.LabelDTO;
import hexlet.code.dto.label.LabelUpdateDTO;
import hexlet.code.service.LabelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/labels")
@RequiredArgsConstructor
public class LabelsController {

    private final LabelService labelService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<LabelDTO>> index() {
        var labels = labelService.findAll();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(labels.size()))
                .body(labels);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LabelDTO> create(@Valid @RequestBody LabelCreateDTO labelData) {
        var label = labelService.create(labelData);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(label);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LabelDTO> show(@PathVariable Long id) {
        var label = labelService.findById(id);
        return ResponseEntity.ok(label);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LabelDTO> update(@Valid @RequestBody LabelUpdateDTO labelData, @PathVariable Long id) {
        var label = labelService.update(id, labelData);
        return ResponseEntity.ok(label);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        labelService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
