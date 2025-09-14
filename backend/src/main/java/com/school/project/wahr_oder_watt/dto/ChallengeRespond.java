package com.school.project.wahr_oder_watt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO für die Antwort auf eine Herausforderung.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ChallengeRespond {

  /**
   * Der Name des herausgeforderten Spielers.
   */
  private String targetPlayer;

  /**
   * Gibt an, ob die Herausforderung akzeptiert wurde.
   */
  private boolean accepted;
}
