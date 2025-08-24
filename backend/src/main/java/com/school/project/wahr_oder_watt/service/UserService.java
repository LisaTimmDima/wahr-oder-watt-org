package com.school.project.wahr_oder_watt.service;

import com.school.project.wahr_oder_watt.model.User;
import com.school.project.wahr_oder_watt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service zur Verwaltung von Benutzern.
 */
@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  /**
   * Gibt alle Benutzer zurück.
   *
   * @return Liste aller Benutzer.
   */
  public List<User> findAll() {
    return userRepository.findAll();
  }

  /**
   * Sucht einen Benutzer anhand der ID.
   *
   * @param id ID des gesuchten Benutzers.
   * @return Benutzer mit der angegebenen ID.
   * @throws RuntimeException falls der Benutzer nicht gefunden wird.
   */
  public User findById(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden mit ID: " + id));
  }

  /**
   * Speichert einen neuen Benutzer.
   *
   * @param user Der zu speichernde Benutzer.
   * @return Der gespeicherte Benutzer.
   */
  public User save(User user) {
    return userRepository.save(user);
  }

  /**
   * Aktualisiert einen bestehenden Benutzer.
   *
   * @param id ID des zu aktualisierenden Benutzers.
   * @param user Neue Benutzerdaten.
   * @return Der aktualisierte Benutzer.
   * @throws RuntimeException falls der Benutzer nicht gefunden wird.
   */
  public User update(Long id, User user) {
    User existingUser = findById(id);
    existingUser.setUsername(user.getUsername());
    existingUser.setPassword(user.getPassword());
    existingUser.setAdmin(user.isAdmin());
    existingUser.setEnabled(user.isEnabled());
    existingUser.setScore(user.getScore());
    return userRepository.save(existingUser);
  }

  /**
   * Löscht einen Benutzer anhand der ID.
   *
   * @param id ID des zu löschenden Benutzers.
   */
  public void delete(Long id) {
    userRepository.deleteById(id);
  }
}
