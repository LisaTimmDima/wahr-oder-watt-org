package com.school.project.wahr_oder_watt.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Table;

/**
 * Die Klasse Attribute repräsentiert ein Attribut mit einem Bild und einer Beschreibung.
 * Jedes Attribut hat eine eindeutige ID, eine URL zu einem Bild und eine textuelle Beschreibung.
 */
@Entity
@Table(name = "attribute")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Attribute {

  /**
   * id ist der eindeutige Bezeichner für jedes Attribut.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * imageUrl ist die URL zu einem Bild, das das Attribut repräsentiert.
   */
  @Column(name = "image_url")
  private String imageUrl;

  /**
   * description ist eine textuelle Beschreibung des Attributs.
   */
  @Column(name = "description")
  private String description;

  @ManyToMany(mappedBy = "attributes", fetch = FetchType.LAZY)
  private Set<Device> devices = new HashSet<>();
}
