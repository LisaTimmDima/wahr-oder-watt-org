package com.school.project.wahr_oder_watt.controller;

import com.school.project.wahr_oder_watt.model.Duel;
import com.school.project.wahr_oder_watt.service.DuelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST-Controller zur Verwaltung von Duellen.
 */
@RestController
@RequestMapping("/api/duels")
@RequiredArgsConstructor
public class DuelController {

  private final DuelService duelService;

  /**
   * Gibt alle Duelle zurück.
   */
  @GetMapping
  public ResponseEntity<List<Duel>> getAllDuels() {
    return ResponseEntity.ok(duelService.findAll());
  }

  /**
   * Gibt ein Duell anhand der ID zurück.
   */
  @GetMapping("/{id}")
  public ResponseEntity<Duel> getDuel(@PathVariable Long id) {
    Duel duel = duelService.findById(id);
    return ResponseEntity.ok(duel);
  }

  /**
   * Erstellt ein neues Duell.
   */
  @PostMapping
  public ResponseEntity<Duel> createDuel(@RequestBody Duel duel) {
    Duel created = duelService.save(duel);
    return ResponseEntity.ok(created);
  }

  /**
   * Aktualisiert ein bestehendes Duell.
   */
  @PutMapping("/{id}")
  public ResponseEntity<Duel> updateDuel(@PathVariable Long id, @RequestBody Duel duel) {
    Duel updated = duelService.update(id, duel);
    return ResponseEntity.ok(updated);
  }

  /**
   * Löscht ein Duell anhand der ID.
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteDuel(@PathVariable Long id) {
    duelService.delete(id);
    return ResponseEntity.noContent().build();
  }
}