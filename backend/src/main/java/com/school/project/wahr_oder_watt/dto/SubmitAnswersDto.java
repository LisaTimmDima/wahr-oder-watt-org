// java
package com.school.project.wahr_oder_watt.dto;

import java.util.List;

public class SubmitAnswersDto {
  private String playerId;
  private int round;
  private List<String> answers;
  private boolean timeout;
  // getters/setters
  public String getPlayerId(){return playerId;}
  public void setPlayerId(String p){this.playerId=p;}
  public int getRound(){return round;}
  public void setRound(int r){this.round=r;}
  public List<String> getAnswers(){return answers;}
  public void setAnswers(List<String> a){this.answers=a;}
  public boolean isTimeout(){return timeout;}
  public void setTimeout(boolean t){this.timeout=t;}
}