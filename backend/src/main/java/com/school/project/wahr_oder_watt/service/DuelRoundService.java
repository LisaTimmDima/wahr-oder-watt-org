package com.school.project.wahr_oder_watt.service;

import com.school.project.wahr_oder_watt.model.DuelRound;
import com.school.project.wahr_oder_watt.repository.DuelRoundRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service zur Verwaltung von Duellrunden.
 */
@Service
@RequiredArgsConstructor
public class DuelRoundService {

  private final DuelRoundRepository duelRoundRepository;

  /**
   * Gibt alle Duellrunden zurück.
   *
   * @return Liste aller Duellrunden.
   */
  public List<DuelRound> findAll() {
    return duelRoundRepository.findAll();
  }

  /**
   * Sucht eine Duellrunde anhand der ID.
   *
   * @param id ID der gesuchten Duellrunde.
   * @return Duellrunde mit der angegebenen ID.
   * @throws RuntimeException falls die Duellrunde nicht gefunden wird.
   */
  public DuelRound findById(Long id) {
    return duelRoundRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Duellrunde nicht gefunden mit ID: " + id));
  }

  /**
   * Speichert eine neue Duellrunde.
   *
   * @param duelRound Die zu speichernde Duellrunde.
   * @return Die gespeicherte Duellrunde.
   */
  public DuelRound save(DuelRound duelRound) {
    return duelRoundRepository.save(duelRound);
  }

  /**
   * Aktualisiert eine bestehende Duellrunde.
   *
   * @param id ID der zu aktualisierenden Duellrunde.
   * @param duelRound Neue Duellrundendaten.
   * @return Die aktualisierte Duellrunde.
   * @throws RuntimeException falls die Duellrunde nicht gefunden wird.
   */
  public DuelRound update(Long id, DuelRound duelRound) {
    DuelRound existingRound = findById(id);
    existingRound.setDevice(duelRound.getDevice());
    existingRound.setRoundNumber(duelRound.getRoundNumber());
    return duelRoundRepository.save(existingRound);
  }

  /**
   * Löscht eine Duellrunde anhand der ID.
   *
   * @param id ID der zu löschenden Duellrunde.
   */
  public void delete(Long id) {
    duelRoundRepository.deleteById(id);
  }
}
