package hexlet.code.component;

import hexlet.code.model.User;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.Select;
import org.springframework.stereotype.Component;

@Getter
@Component
@RequiredArgsConstructor
public class ModelGenerator {

    private final Faker faker;

    private Model<User> userModel;

    @PostConstruct
    private void init() {
        userModel = Instancio.of(User.class).ignore(Select.field(User::getId))
                .supply(Select.field(User::getEmail), () -> faker.internet().emailAddress()).toModel();
    }
}
