package hexlet.code.dto.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class TaskCreateDTO {
    @NotBlank @Size(min = 1) @JsonProperty("title")
    private String name;
    private Long index;
    @JsonProperty("content")
    private String description;
    @JsonProperty("assignee_id")
    private Long assigneeId;
    private Set<Long> taskLabelIds;
    @NotBlank @JsonProperty("status")
    private String taskStatus;
}
