package com.school.project.wahr_oder_watt.service;

import com.school.project.wahr_oder_watt.model.Duel;
import com.school.project.wahr_oder_watt.repository.DuelRepository;
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

  public Duel instantiateDuel(Duel duel) {
    //TODO: Logik zur Initialisierung eines neuen Duells
    return duel;
  }
}
