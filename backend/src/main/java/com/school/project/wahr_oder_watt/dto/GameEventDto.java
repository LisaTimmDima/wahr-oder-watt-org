// java
package com.school.project.wahr_oder_watt.dto;

import java.util.Map;

public class GameEventDto {
  private String type;
  private Map<String, Object> payload;
  public GameEventDto(String type, Map<String,Object> payload){
    this.type = type; this.payload = payload;
  }
  public String getType(){return type;}
  public Map<String,Object> getPayload(){return payload;}
}