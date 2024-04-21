package uk.ac.soton.comp1206.component;

import java.util.ArrayList;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Transition;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import javafx.util.Pair;

/**
 * The ScoreList class is a custom JavaFX component that displays a list of scores in a VBox. It is
 * used to display the scores of the players in the game in a readable manner.
 */
public class ScoreList extends VBox {
  /*
  Temporarily stores scores
   */

  protected SimpleListProperty<Pair<String, Integer>> scores = new SimpleListProperty<>();
  protected StringProperty name = new SimpleStringProperty();
  public Text playerName;
  public Text playerScore;
  /*
  Used to loop through the scores and apply animation to them
   */
  protected ArrayList<HBox> scoresAnimated = new ArrayList<>();

  /*
  Constructor for the ScoreList class. It sets the style of the component and adds a listener to the scores property to update the list when the scores change.
   */
  public ScoreList() {
    getStyleClass().add("scorelist");
    scores.addListener(((InvalidationListener) observable -> this.updateList()));
    name.addListener(e -> this.updateList());
  }

  /**
   * Updates the list of scores displayed in the component. It loops through the scores and creates
   * a new HBox for each score, containing the player's name and score. It then adds the HBox to the
   * list of scores to be animated and adds the HBox to the VBox. Finally, it calls the reveal
   * method to animate the scores.
   */
  public void updateList() {
    int i = 0;
    scoresAnimated.clear();
    getChildren().clear();

    for (Pair<String, Integer> pair : scores) {
      if (i > 10) {
        break;
      }
      HBox indLine = new HBox();
      indLine.setAlignment(Pos.CENTER);

      playerName = new Text(pair.getKey() + ": ");
      playerName.getStyleClass().add("player-name");

      playerName.setTextAlignment(TextAlignment.CENTER);
      HBox.setHgrow(playerName, javafx.scene.layout.Priority.ALWAYS);

      playerScore = new Text(pair.getValue().toString());
      playerScore.getStyleClass().add("player-score");

      playerScore.setTextAlignment(TextAlignment.CENTER);
      HBox.setHgrow(playerScore, javafx.scene.layout.Priority.ALWAYS);

      indLine.getChildren().addAll(playerName, playerScore);
      scoresAnimated.add(indLine);

      i++;
    }
    for (HBox line : scoresAnimated) {
      getChildren().add(line);
    }
    reveal();


  }

  /**
   * Animates the scores in the list by fading them in.
   */
  public void reveal() {
    ArrayList<Transition> transitions = new ArrayList<>();
    for (HBox line : scoresAnimated) {
      FadeTransition ft = new FadeTransition(new Duration(1000), line);
      ft.setFromValue(0);
      ft.setToValue(1);
      transitions.add(ft);
    }
    SequentialTransition st = new SequentialTransition(transitions.toArray(Animation[]::new));
    st.play();
  }

  /*
  Accessor methods for the scores and name properties.
   */
  public ListProperty<Pair<String, Integer>> returnScores() {
    return scores;
  }

  public StringProperty returnName() {
    return name;
  }
}
