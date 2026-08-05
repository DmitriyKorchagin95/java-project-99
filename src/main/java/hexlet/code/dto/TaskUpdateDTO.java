package hexlet.code.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class TaskUpdateDTO {

    @NotNull private JsonNullable<@NotBlank @Size(min = 1) String> name = JsonNullable.undefined();

    @NotNull private JsonNullable<Long> index = JsonNullable.undefined();

    @NotNull private JsonNullable<String> description = JsonNullable.undefined();

    @NotNull private JsonNullable<Long> assigneeId = JsonNullable.undefined();

    @NotNull private JsonNullable<@NotBlank @Size(min = 1) String> status = JsonNullable.undefined();
}
