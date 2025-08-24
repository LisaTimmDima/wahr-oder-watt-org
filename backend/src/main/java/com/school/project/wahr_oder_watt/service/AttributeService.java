package com.school.project.wahr_oder_watt.service;

import com.school.project.wahr_oder_watt.model.Attribute;
import com.school.project.wahr_oder_watt.repository.AttributeRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service-Klasse für die Verwaltung von Attributen.
 */
@Service
@RequiredArgsConstructor
public class AttributeService {

  private final AttributeRepository attributeRepository;

  /**
   * Gibt alle Attribute zurück.
   *
   * @return Liste aller Attribute.
   */
  public List<Attribute> findAll() {
    return attributeRepository.findAll();
  }

  /**
   * Sucht ein Attribut anhand der ID.
   *
   * @param id ID des gesuchten Attributs.
   * @return Attribut mit der angegebenen ID.
   * @throws RuntimeException falls das Attribut nicht gefunden wird.
   */
  public Attribute findById(Long id) {
    return attributeRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Attibute nicht gefunden mit ID: " + id));
  }

  /**
   * Speichert ein neues Attribut.
   *
   * @param attribute Das zu speichernde Attribut.
   * @return Das gespeicherte Attribut.
   */
  public Attribute save(Attribute attribute) {
    return attributeRepository.save(attribute);
  }

  /**
   * Aktualisiert ein bestehendes Attribut.
   *
   * @param id ID des zu aktualisierenden Attributs.
   * @param attribute Neue Attributsdaten.
   * @return Das aktualisierte Attribut.
   * @throws RuntimeException falls das Attribut nicht gefunden wird.
   */
  public Attribute update(Long id, Attribute attribute) {
    Attribute existingAttribute = findById(id);
    existingAttribute.setImageUrl(attribute.getImageUrl());
    existingAttribute.setDescription(attribute.getDescription());
    existingAttribute.setLockedBy(attribute.getLockedBy());
    return attributeRepository.save(existingAttribute);
  }

  /**
   * Löscht ein Attribut anhand der ID.
   *
   * @param id ID des zu löschenden Attributs.
   */
  public void delete(Long id) {
    attributeRepository.deleteById(id);
  }
}
