package com.school.project.wahr_oder_watt.controller;

import com.school.project.wahr_oder_watt.dto.DeviceWithAttributesDto;
import com.school.project.wahr_oder_watt.service.DeviceQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devicesattributes")
@RequiredArgsConstructor
public class DeviceQueryController {

  private final DeviceQueryService deviceQueryService;

  @GetMapping("/with-attributes")
  public ResponseEntity<List<DeviceWithAttributesDto>> getDevicesWithAttributes() {
    return ResponseEntity.ok(deviceQueryService.findAllWithAttributes());
  }
}
