package com.school.project.wahr_oder_watt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class DeviceWithAttributesDto {
  private final Long id;
  private final String description;
  private final String imageUrl;
  private final List<AttributeDto> attributes;
}