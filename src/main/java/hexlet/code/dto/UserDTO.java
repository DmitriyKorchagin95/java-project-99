package hexlet.code.dto;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserDTO {
    private Long id;

    private String email;

    private String firstName;

    private String lastName;

    private Instant createdAt;
}
