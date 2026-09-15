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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
class UsersControllerTest {

    private static final String BASE_URL = "/api/users";

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
        testUser = buildUser();
    }

    @Test
    void testIndex() throws Exception {
        userRepository.save(testUser);

        var body = mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThatJson(body).isArray();

        assertThatJson(body)
                .inPath("$[0].email")
                .isEqualTo(testUser.getEmail());
    }

    @Test
    void testShow() throws Exception {
        testUser = userRepository.save(testUser);

        var body = mockMvc.perform(get(BASE_URL + "/{id}", testUser.getId()))
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
                        post(BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(dto))
                )
                .andExpect(status().isCreated());

        var user = userRepository.findByEmail(dto.getEmail()).orElseThrow();

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
    void testCreateValidationError() throws Exception {
        var dto = buildCreateDto();
        dto.setEmail("invalid-email");

        mockMvc.perform(
                        post(BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(dto))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdate() throws Exception {
        testUser = userRepository.save(testUser);

        var oldFirstName = testUser.getFirstName();

        var dto = buildUpdateDto(JsonNullable.of("updated@email.com"));

        mockMvc.perform(
                        patch(BASE_URL + "/{id}", testUser.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(dto))
                )
                .andExpect(status().isOk());

        var updatedUser = userRepository.findById(testUser.getId()).orElseThrow();

        assertThat(updatedUser.getEmail())
                .isEqualTo("updated@email.com");

        assertThat(updatedUser.getFirstName())
                .isEqualTo(oldFirstName);
    }

    @Test
    void testUpdateValidationError() throws Exception {
        testUser = userRepository.save(testUser);

        var dto = buildUpdateDto(JsonNullable.of("not-email"));

        mockMvc.perform(
                        patch(BASE_URL + "/{id}", testUser.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(dto))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDestroy() throws Exception {
        testUser = userRepository.save(testUser);

        mockMvc.perform(delete(BASE_URL + "/{id}", testUser.getId()))
                .andExpect(status().isNoContent());

        assertThat(userRepository.existsById(testUser.getId()))
                .isFalse();
    }

    private User buildUser() {
        return Instancio.of(User.class)
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
