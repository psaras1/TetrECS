package uk.ac.soton.comp1206.game;

import java.io.File;
import java.util.HashSet;
import java.util.Random;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;
import uk.ac.soton.comp1206.event.NextPieceListener;

/**
 * The Game class handles the main logic, state and properties of the TetrECS game. Methods to
 * manipulate the game state and to handle actions made by the player should take place inside this
 * class.
 */
public class Game {

  private static final Logger logger = LogManager.getLogger(Game.class);
  //declare a listener for when the next piece is generated
  private NextPieceListener nextPieceListener = null;
  private Random random = new Random(); //Allows us to generate a random number in order to get random pieces/shapes

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


  //    Add bindable properties for the score, level, lives and multiplier to the Game class, with appropriate accessor methods.
  public GamePiece currentPiece, followingPiece, tempPiece;
  private IntegerProperty score;
  private IntegerProperty level;
  private IntegerProperty lives;
  private IntegerProperty multiplier;

  private int lines;
  private int intersectingBlocks;
  private int uniqueClearedBlocks;


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
    this.intersectingBlocks = 0;
    this.uniqueClearedBlocks = 0;

    //These should default to 0 score, level 0, 3 lives and 1 x multiplier respectively.
    this.score = new SimpleIntegerProperty(0);
    this.level = new SimpleIntegerProperty(0);
    this.lives = new SimpleIntegerProperty(3);
    this.multiplier = new SimpleIntegerProperty(1);

