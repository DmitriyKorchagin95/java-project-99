package hexlet.code.dto;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskStatusDTO {
    private Long id;

    private String name;

    private String slug;

    private Instant createdAt;
}
