package hexlet.code.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserCreateDTO {

    @NotBlank private String firstName;

    @NotBlank private String lastName;

    @NotBlank @Email private String email;

    @Size(min = 6) private String password;
}
