package com.school.project.wahr_oder_watt.service;

import com.school.project.wahr_oder_watt.model.Device;
import com.school.project.wahr_oder_watt.repository.DeviceRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeviceService {

  private final DeviceRepository deviceRepository;

  /**
   * Gibt alle Geräte zurück.
   *
   * @return Liste aller Geräte.
   */
  public List<Device> findAll() {
    return deviceRepository.findAll();
  }

  /**
   * Sucht ein Gerät anhand der ID.
   *
   * @param id ID des gesuchten Geräts.
   * @return Gerät mit der angegebenen ID.
   * @throws RuntimeException falls das Gerät nicht gefunden wird.
   */
  public Device findById(Long id) {
    return deviceRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Gerät nicht gefunden mit ID: " + id));
  }

  /**
   * Speichert ein neues Gerät.
   *
   * @param device Das zu speichernde Gerät.
   * @return Das gespeicherte Gerät.
   */
  public Device save(Device device) {
    return deviceRepository.save(device);
  }

  /**
   * Aktualisiert ein bestehendes Gerät.
   *
   * @param id ID des zu aktualisierenden Geräts.
   * @param device Neue Gerätedaten.
   * @return Das aktualisierte Gerät.
   * @throws RuntimeException falls das Gerät nicht gefunden wird.
   */
  public Device update(Long id, Device device) {
    Device existingDevice = findById(id);
    existingDevice.setImageUrl(device.getImageUrl());
    existingDevice.setDescription(device.getDescription());
    existingDevice.setActive(device.isActive());
    existingDevice.setAttributes(device.getAttributes());
    return deviceRepository.save(existingDevice);
  }

  /**
   * Löscht ein Gerät anhand der ID.
   *
   * @param id ID des zu löschenden Geräts.
   */
  public void delete(Long id) {
    deviceRepository.deleteById(id);
  }
}
