package com.school.project.wahr_oder_watt.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Die Device-Klasse repräsentiert ein Gerät, das in einem Duell verwendet werden kann.
 * Jedes Gerät hat eine eindeutige ID, eine Bild-URL, einen Aktivitätsstatus und eine Beschreibung.
 */
@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Device {

  /**
   * id ist der eindeutige Bezeichner für jedes Gerät.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * imageUrl ist die URL zu einem Bild, das das Gerät repräsentiert.
   */
  private String imageUrl;

  /**
   * isActive gibt an, ob das Gerät aktiv und verfügbar für die Auswahl in einem Duell ist.
   */
  private boolean isActive;

  /**
   * description ist eine textuelle Beschreibung des Geräts.
   */
  private String description;

  /**
   * attributes ist eine Liste von Attributen, die dem Gerät zugeordnet sind.
   */
  @ManyToMany
  @JoinTable(
      name = "device_attributes",
      joinColumns = @JoinColumn(name = "device_id"),
      inverseJoinColumns = @JoinColumn(name = "attribute_id")
  )
  private List<Attribute> attributes;
}
