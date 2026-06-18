package hexlet.code.util;

import hexlet.code.model.User;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.Select;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ModelGenerator {
    private Model<User> userModel;

    private final Faker faker;

    public ModelGenerator(Faker faker) {
        this.faker = faker;
    }

    @PostConstruct
    private void init() {
        userModel = Instancio.of(User.class).ignore(Select.field(User::getId))
                .supply(Select.field(User::getEmail), () -> faker.internet().emailAddress()).toModel();
    }
}
