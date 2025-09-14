package com.school.project.wahr_oder_watt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChallengeResponse {
  private Long duelId;
  private String challenger;
  private int level;
}
