package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.dto.user.UserUpdateDTO;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.ModelGenerator;
import net.datafaker.Faker;
import org.hamcrest.Matchers;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
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

    @Autowired
    private ModelGenerator modelGenerator;

    private User testUser;
    private JwtRequestPostProcessor token;

    @BeforeEach
    void setUp() {
        testUser = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(testUser);

        token = jwt().jwt(builder -> builder
                .subject(testUser.getEmail())
                .claim("id", testUser.getId())
        );
    }

    @Test
    void testIndex() throws Exception {
        var body = mockMvc.perform(get("/api/users").with(token))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].email")
                        .value(Matchers.hasItem(testUser.getEmail())))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThatJson(body)
                .isArray();
    }

    @Test
    void testIndexWithoutTokenUnauthorized() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testShow() throws Exception {
        var body = mockMvc.perform(get("/api/users/{id}", testUser.getId()).with(token))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThatJson(body)
                .node("email")
                .isEqualTo(testUser.getEmail());

        assertThatJson(body)
                .node("password")
                .isAbsent();
    }

    @Test
    void testCreate() throws Exception {
        var dto = buildCreateDto();

        mockMvc.perform(
                        post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(dto))
                )
                .andExpect(status().isCreated());

        var user = userRepository.findByEmail(dto.getEmail()).orElseThrow();

        assertNotNull(user);
        assertThat(user.getEmail()).isEqualTo(dto.getEmail());
        assertThat(user.getPasswordHash()).isNotEqualTo(dto.getPassword());
        assertThat(passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())).isTrue();
    }

    @Test
    void testCreateDuplicateEmailConflict() throws Exception {
        var dto = buildCreateDto();
        dto.setEmail(testUser.getEmail());

        mockMvc.perform(
                        post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(dto))
                )
                .andExpect(status().isConflict());
    }

    @Test
    void testCreateValidationError() throws Exception {
        var dto = buildCreateDto();
        dto.setEmail("invalid-email");

        mockMvc.perform(
                        post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(dto))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdate() throws Exception {
        var oldFirstName = testUser.getFirstName();
        var dto = buildUpdateDto(JsonNullable.of("updated@email.com"));

        mockMvc.perform(
                        put("/api/users/{id}", testUser.getId())
                                .with(token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(dto))
                )
                .andExpect(status().isOk());

        userRepository.flush();

        var updatedUser = userRepository.findById(testUser.getId()).orElseThrow();

        assertThat(updatedUser.getEmail()).isEqualTo("updated@email.com");
        assertThat(updatedUser.getFirstName()).isEqualTo(oldFirstName);
    }

    @Test
    void testUpdateForeignProfileForbidden() throws Exception {
        var alienUser = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(alienUser);

        var alienToken = jwt().jwt(builder -> builder
                .subject(alienUser.getEmail())
                .claim("id", alienUser.getId())
        );

        var dto = buildUpdateDto(JsonNullable.of("hacked@email.com"));

        mockMvc.perform(
                        put("/api/users/{id}", testUser.getId())
                                .with(alienToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(dto))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void testUpdateValidationError() throws Exception {
        var dto = buildUpdateDto(JsonNullable.of("not-email"));

        mockMvc.perform(
                        put("/api/users/{id}", testUser.getId())
                                .with(token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(dto))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDestroy() throws Exception {
        mockMvc.perform(delete("/api/users/{id}", testUser.getId()).with(token))
                .andExpect(status().isNoContent());

        userRepository.flush();

        assertThat(userRepository.existsById(testUser.getId())).isFalse();
    }

    @Test
    void testDestroyForeignProfileForbidden() throws Exception {
        var alienUser = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(alienUser);

        var alienToken = jwt().jwt(builder -> builder
                .subject(alienUser.getEmail())
                .claim("id", alienUser.getId())
        );

        mockMvc.perform(delete("/api/users/{id}", testUser.getId()).with(alienToken))
                .andExpect(status().isForbidden());
    }

    private UserCreateDTO buildCreateDto() {
        var dto = new UserCreateDTO();
        dto.setEmail(faker.internet().emailAddress());
        dto.setFirstName(faker.name().firstName());
        dto.setLastName(faker.name().lastName());
        dto.setPassword("password");
        return dto;
    }

    private UserUpdateDTO buildUpdateDto(JsonNullable<String> email) {
        var dto = new UserUpdateDTO();
        dto.setEmail(email);
        return dto;
    }
}

