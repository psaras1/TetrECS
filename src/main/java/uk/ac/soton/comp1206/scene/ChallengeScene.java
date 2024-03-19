package uk.ac.soton.comp1206.scene;

import java.util.ArrayList;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.game.Grid;
import uk.ac.soton.comp1206.game.Multimedia;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;


/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the
 * game.
 */
public class ChallengeScene extends BaseScene {

  private static final Logger logger = LogManager.getLogger(MenuScene.class);
  protected Game game;
  private Multimedia gameMusic = new Multimedia();
  /*
   * Labels for the game stats
   */
  private Label scoreLabel;
  private Label levelLabel;
  private Label livesLabel;
  private Label multiplierLabel;
  private PieceBoard currentPiece,nextPiece;

  /**
   * Create a new Single Player challenge scene
   *
   * @param gameWindow the Game Window
   */
  public ChallengeScene(GameWindow gameWindow) {
    super(gameWindow);
    this.scoreLabel = new Label();
    this.levelLabel = new Label();
    this.livesLabel = new Label();
    this.multiplierLabel = new Label();
    logger.info("Creating Challenge Scene");
  }

  /**
   * Bind the properties of the game to the UI
   */
  public void bindProperties() {
    scoreLabel.textProperty().bind(Bindings.concat("Score: ", game.getScore().asString()));
    levelLabel.textProperty().bind(Bindings.concat("Level: ", game.getLevel().asString()));
    livesLabel.textProperty().bind(Bindings.concat("Lives: ", game.getLives().asString()));
    multiplierLabel.textProperty()
        .bind(Bindings.concat("Multiplier: ", game.getMultiplier().asString()));
  }

  /**
   * Build the Challenge window
   */
  @Override
  public void build() {
    logger.info("Building " + this.getClass().getName());
    setupGame();
//        Implement background music in the game scene
    this.gameMusic.playBackgroundMusic("/music/game.wav");
    bindProperties();

    root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

    var mainPane = new BorderPane();

    var challengePane = new StackPane();

    challengePane.setMaxWidth(gameWindow.getWidth());
    challengePane.setMaxHeight(gameWindow.getHeight());
    challengePane.getStyleClass().add("challenge-background");
    root.getChildren().add(challengePane);
//        Build the User Interface
    var stats = new HBox(135);
    stats.setAlignment(Pos.CENTER);
    BorderPane.setMargin(stats, new Insets(10, 0, 0, 0));
    mainPane.setTop(stats);
    stats.getChildren().addAll(scoreLabel, levelLabel, livesLabel, multiplierLabel);

    //Style the stats through an array list to avoid repetition
    ArrayList<Label> labels = new ArrayList<>();
    labels.add(scoreLabel);
    labels.add(levelLabel);
    labels.add(livesLabel);
    labels.add(multiplierLabel);
    for (Label label : labels) {
      //Style the labels
      label.setStyle(
          "-fx-border-color: white; -fx-border-width: 2; -fx-text-fill: white; -fx-padding: 10;"
              + " -fx-border-radius: 5;-fx-font-family: 'Arial'; -fx-font-weight: bold;");
    }

    //Create the game board
    var board = new GameBoard(game.getGrid(), gameWindow.getWidth() / 2, gameWindow.getWidth() / 2);
    mainPane.setCenter(board);
    challengePane.getChildren().add(mainPane);

    board.setOnBlockClick(this::blockClicked); //calls blockClicked from GameBoard class

    //Create a mute button
    var muteButton = createMuteButton(gameMusic, "/music/game.wav");
    AnchorPane muteButtonPane = new AnchorPane();
    muteButtonPane.getChildren().add(muteButton);
    AnchorPane.setLeftAnchor(muteButton, 5.0);
    AnchorPane.setBottomAnchor(muteButton, 5.0);
    muteButtonPane.setPickOnBounds(false);
    root.getChildren().add(muteButtonPane);

    //PieceBoard object, displays current and incoming pieces
    var leftContainer = new VBox();
    currentPiece = new PieceBoard(100, 100);
    var currentPieceLabel = new Label("Current Piece:");
    nextPiece = new PieceBoard(80,80);
    var nextPieceLabel = new Label("Next Piece:");
    currentPieceLabel.setStyle("-fx-text-fill: white; -fx-font-family: 'Arial'; -fx-font-weight: bold;");
    nextPieceLabel.setStyle("-fx-text-fill: white; -fx-font-family: 'Arial'; -fx-font-weight: bold;");
    leftContainer.getChildren().addAll(currentPieceLabel,currentPiece, nextPieceLabel,nextPiece);
    leftContainer.setAlignment(Pos.CENTER);
    leftContainer.setPadding(new Insets(20));
    mainPane.setLeft(leftContainer);

  }

  /**
   * Update the game board with the next piece
   *
   * @param piece the next piece
   */
  protected void nextPiece(GamePiece piece) {
    currentPiece.displayPiece(piece);
    nextPiece.displayPiece(game.nextPiece);

  }


  /**
   * Handle when a block is clicked
   *
   * @param gameBlock the Game Block that was clocked
   */
  private void blockClicked(
      GameBlock gameBlock) { //calls blockClicked method on game, passing through current gameBlock
    game.blockClicked(gameBlock);
  }

  /**
   * Setup the game object and model
   */
  public void setupGame() {
    logger.info("Starting a new challenge");

    //Start new game
    game = new Game(5, 5);
  }

  /**
   * Initialise the scene and start the game
   */
  @Override
  public void initialise() {
    logger.info("Initialising Challenge");
    //Set the next piece listener
    game.setNextPieceListener(this::nextPiece); //next piece passed as GamePiece to interface
    game.start();

  }

}
