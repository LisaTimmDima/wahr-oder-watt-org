package com.school.project.wahr_oder_watt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for user registration requests.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class LoginRequest {
    /**
     * The email of the user.
     */
    private String email;

    /**
     * The password of the user.
     */
    private String password;
}
