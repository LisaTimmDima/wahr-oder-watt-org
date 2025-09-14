package com.school.project.wahr_oder_watt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;
import java.util.List;

@Getter @Setter @AllArgsConstructor
public class DuelFullDto {
  private Long id;
  private Long player1Id;
  private Long player2Id;
  private String mode;
  private String status;
  private Date playtime;
  private List<DuelRoundDto> rounds;
}