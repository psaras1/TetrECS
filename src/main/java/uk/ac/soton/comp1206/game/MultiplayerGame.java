package uk.ac.soton.comp1206.game;


import java.util.ArrayDeque;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.network.Communicator;

public class MultiplayerGame extends Game {

  private static final Logger logger = LogManager.getLogger(MultiplayerGame.class);
  private Communicator communicator;
  private ArrayDeque<GamePiece> pieceQueue = new ArrayDeque<>();

  /**
   * Create a new game with the specified rows and columns. Creates a corresponding grid model.
   *
   * @param cols number of columns
   * @param rows number of rows
   */
  public MultiplayerGame(int cols, int rows, Communicator communicator) {
    super(cols, rows);
    this.communicator = communicator;
    communicator.addListener((message) -> {
      Platform.runLater(() -> {
        this.handleCommunication(message.trim());
      });
    });
  }

  public void initialiseGame() {
    this.getPieces();
    logger.info("Initialising Multiplayer Game");
  }

  /*
   * Handle communication from the server
   */
  private void handleCommunication(String message) {
    String[] parts = message.split(" ", 2);
    String command = parts[0];
    String info;
    if (command.equals("PIECE") && parts.length > 1) {
      info = parts[1];
      this.addPiece(Integer.parseInt(info));
      logger.info("Queue size: " + pieceQueue.size());
    }
  }

  /*
   * Get pieces from the server
   * Called initially when the queue is empty(acquires 5 pieces)
   */
  public void getPieces() {
    for (int i = 0; i < 5; i++) {
      communicator.send("PIECE");
    }
  }

  /*
  Adds pieces recieved from the server to the queue
   */
  public void addPiece(int x) {
    GamePiece piece = GamePiece.createPiece(x);
    this.pieceQueue.add(piece);
    if (!this.began && this.pieceQueue.size() > 2) {
      logger.info("Game has begun");
      this.followingPiece = this.spawnPiece();
      this.nextPiece();
      began = true;
    }
  }

  /*
   * Spawns a piece from the queue
   * (Overrides the spawnPiece method in Game class)
   */
  @Override
  public GamePiece spawnPiece() {
    this.communicator.send("PIECE");
    return this.pieceQueue.pop();
  }

  /*
   * Send the board to the server, after a block is clicked
   * All other logic is the same as the blockClicked method in the Game class
   */
  @Override
  public boolean blockClicked(GameBlock block) {
    logger.info("Block clicked");
    boolean placed = super.blockClicked(block);
    StringBuilder toSend = new StringBuilder();
    for (int i = 0; i < this.cols; i++) {
      for (int j = 0; j < this.rows; j++) {
        int val = this.grid.get(i, j);
        toSend.append(" " + val + " ");
      }
    }
    String board = toSend.toString().trim();
    this.communicator.send("BOARD " + board);
    return placed;
  }

  @Override
  public void exitGame() {
    super.exitGame();
    this.communicator.send("DIE");
  }

  /*
   * Send the current score to the server
   */
  @Override
  public void score(int lines, int blocks) {
    super.score(lines, blocks);
    logger.info("Sending score");
    this.communicator.send("SCORE " + newScore);
  }

}
