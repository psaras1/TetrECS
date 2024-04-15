package uk.ac.soton.comp1206.game;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;
import uk.ac.soton.comp1206.event.GameEndListener;
import uk.ac.soton.comp1206.event.GameLoopListener;
import uk.ac.soton.comp1206.event.LineClearedListener;
import uk.ac.soton.comp1206.event.NextPieceListener;

/**
 * The Game class handles the main logic, state and properties of the TetrECS game. Methods to
 * manipulate the game state and to handle actions made by the player should take place inside this
 * class.
 */
public class Game {

  private static final Logger logger = LogManager.getLogger(Game.class);
  //declare a listener for when the next piece is generated
  protected NextPieceListener nextPieceListener = null;
  protected Boolean began = false;
  protected int newScore;
  private Multimedia multimedia = new Multimedia();

  private LineClearedListener lineClearedListener = null;
  private Random random = new Random(); //Allows us to generate a random number in order to get random pieces/shapes
  public Boolean eraseMode = false;
  /**
   * Number of rows
   */
  protected final int rows;
  /**
   * Number of columns
   */
  protected final int cols;

  /**
   * The grid model linked to the game
   */
  protected final Grid grid;


  /*
  Add bindable properties for the score, level, lives and multiplier to the Game class, with appropriate accessor methods.
   */
  public GamePiece currentPiece, followingPiece, tempPiece;
  public IntegerProperty score;
  protected IntegerProperty level;
  public IntegerProperty lives;
  protected IntegerProperty multiplier;

  private int lines;
  /**
   * GameLoop for the game
   */
  protected ScheduledFuture<?> gameLoop;
  /**
   * Game loop listener
   */
  protected GameLoopListener gameLoopListener = null;
  /**
   * Timer for the game loop(calls the gameLoop method)
   */
  protected ScheduledExecutorService timer;
  /*
  called when the game ends
   */
  private GameEndListener gameEndListener = null;
  /*
  Array List to store the scores, passed to scores scene
   */
  public ArrayList<Pair<String, Integer>> scores = new ArrayList<>();

  protected long delay;

  /**
   * Create a new game with the specified rows and columns. Creates a corresponding grid model.
   *
   * @param cols number of columns
   * @param rows number of rows
   */
  public Game(int cols, int rows) {
    this.lines = 0;
    this.cols = cols;
    this.rows = rows;
    //These should default to 0 score, level 0, 3 lives and 1 x multiplier respectively.
    this.score = new SimpleIntegerProperty(0);
    this.level = new SimpleIntegerProperty(0);
    this.lives = new SimpleIntegerProperty(3);
    this.multiplier = new SimpleIntegerProperty(1);

    //Create a new grid model to represent the game state
    this.grid = new Grid(cols, rows);

    timer = Executors.newSingleThreadScheduledExecutor();
  }

  /**
   * Set the listener for when the next piece is generated
   *
   * @param listener
   */
  public void setNextPieceListener(NextPieceListener listener) {
    nextPieceListener = listener;
  }

  /**
   * Set the listener for when a line is cleared
   *
   * @param listener
   */
  public void setLineClearedListener(LineClearedListener listener) {
    lineClearedListener = listener;
  }


  /**
   * Bind the score, level, lives and multiplier properties to the Game class (Add bindable
   * properties for the score, level, lives and multiplier to the Game class, with appropriate
   * accessor methods.)
   */
  public IntegerProperty getScore() {
    return score;
  }

  public IntegerProperty getLevel() {
    return level;
  }

  public IntegerProperty getLives() {
    return lives;
  }

  public IntegerProperty getMultiplier() {
    return multiplier;
  }

  public void setScore(int score) {
    this.score.set(score);
  }

  public void setMultiplier(int multiplier) {
    this.multiplier.set(multiplier);
  }

  public void setLives(int lives) {
    this.lives.set(lives);
  }

  public void incrementLevel() {
    this.level.set(level.get() + 1);
  }

  /**
   * Start the game
   */
  public void start() {
    logger.info("Starting game");
    initialiseGame();
    gameLoop = timer.schedule(this::gameLoop, getTimerDelay(), TimeUnit.MILLISECONDS);
    gameLoopListener();

  }

