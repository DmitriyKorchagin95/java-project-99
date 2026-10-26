package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.status.TaskStatusCreateDTO;
import hexlet.code.dto.status.TaskStatusUpdateDTO;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.util.ModelGenerator;
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

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Transactional
class TaskStatusesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private Faker faker;

    @Autowired
    private ModelGenerator modelGenerator;

    private TaskStatus taskStatus;
    private JwtRequestPostProcessor token;

    @BeforeEach
    void setUp() {
        taskStatus = Instancio.of(modelGenerator.getStatusModel()).create();
        token = jwt().jwt(builder -> builder.subject("test-user@example.com"));
    }

    private TaskStatusCreateDTO createDto(String name, String slug) {
        var dto = new TaskStatusCreateDTO();
        dto.setName(name);
        dto.setSlug(slug);
        return dto;
    }

    private TaskStatusUpdateDTO updateDto(JsonNullable<String> name, JsonNullable<String> slug) {
        var dto = new TaskStatusUpdateDTO();
        dto.setName(name);
        dto.setSlug(slug);
        return dto;
    }

    @Test
    void testIndex() throws Exception {
        taskStatusRepository.save(taskStatus);

        mockMvc.perform(get("/api/task_statuses").with(token))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].slug").value(hasItem(taskStatus.getSlug())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].name").value(hasItem(taskStatus.getName())));
    }

    @Test
    void testShow() throws Exception {
        taskStatus = taskStatusRepository.save(taskStatus);

        var result = mockMvc.perform(
                        get("/api/task_statuses/{id}", taskStatus.getId()).with(token))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body).node("name").isEqualTo(taskStatus.getName());
        assertThatJson(body).node("slug").isEqualTo(taskStatus.getSlug());
    }

    @Test
    void testShowNotFound() throws Exception {
        mockMvc.perform(get("/api/task_statuses/{id}", 999999L).with(token))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreate() throws Exception {
        var dto = createDto("New", "new");

        mockMvc.perform(
                        post("/api/task_statuses")
                                .with(token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        var created = taskStatusRepository.findBySlug("new").orElseThrow();

        assertNotNull(created);
        assertThat(created.getName()).isEqualTo(dto.getName());
        assertThat(created.getSlug()).isEqualTo(dto.getSlug());
    }

    @Test
    void testCreateValidationError() throws Exception {
        var dto = createDto("", "");

        mockMvc.perform(
                        post("/api/task_statuses")
                                .with(token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateDuplicateName() throws Exception {
        taskStatusRepository.save(taskStatus);

        var dto = createDto(taskStatus.getName(), "another-slug");

        mockMvc.perform(
                        post("/api/task_statuses")
                                .with(token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

    @Test
    void testCreateDuplicateSlug() throws Exception {
        taskStatusRepository.save(taskStatus);

        var dto = createDto("Another name", taskStatus.getSlug());

        mockMvc.perform(
                        post("/api/task_statuses")
                                .with(token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

    @Test
    void testUpdate() throws Exception {
        taskStatus = taskStatusRepository.save(taskStatus);
        var oldSlug = taskStatus.getSlug();

        var dto = updateDto(
                JsonNullable.of("Updated"),
                JsonNullable.undefined());

        mockMvc.perform(
                        put("/api/task_statuses/{id}", taskStatus.getId())
                                .with(token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(dto)))
                .andExpect(status().isOk());

        taskStatusRepository.flush();

        var updated = taskStatusRepository.findById(taskStatus.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("Updated");
        assertThat(updated.getSlug()).isEqualTo(oldSlug);
    }

    @Test
    void testUpdateValidationError() throws Exception {
        taskStatus = taskStatusRepository.save(taskStatus);

        var dto = updateDto(
                JsonNullable.of(""),
                JsonNullable.undefined());

        mockMvc.perform(
                        put("/api/task_statuses/{id}", taskStatus.getId())
                                .with(token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateNotFound() throws Exception {
        var dto = updateDto(
                JsonNullable.of("Updated"),
                JsonNullable.undefined());

        mockMvc.perform(
                        put("/api/task_statuses/{id}", 999999L)
                                .with(token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDestroy() throws Exception {
        taskStatus = taskStatusRepository.save(taskStatus);

        mockMvc.perform(delete("/api/task_statuses/{id}", taskStatus.getId()).with(token))
                .andExpect(status().isNoContent());

        taskStatusRepository.flush();

        assertThat(taskStatusRepository.existsById(taskStatus.getId())).isFalse();
    }
}
