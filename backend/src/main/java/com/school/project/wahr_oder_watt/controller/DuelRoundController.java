package com.school.project.wahr_oder_watt.controller;

import com.school.project.wahr_oder_watt.model.DuelRound;
import com.school.project.wahr_oder_watt.service.DuelRoundService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST-Controller zur Verwaltung von Duellrunden.
 */
@RestController
@RequestMapping("/api/duel-rounds")
@RequiredArgsConstructor
public class DuelRoundController {

  private final DuelRoundService duelRoundService;

  /**
   * Gibt alle Duellrunden zurück.
   */
  @GetMapping
  public ResponseEntity<List<DuelRound>> getAllDuelRounds() {
    return ResponseEntity.ok(duelRoundService.findAll());
  }

  /**
   * Gibt eine Duellrunde anhand der ID zurück.
   */
  @GetMapping("/{id}")
  public ResponseEntity<DuelRound> getDuelRound(@PathVariable Long id) {
    DuelRound duelRound = duelRoundService.findById(id);
    return ResponseEntity.ok(duelRound);
  }

  /**
   * Erstellt eine neue Duellrunde.
   */
  @PostMapping
  public ResponseEntity<DuelRound> createDuelRound(@RequestBody DuelRound duelRound) {
    DuelRound created = duelRoundService.save(duelRound);
    return ResponseEntity.ok(created);
  }

  /**
   * Aktualisiert eine bestehende Duellrunde.
   */
  @PutMapping("/{id}")
  public ResponseEntity<DuelRound> updateDuelRound(@PathVariable Long id, @RequestBody DuelRound duelRound) {
    DuelRound updated = duelRoundService.update(id, duelRound);
    return ResponseEntity.ok(updated);
  }

  /**
   * Löscht eine Duellrunde anhand der ID.
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteDuelRound(@PathVariable Long id) {
    duelRoundService.delete(id);
    return ResponseEntity.noContent().build();
  }

  /**
   * Spieler wählt Attribute für die Duellrunde aus.
   */
  @PostMapping("/{id}/select-attributes")
  public ResponseEntity<Void> selectAttributes(
      @PathVariable Long id,
      @RequestParam Long playerId,
      @RequestBody List<String> selectedAttributes) {
    duelRoundService.selectAttributes(id, playerId, selectedAttributes);
    return ResponseEntity.ok().build();
  }

  /**
   * Spieler bestätigt seine Auswahl für die Duellrunde.
   */
  @PostMapping("/{id}/confirm-selection")
  public ResponseEntity<Void> confirmSelection(
      @PathVariable Long id,
      @RequestParam Long playerId) {
    duelRoundService.confirmSelection(id, playerId);
    return ResponseEntity.ok().build();
  }
}
