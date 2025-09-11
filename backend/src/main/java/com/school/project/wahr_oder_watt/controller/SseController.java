package com.school.project.wahr_oder_watt.controller;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.stereotype.Controller;
import com.school.project.wahr_oder_watt.dto.GameEventDto;

@Controller
public class SseController {
  private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

  @GetMapping("/sse/game")
  public SseEmitter stream(@RequestParam String userId) {
    SseEmitter emitter = new SseEmitter(0L);
    emitters.put(userId, emitter);
    emitter.onCompletion(() -> emitters.remove(userId));
    emitter.onTimeout(() -> emitters.remove(userId));
    return emitter;
  }

  public void sendToUser(String userId, GameEventDto event) {
    SseEmitter emitter = emitters.get(userId);
    if (emitter == null) return;
    try {
      emitter.send(event, MediaType.APPLICATION_JSON);
    } catch (IOException e) {
      emitters.remove(userId);
    }
  }
}