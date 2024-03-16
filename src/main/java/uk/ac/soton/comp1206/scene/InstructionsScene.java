package uk.ac.soton.comp1206.scene;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The instructions scene of the game. Provides a gateway to the rest of the game.
 */

public class InstructionsScene extends BaseScene {
  private static final Logger logger = LogManager.getLogger(InstructionsScene.class);

  private ImageView imageView;

  private BorderPane mainPane = new BorderPane();
  public InstructionsScene(GameWindow gameWindow){
    super(gameWindow);
    logger.info("Creating Instructions Scene");
  }

  /**
   * Initialise the scene
   * If escape is pressed, return to the menu
   */
  @Override
  public void initialise() {
    logger.info("Initialising " + this.getClass().getName());
    scene.setOnKeyPressed(e -> {
      switch (e.getCode()) {
        case ESCAPE:
          logger.info("Escape pressed, returning to menu");
          gameWindow.startMenu();
      }
    });
}


  @Override
  public void build() {
    logger.info("Building " + this.getClass().getName());
    //Create the instructions scene
    imageView = new ImageView(InstructionsScene.class.getResource("/images/suggestedControls.png").toExternalForm());
    imageView.setFitHeight(gameWindow.getHeight()-40);
    imageView.setFitWidth(gameWindow.getWidth()-40);

    root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());
    mainPane.setMaxWidth(gameWindow.getWidth());
    mainPane.setMaxHeight(gameWindow.getHeight());
    mainPane.getStyleClass().add("menu-background");

    mainPane.setCenter(imageView);
    //Add the instructions to the main pane
    var optionsBar = new HBox();
    optionsBar.setAlignment(Pos.CENTER);
    mainPane.setTop(optionsBar);
    var escape = new Button("Menu");
    escape.setOnAction(e -> gameWindow.startMenu());
    escape.getStyleClass().add("menu-button");
    optionsBar.getChildren().add(escape);

    root.getChildren().add(mainPane);
  }

}
