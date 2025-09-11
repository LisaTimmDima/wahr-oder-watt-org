// java
package com.school.project.wahr_oder_watt.service;

import java.util.*;
import java.util.concurrent.*;
import org.springframework.stereotype.Service;
import com.school.project.wahr_oder_watt.controller.SseController;
import com.school.project.wahr_oder_watt.dto.*;

@Service
public class GameService {
  private final Map<String, GameSession> sessions = new ConcurrentHashMap<>();
  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);
  private final SseController sse;

  public GameService(SseController sse) {
    this.sse = sse;
  }

  public GameSession createChallenge(ChallengeRequestDto req) {
    String gameId = UUID.randomUUID().toString();
    GameSession s = new GameSession(gameId, req.getChallengerId(), req.getOpponentId(), req.getMode());
    sessions.put(gameId, s);

    // notify opponent
    sse.sendToUser(req.getOpponentId(), new GameEventDto("challenge-request", Map.of(
      "gameId", gameId,
      "challengerId", req.getChallengerId(),
      "challengerUsername", req.getChallengerUsername(),
      "mode", req.getMode()
    )));

    // notify challenger that request was sent (frontend zeigt "Anfrage gesendet...")
    sse.sendToUser(req.getChallengerId(), new GameEventDto("challenge-sent", Map.of(
      "gameId", gameId,
      "opponentId", req.getOpponentId(),
      "mode", req.getMode()
    )));

    // schedule 30s timeout to auto-decline
    scheduler.schedule(() -> {
      GameSession current = sessions.get(gameId);
      if (current != null && current.getState() == GameSession.State.PENDING) {
        current.setState(GameSession.State.CANCELLED);
        sse.sendToUser(req.getChallengerId(), new GameEventDto("challenge-declined", Map.of("reason", "timeout")));
        sessions.remove(gameId);
      }
    }, 30, TimeUnit.SECONDS);

    return s;
  }

  public void handleChallengeResponse(String gameId, ChallengeResponseDto resp) {
    GameSession s = sessions.get(gameId);
    if (s == null) return;
    if (!resp.isAccept()) {
      s.setState(GameSession.State.CANCELLED);
      sse.sendToUser(s.getChallengerId(), new GameEventDto("challenge-declined", Map.of("reason", "rejected")));
      sessions.remove(gameId);
      return;
    }

    // accepted -> start game
    s.setState(GameSession.State.ACTIVE);
    s.loadQuestions(); // fills internal queue from DB (stub)
    // prepare first question in expected frontend shape
    GameSession.QuestionDto first = s.peekQuestionDto();
    sse.sendToUser(s.getChallengerId(), new GameEventDto("game-start", Map.of("gameId", gameId, "firstQuestion", first)));
    sse.sendToUser(s.getOpponentId(), new GameEventDto("game-start", Map.of("gameId", gameId, "firstQuestion", first)));

    // start mode timer when needed (speedrun)
    if ("SPEEDRUN".equalsIgnoreCase(s.getMode())) {
      scheduler.schedule(() -> endGameDueToTime(gameId), 60, TimeUnit.SECONDS);
    }
  }

  public void submitAnswers(String gameId, SubmitAnswersDto dto) {
    GameSession s = sessions.get(gameId);
    if (s == null || s.getState() != GameSession.State.ACTIVE) return;

    s.submitPlayerAnswers(dto.getPlayerId(), dto.getRound(), dto.getAnswers(), dto.isTimeout());

    // first submit per round -> notify opponent to lock attributes
    sse.sendToUser(s.getOtherPlayerId(dto.getPlayerId()), new GameEventDto("attribute-locked", Map.of("answerIds", dto.getAnswers())));

    // check round completion
    if (s.isRoundComplete(dto.getRound())) {
      var result = s.evaluateRound(dto.getRound());
      sse.sendToUser(s.getChallengerId(), new GameEventDto("round-result", result));
      sse.sendToUser(s.getOpponentId(), new GameEventDto("round-result", result));

      if (s.isFinished()) {
        finishGame(gameId);
      } else {
        s.nextQuestion();
        GameSession.QuestionDto next = s.peekQuestionDto();
        sse.sendToUser(s.getChallengerId(), new GameEventDto("question", Map.of("question", next)));
        sse.sendToUser(s.getOpponentId(), new GameEventDto("question", Map.of("question", next)));
      }
    }
  }

  private void endGameDueToTime(String gameId) {
    GameSession s = sessions.get(gameId);
    if (s == null) return;
    finishGame(gameId);
  }

  private void finishGame(String gameId) {
    GameSession s = sessions.remove(gameId);
    if (s == null) return;
    var summary = s.buildSummary();
    sse.sendToUser(s.getChallengerId(), new GameEventDto("game-end", summary));
    sse.sendToUser(s.getOpponentId(), new GameEventDto("game-end", summary));
    // persist highscores etc. (stub)
  }
}