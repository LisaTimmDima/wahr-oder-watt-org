package com.school.project.wahr_oder_watt.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Table;

/**
 * Die Klasse User repräsentiert einen Benutzer in der Datenbank. Jeder Benutzer hat eine eindeutige
 * ID, einen Benutzernamen, ein Passwort, eine E-Mail-Adresse sowie Attribute zur Verwaltung von
 * Administratorrechten, Aktivierungsstatus und Punktzahl.
 */
@Builder
@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

  /**
   * id ist der eindeutige Bezeichner für jeden Benutzer.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * Der Benutzername des Users. Muss eindeutig sein und darf nicht null sein.
   */
  @Column(nullable = false, unique = true)
  private String username;

  /**
   * Das Passwort des Benutzers. Darf nicht null sein. Wird in JSON-Antworten ignoriert, um die
   * Sicherheit zu gewährleisten.
   */
  @Column(nullable = false)
  @JsonIgnore
  private String password;

  /**
   * Die E-Mail-Adresse des Benutzers. Muss eindeutig sein und darf nicht null sein.
   */
  @Column(nullable = false, unique = true)
  private String email;

  /**
   * Gibt an, ob der Benutzer Administratorrechte hat.
   */
  @Column
  private boolean isAdmin;

  /**
   * Gibt an, ob der Benutzeraccount aktiviert ist.
   */
  @Column
  private boolean isEnabled;

  /**
   * Das aktuelle Duell, an dem der Benutzer teilnimmt. Ein Benutzer kann nur an einem aktiven Duell
   * teilnehmen.
   */
  @ManyToOne
  @JoinColumn(name = "duel_id")
  private Duel duel;

  /**
   * Die Punktzahl des Benutzers.
   */
  private int score;
}
