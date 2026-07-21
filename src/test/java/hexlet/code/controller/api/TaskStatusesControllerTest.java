package hexlet.code.controller.api;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatusUpdateDTO;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class TaskStatusesControllerTest {

    private static final String BASE_URL = "/api/task_statuses";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private Faker faker;

    private TaskStatus taskStatus;

    @BeforeEach
    void setUp() {
        taskStatusRepository.deleteAll();
        taskStatus = buildTaskStatus();
    }

    private TaskStatus buildTaskStatus() {
        return Instancio.of(TaskStatus.class)
                .ignore(Select.field(TaskStatus::getId))
                .ignore(Select.field(TaskStatus::getCreatedAt))
                .supply(
                        Select.field(TaskStatus::getName),
                        () -> faker.book().title()
                )
                .supply(
                        Select.field(TaskStatus::getSlug),
                        () -> faker.lorem().word() + "-" + faker.number().digits(5)
                )
                .create();
    }

    private TaskStatusCreateDTO createDto(String name, String slug) {
        var dto = new TaskStatusCreateDTO();
        dto.setName(name);
        dto.setSlug(slug);
        return dto;
    }

    private TaskStatusUpdateDTO updateDto(JsonNullable<String> name,
                                          JsonNullable<String> slug) {
        var dto = new TaskStatusUpdateDTO();
        dto.setName(name);
        dto.setSlug(slug);
        return dto;
    }

    @Test
    void testIndex() throws Exception {
        taskStatusRepository.save(taskStatus);

        var result = mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body).isArray();

        assertThatJson(body)
                .inPath("$[0].name")
                .isEqualTo(taskStatus.getName());

        assertThatJson(body)
                .inPath("$[0].slug")
                .isEqualTo(taskStatus.getSlug());
    }

    @Test
    void testShow() throws Exception {
        taskStatus = taskStatusRepository.save(taskStatus);

        var result = mockMvc.perform(
                        get(BASE_URL + "/{id}", taskStatus.getId()))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body)
                .node("name")
                .isEqualTo(taskStatus.getName());

        assertThatJson(body)
                .node("slug")
                .isEqualTo(taskStatus.getSlug());
    }

    @Test
    void testShowNotFound() throws Exception {
        mockMvc.perform(get(BASE_URL + "/{id}", 999999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreate() throws Exception {

        var dto = createDto("New", "new");

        mockMvc.perform(
                        post(BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        var created = taskStatusRepository.findAll().getFirst();

        assertNotNull(created);

        assertThat(created.getName()).isEqualTo(dto.getName());
        assertThat(created.getSlug()).isEqualTo(dto.getSlug());
    }

    @Test
    void testCreateValidationError() throws Exception {

        var dto = createDto("", "");

        mockMvc.perform(
                        post(BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateDuplicateName() throws Exception {

        taskStatusRepository.save(taskStatus);

        var dto = createDto(taskStatus.getName(), "another-slug");

        mockMvc.perform(
                        post(BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

    @Test
    void testCreateDuplicateSlug() throws Exception {

        taskStatusRepository.save(taskStatus);

        var dto = createDto("Another name", taskStatus.getSlug());

        mockMvc.perform(
                        post(BASE_URL)
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
                        patch(BASE_URL + "/{id}", taskStatus.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(dto)))
                .andExpect(status().isOk());

        var updated = taskStatusRepository.findById(taskStatus.getId())
                .orElseThrow();

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
                        patch(BASE_URL + "/{id}", taskStatus.getId())
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
                        patch(BASE_URL + "/{id}", 999999L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDestroy() throws Exception {

        taskStatus = taskStatusRepository.save(taskStatus);

        mockMvc.perform(
                        delete(BASE_URL + "/{id}", taskStatus.getId()))
                .andExpect(status().isNoContent());

        assertThat(taskStatusRepository.existsById(taskStatus.getId()))
                .isFalse();
    }

    @Test
    void testDestroyNotFound() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/{id}", 999999L))
                .andExpect(status().isNotFound());
    }
}
