package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.label.LabelCreateDTO;
import hexlet.code.dto.label.LabelUpdateDTO;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.util.ModelGenerator;
import jakarta.persistence.EntityManager;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Transactional
class LabelsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private Faker faker;

    private Label label;
    private JwtRequestPostProcessor token;

    @BeforeEach
    void setUp() {
        label = Instancio.of(modelGenerator.getLabelWithoutTasksModel()).create();
        token = jwt().jwt(builder -> builder.subject("test-user@example.com"));
    }

    @Test
    void testIndex() throws Exception {
        labelRepository.save(label);

        mockMvc.perform(get("/api/labels").with(token))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Total-Count"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].name").value(hasItem(label.getName())));
    }

    @Test
    void testIndexWithoutTokenUnauthorized() throws Exception {
        mockMvc.perform(get("/api/labels"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testShow() throws Exception {
        label = labelRepository.save(label);

        var result = mockMvc.perform(get("/api/labels/{id}", label.getId()).with(token))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body).node("id").isEqualTo(label.getId());
        assertThatJson(body).node("name").isEqualTo(label.getName());
        assertThatJson(body).node("createdAt").isPresent();
    }

    @Test
    void testShowNotFound() throws Exception {
        mockMvc.perform(get("/api/labels/{id}", 999999L).with(token))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreate() throws Exception {
        var dto = new LabelCreateDTO();
        dto.setName("new label");

        mockMvc.perform(
                        post("/api/labels")
                                .with(token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(dto))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(dto.getName()))
                .andExpect(jsonPath("$.id").isNumber());

        var created = labelRepository.findByName(dto.getName()).orElseThrow();

        assertNotNull(created);
        assertThat(created.getName()).isEqualTo(dto.getName());
    }

    @Test
    void testCreateValidationError() throws Exception {
        var dto = new LabelCreateDTO();
        dto.setName("");

        mockMvc.perform(
                        post("/api/labels")
                                .with(token)
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
                                .with(token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(dto))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateNameTooLong() throws Exception {
        var dto = new LabelCreateDTO();
        dto.setName(faker.lorem().characters(1001));

        mockMvc.perform(
                        post("/api/labels")
                                .with(token)
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
                                .with(token)
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
                        put("/api/labels/{id}", label.getId())
                                .with(token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(dto))
                )
                .andExpect(status().isOk());

        labelRepository.flush();

        var updated = labelRepository.findById(label.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("updated label");
    }

    @Test
    void testUpdateValidationError() throws Exception {
        label = labelRepository.save(label);

        var dto = new LabelUpdateDTO();
        dto.setName(JsonNullable.of("ab"));

        mockMvc.perform(
                        put("/api/labels/{id}", label.getId())
                                .with(token)
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
                        put("/api/labels/{id}", 999999L)
                                .with(token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(dto))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void testDestroy() throws Exception {
        label = labelRepository.save(label);

        mockMvc.perform(delete("/api/labels/{id}", label.getId()).with(token))
                .andExpect(status().isNoContent());

        labelRepository.flush();

        assertThat(labelRepository.existsById(label.getId())).isFalse();
    }

    @Test
    void testDestroyUsedByTasksConflict() throws Exception {
        label = labelRepository.save(label);

        var taskStatus = Instancio.of(modelGenerator.getStatusModel()).create();
        taskStatusRepository.save(taskStatus);

        var task = Instancio.of(modelGenerator.getTaskModel()).create();
        task.setTaskStatus(taskStatus);
        task.setLabels(Set.of(label));
        taskRepository.save(task);


        taskRepository.flush();
        entityManager.clear();

        mockMvc.perform(delete("/api/labels/{id}", label.getId()).with(token))
                .andExpect(status().isConflict());
    }
}
