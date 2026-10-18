package hexlet.code.dto.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class TaskDTO {
    private Long id;
    @JsonProperty("title")
    private String name;
    private Long index;
    @JsonProperty("assignee_id")
    private Long assigneeId;
    @JsonProperty("content")
    private String description;
    @JsonProperty("status")
    private String taskStatus;
    private List<Long> taskLabelIds;
    private Instant createdAt;
}
