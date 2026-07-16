package org.example.frusitshopapp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequestDto {
    private String username;
    private String name;
    private String email;
    private String password;
}
