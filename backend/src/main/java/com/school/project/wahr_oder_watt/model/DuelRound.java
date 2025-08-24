package com.school.project.wahr_oder_watt.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Die Klasse DuelRound repräsentiert eine Runde in einem Duell.
 * Jede Runde ist durch eine eindeutige ID gekennzeichnet und enthält Informationen über das verwendete Gerät
 * und die Rundennummer innerhalb des Duells.
 */
@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class DuelRound {

  /**
   * id ist der eindeutige Bezeichner für jede Runde.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * device gibt das Gerät an, das in dieser Runde verwendet wurde.
   */
  @ManyToOne
  @JoinColumn(name = "device_id")
  private Device device;

  /**
   * roundNumber gibt die Nummer der Runde innerhalb eines Duells an.
   */
  private int roundNumber;

}
