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
import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserUpdateDTO;
import hexlet.code.model.User;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class UsersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private Faker faker;

    private User testUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        testUser = Instancio.of(User.class)
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
    }

    @Test
    void testIndex() throws Exception {
        userRepository.save(testUser);

        var result = mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body).isArray();
        assertThatJson(body)
                .inPath("$[0].email")
                .isEqualTo(testUser.getEmail());
    }

    @Test
    void testShow() throws Exception {
        testUser = userRepository.save(testUser);

        var result = mockMvc.perform(
                        get("/api/v1/users/{id}", testUser.getId())
                )
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body)
                .node("email")
                .isEqualTo(testUser.getEmail());

        assertThatJson(body)
                .node("password")
                .isAbsent();
    }

    @Test
    void testCreate() throws Exception {
        var dto = new UserCreateDTO();

        dto.setEmail(faker.internet().emailAddress());
        dto.setFirstName(faker.name().firstName());
        dto.setLastName(faker.name().lastName());
        dto.setPassword("password123");

        var request = post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        var user = userRepository.findByEmail(dto.getEmail()).orElseThrow();

        System.out.println("Password from DTO = " + dto.getPassword());

        System.out.println("Password from DB  = " + user.getPassword());

        assertNotNull(user);

        assertThat(user.getEmail())
                .isEqualTo(dto.getEmail());

        assertThat(user.getPassword())
                .isNotEqualTo(dto.getPassword());

        assertThat(passwordEncoder.matches(
                dto.getPassword(),
                user.getPassword()
        )).isTrue();
    }

    @Test
    void testUpdate() throws Exception {
        testUser = userRepository.save(testUser);

        var oldFirstName = testUser.getFirstName();

        var dto = new UserUpdateDTO();
        dto.setEmail(JsonNullable.of("updated@email.com"));

        var request = patch("/api/v1/users/{id}", testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var updatedUser = userRepository
                .findById(testUser.getId())
                .orElseThrow();

        assertThat(updatedUser.getEmail())
                .isEqualTo("updated@email.com");

        assertThat(updatedUser.getFirstName())
                .isEqualTo(oldFirstName);
    }

    @Test
    void testDestroy() throws Exception {
        testUser = userRepository.save(testUser);

        mockMvc.perform(
                        delete("/api/v1/users/{id}", testUser.getId())
                )
                .andExpect(status().isNoContent());

        assertThat(
                userRepository.existsById(testUser.getId())
        ).isFalse();
    }

    @Test
    void testCreateValidationError() throws Exception {
        var dto = new UserCreateDTO();

        dto.setEmail("invalid-email");
        dto.setPassword("123");

        mockMvc.perform(
                        post("/api/v1/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(dto))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateValidationError() throws Exception {
        testUser = userRepository.save(testUser);

        var dto = new UserUpdateDTO();
        dto.setEmail(JsonNullable.of("not-email"));

        mockMvc.perform(
                        patch("/api/v1/users/{id}", testUser.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(dto))
                )
                .andExpect(status().isBadRequest());
    }
}
