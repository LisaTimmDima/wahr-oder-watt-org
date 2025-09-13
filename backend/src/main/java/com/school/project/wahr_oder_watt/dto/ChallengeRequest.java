package com.school.project.wahr_oder_watt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO für Challenge-Anfragen.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ChallengeRequest {

  /**
   * Der Spieler, der die Herausforderung sendet.
   */
  private String fromPlayer;

  /**
   * Der Spieler, der die Herausforderung empfängt.
   */
  private String targetPlayer;
}
