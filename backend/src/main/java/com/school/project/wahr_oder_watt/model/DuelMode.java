package com.school.project.wahr_oder_watt.model;

import lombok.RequiredArgsConstructor;

/**
 * Enum repräsentiert die verschiedenen Duellmodi im Spiel.
 * Es gibt zwei Modi:
 * SPEEDRUN: Dieser Modus hat beliebig viele Runden und geht insgesamt über 60 Sek.
 * RUNDENDUELL: Dieser Modus hat maximal 5 Runden, welche je maximal 10 Sek lang sind.
 */
public enum DuelMode {
  SPEEDRUN("Speedrun", 0),
  RUNDENDUELL("Rundenduell", 5);

  public final String name;
  public final int maxRounds;

  DuelMode(String name, int maxRounds) {
    this.name = name;
    this.maxRounds = maxRounds;
  }
}
