package com.school.project.wahr_oder_watt.service;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class GameSession {
  public enum State { PENDING, ACTIVE, CANCELLED }
  private final String gameId;
  private final String challengerId;
  private final String opponentId;
  private final String mode;
  private State state = State.PENDING;
  private int challengerScore = 0;
  private int opponentScore = 0;
  private int round = 1;
  private final int maxRounds = 5;
  private final Queue<QuestionDto> questions = new ArrayDeque<>();
  private final Map<Integer, Map<String,List<String>>> submissions = new HashMap<>();

  public GameSession(String gameId, String c, String o, String mode){
    this.gameId = gameId; this.challengerId = c; this.opponentId = o; this.mode = mode;
  }
  public String getGameId(){return gameId;}
  public String getChallengerId(){return challengerId;}
  public String getOpponentId(){return opponentId;}
  public String getMode(){return mode;}
  public State getState(){return state;}
  public void setState(State s){this.state = s;}
  public String getOtherPlayerId(String id){ return id.equals(challengerId) ? opponentId : challengerId; }

  public void loadQuestions(){
    // stub: fill with 5 dummy questions in the shape frontend expects
    for(int i=0;i<maxRounds;i++){
      String qId = "q" + i;
      Item item = new Item("Frage " + (i+1), pickIcon(i));
      List<Answer> answers = List.of(
        new Answer(qId + "_a", "🔵", "Antwort A"),
        new Answer(qId + "_b", "🟢", "Antwort B"),
        new Answer(qId + "_c", "🟡", "Antwort C"),
        new Answer(qId + "_d", "🔴", "Antwort D")
      );
      // pick two correct randomly
      List<String> correct = pickTwoCorrect(answers);
      questions.add(new QuestionDto(qId, item, answers, correct));
    }
  }

  private String pickIcon(int i) {
    String[] icons = {"❓","⚡","🌊","🔥","🌟"};
    return icons[i % icons.length];
  }

  private List<String> pickTwoCorrect(List<Answer> answers) {
    int n = answers.size();
    Set<Integer> picked = new HashSet<>();
    ThreadLocalRandom rnd = ThreadLocalRandom.current();
    while (picked.size() < 2) picked.add(rnd.nextInt(n));
    List<String> res = new ArrayList<>();
    for (int idx : picked) res.add(answers.get(idx).getId());
    return res;
  }

  public QuestionDto peekQuestionDto(){ return questions.peek(); }
  public void nextQuestion(){ questions.poll(); round++; }

  public void submitPlayerAnswers(String playerId, int round, List<String> answers, boolean timeout){
    submissions.computeIfAbsent(round, r -> new HashMap<>()).put(playerId, answers == null ? List.of() : answers);
  }

  public boolean isRoundComplete(int roundIndex){
    var m = submissions.get(roundIndex);
    if (m == null) return false;
    return m.size() == 2;
  }

  public Map<String,Object> evaluateRound(int roundIndex){
    Map<String,List<String>> map = submissions.getOrDefault(roundIndex, Map.of());
    QuestionDto q = questions.peek(); // note: evaluation uses current question snapshot - ensure ordering in GameService
    List<String> qCorrect = q != null ? q.getCorrectAnswers() : List.of();
    int scoreCh = 0, scoreOp = 0;
    var ch = map.getOrDefault(challengerId, List.of());
    var op = map.getOrDefault(opponentId, List.of());
    for(String a: ch) if (qCorrect.contains(a)) scoreCh++;
    for(String a: op) if (qCorrect.contains(a)) scoreOp++;
    challengerScore += scoreCh;
    opponentScore += scoreOp;
    return Map.of("round", roundIndex, "scores", Map.of(challengerId, scoreCh, opponentId, scoreOp));
  }

  public boolean isFinished(){
    return round >= maxRounds || questions.isEmpty();
  }

  public Map<String,Object> buildSummary(){
    String winner = challengerScore > opponentScore ? challengerId : (opponentScore > challengerScore ? opponentId : null);
    return Map.of("winner", winner, "scores", Map.of(challengerId, challengerScore, opponentId, opponentScore));
  }

  // DTOs used for SSE payloads - structured to match frontend expectation
  public static class QuestionDto {
    private final String id;
    private final Item item;
    private final List<Answer> answers;
    private final List<String> correctAnswers;
    public QuestionDto(String id, Item item, List<Answer> answers, List<String> correctAnswers){
      this.id = id; this.item = item; this.answers = answers; this.correctAnswers = correctAnswers;
    }
    public String getId(){ return id; }
    public Item getItem(){ return item; }
    public List<Answer> getAnswers(){ return answers; }
    public List<String> getCorrectAnswers(){ return correctAnswers; }
  }

  public static class Item {
    private final String name;
    private final String icon;
    public Item(String name, String icon){ this.name = name; this.icon = icon; }
    public String getName(){ return name; }
    public String getIcon(){ return icon; }
  }

  public static class Answer {
    private final String id;
    private final String icon;
    private final String text;
    public Answer(String id, String icon, String text){ this.id = id; this.icon = icon; this.text = text; }
    public String getId(){ return id; }
    public String getIcon(){ return icon; }
    public String getText(){ return text; }
  }
}