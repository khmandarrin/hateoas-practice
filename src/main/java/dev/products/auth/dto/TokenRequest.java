package dev.products.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TokenRequest {

    @NotBlank(message = "username은 필수입니다.")
    @Schema(description = "user name", example = "user2")
    private String username;

    @NotBlank(message = "password는 필수입니다.")
    @Schema(description = "password", example = "1234")
    private String password;
}
