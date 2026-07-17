package hexlet.code.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class TaskStatusUpdateDTO {

    @NotNull
    private JsonNullable<@NotBlank String> name = JsonNullable.undefined();

    @NotNull
    private JsonNullable<@NotBlank String> slug = JsonNullable.undefined();
}
