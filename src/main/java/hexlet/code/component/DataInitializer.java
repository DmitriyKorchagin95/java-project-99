package hexlet.code.component;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private static final String ADMIN_EMAIL = "hexlet@example.com";

    private final UserService userService;
    private final UserRepository userRepository;

    @Override
    public void run(ApplicationArguments args) {

        if (userRepository.existsByEmail(ADMIN_EMAIL)) {
            return;
        }

        var admin = new UserCreateDTO();
        admin.setEmail(ADMIN_EMAIL);
        admin.setFirstName("Admin");
        admin.setLastName("Admin");
        admin.setPassword("qwerty");
        userService.create(admin);
    }
}
