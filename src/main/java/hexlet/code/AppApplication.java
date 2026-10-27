package hexlet.code;

import io.sentry.Sentry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class AppApplication {
    public static void main(String[] args) {

        try {
            throw new Exception("This is a test.");
        } catch (Exception e) {
            Sentry.captureException(e);
        }

        SpringApplication.run(AppApplication.class, args);
    }
}
