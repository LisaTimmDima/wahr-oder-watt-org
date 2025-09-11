package com.school.project.wahr_oder_watt.controller;

import java.util.Map;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import com.school.project.wahr_oder_watt.service.GameService;
import com.school.project.wahr_oder_watt.dto.*;

@RestController
@RequestMapping("/api/game")
public class GameController {

  @Autowired
  private GameService gameService;

  @PostMapping("/challenge")
  public ResponseEntity<?> challenge(@RequestBody ChallengeRequestDto req) {
    var session = gameService.createChallenge(req);
    return ResponseEntity.ok(Map.of("gameId", session.getGameId()));
  }

  @PostMapping("/{gameId}/challenge-response")
  public ResponseEntity<?> challengeResponse(@PathVariable String gameId, @RequestBody ChallengeResponseDto resp) {
    gameService.handleChallengeResponse(gameId, resp);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/{gameId}/submit")
  public ResponseEntity<?> submit(@PathVariable String gameId, @RequestBody SubmitAnswersDto dto) {
    gameService.submitAnswers(gameId, dto);
    return ResponseEntity.ok().build();
  }
}