  /**
   * Spawns a random(1/15) piece using spawnPiece
   *
   * @return returns the random piece
   */
  public GamePiece nextPiece() {
    currentPiece = followingPiece; //using the next piece as the current piece
    followingPiece = spawnPiece(); //spawn a new piece, saving it for the next placement
    logger.info("The next piece is: {}", currentPiece);
    updateListener();
    return currentPiece;
  }

    /*
    At first, we need to be able to track a piece and then be able to keep track of current pieces
     */

  /**
   * Uses the random class to generate a random(1/15) pieces
   *
   * @return returns the generated piece
   */
  public GamePiece spawnPiece() {
    var maxPieces = GamePiece.PIECES; //total number of possible pieces is 15
    var randomPiece = random.nextInt(maxPieces);//used to generate 1 of 15 random pieces
    logger.info("Picking a random piece(1/15)");
    var piece = GamePiece.createPiece(randomPiece);
    return piece;
  }

  /**
   * Handles game mechanics after a block is placed Checks if any lines are cleared Updates the
   * score, multiplier and level
   */
  public void afterPiece() {
    var clearedBlocks = new HashSet<GameBlockCoordinate>();
    var cleared = new HashSet<GameBlockCoordinate>();

    int totalCleared = 0;

    //check all rows
    for (int x = 0; x < cols; x++) {
      int counter = 0;
      for (int y = 0; y < rows; y++) {
        if (grid.get(x, y) == 0) {
          break;
        }
        counter++;
      }
      if (counter == rows) {
        totalCleared++;
        for (int y = 0; y < rows; y++) {
          clearedBlocks.add(new GameBlockCoordinate(x, y));
          cleared.add(new GameBlockCoordinate(x, y));
        }
      }
    }
    //check all columns
    for (int y = 0; y < rows; y++) {
      int counter = 0;
      for (int x = 0; x < cols; x++) {
        if (grid.get(x, y) == 0) {
          break;
        }
        counter++;
      }
      if (counter == cols) {
        totalCleared++;
        for (int x = 0; x < cols; x++) {
          clearedBlocks.add(new GameBlockCoordinate(x, y));
          cleared.add(new GameBlockCoordinate(x, y));
        }
      }
    }
    //If any lines were cleared
    if (totalCleared > 0) {
      //update the score
      score(totalCleared, clearedBlocks.size());
      //update the multiplier
      multiplier.set(multiplier.get() + 1);
      //update the level
      level.set(Math.floorDiv(score.get(), 1000));
      //if a LineClearedListener is set, the listener should be called with the set of cleared blocks
      if (lineClearedListener != null) {
        lineClearedListener.linesCleaned(cleared);
      }
      for (var block : clearedBlocks) {
        grid.set(block.getX(), block.getY(), 0);
      }
    } else {
      //If no lines were cleared, the multiplier should be reset to 1
      multiplier.set(1);
    }
  }

  /**
   * Handles scoring
   *
   * @param lines  passed from afterPiece method
   * @param blocks passed from afterPiece method
   */
  public void score(int lines, int blocks) {
    int oldScore = score.get();
    setScore(score.get() + (lines * blocks * 10 * multiplier.get()));
    newScore = score.get();
    logger.info("Score changed to: {}", score.get());
    //The level should increase per 1000 points
    if (newScore > oldScore) {
      multimedia.playAudio("/sounds/clear.wav");
    }
    if (score.get() >= 1000 * (level.get() + 1)) {
      incrementLevel();
      multimedia.playAudio("/sounds/level.wav");
      logger.info("Level changed to: {}", level.get());
    }
  }

  /**
   * Initialise a new game and set up anything that needs to be done at the start
   */
  public void initialiseGame() {
    logger.info("Initialising game");
    this.followingPiece = spawnPiece();
    nextPiece(); //So the game starts with a piece
    this.began = true;
  }


