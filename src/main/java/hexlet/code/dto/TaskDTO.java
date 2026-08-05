package hexlet.code.dto;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskDTO {
    private Long id;
    private String name;
    private Long index;
    private String description;
    private Long assigneeId;
    private String status;
    private Instant createdAt;
}
