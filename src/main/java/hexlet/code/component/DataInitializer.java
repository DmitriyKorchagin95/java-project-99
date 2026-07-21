package hexlet.code.component;

import hexlet.code.dto.TaskStatusCreateDTO;
import hexlet.code.dto.UserCreateDTO;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.TaskStatusesService;
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

    private final TaskStatusesService taskStatusesService;
    private final TaskStatusRepository taskStatusRepository;

    @Override
    public void run(ApplicationArguments args) {

        if (!userRepository.existsByEmail(ADMIN_EMAIL)) {
            var admin = new UserCreateDTO();
            admin.setEmail(ADMIN_EMAIL);
            admin.setFirstName("Admin");
            admin.setLastName("Admin");
            admin.setPassword("qwerty");

            userService.create(admin);
        }

        createStatus("Draft", "draft");
        createStatus("To Review", "to_review");
        createStatus("To Be Fixed", "to_be_fixed");
        createStatus("To Publish", "to_publish");
        createStatus("Published", "published");
    }

    private void createStatus(String name, String slug) {
        if (taskStatusRepository.findBySlug(slug).isPresent()) {
            return;
        }

        var taskStatus = new TaskStatusCreateDTO();
        taskStatus.setName(name);
        taskStatus.setSlug(slug);

        taskStatusesService.create(taskStatus);
    }
}
