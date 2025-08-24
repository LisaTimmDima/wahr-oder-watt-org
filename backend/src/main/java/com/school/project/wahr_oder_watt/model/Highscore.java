package com.school.project.wahr_oder_watt.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Die Klasse Highscore repräsentiert einen Highscore-Eintrag in der Datenbank.
 * Jeder Eintrag enthält eine eindeutige ID, einen Verweis auf den Benutzer (User) und die erreichte Punktzahl (score).
 */
@Entity
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
  @ManyToMany
  @JoinTable(
      name = "highscore_user",
      joinColumns = @JoinColumn(name = "highscore_id"),
      inverseJoinColumns = @JoinColumn(name = "user_id")
  )
  private List<User> players;

  /**
   * score ist die erreichte Punktzahl des Benutzers.
   */
  private int score;
}
