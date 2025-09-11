package com.school.project.wahr_oder_watt.repository;

import com.school.project.wahr_oder_watt.model.Device;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Repository interface for managing Device entities.
 */
public interface DeviceRepository extends JpaRepository<Device, Long> {

  @Query("select distinct d from Device d left join fetch d.attributes")
  List<Device> findAllWithAttributes();
  public List<Device> findAll();
}
