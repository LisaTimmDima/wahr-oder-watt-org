package com.school.project.wahr_oder_watt.repository;

import com.school.project.wahr_oder_watt.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing Device entities.
 */
public interface DeviceRepository extends JpaRepository<Device, Long> {
}
