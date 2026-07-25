package com.lionproject24.fruitshop.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "로그인 요청 DTO")
public class LoginRequestDto {
    @NotEmpty(message = "아이디를 입력해주세요.")
    @Schema(description = "ID", example = "aaa")
    private String username;

    @NotEmpty(message = "비밀번호를 입력해주세요.")
    @Schema(description = "PW", example = "1234")
    private String password;
}
