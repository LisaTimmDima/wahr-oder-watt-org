package com.school.project.wahr_oder_watt.controller;

import com.school.project.wahr_oder_watt.model.User;
import com.school.project.wahr_oder_watt.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST-Controller zur Verwaltung von Benutzern.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  /**
   * Gibt alle Benutzer zurück.
   */
  @GetMapping
  public ResponseEntity<List<User>> getAllUsers() {
    return ResponseEntity.ok(userService.findAll());
  }

  /**
   * Gibt einen Benutzer anhand der ID zurück.
   */
  @GetMapping("/{id}")
  public ResponseEntity<User> getUser(@PathVariable Long id) {
    User user = userService.findById(id);
    return ResponseEntity.ok(user);
  }

  /**
   * Erstellt einen neuen Benutzer.
   */
  @PostMapping
  public ResponseEntity<User> createUser(@RequestBody User user) {
    User created = userService.save(user);
    return ResponseEntity.ok(created);
  }

  /**
   * Aktualisiert einen bestehenden Benutzer.
   */
  @PutMapping("/{id}")
  public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
    User updated = userService.update(id, user);
    return ResponseEntity.ok(updated);
  }

  /**
   * Löscht einen Benutzer anhand der ID.
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    userService.delete(id);
    return ResponseEntity.noContent().build();
  }
}