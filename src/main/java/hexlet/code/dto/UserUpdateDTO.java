package hexlet.code.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class UserUpdateDTO {

    @NotNull private JsonNullable<@Email @NotBlank String> email = JsonNullable.undefined();

    @NotNull private JsonNullable<@NotBlank String> firstName = JsonNullable.undefined();

    @NotNull private JsonNullable<@NotBlank String> lastName = JsonNullable.undefined();

    @NotNull private JsonNullable<@NotBlank @Size(min = 3) String> password = JsonNullable.undefined();
}
