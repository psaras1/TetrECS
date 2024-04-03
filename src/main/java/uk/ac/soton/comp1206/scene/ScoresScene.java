package uk.ac.soton.comp1206.scene;

import javafx.scene.layout.BorderPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.event.GameEndListener;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The scores scene of the game. Holds the scores of the game for the player to see.

 */
public class ScoresScene extends BaseScene implements GameEndListener {
  private Game game;
  private static final Logger logger = LogManager.getLogger(InstructionsScene.class);
  private BorderPane mainPane = new BorderPane();
  public ScoresScene(GameWindow gameWindow, Game game) {
    super(gameWindow);
    this.game = game;
    logger.info("Creating Scores Scene");
  }
  /*
  Method to handle the end of the game
   */
  public void onGameEnd() {
    logger.info("Game has ended");
    gameWindow.showScores(game);
  }

  @Override
  public void initialise() {
    logger.info("Initialising " + this.getClass().getName());
    controls();
  }
/*
Build the Scores Window
 */
  @Override
  public void build() {
    logger.info("Building " + this.getClass().getName());
    root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());
    mainPane.setMaxWidth(gameWindow.getWidth());
    mainPane.setMaxHeight(gameWindow.getHeight());
    mainPane.getStyleClass().add("menu-background");
    root.getChildren().add(mainPane);
  }
  /*
  Method to handle the keyboard controls of the scene
   */
  public void controls(){
    scene.setOnKeyPressed(e -> {
      logger.info("Key Pressed: {}" ,e.getCode());
      switch (e.getCode()) {
        case ESCAPE:
          logger.info("Escape pressed, returning to menu");
          gameWindow.startMenu();
          break;
      }
    });
  }

}
