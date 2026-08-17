package hexlet.code.controller.api;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.TaskCreateDTO;
import hexlet.code.dto.TaskUpdateDTO;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
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
class TasksControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Faker faker;

    private Task task;
    private TaskStatus taskStatus;
    private User assignee;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        userRepository.deleteAll();
        taskStatusRepository.deleteAll();

        assignee = Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .ignore(Select.field(User::getCreatedAt))
                .supply(
                        Select.field(User::getEmail),
                        () -> faker.internet().emailAddress()
                )
                .supply(
                        Select.field(User::getPasswordHash),
                        () -> faker.internet().password()
                )
                .create();

        assignee = userRepository.save(assignee);

        taskStatus = Instancio.of(TaskStatus.class)
                .ignore(Select.field(TaskStatus::getId))
                .ignore(Select.field(TaskStatus::getCreatedAt))
                .supply(
                        Select.field(TaskStatus::getName),
                        () -> faker.book().title()
                )
                .supply(
                        Select.field(TaskStatus::getSlug),
                        () -> faker.lorem().word()
                                + "-"
                                + faker.number().digits(5)
                )
                .create();

        taskStatus = taskStatusRepository.save(taskStatus);

        task = Instancio.of(Task.class)
                .ignore(Select.field(Task::getId))
                .ignore(Select.field(Task::getCreatedAt))
                .ignore(Select.field(Task::getLabels))
                .set(Select.field(Task::getTaskStatus), taskStatus)
                .set(Select.field(Task::getAssignee), assignee)
                .create();
    }

    @Test
    void testIndex() throws Exception {
        taskRepository.save(task);

        var result = mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body).isArray();

        assertThatJson(body)
                .inPath("$[0].name")
                .isEqualTo(task.getName());
    }

    @Test
    void testShow() throws Exception {
        task = taskRepository.save(task);

        var result = mockMvc.perform(
                        get("/api/tasks/{id}", task.getId())
                )
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body)
                .node("name")
                .isEqualTo(task.getName());

        assertThatJson(body)
                .node("description")
                .isEqualTo(task.getDescription());

        assertThatJson(body)
                .node("status")
                .isEqualTo(taskStatus.getSlug());

        assertThatJson(body)
                .node("assigneeId")
                .isEqualTo(assignee.getId());
    }

    @Test
    void testShowNotFound() throws Exception {
        mockMvc.perform(get("/api/tasks/{id}", 999999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreate() throws Exception {
        var dto = new TaskCreateDTO();

        dto.setName("Test task");
        dto.setDescription("Task description");
        dto.setIndex(100L);
        dto.setStatus(taskStatus.getSlug());
        dto.setAssigneeId(assignee.getId());

        mockMvc.perform(
                        post("/api/tasks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(dto))
                )
                .andExpect(status().isCreated());

        var created = taskRepository.findAll().getFirst();

        assertNotNull(created);

        assertThat(created.getName())
                .isEqualTo(dto.getName());

        assertThat(created.getDescription())
                .isEqualTo(dto.getDescription());

        assertThat(created.getIndex())
                .isEqualTo(dto.getIndex());

        assertThat(created.getTaskStatus().getSlug())
                .isEqualTo(dto.getStatus());

        assertThat(created.getAssignee().getId())
                .isEqualTo(dto.getAssigneeId());
    }

    @Test
    void testCreateValidationError() throws Exception {
        var dto = new TaskCreateDTO();

        dto.setName("");
        dto.setStatus(taskStatus.getSlug());

        mockMvc.perform(
                        post("/api/tasks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(dto))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdate() throws Exception {
        task = taskRepository.save(task);

        var dto = new TaskUpdateDTO();
        dto.setName(JsonNullable.of("Updated task"));
        dto.setDescription(JsonNullable.of("Updated description"));

        mockMvc.perform(
                        patch("/api/tasks/{id}", task.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(dto))
                )
                .andExpect(status().isOk());

        var updated = taskRepository.findById(task.getId())
                .orElseThrow();

        assertThat(updated.getName())
                .isEqualTo("Updated task");

        assertThat(updated.getDescription())
                .isEqualTo("Updated description");

        assertThat(updated.getTaskStatus().getId())
                .isEqualTo(taskStatus.getId());

        assertThat(updated.getAssignee().getId())
                .isEqualTo(assignee.getId());
    }

    @Test
    void testUpdateValidationError() throws Exception {
        task = taskRepository.save(task);

        var dto = new TaskUpdateDTO();
        dto.setName(JsonNullable.of(""));

        mockMvc.perform(
                        patch("/api/tasks/{id}", task.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(dto))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateNotFound() throws Exception {
        var dto = new TaskUpdateDTO();
        dto.setName(JsonNullable.of("Updated"));

        mockMvc.perform(
                        patch("/api/tasks/{id}", 999999L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(dto))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void testDestroy() throws Exception {
        task = taskRepository.save(task);

        mockMvc.perform(
                        delete("/api/tasks/{id}", task.getId())
                )
                .andExpect(status().isNoContent());

        assertThat(taskRepository.existsById(task.getId()))
                .isFalse();
    }

    @Test
    void testDestroyNotFound() throws Exception {
        mockMvc.perform(
                        delete("/api/tasks/{id}", 999999L)
                )
                .andExpect(status().isNotFound());
    }
}
