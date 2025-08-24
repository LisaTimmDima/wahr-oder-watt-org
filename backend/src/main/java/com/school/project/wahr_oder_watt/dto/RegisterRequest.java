package com.school.project.wahr_oder_watt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for user registration requests.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class RegisterRequest {
  /**
   * The username of the user.
   */
  private String username;

  /**
   * The password of the user.
   */
  private String password;
}
