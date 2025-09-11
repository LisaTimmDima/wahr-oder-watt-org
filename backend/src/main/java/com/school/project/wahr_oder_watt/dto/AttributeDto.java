package com.school.project.wahr_oder_watt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AttributeDto {
  private final Long id;
  private final String description;
  private final String imageUrl;
}