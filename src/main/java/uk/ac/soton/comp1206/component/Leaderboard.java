package uk.ac.soton.comp1206.component;

import java.util.ArrayList;
import javafx.beans.property.ListProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Pair;

public class Leaderboard extends ScoreList{
  protected ArrayList<String> deadPlayers = new ArrayList<>();
  public Leaderboard() {
    super();
  }
  public void died(String name){
    this.deadPlayers.add(name);
  }
  public ListProperty<Pair<String,Integer>> scoreProperty(){
    return this.scores;
  }
  public StringProperty nameProperty(){
    return this.name;
  }

}
