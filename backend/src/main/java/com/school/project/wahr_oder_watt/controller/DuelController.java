package com.school.project.wahr_oder_watt.controller;

import com.school.project.wahr_oder_watt.dto.ChallengeResponse;
import com.school.project.wahr_oder_watt.dto.DuelDto;
import com.school.project.wahr_oder_watt.model.Duel;
import com.school.project.wahr_oder_watt.model.DuelMode;
import com.school.project.wahr_oder_watt.model.DuelStatus;
import com.school.project.wahr_oder_watt.service.ChallengeService;
import com.school.project.wahr_oder_watt.service.DuelService;
import java.text.ParseException;
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
  private final ChallengeService challengeService;

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
  public ResponseEntity<Duel> createDuel(@RequestBody DuelDto duelDto) {
    Long challengerId = duelDto.getChallengerId();
    Long opponentId = duelDto.getOpponentId();
    int level = duelDto.getLevel();
    long currentTime = duelDto.getCurrentTime();

    Duel duel = duelService.instantiateDuel(challengerId, opponentId, level, currentTime);
    duel = duelService.save(duel);

    // Challenge für den Gegner speichern
    String challengerName = duel.getPlayer1().getUsername();
    String opponentName = duel.getPlayer2().getUsername();
    challengeService.addChallenge(opponentName, new ChallengeResponse(duel.getId(), challengerName, level));

    return ResponseEntity.ok(duel);
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

  /**
   * Prüft, ob die Zeit für ein Duell abgelaufen ist (nur für Speedrun-Modus relevant).
   *
   * @param id ID des zu prüfenden Duells.
   * @return true, wenn die Zeit abgelaufen ist, sonst false.
   * @throws ParseException falls ein Fehler beim Parsen der Zeit auftritt.
   */
  @PostMapping("/{id}/check-time")
  public ResponseEntity<Boolean> checkTime(@PathVariable Long id) {
    Duel duel = duelService.findById(id);
    // Nur für Speedrun-Modus relevant
    if (duel.getMode() != DuelMode.SPEEDRUN) {
      return ResponseEntity.ok(false);
    }
    long startTime = duel.getPlaytime().getTime();
    long endTime = startTime + 60000; // 60 Sekunden
    boolean isFinished = System.currentTimeMillis() > endTime;
    if (isFinished && duel.getStatus() != DuelStatus.FINISHED) {
      duel.setStatus(DuelStatus.FINISHED);
      duelService.save(duel);
    }
    return ResponseEntity.ok(isFinished);
  }

  /**
   * Prüft den Status eines Duells und aktualisiert ihn gegebenenfalls.
   * Für Rundenduelle wird geprüft, ob alle Runden gespielt und bestätigt sind.
   *
   * @param id ID des zu prüfenden Duells.
   * @return Der aktuelle Status des Duells.
   */
  @PostMapping("/{id}/check-status")
  public ResponseEntity<DuelStatus> checkStatus(@PathVariable Long id) {
    Duel duel = duelService.findById(id);
    // Rundenduell: Prüfen, ob alle Runden gespielt und bestätigt sind
    if (duel.getMode() == DuelMode.RUNDENDUELL && duel.getStatus() == DuelStatus.RUNNING) {
      boolean allRoundsConfirmed = duelService.allRoundsConfirmed(duel);
      if (allRoundsConfirmed) {
        duel.setStatus(DuelStatus.FINISHED);
        duelService.save(duel);
      }
    }
    return ResponseEntity.ok(duel.getStatus());
  }

  /**
   * Spieler verlässt das Duell.
   *
   * @param id ID des Duells.
   * @param playerId ID des Spielers, der das Duell verlässt.
   * @return HTTP 200 OK, wenn der Spieler erfolgreich das Duell verlassen hat.
   */
  @PostMapping("/{id}/leave")
  public ResponseEntity<Void> leaveGame(@PathVariable Long id, @RequestParam Long playerId) {
    duelService.leaveGame(id, playerId);
    return ResponseEntity.ok().build();
  }
}