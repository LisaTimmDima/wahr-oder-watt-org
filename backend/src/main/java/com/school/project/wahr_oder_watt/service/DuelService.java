package com.school.project.wahr_oder_watt.service;

import com.school.project.wahr_oder_watt.model.Duel;
import static com.school.project.wahr_oder_watt.model.DuelMode.*;
import static com.school.project.wahr_oder_watt.model.DuelStatus.*;
import com.school.project.wahr_oder_watt.repository.DuelRepository;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.RequiredArgsConstructor;
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
    existingDuel.setPlayers(duel.getPlayers());
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
   * @param challengerId ID des Herausfordernden.
   * @param opponentId ID des Herausgeforderten.
   * @param level Name des Spielmodus (z.B. "Speedrun", "Rundenduell").
   * @param currentTime Startzeit des Duells im Format "yyyy-MM-dd HH:mm:ss".
   * @return Das instanziierte Duell.
   * @throws ParseException falls die Startzeit nicht im korrekten Format ist.
   */
  public Duel instantiateDuel(Long challengerId, Long opponentId,
      int level, long currentTime) {
    /**
     * Instanziiert ein neues Duell mit den übergebenen Parametern.
     */
    Duel duel = new Duel();
    duel.getPlayers().add(userService.findById(challengerId));
    duel.getPlayers().add(userService.findById(opponentId));
    duel.setMode(level == 1 ? SPEEDRUN : RUNDENDUELL);
    duel.setPlaytime(new Date(currentTime));
    duel.setStatus(RUNNING);
    return duel;
  }
}
