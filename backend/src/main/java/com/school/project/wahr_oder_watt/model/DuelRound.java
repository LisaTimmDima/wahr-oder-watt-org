package com.school.project.wahr_oder_watt.model;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Table;

/**
 * Die Klasse DuelRound repräsentiert eine Runde in einem Duell.
 * Jede Runde ist durch eine eindeutige ID gekennzeichnet und enthält Informationen über das verwendete Gerät
 * und die Rundennummer innerhalb des Duells.
 */
@Entity
@Table(name = "duel_round")
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

  /**
   * allAttributes enthält alle möglichen Attribute für das Gerät in dieser Runde.
   */
  @ElementCollection
  private List<String> allAttributes;

  /**
   * visibleAttributes enthält die Attribute, die den Spielern in dieser Runde angezeigt werden.
   */
  @ElementCollection
  private List<String> visibleAttributes;

  /**
   * playerSelections speichert die Auswahl der Spieler für die Attribute in dieser Runde.
   * Der Schlüssel ist die Spieler-ID und der Wert ist eine Liste der ausgewählten Attribute.
   */
  @ElementCollection
  private Map<Long, List<String>> playerSelections;

  /**
   * lockedAttributes enthält die Attribute, die in dieser Runde gesperrt sind und nicht ausgewählt werden können.
   */
  @ElementCollection
  private List<String> lockedAttributes;

}
