package uk.ac.soton.comp1206.component;

import java.util.ArrayList;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.scene.InstructionsScene;

public class ScoreList extends VBox {
  private static final Logger logger = LogManager.getLogger(ScoreList.class);
  /* Achieved scores */
  public final SimpleListProperty<Pair<String,Integer>> scores;
  /* Name of the player */
  public final StringProperty name;
  /* List of score boxes */
  private final ArrayList<VBox> scoreBoxes = new ArrayList<>();

  public ScoreList(){
    getStyleClass().add("score-list");
    scores = new SimpleListProperty<>();
    scores.addListener((ListChangeListener<? super Pair<String, Integer>>) c -> {
      scoreBoxes.clear();
      getChildren().clear();
      for(Pair<String, Integer> score : scores){
        var scoreBox = new VBox();
        scoreBox.setAlignment(Pos.CENTER);
        scoreBoxes.add(scoreBox);
        getChildren().add(scoreBox);
      }
    });


    name = new SimpleStringProperty();
    logger.info("Creating ScoreList");
  }

}
