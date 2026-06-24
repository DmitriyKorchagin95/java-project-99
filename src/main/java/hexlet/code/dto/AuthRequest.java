package hexlet.code.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthRequest {
    @NotNull @NotBlank private String username;

    @NotNull @NotBlank private String password;
}
