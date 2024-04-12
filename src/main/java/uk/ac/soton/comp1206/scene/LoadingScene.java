package uk.ac.soton.comp1206.scene;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

public class LoadingScene extends BaseScene{
  public LoadingScene(GameWindow gameWindow) {
    super(gameWindow);
  }

  @Override
  public void initialise() {

  }

  @Override
  public void build() {
    this.root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());
    StackPane stackPane = new StackPane();
    stackPane.getStyleClass().add("loading");
    Image image = new Image(getClass().getResource("/images/ECSGames.png").toString());
    ImageView ecs = new ImageView(image);
    ecs.setFitWidth(gameWindow.getWidth() / 3);
    ecs.setPreserveRatio(true);
    ecs.setOpacity(0.0);

    stackPane.getChildren().add(ecs);
    this.root.getChildren().add(stackPane);
    FadeTransition fIn = new FadeTransition(new Duration(2000.0), ecs);
    fIn.setToValue(1.0);
    FadeTransition fOut = new FadeTransition(new Duration(2000.0), ecs);
    fOut.setToValue(0.0);

    SequentialTransition sequence = new SequentialTransition(fIn, fOut);
    sequence.play();
    sequence.setOnFinished(e -> {
      gameWindow.startMenu();
    });
  }

}