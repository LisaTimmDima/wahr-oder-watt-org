package com.school.project.wahr_oder_watt.dto;

import com.school.project.wahr_oder_watt.model.User;
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
   * Liste der Spieler im Duell
   */
  private List<User> players;

  /**
   * Name des Spielmodus (z.B. "Speedrun", "Rundenduell")
   */
  private String modeName;

  /**
   * Startzeit des Duells im Format "YYYY-MM-DD HH:MM:SS"
   */
  private String starttime;

}
