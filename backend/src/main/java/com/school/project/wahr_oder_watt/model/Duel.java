package com.school.project.wahr_oder_watt.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

  /**
   * players sind die beiden Spieler, die an dem Duell teilnehmen.
   */
  @OneToMany(mappedBy = "duel", cascade = CascadeType.ALL)
  @Size(min = 2, max = 2)
  private List<User> players;

  /**
   * mode gibt den Modus des Duells an (z.B. "Best of 3", "Best of 5").
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

  //TODO: Richtige Implementierung
  /**
   * playtime gibt die Zeit an, wie lange das Duell verläuft.
   */
  private Date playtime;
}
