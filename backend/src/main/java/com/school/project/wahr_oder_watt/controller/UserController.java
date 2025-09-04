package com.school.project.wahr_oder_watt.controller;

import com.school.project.wahr_oder_watt.model.User;
import com.school.project.wahr_oder_watt.service.UserService;
import com.school.project.wahr_oder_watt.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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

  @GetMapping("/available")
  public ResponseEntity<List<User>> getAvailableUsers() {
    return ResponseEntity.ok(
        userService.findAll().stream()
            .filter(User::isEnabled) // Voraussetzung: Methode/Flag existiert
            .toList()
    );
  }
/**
   * Gibt den aktuell angemeldeten Benutzer zurück.
   */
@GetMapping("/me")
@ResponseStatus(HttpStatus.OK)
public Map<String, Object> me(Authentication authentication) {
  Map<String, Object> dto = new HashMap<>();
  if (authentication == null) {
    dto.put("id", 0);
    dto.put("username", "unknown");
    return dto;
  }
  User user = userService.findByUsername(authentication.getName());
  if (user == null) {
    dto.put("id", 0);
    dto.put("username", "unknown");
    return dto;
  }
  dto.put("id", user.getId());
  dto.put("username", user.getUsername());
  return dto;
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