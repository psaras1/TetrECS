package uk.ac.soton.comp1206.scene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import javafx.animation.AnimationTimer;
import javafx.animation.FillTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.event.GameLoopListener;
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

  /*
  game loop implementation
   */
  private Rectangle timeBar;
  private long timeBarWidth;
  private Timeline timeline;

  private static final Logger logger = LogManager.getLogger(ChallengeScene.class);
  protected Game game;
  private Multimedia gameMusic = new Multimedia();
  /*
   * Labels for the game stats
   */
  protected Text scoreLabel;
  protected Text levelLabel;
  protected Text livesLabel;
  protected Text multiplierLabel;
  protected PieceBoard currentPiece, followingPiece;
  private int coordX = 0, coordY = 0;
  private boolean mouseMode;
  private GameBoard board;
  private GameBlock keyboardSelectedBlock = null;
  /* Holds the high score */
  private IntegerProperty highScore = new SimpleIntegerProperty();
  protected BorderPane mainPane = new BorderPane();
  protected VBox rightBox = new VBox();
  protected VBox levelBox = new VBox();
  protected VBox scoreBox = new VBox();
  protected VBox livesBox = new VBox();
  protected VBox multiplierBox = new VBox();
  protected VBox centerBox = new VBox();


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
    /*Implement background music in the game scene*/
    this.gameMusic.playBackgroundMusic("/music/game.wav");

    /*binds the properties of the game to the UI*/
    bindProperties();

    root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());
    var challengePane = new StackPane();
    challengePane.setMaxWidth(gameWindow.getWidth());
    challengePane.setMaxHeight(gameWindow.getHeight());
    challengePane.getStyleClass().add("challenge-background");

    root.getChildren().add(challengePane);

    /*top*/
    /*title*/
    var title = new Text("TetrECS");
    title.getStyleClass().add("bigtitle");
    var topBox = new HBox();
    topBox.setAlignment(Pos.CENTER);
    topBox.getChildren().add(title);
    mainPane.setTop(topBox);

    /*right*/
    rightBox.setAlignment(Pos.CENTER);
    rightBox.setSpacing(20);
    rightBox.setPadding(new Insets(0, 15, 0, 0));
    mainPane.setRight(rightBox);

    /*score*/
    scoreBox.setAlignment(Pos.CENTER);
    var scoreTitle = new Text("Score:");
    scoreTitle.getStyleClass().add("heading");
    scoreLabel.getStyleClass().add("score");
    scoreBox.getChildren().addAll(scoreTitle, scoreLabel);

    /*level*/
    levelBox.setAlignment(Pos.CENTER);
    var levelTitle = new Text("Level:");
    levelTitle.getStyleClass().add("heading");
    levelLabel.getStyleClass().add("level");
    levelBox.getChildren().addAll(levelTitle, levelLabel);

    /*lives*/
    livesBox.setAlignment(Pos.CENTER);
    var livesTitle = new Text("Lives:");
    livesTitle.getStyleClass().add("heading");
    /*initial style of lives label*/
    livesLabel.getStyleClass().add("lives");
    livesLabel.styleProperty().setValue("-fx-fill: green;");
    /*
    add listener to livesLabel to change style based on the number of lives(dynamically)
     */
    game.getLives().addListener((obs, oldVal, newVal) -> {
      /*remove previously added styles*/
      livesLabel.getStyleClass().clear();
      updateLivesLabel(newVal.intValue());
    });
    livesBox.getChildren().addAll(livesTitle, livesLabel);

    /*multiplier*/
    multiplierBox.setAlignment(Pos.CENTER);
    var multiplierTitle = new Text("Multiplier:");
    multiplierTitle.getStyleClass().add("heading");
    multiplierLabel.getStyleClass().add("level");
    multiplierBox.getChildren().addAll(multiplierTitle, multiplierLabel);
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

    /*
    Current High Score
     */
    var highScoreLabel = new Text("High Score:");
    highScoreLabel.getStyleClass().add("hiscore");
    var highScoreVal = new Text();
    highScoreVal.getStyleClass().add("hiscore");
    /*Text object binded to highScore property*/
    highScoreVal.textProperty().bind(highScore.asString());

    Region spacing = new Region();
    spacing.setPrefHeight(50);

    leftBox.getChildren().addAll(currentPieceLabel, currentPiece, nextPieceLabel, followingPiece,spacing,
        highScoreLabel, highScoreVal);

    /*botom*/
    /*
    timebar implementation
     */
    game.setOnGameLoop(this::gameLoopAnimation);
    timeBar = new Rectangle();
    timeBar.setHeight(10);
    timeBarWidth = gameWindow.getWidth();
    timeBar.setWidth(timeBarWidth);
    timeBar.setFill(Color.GREEN);//initial color of the timebar
    var timeBox = new HBox(timeBar);
    timeBox.setAlignment(Pos.CENTER);
    mainPane.setBottom(timeBox);

    //Create the game board
    board = new GameBoard(game.getGrid(), gameWindow.getWidth() / 2, gameWindow.getWidth() / 2);
    mainPane.setCenter(board);
    //Implement it so that right clicking on the main GameBoard or left clicking on the current piece board rotates the next piece

    //activate listener on board instance of GameBoard
    board.setOnRightClick(this::rotate);
    challengePane.getChildren().add(mainPane);

    board.setOnBlockClick(this::blockClicked); //calls blockClicked from GameBoard class

    //Create a mute button
    addMuteButton("/music/game.wav",gameMusic);


    //Create a menu button
    var menuButton = new Text("Menu");
    menuButton.getStyleClass().add("option1-button");
    var menuButtonPane = new AnchorPane();
    menuButtonPane.getChildren().add(menuButton);
    AnchorPane.setLeftAnchor(menuButton, 10.0);
    AnchorPane.setTopAnchor(menuButton, 5.0);
    menuButtonPane.setPickOnBounds(false);
    menuButton.setOnMouseClicked(e -> {
      logger.info("Menu button clicked, returning to menu");
      game.exitGame();
      gameMusic.stopBackgroundMusic();
      gameWindow.startMenu();
    });
    root.getChildren().add(menuButtonPane);

  }

  /*
  *Update lives label based on the number of lives
   */
  public void updateLivesLabel(int lives) {
    if (lives == 3) {
      livesLabel.getStyleClass().add("lives");
      livesLabel.styleProperty().setValue("-fx-fill: green;");
    }
    if (lives == 2) {
      livesLabel.getStyleClass().add("lives");
      livesLabel.styleProperty().setValue("-fx-fill: yellow;");
    }
    if (lives == 1) {
      livesLabel.getStyleClass().add("lives");
      livesLabel.styleProperty().setValue("-fx-fill: orange;");
    }
    if (lives == 0) {
      livesLabel.getStyleClass().add("lives");
      livesLabel.styleProperty().setValue("-fx-fill: red;");
    }
  }

  public void gameLoopAnimation() {
    timeBar.setScaleX(1);
    timeline = new Timeline();
    timeBar.setFill(Color.GREEN);
    timeline.getKeyFrames().add(new KeyFrame(Duration.millis(game.getTimerDelay()),
        new KeyValue(timeBar.scaleXProperty(), 0)));
    timeline.playFromStart();
    FillTransition color = new FillTransition();
    color.setDuration(Duration.millis(game.getTimerDelay()));
    color.setShape(timeBar);
    color.setToValue(Color.RED);
    color.play();
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

  protected void clearedLines(HashSet<GameBlockCoordinate> linesCleared) {
    logger.info("Lines cleared: {}", linesCleared);
    board.fadeOut(linesCleared);
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
    game = new Game(5, 5);
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
    keyboardControls();
    timeline = new Timeline();
    //Set the next piece listener
    //(Update the NextPieceListener to pass the following piece as well, and use this to update the following piece board.)
    game.setNextPieceListener(this::nextPiece); //next piece passed as GamePiece to interface
    game.setLineClearedListener(
        this::clearedLines);//linesCleared passed as HashSet<GameBlockCoordinate> to interface
    game.setOnGameEnd(() -> {
      logger.info("Game over");
      game.endTimer();
      gameMusic.stopBackgroundMusic();
      gameWindow.showScores(game);
    });
    /*Attatching a listener to the score variable of the game instance, call updateHighScore whenever it changes*/
    game.score.addListener(this::updateHighScore);
    highScore.set(ScoresScene.loadScores().get(0).getValue());

    game.start();

    gameLoopAnimation();


  }

  /**
   * Update the high score

   * Called in the initialise method
   * Added a listener to the score, so whenever it changes updateHighScore is called
   * @param observable
   * @param oldValue
   * @param newValue
   */
  public void updateHighScore(ObservableValue<? extends Number> observable, Number oldValue, Number newValue){
    if(newValue.intValue() > this.highScore.get()){
      this.highScore.set(newValue.intValue());
      logger.info("High Score updated to: {}", newValue.intValue());
    }
    if(newValue.intValue() < this.highScore.get()){
      if(newValue.intValue() > ScoresScene.loadScores().get(0).getValue()){
        this.highScore.set(newValue.intValue());
        logger.info("High Score updated to: {}", newValue.intValue());
      }else{
        this.highScore.set(ScoresScene.loadScores().get(0).getValue());
      }
    }
  }

  /**
   * Keyboard controls for the game
   * (Using .setOnKeyPressed() method to listen for key presses didn't work for me
   * (arrows and space bar weren't registering)
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
    gameWindow.getScene().addEventFilter(KeyEvent.KEY_PRESSED,event -> {
      logger.info("Key pressed: {}", event.getCode());
      switch (event.getCode()) {
        //Go back to menu
        case ESCAPE:
          logger.info("Escape pressed, returning to menu");
          game.exitGame();
          gameMusic.stopBackgroundMusic();
          gameWindow.startMenu();
          break;
        //Move the current piece left
        case M:
          if (!gameMusic.isPlaying()) {
            gameMusic.playBackgroundMusic("/music/game.wav");
            changeToUnmuteImage();
          } else {
            gameMusic.stopBackgroundMusic();
            changeToMuteImage();
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
        case R, SPACE:
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



