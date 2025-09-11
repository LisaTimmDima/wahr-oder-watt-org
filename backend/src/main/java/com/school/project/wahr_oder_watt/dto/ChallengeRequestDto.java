package com.school.project.wahr_oder_watt.dto;

public class ChallengeRequestDto {
  private String challengerId;
  private String challengerUsername;
  private String opponentId;
  private String mode; // "SPEEDRUN" or "RUNDE"

  // getters / setters
  public String getChallengerId(){return challengerId;}
  public void setChallengerId(String v){this.challengerId=v;}
  public String getChallengerUsername(){return challengerUsername;}
  public void setChallengerUsername(String v){this.challengerUsername=v;}
  public String getOpponentId(){return opponentId;}
  public void setOpponentId(String v){this.opponentId=v;}
  public String getMode(){return mode;}
  public void setMode(String v){this.mode=v;}
}