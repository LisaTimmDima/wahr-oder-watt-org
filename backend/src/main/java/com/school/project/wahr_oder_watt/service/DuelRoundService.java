package com.school.project.wahr_oder_watt.service;

import com.school.project.wahr_oder_watt.model.Device;
import com.school.project.wahr_oder_watt.model.DuelRound;
import com.school.project.wahr_oder_watt.repository.DuelRoundRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.stream.Collectors;
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
  private final DeviceService deviceService;

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

  /**
   * Erstellt eine neue Duellrunde mit zufälligen Attributen für ein gegebenes Gerät.
   *
   * @param device      Das Gerät für die Duellrunde.
   * @param roundNumber Die Rundennummer.
   * @return Die erstellte Duellrunde.
   */
  public DuelRound createRandomRound(Device device, int roundNumber) {
    List<String> allAttributes = deviceService.getDeviceAttributes(device);
    Collections.shuffle(allAttributes);
    List<String> visibleAttributes = allAttributes.stream().limit(4).collect(Collectors.toList());

    DuelRound round = new DuelRound();
    round.setDevice(device);
    round.setRoundNumber(roundNumber);
    round.setAllAttributes(allAttributes);
    round.setVisibleAttributes(visibleAttributes);
    round.setPlayerSelections(new HashMap<>());
    round.setLockedAttributes(new ArrayList<>());
    return duelRoundRepository.save(round);
  }

  /**
   * Ermöglicht einem Spieler, Attribute für eine Duellrunde auszuwählen.
   *
   * @param roundId            ID der Duellrunde.
   * @param playerId           ID des Spielers.
   * @param selectedAttributes Liste der ausgewählten Attribute.
   * @throws IllegalArgumentException falls die Auswahl ungültig ist (mehr als 2 oder gesperrte Attribute).
   */
  public void selectAttributes(Long roundId, Long playerId, List<String> selectedAttributes) {
    DuelRound round = findById(roundId);

    // Validierung: Maximal 2, keine gesperrten
    if (selectedAttributes.size() > 2 || !round.getLockedAttributes().isEmpty() &&
        selectedAttributes.stream().anyMatch(round.getLockedAttributes()::contains)) {
      throw new IllegalArgumentException("Ungültige Auswahl.");
    }
    round.getPlayerSelections().put(playerId, selectedAttributes);
    save(round);
  }

  /**
   * Bestätigt die Attributauswahl eines Spielers und sperrt diese Attribute für den anderen Spieler.
   *
   * @param roundId  ID der Duellrunde.
   * @param playerId ID des Spielers, der die Auswahl bestätigt.
   * @throws IllegalArgumentException falls nicht genau 2 Attribute ausgewählt wurden.
   */
  public void confirmSelection(Long roundId, Long playerId) {
    DuelRound round = findById(roundId);
    List<String> selected = round.getPlayerSelections().get(playerId);
    if (selected == null || selected.size() != 2) {
      throw new IllegalArgumentException("Es müssen genau 2 Attribute gewählt werden.");
    }
    // Sperre die gewählten Attribute für den anderen Spieler
    round.getLockedAttributes().addAll(selected);
    save(round);
  }

  public boolean bothPlayersConfirmed(Long roundId, Long challengerId, Long opponentId) {
    DuelRound round = findById(roundId);
    return round.getPlayerSelections().containsKey(challengerId)
        && round.getPlayerSelections().containsKey(opponentId);
  }
}
