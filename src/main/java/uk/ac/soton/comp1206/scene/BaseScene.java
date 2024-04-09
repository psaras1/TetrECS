package uk.ac.soton.comp1206.scene;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import uk.ac.soton.comp1206.game.Multimedia;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * A Base Scene used in the game. Handles common functionality between all scenes.
 */
public abstract class BaseScene {

  protected final GameWindow gameWindow;

  protected GamePane root;
  protected Scene scene;

  /**
   * Create a new scene, passing in the GameWindow the scene will be displayed in
   *
   * @param gameWindow the game window
   */
  public BaseScene(GameWindow gameWindow) {
    this.gameWindow = gameWindow;
  }

  /**
   * Initialise this scene. Called after creation
   */
  public abstract void initialise();

  /**
   * Build the layout of the scene
   */
  public abstract void build();

  /**
   * Create a new JavaFX scene using the root contained within this scene
   *
   * @return JavaFX scene
   */
  public Scene setScene() {
    var previous = gameWindow.getScene();
    Scene scene = new Scene(root, previous.getWidth(), previous.getHeight(), Color.BLACK);
    scene.getStylesheets().add(getClass().getResource("/style/game.css").toExternalForm());
    this.scene = scene;
    return scene;
  }
  abstract String getMusic();

  /**
   * Get the JavaFX scene contained inside
   *
   * @return JavaFX scene
   */
  public Scene getScene() {
    return this.scene;
  }

  protected void addMuteButton(Parent root){
    ImageView muteImageView = new ImageView();
    muteImageView.setFitHeight(30);
    muteImageView.setFitWidth(30);
    muteImageView.styleProperty().setValue("-fx-effect: dropshadow(gaussian, aqua, 10, 0, 0, 0);");
    AnchorPane muteButtonPane = new AnchorPane();

    Button muteButton = new Button("",muteImageView);
    muteButtonPane.getChildren().add(muteButton);
    AnchorPane.setLeftAnchor(muteButton, 5.0);
    AnchorPane.setBottomAnchor(muteButton, 13.0);
    muteButtonPane.setPickOnBounds(false);
    muteButton.setBackground(null);
    muteButton.setOnMouseClicked(actionEvent -> {
      if (!gameMusic.isPlaying()) {
        gameMusic.playBackgroundMusic(getMusic());
        muteImageView.setImage(unmuteImage);
      } else {
        gameMusic.stopBackgroundMusic();
        muteImageView.setImage(muteImage);
      }
    });
    root.getChildren().add(muteButtonPane);
  }
}
