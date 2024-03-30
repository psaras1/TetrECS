package uk.ac.soton.comp1206.scene;

import java.util.ArrayList;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.GamePiece;
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
  private Text scoreLabel;
  private Text levelLabel;
  private Text livesLabel;
  private Text multiplierLabel;
  private PieceBoard currentPiece, followingPiece;
  private Image muteImage = new Image(getClass().getResource("/images/mute.png").toString());
  private Image unmuteImage = new Image(getClass().getResource("/images/play.png").toString());
  private ImageView muteImageView = new ImageView(muteImage);
  private Button muteButton = new Button("", muteImageView);

  int coordX = 0, coordY = 0;
  boolean mouseMode;
  private GameBoard board;
  private GameBlock keyboardSelectedBlock = null;


  /**
   * Create a new Single Player challenge scene
   *
   * @param gameWindow the Game Window
   */
  public ChallengeScene(GameWindow gameWindow) {
    super(gameWindow);
    this.scoreLabel = new Text();
    this.levelLabel = new Text();
    this.livesLabel = new Text();
    this.multiplierLabel = new Text();
    logger.info("Creating Challenge Scene");
  }

  /**
   * Bind the properties of the game to the UI
   */
  public void bindProperties() {
    scoreLabel.textProperty().bind(Bindings.concat(game.getScore().asString()));
    levelLabel.textProperty().bind(Bindings.concat(game.getLevel().asString()));
    livesLabel.textProperty().bind(Bindings.concat(game.getLives().asString()));
    multiplierLabel.textProperty().bind(Bindings.concat(game.getMultiplier().asString()));
  }

  /**
   * Build the Challenge window
   */
  @Override
  public void build() {
    logger.info("Building " + this.getClass().getName());
    setupGame();
    // Implement background music in the game scene
    this.gameMusic.playBackgroundMusic("/music/game.wav");
    bindProperties();
    root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());
    var challengePane = new StackPane();
    challengePane.setMaxWidth(gameWindow.getWidth());
    challengePane.setMaxHeight(gameWindow.getHeight());
    challengePane.getStyleClass().add("challenge-background");
    root.getChildren().add(challengePane);
    var mainPane = new BorderPane();

    /*top*/
    //title
    var title = new Text("TetrECS");
    title.getStyleClass().add("bigtitle");
    var topBox = new HBox();
    topBox.setAlignment(Pos.CENTER);
    topBox.getChildren().add(title);
    mainPane.setTop(topBox);

    /*right*/
    var rightBox = new VBox();
    rightBox.setAlignment(Pos.CENTER);
    rightBox.setSpacing(20);
    rightBox.setPadding(new Insets(0, 15, 0, 0));
    mainPane.setRight(rightBox);

    //score
    var scoreBox = new VBox();
    scoreBox.setAlignment(Pos.CENTER);
    var scoreTitle = new Text("Score:");
    scoreTitle.getStyleClass().add("heading");
    scoreLabel.getStyleClass().add("score");
    scoreBox.getChildren().addAll(scoreTitle, scoreLabel);

    //level
    var levelBox = new VBox();
    levelBox.setAlignment(Pos.CENTER);
    var levelTitle = new Text("Level:");
    levelTitle.getStyleClass().add("heading");
    levelLabel.getStyleClass().add("level");
    levelBox.getChildren().addAll(levelTitle, levelLabel);

    //lives
    var livesBox = new VBox();
    livesBox.setAlignment(Pos.CENTER);
    var livesTitle = new Text("Lives:");
    livesTitle.getStyleClass().add("heading");
    livesLabel.getStyleClass().add("lives");
    livesBox.getChildren().addAll(livesTitle, livesLabel);

    //multiplier
    var multiplierBox = new VBox();
    multiplierBox.setAlignment(Pos.CENTER);
    var multiplierTitle = new Text("Multiplier:");
    multiplierTitle.getStyleClass().add("heading");
    multiplierLabel.getStyleClass().add("level");
    multiplierBox.getChildren().addAll(multiplierTitle, multiplierLabel);

    //add everything to the topBox
    rightBox.getChildren().addAll(scoreBox, livesBox, levelBox, multiplierBox);

    /*left*/
    var leftBox = new VBox();
    leftBox.setAlignment(Pos.CENTER);
    leftBox.setPadding(new Insets(0, 5, 0, 15));
    mainPane.setLeft(leftBox);

    currentPiece = new PieceBoard(100, 100);
    currentPiece.setPadding(new Insets(5, 0, 0, 0));
    currentPiece.blocks[1][1].setCenter();
    currentPiece.setOnMouseClicked(e -> {
      logger.info("Rotating piece{}", currentPiece);
      game.rotateCurrentPieceLeft();
    });
    var currentPieceLabel = new Text("Current Piece:");
    currentPieceLabel.getStyleClass().add("heading");

    followingPiece = new PieceBoard(80, 80);
    followingPiece.setPadding(new Insets(5, 0, 0, 0));
    followingPiece.setOnMouseClicked(e -> {
      logger.info("Swapping pieces");
      game.swapCurrentPiece();
    });
    var nextPieceLabel = new Text("Next Piece:");
    nextPieceLabel.getStyleClass().add("heading");

    leftBox.getChildren().addAll(currentPieceLabel, currentPiece, nextPieceLabel, followingPiece);
    //Create the game board
    board = new GameBoard(game.getGrid(), gameWindow.getWidth() / 2, gameWindow.getWidth() / 2);
    mainPane.setCenter(board);
    //Implement it so that right clicking on the main GameBoard or left clicking on the current piece board rotates the next piece

    //activate listener on board instance of GameBoard
    board.setOnRightClick(this::rotate);
    challengePane.getChildren().add(mainPane);

    board.setOnBlockClick(this::blockClicked); //calls blockClicked from GameBoard class

    //Create a mute button

    muteImageView.setFitHeight(30);
    muteImageView.setFitWidth(30);
    AnchorPane muteButtonPane = new AnchorPane();
    muteButtonPane.getChildren().add(muteButton);
    AnchorPane.setLeftAnchor(muteButton, 5.0);
    AnchorPane.setBottomAnchor(muteButton, 5.0);
    muteButtonPane.setPickOnBounds(false);
    root.getChildren().add(muteButtonPane);
    muteButton.setBackground(null);
    muteButton.setOnAction(actionEvent -> {
      if (!gameMusic.isPlaying()) {
        gameMusic.playBackgroundMusic("/music/game.wav");
        muteImageView.setImage(muteImage);
      } else {
        gameMusic.stopBackgroundMusic();
        muteImageView.setImage(unmuteImage);
      }
    });

  }

  /**
   * Update the game board with the next piece
   *
   * @param piece the next piece
   */
  protected void nextPiece(GamePiece piece) {
    currentPiece.displayPiece(piece);
    followingPiece.displayPiece(game.followingPiece);
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
   * Shutdown the game, resetting the score, multiplier, level and lives
   */

  public void shutdownGame() {
    game.setScore(0);
    game.setMultiplier(1);
    game.setLevel(0);
    game.setLives(1);
  }

  /**
   * Rotate the current piece
   */
  protected void rotate() {
    game.rotateCurrentPieceRight();
  }

  /**
   * Initialise the scene and start the game
   */
  @Override
  public void initialise() {
    logger.info("Initialising Challenge");
    //Set the next piece listener
    //(Update the NextPieceListener to pass the following piece as well, and use this to update the following piece board.)
    game.setNextPieceListener(this::nextPiece); //next piece passed as GamePiece to interface
    game.start();
    keyboardControls();

  }

  /**
   * Keyboard controls for the game
   */
  private void keyboardControls() {
    board.setOnMouseMoved((e) -> {
      mouseMode = true;
      /*
      Ensures that if a user moves the mouse over the board, the keyboard selected block is deselected before
      the block the mouse is over is selected
       */
      if (keyboardSelectedBlock != null) {
        board.mouseExitBlock(keyboardSelectedBlock);
        keyboardSelectedBlock = null;
      }
      coordX = board.currentBlock.getX();
      coordY = board.currentBlock.getY();
    });
    gameWindow.getScene().setOnKeyPressed(event -> {
      logger.info("Key pressed{}", event.getCode());
      switch (event.getCode()) {
        //Go back to menu
        case ESCAPE:
          logger.info("Escape pressed, returning to menu");
          shutdownGame();
          gameMusic.stopBackgroundMusic();
          gameWindow.startMenu();
          break;
        //Move the current piece left
        case M:
          if (!gameMusic.isPlaying()) {
            gameMusic.playBackgroundMusic("/music/game.wav");
            muteImageView.setImage(muteImage);
          } else {
            gameMusic.stopBackgroundMusic();
            muteImageView.setImage(unmuteImage);
          }
          break;
        case CLOSE_BRACKET, E, C:
          game.rotateCurrentPieceRight();
          break;
        //Rotate the current piece
        case OPEN_BRACKET, Q, Z:
          game.rotateCurrentPieceLeft();
          break;
        //Swap the current piece(TODO: Fix spacebar)
        case SPACE, R:
          game.swapCurrentPiece();
          break;

        case W, UP:
          if (mouseMode) {
            mouseMode = false;
            board.mouseExitBlock(board.getBlock(coordX, coordY));
            coordY = 0;
            coordX = 0;
            keyboardSelectedBlock = board.getBlock(coordX, coordY);
            board.mouseEnterBlock(board.getBlock(coordX, coordY));
          } else {
            if (coordY != 0) {
              board.mouseExitBlock(board.getBlock(coordX, coordY));
              coordY--;
              keyboardSelectedBlock = board.getBlock(coordX, coordY);
              board.mouseEnterBlock(board.getBlock(coordX, coordY));
            }
          }
          break;
        case S, DOWN:
          if (mouseMode) {
            mouseMode = false;
            board.mouseExitBlock(board.getBlock(coordX, coordY));
            coordY = 0;
            coordX = 0;
            keyboardSelectedBlock = board.getBlock(coordX, coordY);
            board.mouseEnterBlock(board.getBlock(coordX, coordY));
          } else {
            if (coordY != 4) {
              board.mouseExitBlock(board.getBlock(coordX, coordY));
              coordY++;
              keyboardSelectedBlock = board.getBlock(coordX, coordY);
              board.mouseEnterBlock(board.getBlock(coordX, coordY));
            }
          }
          break;
        case A, LEFT:
          if (mouseMode) {
            mouseMode = false;
            board.mouseExitBlock(board.getBlock(coordX, coordY));
            coordY = 0;
            coordX = 0;
            keyboardSelectedBlock = board.getBlock(coordX, coordY);
            board.mouseEnterBlock(board.getBlock(coordX, coordY));
          } else {
            if (coordX != 0) {
              board.mouseExitBlock(board.getBlock(coordX, coordY));
              coordX--;
              keyboardSelectedBlock = board.getBlock(coordX, coordY);
              board.mouseEnterBlock(board.getBlock(coordX, coordY));
            }
          }
          break;
        case D, RIGHT:
          if (mouseMode) {
            mouseMode = false;
            board.mouseExitBlock(board.getBlock(coordX, coordY));
            coordY = 0;
            coordX = 0;
            keyboardSelectedBlock = board.getBlock(coordX, coordY);
            board.mouseEnterBlock(board.getBlock(coordX, coordY));
          } else {
            if (coordX != 4) {
              board.mouseExitBlock(board.getBlock(coordX, coordY));
              coordX++;
              keyboardSelectedBlock = board.getBlock(coordX, coordY);
              board.mouseEnterBlock(board.getBlock(coordX, coordY));
            }
          }
          break;
        case ENTER, X:
          game.blockClicked(board.getBlock(coordX, coordY));
      }
    });
  }

}



