package com.school.project.wahr_oder_watt.service;

import com.school.project.wahr_oder_watt.dto.*;
import com.school.project.wahr_oder_watt.model.Duel;
import com.school.project.wahr_oder_watt.model.DuelRound;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DuelDtoMapper {

  private final DeviceQueryService deviceQueryService;

  public DuelFullDto toDto(Duel duel) {
    List<DuelRoundDto> roundDtos = duel.getRounds() == null ? List.of() :
        duel.getRounds().stream().map(round -> {
          DeviceWithAttributesDto deviceDto = deviceQueryService.findAllWithAttributes().stream()
              .filter(d -> d.getId().equals(round.getDevice().getId()))
              .findFirst().orElse(null);
          return new DuelRoundDto(
              round.getId(),
              round.getRoundNumber(),
              deviceDto,
              round.getAllAttributes(),
              round.getVisibleAttributes(),
              round.getLockedAttributes()
          );
        }).toList();

    return new DuelFullDto(
        duel.getId(),
        duel.getPlayer1().getId(),
        duel.getPlayer2().getId(),
        duel.getMode().name(),
        duel.getStatus().name(),
        duel.getPlaytime(),
        roundDtos
    );
  }
}