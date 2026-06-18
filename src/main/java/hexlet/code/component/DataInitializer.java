package hexlet.code.component;

import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private static final String ADMIN_EMAIL = "hexlet@example.com";

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public void run(ApplicationArguments args) {

        if (userRepository.existsByEmail(ADMIN_EMAIL)) {
            return;
        }

        User admin = new User();
        admin.setEmail(ADMIN_EMAIL);
        admin.setFirstName("Admin");
        admin.setLastName("Admin");
        admin.setPassword(passwordEncoder.encode("qwerty"));
        userRepository.save(admin);
    }
}
