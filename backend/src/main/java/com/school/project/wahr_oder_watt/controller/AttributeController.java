package com.school.project.wahr_oder_watt.controller;

import com.school.project.wahr_oder_watt.model.Attribute;
import com.school.project.wahr_oder_watt.service.AttributeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing attributes.
 */
@RestController
@RequestMapping("/api/attributes")
@RequiredArgsConstructor
public class AttributeController {

  private final AttributeService attributeService;

  /**
   * Retrieves all attributes.
   *
   * @return List of all attributes.
   */
  @GetMapping
  public ResponseEntity<List<Attribute>> getAllAttributes() {
    return ResponseEntity.ok(attributeService.findAll());
  }

  /**
   * Retrieves an attribute by its ID.
   *
   * @param id ID of the attribute to retrieve.
   * @return The attribute with the specified ID.
   */
  @GetMapping("/{id}")
  public ResponseEntity<Attribute> getAttribute(@PathVariable Long id) {
    Attribute attribute = attributeService.findById(id);
    return ResponseEntity.ok(attribute);
  }

  /**
   * Creates a new attribute.
   *
   * @param attribute The attribute to create.
   * @return The created attribute.
   */
  @PostMapping
  public ResponseEntity<Attribute> createAttribute(@RequestBody Attribute attribute) {
    Attribute created = attributeService.save(attribute);
    return ResponseEntity.ok(created);
  }

  /**
   * Updates an existing attribute.
   *
   * @param id ID of the attribute to update.
   * @param attribute The updated attribute data.
   * @return The updated attribute.
   */
  @PutMapping("/{id}")
  public ResponseEntity<Attribute> updateAttribute(@PathVariable Long id, @RequestBody Attribute attribute) {
    Attribute updated = attributeService.update(id, attribute);
    return ResponseEntity.ok(updated);
  }

  /**
   * Deletes an attribute by its ID.
   *
   * @param id ID of the attribute to delete.
   * @return Response entity with no content.
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteAttribute(@PathVariable Long id) {
    attributeService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
