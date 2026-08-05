package hexlet.code.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskCreateDTO {

    @NotBlank @Size(min = 1) private String name;
    private Long index;
    private String description;
    private Long assigneeId;

    @NotBlank @Size(min = 1) private String status;
}
