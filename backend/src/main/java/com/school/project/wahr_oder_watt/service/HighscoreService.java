package com.school.project.wahr_oder_watt.service;

import com.school.project.wahr_oder_watt.dto.HighscoreDto;
import com.school.project.wahr_oder_watt.model.Highscore;
import com.school.project.wahr_oder_watt.model.User;
import com.school.project.wahr_oder_watt.repository.HighscoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HighscoreService {

  private final HighscoreRepository highscoreRepository;

  @Transactional(readOnly = true)
  public List<HighscoreDto> findAllDtosSortedByScoreDesc() {
    return highscoreRepository.findAllDtoOrderByScoreDesc();
  }

  private String resolveUsername(User u) {
    if (u == null) return "Unbekannt";
    if (u.getUsername() != null && !u.getUsername().isBlank()) return u.getUsername();
    return u.getId() != null ? "User#" + u.getId() : "Unbekannt";
  }

  @Transactional(readOnly = true)
  public Highscore findById(Long id) {
    return highscoreRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Highscore nicht gefunden: " + id));
  }

  @Transactional
  public Highscore save(Highscore highscore) {
    return highscoreRepository.save(highscore);
  }

  @Transactional
  public Highscore update(Long id, Highscore updated) {
    Highscore existing = findById(id);
    existing.setScore(updated.getScore());
    existing.setPlayer(updated.getPlayer());
    return highscoreRepository.save(existing);
  }

  @Transactional
  public void delete(Long id) {
    highscoreRepository.deleteById(id);
  }
}