  /**
   * Handle what should happen when a particular block is clicked
   *
   * @param gameBlock the block that was clicked
   */
  public boolean blockClicked(GameBlock gameBlock) {
    if (eraseMode) {
      logger.info("Erasing block(paint empty)");
      grid.set(gameBlock.getX(), gameBlock.getY(), 0);
      return true;
    } else {
      //Get the position of this block
      int x = gameBlock.getX();
      int y = gameBlock.getY();
      if (grid.canPlayPiece(currentPiece, x, y)) {
        //Can play the piece
        grid.playPiece(currentPiece, x, y);
        multimedia.playAudio("/sounds/place.wav");
        nextPiece(); //Once one piece has been placed, generate a new one
        afterPiece();
        /* if a block is placed, the game loop should be reset */
        gameLoop.cancel(false);
        gameLoop = timer.schedule(this::gameLoop, getTimerDelay(), TimeUnit.MILLISECONDS);
        gameLoopListener();
        logger.info("Timer reset");
        return true;
      } else {
        multimedia.playAudio("/sounds/fail.wav");
        logger.info("Cannot place piece at: {}, {}", x, y);
        return false;
      }
    }

  }





  /**
   * Get the grid model inside this game representing the game state of the board
   *
   * @return game grid model
   */
  public Grid getGrid() {
    return grid;
  }

  /**
   * Rotating once, rotates piece to the right
   */
  public void rotateCurrentPieceRight() {
    currentPiece.rotate();
    multimedia.playAudio("/sounds/rotate.wav");
    updateListener();
  }

  /**
   * Rotating three times, rotates piece to the left
   */
  public void rotateCurrentPieceLeft() {
    currentPiece.rotate(3);
    multimedia.playAudio("/sounds/rotate.wav");
    updateListener();
  }

  /**
   * Add a swapCurrentPiece method to swap the current and following pieces
   */

  public void swapCurrentPiece() {
    tempPiece = currentPiece;
    currentPiece = followingPiece;
    followingPiece = tempPiece;
    multimedia.playAudio("/sounds/pling.wav");
    updateListener();
  }

  /**
   * Update the listener to update the preview of the current piece
   */
  public void updateListener() {
    if (nextPieceListener != null) {
      //call next piece method from NextPieceListener interface to update the preview of the current piece
      nextPieceListener.nextPiece(currentPiece);
    }
  }

  public long getTimerDelay() {
    delay = Math.max(12000 - (level.get() * 500), 2500);
    return delay;
  }

  /**
   * Handles execution after timeline ends
   *
   * @param listener
   */
  public void setOnGameLoop(GameLoopListener listener) {
    gameLoopListener = listener;
  }

  public void setOnGameEnd(GameEndListener listener) {
    gameEndListener = listener;
  }

  /**
   * Listens for when the timer ends
   */
  public void gameLoopListener() {
    if (gameLoopListener != null) {
      gameLoopListener.setOnGameLoop();
    }
  }

  /**
   * Reset the game after timer  ends
   */
  public void gameLoop() {
    setLives(lives.get() - 1);
    Multimedia lifeLost = new Multimedia();
    lifeLost.playAudio("/sounds/lifelose.wav");
    if (lives.get() < 0) {
      if (gameEndListener != null) {
        Platform.runLater(() -> gameEndListener.onGameEnd());
      }
    } else {
      setMultiplier(1);
      nextPiece();
      gameLoopListener();
      gameLoop = timer.schedule(this::gameLoop, getTimerDelay(), TimeUnit.MILLISECONDS);
      logger.info("Lives left: {}", lives.get() + ", Multiplier reset");
    }
  }


  /**
   * Shutdown the game, resetting the score, multiplier, level and lives(upon exit)
   */

  public void exitGame() {
    logger.info("Shutting down game");
    lives.set(3);
    score.set(0);
    level.set(0);
    multiplier.set(1);
    grid.clean();
    gameLoop.cancel(false);

  }

  /* Called on game over */
  public void endTimer() {
    this.timer.shutdown();
  }

  /**
   * POWER UPS
   */
  public Boolean powerLives() {
    if (this.getScore().get() >= 100) {
      multimedia.playAudio("/sounds/wow.mp3");
      setLives(this.getLives().get() + 1);
      setScore(this.getScore().get() - 100);
      logger.info("Lives increased by 1, lives left: {}", this.getLives().get());
      return true;
    }
    return false;
  }

  public Boolean powerPiece() {
    if (this.getScore().get() >= 300) {
      multimedia.playAudio("/sounds/wow.mp3");
      nextPiece();
      setScore(this.getScore().get() - 300);
      logger.info("Current piece changed to: {}", this.currentPiece);
      return true;
    }
    return false;
  }


}
