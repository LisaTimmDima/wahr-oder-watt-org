package com.school.project.wahr_oder_watt.service;

import com.school.project.wahr_oder_watt.dto.AttributeDto;
import com.school.project.wahr_oder_watt.dto.DeviceWithAttributesDto;
import com.school.project.wahr_oder_watt.model.Attribute;
import com.school.project.wahr_oder_watt.model.Device;
import com.school.project.wahr_oder_watt.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceQueryService {

  private final DeviceRepository deviceRepository;

  public List<DeviceWithAttributesDto> findAllWithAttributes() {
    List<Device> devices = deviceRepository.findAllWithAttributes();
    return devices.stream()
        .map(this::toDto)
        .toList();
  }

  private DeviceWithAttributesDto toDto(Device d) {
    List<AttributeDto> attrs = d.getAttributes().stream()
        .sorted(Comparator.comparing(Attribute::getId))
        .map(a -> new AttributeDto(a.getId(), a.getDescription(), a.getImageUrl()))
        .toList();

    return new DeviceWithAttributesDto(
        d.getId(),
        d.getDescription(),
        d.getImageUrl(),
        attrs
    );
  }
}