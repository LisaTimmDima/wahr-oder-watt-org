package com.school.project.wahr_oder_watt.service;

import com.school.project.wahr_oder_watt.model.Device;
import com.school.project.wahr_oder_watt.model.Duel;
import static com.school.project.wahr_oder_watt.model.DuelMode.*;
import static com.school.project.wahr_oder_watt.model.DuelStatus.*;

import com.school.project.wahr_oder_watt.model.DuelRound;
import com.school.project.wahr_oder_watt.model.User;
import com.school.project.wahr_oder_watt.repository.DuelRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service zur Verwaltung von Duellen.
 */
@Service
@RequiredArgsConstructor
public class DuelService {

  private final DuelRepository duelRepository;
  private final UserService userService;
  @Autowired
  private DeviceService deviceService;
  @Autowired
  private DuelRoundService duelRoundService;

  /**
   * Gibt alle Duelle zurück.
   *
   * @return Liste aller Duelle.
   */
  public List<Duel> findAll() {
    return duelRepository.findAll();
  }

  /**
   * Sucht ein Duell anhand der ID.
   *
   * @param id ID des gesuchten Duells.
   * @return Duell mit der angegebenen ID.
   * @throws RuntimeException falls das Duell nicht gefunden wird.
   */
  public Duel findById(Long id) {
    return duelRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Duell nicht gefunden mit ID: " + id));
  }

  /**
   * Speichert ein neues Duell.
   *
   * @param duel Das zu speichernde Duell.
   * @return Das gespeicherte Duell.
   */
  public Duel save(Duel duel) {
    return duelRepository.save(duel);
  }

  /**
   * Aktualisiert ein bestehendes Duell.
   *
   * @param id ID des zu aktualisierenden Duells.
   * @param duel Neue Duell-Daten.
   * @return Das aktualisierte Duell.
   * @throws RuntimeException falls das Duell nicht gefunden wird.
   */
  public Duel update(Long id, Duel duel) {
    Duel existingDuel = findById(id);
    existingDuel.setStatus(duel.getStatus());
    existingDuel.setMode(duel.getMode());
    existingDuel.setRounds(duel.getRounds());
    existingDuel.setPlayer1(duel.getPlayer1());
    existingDuel.setPlayer2(duel.getPlayer2());
    existingDuel.setPlaytime(duel.getPlaytime());
    return duelRepository.save(existingDuel);
  }

  /**
   * Löscht ein Duell anhand der ID.
   *
   * @param id ID des zu löschenden Duells.
   */
  public void delete(Long id) {
    duelRepository.deleteById(id);
  }

  /**
   * Instanziiert ein neues Duell mit den gegebenen Parametern.
   *
   * @param challengerId ID des Herausforderers.
   * @param opponentId ID des Gegners.
   * @param level Spielmodus (1 für Speedrun, 2 für Rundenduell).
   * @param currentTime Aktuelle Zeit in Millisekunden seit Epoch.
   * @return Das instanzierte Duell.
   */
  public Duel instantiateDuel(Long challengerId, Long opponentId,
      int level, long currentTime) {
    /**
     * Instanziiert ein neues Duell mit den übergebenen Parametern.
     */
    Duel duel = new Duel();
    duel.setPlayer1(userService.findById(challengerId));
    duel.setPlayer2(userService.findById(opponentId));
    duel.setChallengerLeft(false);
    duel.setOpponentLeft(false);
    duel.setMode(level == 1 ? SPEEDRUN : RUNDENDUELL);
    duel.setPlaytime(new Date(currentTime));
    duel.setStatus(RUNNING);

    return duel;
  }

  public Duel startDuel(Long challengerId, Long opponentId, int level, long currentTime) {
    Duel duel = instantiateDuel(challengerId, opponentId, level, currentTime);

    if (level == 2) { // Rundenduell
      int roundCount = 5;
      List<Device> devices = deviceService.findAll().stream()
          .map(dto -> deviceService.findById(dto.getId()))
          .toList();
      Collections.shuffle(devices);

      List<DuelRound> rounds = new ArrayList<>();
      for (int i = 0; i < roundCount; i++) {
        Device device = devices.get(i % devices.size());
        DuelRound round = duelRoundService.createRandomRound(device, i + 1);
        rounds.add(round);
      }
      duel.setRounds(rounds);
    } else if (level == 1) { // Speedrun
      duel.setRounds(new ArrayList<>()); // Keine festen Runden, Zeitsteuerung extern
      // Die Logik zur Zeitsteuerung (z.B. 60 Sekunden) sollte im Game-Controller oder Frontend erfolgen
    }

    return save(duel);
  }

  /**
   * Ein Spieler verlässt das Duell.
   * Das Duell wird beendet, wenn ein Spieler das Spiel verlässt.
   *
   * @param duelId ID des Duells.
   * @param playerId ID des Spielers, der das Duell verlässt.
   */
  public void leaveGame(Long duelId, Long playerId) {
    Duel duel = findById(duelId);
    duel.removePlayer(playerId);
    // Wenn ein Spieler verlässt, Spiel beenden
    duel.setStatus(FINISHED);
    save(duel);
  }

  /**
   * Prüft, ob alle Runden in einem Rundenduell von beiden Spielern bestätigt wurden.
   *
   * @param duel Das zu prüfende Duell.
   * @return true, wenn alle Runden bestätigt sind, sonst false.
   */
  public boolean allRoundsConfirmed(Duel duel) {
    if (duel.getRounds() == null || duel.getRounds().isEmpty()) return false;
    Long challengerId = duel.getPlayer1().getId();
    Long opponentId = duel.getPlayer2().getId();
    return duel.getRounds().stream()
        .allMatch(round -> duelRoundService.bothPlayersConfirmed(round.getId(), challengerId, opponentId));
  }
}
