package uk.ac.soton.comp1206.scene;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import uk.ac.soton.comp1206.ui.GameWindow;

public class LocalChallengeScene extends ChallengeScene {

  /**
   * Create a new Single Player challenge scene
   *
   * @param gameWindow the Game Window
   */
  public LocalChallengeScene(GameWindow gameWindow) {
    super(gameWindow);
  }

  @Override
  public void build() {
    super.build();
    /*
    top override
     */
    var statsBox = new HBox();
    statsBox.setAlignment(Pos.CENTER);
    statsBox.setSpacing(20);
    statsBox.getChildren().addAll(scoreBox, levelBox, livesBox, multiplierBox);

    var topBox = new VBox();
    topBox.setAlignment(Pos.CENTER);
    topBox.getChildren().addAll(title, statsBox);

    mainPane.setTop(topBox);

        /*
    left override
     */
    var powerUp = new Text("Power Ups:");
    powerUp.getStyleClass().add("powerUp");

    var getLives = new Text("+1 Life");
    var getLivesCost = new Text("Cost: 100");
    getLivesCost.getStyleClass().add("option2-button");
    getLives.getStyleClass().add("option1-button");
    getLives.setOnMouseClicked(e -> {
      Boolean changed = game.powerLives();
      if(!changed) {
        getLives.getStyleClass().remove("option1-button");
        getLives.getStyleClass().add("noPower");
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2),change->{
          getLives.getStyleClass().remove("noPower");
          getLives.getStyleClass().add("option1-button");
        }));
        timeline.play();
      }
    });

    var getLivesContainer = new VBox();
    getLivesContainer.setSpacing(5);
    getLivesContainer.setAlignment(Pos.CENTER);
    getLivesContainer.getChildren().addAll(getLives, getLivesCost);

    var leftContainer = new VBox();
    leftContainer.setPadding(new Insets(10));
    leftContainer.setSpacing(20);
    leftContainer.setAlignment(Pos.TOP_CENTER);

    leftContainer.getChildren().addAll(powerUp, getLivesContainer);

    mainPane.setLeft(leftContainer);

    /*
    right override
     */
    leftBox.setAlignment(Pos.CENTER);
    leftBox.setPadding(new Insets(20, 20, 20, 20));
    mainPane.setRight(leftBox);


  }
}
