package uk.ac.soton.comp1206.game;


import java.util.ArrayDeque;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.network.Communicator;

public class MultiplayerGame extends Game{
  private static final Logger logger = LogManager.getLogger(MultiplayerGame.class);
  private Communicator communicator;
  private ArrayDeque<GamePiece> pieceQueue = new ArrayDeque<>();

  /**
   * Create a new game with the specified rows and columns. Creates a corresponding grid model.
   *
   * @param cols number of columns
   * @param rows number of rows
   */
  public MultiplayerGame(int cols, int rows,Communicator communicator) {
    super(cols, rows);
    this.communicator = communicator;
    communicator.addListener((message) -> {
      Platform.runLater(() -> {
        this.handleCommunication(message.trim());
      });
    });
  }
  public void initialiseGame(){
    this.getPieces();
    logger.info("Initialising Multiplayer Game");
  }
  private void handleCommunication(String message){
    String[] parts = message.split(" ",2);
    String command = parts[0];
    String info;
    if(command.equals("PIECE")&& parts.length > 1){
      info = parts[1];
      this.addPiece(Integer.parseInt(info));
      logger.info("Queue size: " + pieceQueue.size());
    }
  }
  public void getPieces(){
    for(int i = 0; i < 5; i++){
      communicator.send("PIECE");
    }
  }

  public void addPiece(int x){
    GamePiece piece = GamePiece.createPiece(x);
    this.pieceQueue.add(piece);
    if(!this.began&&this.pieceQueue.size() > 2){
      logger.info("Game has begun");
      this.followingPiece = this.spawnPiece();
      this.nextPiece();
      began = true;
    }
  }

  @Override
  public GamePiece spawnPiece(){
    this.communicator.send("PIECE");
    return this.pieceQueue.pop();
  }

}
