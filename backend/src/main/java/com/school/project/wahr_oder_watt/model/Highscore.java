package com.school.project.wahr_oder_watt.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToOne;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Table;

/**
 * Die Klasse Highscore repräsentiert einen Highscore-Eintrag in der Datenbank.
 * Jeder Eintrag enthält eine eindeutige ID, einen Verweis auf den Benutzer (User) und die erreichte Punktzahl (score).
 */
@Entity
@Table(name = "highscore")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Highscore {
  /**
   * id ist der eindeutige Bezeichner für jeden Highscore-Eintrag.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * players ist die Liste der Benutzer, die diesen Highscore erreicht haben.
   */
  @OneToOne
  @JoinColumn(name = "user_id", referencedColumnName = "id")
  private User player;

  /**
   * score ist die erreichte Punktzahl des Benutzers.
   */
  private int score;
}
