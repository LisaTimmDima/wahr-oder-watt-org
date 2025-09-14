package com.school.project.wahr_oder_watt.service;

import com.school.project.wahr_oder_watt.dto.ChallengeResponse;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service to manage challenges between users.
 */
@Service
public class ChallengeService {

  /**
   * Map to store pending challenges with the opponent's username as the key.
   */
  private final Map<String, ChallengeResponse> pendingChallenges = new ConcurrentHashMap<>();

  /**
   * Adds a new challenge for the specified opponent.
   *
   * @param opponentUsername the username of the opponent
   * @param challenge        the challenge response to be added
   */
  public void addChallenge(String opponentUsername, ChallengeResponse challenge) {
    pendingChallenges.put(opponentUsername, challenge);
  }

  /**
   * Finds and removes a pending challenge for the specified user.
   *
   * @param username the username of the user
   * @return the found challenge response, or null if none exists
   */
  public ChallengeResponse findPendingForUser(String username) {
    return pendingChallenges.remove(username);
  }
}
