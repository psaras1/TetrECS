package uk.ac.soton.comp1206.component;

import java.util.ArrayList;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Leaderboard class is a custom JavaFX component that displays a list of scores in a VBox. It
 * is used to display the scores of the players in the game in a readable manner. It extends the
 * ScoreList class and adds a method to handle dead players.
 */
public class Leaderboard extends ScoreList {

  private static final Logger logger = LogManager.getLogger(Leaderboard.class);
  /*Stores the names of dead players*/
  public ArrayList<String> deadPlayers = new ArrayList<>();

  /*
  Constructor inherited from the ScoreList class
   */
  public Leaderboard() {
    super();
  }

  /*
  Method to handle dead players. It adds the name of the dead player to the deadPlayers list and updates the list.
   */
  public void died(String name) {
    this.deadPlayers.add(name);
    logger.info("First dead player: {}", deadPlayers.get(0));
    this.updateList();
  }

  public ListProperty<Pair<String, Integer>> scoreProperty() {
    return this.scores;
  }

  public StringProperty nameProperty() {
    return this.name;
  }

  /*
   * Updates the list of scores displayed in the component. It loops through the scores and creates

   * Makes the names and scores of dead players red
   */
  @Override
  public void updateList() {
    super.updateList();
    logger.info("Dead players: {}", deadPlayers.size());
    for (Node node : scoresAnimated) {
      HBox indLine = (HBox) node;
      Text playerName = (Text) indLine.getChildren().get(0);
      String name = playerName.getText();
      name = name.substring(0, name.length() - 2);
      logger.info("Potential zombie: {}", name);
      if (deadPlayers.contains(name)) {
        logger.info("Dead player found: {}", name);
        Platform.runLater(() -> {
          playerName.getStyleClass().add("player-name-dead");
          playerScore.getStyleClass().add("player-score-dead");
        });
      }

    }
  }

}
