package com.school.project.wahr_oder_watt.controller;

import com.school.project.wahr_oder_watt.dto.ChallengeRequest;
import com.school.project.wahr_oder_watt.dto.ChallengeRespond;
import com.school.project.wahr_oder_watt.dto.ChallengeResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/challenge")
public class ChallengeController {

  private final Map<String, String> challenges = new ConcurrentHashMap<>();

  @PostMapping("/send")
  public void sendChallenge(@RequestBody ChallengeRequest request) {
    challenges.put(request.getTargetPlayer(), request.getFromPlayer());
  }

  @GetMapping("/pending/{player}")
  public ResponseEntity<ChallengeResponse> getPendingChallenge(@PathVariable String player) {
    String challenger = challenges.get(player);
    if (challenger != null) {
      return ResponseEntity.ok(new ChallengeResponse(challenger));
    }
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/leave/{player}")
  public void leaveChallenge(@PathVariable String player) {
    challenges.remove(player);
    // Optional: Auch alle Challenges entfernen, bei denen der Spieler Herausforderer ist
    challenges.entrySet().removeIf(entry -> entry.getValue().equals(player));
  }

  @PostMapping("/respond")
  public void respondToChallenge(@RequestBody ChallengeRespond request) {
    challenges.remove(request.getTargetPlayer());
    // Hier ggf. weitere Logik (z.B. Duell starten)
  }
}

