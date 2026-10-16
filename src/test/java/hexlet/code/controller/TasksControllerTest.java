package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.ModelGenerator;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class TasksControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private Faker faker;

    private Task task;
    private TaskStatus taskStatus;
    private User assignee;
    private JwtRequestPostProcessor token;

    @BeforeEach
    void setUp() {
        assignee = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(assignee);
        taskStatus = Instancio.of(modelGenerator.getStatusModel()).create();
        taskStatusRepository.save(taskStatus);

        task = Instancio.of(modelGenerator.getTaskModel()).create();
        task.setTaskStatus(taskStatus);
        task.setAssignee(assignee);
        task.setLabels(Set.of());

        token = jwt().jwt(builder -> builder
                .subject(assignee.getEmail())
                .claim("id", assignee.getId())
        );
    }

    @Test
    void testIndex() throws Exception {
        taskRepository.save(task);

        mockMvc.perform(get("/api/tasks").with(token))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].title").value(hasItem(task.getName())));
    }

    @Test
    void testIndexFilterByTitle() throws Exception {
        taskRepository.save(task);

        var anotherTask = Instancio.of(modelGenerator.getTaskModel()).create();
        anotherTask.setName("Another task");
        anotherTask.setTaskStatus(taskStatus);
        anotherTask.setAssignee(assignee);
        anotherTask.setLabels(Set.of());
        taskRepository.save(anotherTask);

        mockMvc.perform(
                        get("/api/tasks")
                                .with(token)
                                .param("titleCont", task.getName())
                )
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].title").value(hasItem(task.getName())));
    }

    @Test
    void testIndexFilterByAssignee() throws Exception {
        taskRepository.save(task);

        mockMvc.perform(
                        get("/api/tasks")
                                .with(token)
                                .param("assigneeId", assignee.getId().toString())
                )
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].title").value(hasItem(task.getName())));
    }

    @Test
    void testIndexFilterByStatus() throws Exception {
        taskRepository.save(task);

        mockMvc.perform(
                        get("/api/tasks")
                                .with(token)
                                .param("status", taskStatus.getSlug())
                )
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].title").value(hasItem(task.getName())));
    }

    @Test
    void testIndexFilterByLabel() throws Exception {
        var label = Instancio.of(modelGenerator.getLabelModel()).create();
        label = labelRepository.save(label);

        task.setLabels(Set.of(label));
        taskRepository.save(task);

        mockMvc.perform(
                        get("/api/tasks")
                                .with(token)
                                .param("labelId", label.getId().toString())
                )
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].title").value(hasItem(task.getName())));
    }

    @Test
    void testIndexPagination() throws Exception {
        taskRepository.save(task);

        var anotherTask = Instancio.of(modelGenerator.getTaskModel()).create();
        anotherTask.setTaskStatus(taskStatus);
        anotherTask.setAssignee(assignee);
        anotherTask.setLabels(Set.of());
        taskRepository.save(anotherTask);

        var result = mockMvc.perform(
                        get("/api/tasks")
                                .with(token)
                                .param("page", "1")
                                .param("limit", "1")
                )
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "2"))
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body).isArray();
        assertThatJson(body)
                .inPath("size()")
                .isEqualTo(1);
    }

    @Test
    void testShow() throws Exception {
        task = taskRepository.save(task);

        var result = mockMvc.perform(get("/api/tasks/{id}", task.getId()).with(token))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body).node("title").isEqualTo(task.getName());
        assertThatJson(body).node("content").isEqualTo(task.getDescription());
        assertThatJson(body).node("status").isEqualTo(taskStatus.getSlug());
        assertThatJson(body).node("assignee_id").isEqualTo(assignee.getId());
    }

    @Test
    void testShowNotFound() throws Exception {
        mockMvc.perform(get("/api/tasks/{id}", 999999L).with(token))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreate() throws Exception {
        var dto = new TaskCreateDTO();
        dto.setName("Test task");
        dto.setDescription("Task description");
        dto.setIndex(100L);
        dto.setTaskStatus(taskStatus.getSlug());
        dto.setAssigneeId(assignee.getId());

        mockMvc.perform(
                        post("/api/tasks")
                                .with(token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(dto))
                )
                .andExpect(status().isCreated());

        var created = taskRepository.findAll().stream()
                .filter(savedTask -> savedTask.getName().equals("Test task"))
                .findFirst()
                .orElseThrow();

        assertNotNull(created);
        assertThat(created.getName()).isEqualTo(dto.getName());
    }

    @Test
    void testCreateValidationError() throws Exception {
        var dto = new TaskCreateDTO();
        dto.setName("");
        mockMvc.perform(
                        post("/api/tasks")
                                .with(token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(dto))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdate() throws Exception {
        task = taskRepository.save(task);

        var dto = new TaskUpdateDTO();
        dto.setName(JsonNullable.of("Updated Task Name"));

        mockMvc.perform(
                        put("/api/tasks/{id}", task.getId())
                                .with(token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(dto))
                )
                .andExpect(status().isOk());

        taskRepository.flush();

        var updated = taskRepository.findById(task.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("Updated Task Name");
    }
    @Test
    void testDestroy() throws Exception {
        task = taskRepository.save(task);
        mockMvc.perform(delete("/api/tasks/{id}", task.getId()).with(token))
                .andExpect(status().isNoContent());
        taskRepository.flush();
        assertThat(taskRepository.existsById(task.getId())).isFalse();
    }
    @Test
    void testDestroyForeignTaskForbidden() throws Exception {
        task = taskRepository.save(task);
        var alienUser = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(alienUser);
        var alienToken = jwt().jwt(builder -> builder
                .subject(alienUser.getEmail())
                .claim("id", alienUser.getId())
        );
        mockMvc.perform(delete("/api/tasks/{id}", task.getId()).with(alienToken))
                .andExpect(status().isForbidden());
    }
}
