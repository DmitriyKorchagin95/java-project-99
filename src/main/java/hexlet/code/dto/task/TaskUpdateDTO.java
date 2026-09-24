package hexlet.code.dto.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class TaskUpdateDTO {

    @NotNull @JsonProperty("title")
    private JsonNullable<@NotBlank String> name = JsonNullable.undefined();

    @NotNull private JsonNullable<Long> index = JsonNullable.undefined();

    @NotNull @JsonProperty("content")
    private JsonNullable<String> description = JsonNullable.undefined();

    @NotNull @JsonProperty("assignee_id")
    private JsonNullable<Long> assigneeId = JsonNullable.undefined();

    @NotNull @JsonProperty("status")
    private JsonNullable<@NotBlank String> taskStatus = JsonNullable.undefined();

    @NotNull private JsonNullable<Set<Long>> taskLabelIds = JsonNullable.undefined();
}
