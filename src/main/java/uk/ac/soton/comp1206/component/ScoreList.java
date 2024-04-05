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
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.scene.InstructionsScene;

public class ScoreList extends VBox {
  private static final Logger logger = LogManager.getLogger(ScoreList.class);
  private SimpleListProperty<Pair<String, Integer>> scores = new SimpleListProperty<>();
  StringProperty name = new SimpleStringProperty();
  ArrayList<HBox> scoresAnimated = new ArrayList<>();

  public ScoreList(){
    getStyleClass().add("scorelist");
    scores.addListener(((InvalidationListener) observable -> this.updateList()));
    name.addListener(e -> this.updateList());
  }

  public void updateList(){
    int i = 0;
    scoresAnimated.clear();
    getChildren().clear();

    for(Pair<String, Integer> pair : scores){
      if(i >10){
        break;
      }
      HBox indLine = new HBox();
      indLine.setAlignment(Pos.CENTER);

      var playerName = new Text(pair.getKey());
      playerName.setTextAlignment(TextAlignment.CENTER);
      HBox.setHgrow(playerName, javafx.scene.layout.Priority.ALWAYS);

      var playerScore = new Text(pair.getValue().toString());
      playerScore.setTextAlignment(TextAlignment.CENTER);
      HBox.setHgrow(playerScore, javafx.scene.layout.Priority.ALWAYS);

      indLine.getChildren().addAll(playerName, playerScore);
      scoresAnimated.add(indLine);

      i++;
    }
    for(HBox line : scoresAnimated){
      getChildren().add(line);
    }
    reveal();

  }
  public void reveal(){
    ArrayList<Transition> transitions = new ArrayList<>();
    for(HBox line : scoresAnimated){
      FadeTransition ft = new FadeTransition(new Duration(1000), line);
      ft.setFromValue(0);
      ft.setToValue(1);
      transitions.add(ft);
    }
    SequentialTransition st = new SequentialTransition(transitions.toArray(Animation[]::new));
    st.play();
  }
  public ListProperty<Pair<String,Integer>> returnScores(){
    return scores;
  }
  public StringProperty returnName(){
    return name;
  }

}
