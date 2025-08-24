package com.school.project.wahr_oder_watt.controller;

import com.school.project.wahr_oder_watt.model.Device;
import com.school.project.wahr_oder_watt.service.DeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST-Controller zur Verwaltung von Geräten.
 */
@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class DeviceController {

  private final DeviceService deviceService;

  /**
   * Gibt alle Geräte zurück.
   */
  @GetMapping
  public List<Device> getAllDevices() {
    return deviceService.findAll();
  }

  /**
   * Gibt ein Gerät anhand der ID zurück.
   */
  @GetMapping("/{id}")
  public ResponseEntity<Device> getDevice(@PathVariable Long id) {
    Device device = deviceService.findById(id);
    return ResponseEntity.ok(device);
  }

  /**
   * Erstellt ein neues Gerät.
   */
  @PostMapping
  public ResponseEntity<Device> createDevice(@RequestBody Device device) {
    Device savedDevice = deviceService.save(device);
    return ResponseEntity.ok(savedDevice);
  }

  /**
   * Aktualisiert ein bestehendes Gerät.
   */
  @PutMapping("/{id}")
  public ResponseEntity<Device> updateDevice(@PathVariable Long id, @RequestBody Device device) {
    Device updatedDevice = deviceService.update(id, device);
    return ResponseEntity.ok(updatedDevice);
  }

  /**
   * Löscht ein Gerät anhand der ID.
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteDevice(@PathVariable Long id) {
    deviceService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
