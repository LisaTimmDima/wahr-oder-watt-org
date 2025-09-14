package com.school.project.wahr_oder_watt.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Size;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Table;

/**
 * Die Klasse Duel repräsentiert ein Duell zwischen zwei Spielern.
 * Ein Duell besteht aus mehreren Runden und hat einen bestimmten Modus und Status.
 */
@Entity
@Table(name = "duel")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Duel {
  /**
   * id ist der eindeutige Bezeichner für jedes Duell.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "player1_id", nullable = false)
  private User player1;

  @ManyToOne
  @JoinColumn(name = "player2_id", nullable = false)
  private User player2;

  /**
   * challengerLeft und opponentLeft geben an, ob ein Spieler das Duell verlassen hat.
   */
  private boolean challengerLeft = false;
  private boolean opponentLeft = false;

  /**
   * mode gibt den Modus des Duells an (z.B. "Speedrun", "Rundenduell").
   */
  @Enumerated(EnumType.STRING)
  private DuelMode mode;

  /**
   * rounds sind die einzelnen Runden, die in diesem Duell gespielt werden.
   */
  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  private List<DuelRound> rounds;

  /**
   * status gibt den aktuellen Status des Duells an (z.B. "WAITING", "RUNNING", "FINISHED").
   */
  @Enumerated(EnumType.STRING)
  private DuelStatus status;

  /**
   * playtime gibt die Zeit an, wie lange das Duell verläuft.
   */
  private Date playtime;

  /**
   * Die Methode removePlayer markiert einen Spieler als verlassen.
   * Wenn der Spieler der Herausforderer ist, wird challengerLeft auf true gesetzt.
   * Wenn der Spieler der Herausgeforderte ist, wird opponentLeft auf true gesetzt.
   * @param playerId
   */
  public void removePlayer(Long playerId) {
    if (player1.getId() != null && player1.getId().equals(playerId)) {
      challengerLeft = true;
    } else if (player2.getId() != null && player2.getId().equals(playerId)) {
      opponentLeft = true;
    }
  }
}
