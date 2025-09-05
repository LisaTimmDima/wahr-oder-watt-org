package com.school.project.wahr_oder_watt.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO für ein Duell, das die Spieler, den Spielmodus und die Startzeit enthält.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class DuelDto {
  /**
   * ID des Herausfordernden im Duell
   */
  private String challengerId;

  /**
   * ID des Herausgeforderten im Duell
   */
  private String opponentId;

  /**
   * Name des Spielmodus (z.B. "Speedrun", "Rundenduell")
   */
  private String modeName;

  /**
   * Startzeit des Duells im Format "YYYY-MM-DD HH:MM:SS"
   */
  private String starttime;

}
