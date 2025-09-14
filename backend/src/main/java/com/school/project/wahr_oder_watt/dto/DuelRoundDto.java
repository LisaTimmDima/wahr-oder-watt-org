package com.school.project.wahr_oder_watt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter @Setter @AllArgsConstructor
public class DuelRoundDto {
  private Long id;
  private int roundNumber;
  private DeviceWithAttributesDto device;
  private List<String> allAttributes;
  private List<String> visibleAttributes;
  private List<String> lockedAttributes;
}