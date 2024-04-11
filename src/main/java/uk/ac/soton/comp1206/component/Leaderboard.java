package uk.ac.soton.comp1206.component;

import java.util.ArrayList;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Leaderboard extends ScoreList{
  private static final Logger logger = LogManager.getLogger(Leaderboard.class);
  public ArrayList<String> deadPlayers = new ArrayList<>();
  public Leaderboard() {
    super();
  }
  public void died(String name){
    this.deadPlayers.add(name);
    logger.info("First dead player: {}", deadPlayers.get(0));
    this.updateList();
  }
  public ListProperty<Pair<String,Integer>> scoreProperty(){
    return this.scores;
  }
  public StringProperty nameProperty(){
    return this.name;
  }

  @Override
  public void updateList(){
    super.updateList();
    logger.info("Dead players: {}", deadPlayers.size());
    for(Node node: scoresAnimated){
      HBox indLine = (HBox) node;
      Text playerName = (Text) indLine.getChildren().get(0);
      String name = playerName.getText();
      name = name.substring(0, name.length()-2);
      logger.info("Potential zombie: {}", name);
      if(deadPlayers.contains(name)){
        logger.info("Dead player found: {}", name);
        Platform.runLater(()->{
          playerName.getStyleClass().add("player-name-dead");
          playerScore.getStyleClass().add("player-score-dead");
        });
      }

    }
  }

}