    //Create a new grid model to represent the game state
    this.grid = new Grid(cols, rows);
  }

  /**
   * Set the listener for when the next piece is generated
   *
   * @param listener
   */
  public void setNextPieceListener(NextPieceListener listener) {
    nextPieceListener = listener;
  }


  //Add bindable properties for the score, level, lives and multiplier to the Game class, with appropriate accessor methods.
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

  public void setLevel(int level) {
    this.level.set(level);
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
   * Handles clearing of full rows/columns
   */
  public void afterPiece() {
    //separate hashsets to store full rows and columns
    HashSet<GameBlockCoordinate> rowsFull = new HashSet<>();
    HashSet<GameBlockCoordinate> colsFull = new HashSet<>();

    //GameBlockCoordinate representing current column with a fixed row
    for (int col = 0; col < this.cols; col++) {
      colsFull.add(new GameBlockCoordinate(col, -1));
    }
    //iterate through all rows, add full ones to hashset
    for (int row = 0; row < this.rows; row++) {
      boolean rowFull = true;
      for (int col = 0; col < this.cols; col++) {
        if (this.getGrid().get(col, row) == 0) {
          rowFull = false;
          colsFull.remove(new GameBlockCoordinate(col, -1));
        }
      }
      if (rowFull) {
        rowsFull.add(new GameBlockCoordinate(-1, row));

      }
    }
    //Iterate through whole grid to clear full rows and columns
    for (int row = 0; row < this.rows; row++) {
      for (int col = 0; col < this.cols; col++) {
        if (rowsFull.contains(new GameBlockCoordinate(-1, row))) {
          logger.info("Full row found! Row {}", row);
          //clear each cell separately
          this.getGrid().set(col, row, 0);
        }
        if (colsFull.contains(new GameBlockCoordinate(col, -1))) {
          logger.info("Full col found! Col {}", col);
          //clear each cell separately
          this.getGrid().set(col, row, 0);
        }
      }
    }
    this.intersectingBlocks = rowsFull.size() * colsFull.size();
    //uniqueClearedBlocks = total blocks cleared - intersecting blocks
    this.uniqueClearedBlocks = rowsFull.size() * rows + colsFull.size() * cols - intersectingBlocks;
    this.lines = rowsFull.size() + colsFull.size();
    logger.info("Number of blocks cleared: {}", uniqueClearedBlocks);
    //If no rows or columns are full, the multiplier is reset to 1
    if (rowsFull.isEmpty() && colsFull.isEmpty()) {
      score(this.lines, uniqueClearedBlocks);
      setMultiplier(1);
      logger.info("Multiplier changed to: {}", multiplier.get());
    }
    //If there are full rows or columns, the multiplier is incremented by 1
    else {
      score(this.lines, uniqueClearedBlocks);
      setMultiplier(multiplier.get() + 1);
      logger.info("Multiplier changed to: {}", multiplier.get());
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
    int newScore = score.get();
    logger.info("Score changed to: {}", score.get());
    //The level should increase per 1000 points
    if (newScore > oldScore){
      playClearSound();
    }
    if (score.get() >= 1000 * (level.get()+1)) {
      incrementLevel();
      playLevelUpSound();
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
  }


  /**
   * Handle what should happen when a particular block is clicked
   *
   * @param gameBlock the block that was clicked
   */
  public void blockClicked(GameBlock gameBlock){
    //Get the position of this block
    int x = gameBlock.getX();
    int y = gameBlock.getY();
    if (grid.canPlayPiece(currentPiece, x, y)) {
      //Can play the piece
      grid.playPiece(currentPiece, x, y);
      playPlaceSound();
      nextPiece(); //Once one piece has been placed, generate a new one
      afterPiece();
    } else {
      playErrorSound();
    }

  }

  /**
   * Handles misplaced pieces by playing a sound(to be fixed)
   */
private void playErrorSound(){
  String soundFile = getClass().getResource("/sounds/fail.wav").toExternalForm();
  Media sound = new Media(soundFile);
  MediaPlayer mediaPlayer = new MediaPlayer(sound);
  mediaPlayer.play();
}

  /**
   * Handles level up sound
   */
  private void playLevelUpSound(){
  String soundFile = getClass().getResource("/sounds/level.wav").toExternalForm();
  Media sound = new Media(soundFile);
  MediaPlayer mediaPlayer = new MediaPlayer(sound);
  mediaPlayer.play();
}

  /**
   * Handles placement sound
   */
  private void playPlaceSound(){
  String soundFile = getClass().getResource("/sounds/place.wav").toExternalForm();
  Media sound = new Media(soundFile);
  MediaPlayer mediaPlayer = new MediaPlayer(sound);
  mediaPlayer.play();
}

  /**
   * Handles clearing sound
   */
  private void playRotateSound(){
  String soundFile = getClass().getResource("/sounds/rotate.wav").toExternalForm();
  Media sound = new Media(soundFile);
  MediaPlayer mediaPlayer = new MediaPlayer(sound);
  mediaPlayer.play();
}
private void playClearSound(){
    String soundFile = getClass().getResource("/sounds/clear.wav").toExternalForm();
    Media sound = new Media(soundFile);
    MediaPlayer mediaPlayer = new MediaPlayer(sound);
    mediaPlayer.play();
}


  /**
   * Get the number of lines cleared
   *
   * @return number of lines cleared
   */
  public int getLines() {
    return lines;
  }

  /**
   * Get the number of intersecting blocks
   *
   * @return number of intersecting blocks
   */
  public int getIntersectingBlocks() {
    return intersectingBlocks;
  }

  /**
   * Get the number of unique cleared blocks
   *
   * @return number of unique cleared blocks
   */
  public int getUniqueClearedBlocks() {
    return uniqueClearedBlocks;
}

  /**
   * Get the current piece in the game
   *
   * @return current piece
   */
  public GamePiece getCurrentPiece() {
    return currentPiece;
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
   * Get the number of columns in this game
   *
   * @return number of columns
   */
  public int getCols() {
    return cols;
  }

  /**
   * Get the number of rows in this game
   *
   * @return number of rows
   */
  public int getRows() {
    return rows;
  }

  public void rotateCurrentPieceRight() {
    currentPiece.rotate();
    playRotateSound();
    updateListener();
  }
  public void rotateCurrentPieceLeft() {
    currentPiece.rotate(3);
    playRotateSound();
    updateListener();
  }

  /**
   * Add a swapCurrentPiece method to swap the current and following pieces
   */

  public void swapCurrentPiece() {
    tempPiece = currentPiece;
    currentPiece = followingPiece;
    followingPiece = tempPiece;
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


}
