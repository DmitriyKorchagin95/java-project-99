package hexlet.code.component;

import hexlet.code.dto.label.LabelCreateDTO;
import hexlet.code.dto.status.TaskStatusCreateDTO;
import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.impl.LabelServiceImpl;
import hexlet.code.service.impl.TaskStatusServiceImpl;
import hexlet.code.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {
    private static final String ADMIN_EMAIL = "hexlet@example.com";
    private final UserServiceImpl userService;
    private final UserRepository userRepository;
    private final TaskStatusServiceImpl taskStatusService;
    private final TaskStatusRepository taskStatusRepository;
    private final LabelServiceImpl labelService;
    private final LabelRepository labelRepository;

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
        createStatus("ToReview", "to_review");
        createStatus("ToBeFixed", "to_be_fixed");
        createStatus("ToPublish", "to_publish");
        createStatus("Published", "published");
        createLabel("feature");
        createLabel("bug");
    }

    private void createStatus(String name, String slug) {

        if (taskStatusRepository.findBySlug(slug).isPresent()) {
            return;
        }

        var taskStatus = new TaskStatusCreateDTO();
        taskStatus.setName(name);
        taskStatus.setSlug(slug);
        taskStatusService.create(taskStatus);
    }

    private void createLabel(String name) {

        if (labelRepository.findByName(name).isPresent()) {
            return;
        }

        var label = new LabelCreateDTO();
        label.setName(name);
        labelService.create(label);
    }
}
