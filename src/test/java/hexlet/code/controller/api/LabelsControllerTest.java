package hexlet.code.controller.api;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.LabelCreateDTO;
import hexlet.code.dto.LabelUpdateDTO;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
class LabelsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private LabelRepository labelRepository;

    private Label label;

    @BeforeEach
    void setUp() {
        labelRepository.deleteAll();

        label = Instancio.of(Label.class)
                .ignore(Select.field(Label::getId))
                .ignore(Select.field(Label::getCreatedAt))
                .set(Select.field(Label::getName), "test-label")
                .create();
    }

    @Test
    void testIndex() throws Exception {
        labelRepository.save(label);

        var result = mockMvc.perform(
                        get("/api/labels")
                )
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Total-Count"))
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body)
                .isArray()
                .hasSize(1);

        assertThatJson(body)
                .inPath("$[0].name")
                .isEqualTo(label.getName());
    }

    @Test
    void testShow() throws Exception {
        label = labelRepository.save(label);

        var result = mockMvc.perform(
                        get("/api/labels/{id}", label.getId())
                )
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body)
                .node("id")
                .isEqualTo(label.getId());

        assertThatJson(body)
                .node("name")
                .isEqualTo(label.getName());

        assertThatJson(body)
                .node("createdAt")
                .isPresent();

        assertThatJson(body)
                .node("createdAt")
                .isString();
    }

    @Test
    void testShowNotFound() throws Exception {
        mockMvc.perform(
                        get("/api/labels/{id}", 999999L)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreate() throws Exception {
        var dto = new LabelCreateDTO();
        dto.setName("new label");

        mockMvc.perform(
                        post("/api/labels")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(dto))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(dto.getName()))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.createdAt").exists());

        var created = labelRepository
                .findByName(dto.getName())
                .orElseThrow();

        assertNotNull(created);

        assertThat(created.getName())
                .isEqualTo(dto.getName());

        assertNotNull(created.getCreatedAt());
    }

    @Test
    void testCreateValidationError() throws Exception {
        var dto = new LabelCreateDTO();
        dto.setName("");

        mockMvc.perform(
                        post("/api/labels")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(dto))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateNameTooShort() throws Exception {
        var dto = new LabelCreateDTO();
        dto.setName("ab");

        mockMvc.perform(
                        post("/api/labels")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(dto))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateNameTooLong() throws Exception {
        var dto = new LabelCreateDTO();
        dto.setName("a".repeat(1001));

        mockMvc.perform(
                        post("/api/labels")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(dto))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateDuplicateName() throws Exception {
        labelRepository.save(label);

        var dto = new LabelCreateDTO();
        dto.setName(label.getName());

        mockMvc.perform(
                        post("/api/labels")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(dto))
                )
                .andExpect(status().isConflict());
    }

    @Test
    void testUpdate() throws Exception {
        label = labelRepository.save(label);

        var dto = new LabelUpdateDTO();
        dto.setName(JsonNullable.of("updated label"));

        mockMvc.perform(
                        patch("/api/labels/{id}", label.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(dto))
                )
                .andExpect(status().isOk());

        var updated = labelRepository
                .findById(label.getId())
                .orElseThrow();

        assertThat(updated.getName())
                .isEqualTo("updated label");
    }

    @Test
    void testUpdateValidationError() throws Exception {
        label = labelRepository.save(label);

        var dto = new LabelUpdateDTO();
        dto.setName(JsonNullable.of("ab"));

        mockMvc.perform(
                        patch("/api/labels/{id}", label.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(dto))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateNotFound() throws Exception {
        var dto = new LabelUpdateDTO();
        dto.setName(JsonNullable.of("updated label"));

        mockMvc.perform(
                        patch("/api/labels/{id}", 999999L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(dto))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateDuplicateName() throws Exception {
        labelRepository.save(label);

        var anotherLabel = Instancio.of(Label.class)
                .ignore(Select.field(Label::getId))
                .ignore(Select.field(Label::getCreatedAt))
                .supply(
                        Select.field(Label::getName),
                        () -> "another label"
                )
                .create();

        anotherLabel = labelRepository.save(anotherLabel);

        var dto = new LabelUpdateDTO();
        dto.setName(JsonNullable.of(label.getName()));

        mockMvc.perform(
                        patch("/api/labels/{id}", anotherLabel.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(dto))
                )
                .andExpect(status().isConflict());
    }

    @Test
    void testDestroy() throws Exception {
        label = labelRepository.save(label);

        mockMvc.perform(
                        delete("/api/labels/{id}", label.getId())
                )
                .andExpect(status().isNoContent());

        assertThat(
                labelRepository.existsById(label.getId())
        ).isFalse();
    }

    @Test
    void testDestroyNotFound() throws Exception {
        mockMvc.perform(
                        delete("/api/labels/{id}", 999999L)
                )
                .andExpect(status().isNotFound());
    }
}
