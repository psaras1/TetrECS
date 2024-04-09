package uk.ac.soton.comp1206.game;


import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.network.Communicator;

public class MultiplayerGame extends Game{
  private static final Logger logger = LogManager.getLogger(MultiplayerGame.class);
  private Communicator communicator;
  private IntegerProperty nextPieceFinder = new SimpleIntegerProperty();

  /**
   * Create a new game with the specified rows and columns. Creates a corresponding grid model.
   *
   * @param cols number of columns
   * @param rows number of rows
   */
  public MultiplayerGame(int cols, int rows) {
    super(cols, rows);
    timer = Executors.newSingleThreadScheduledExecutor();
  }
  public void initialiseGame(){
    logger.info("Initialising Multiplayer Game");
    super.initialiseGame();
    communicator.addListener(this::handleCommunication);
  }
  private void handleCommunication(String message){
    logger.info("Received message: " + message);
    String[] parts = message.split(" ",2);
    String command = parts[0];
    String data = parts[1];
    if(command.equals("PIECE")){
      logger.info("Received piece: " + data);
      Platform.runLater(()->{
        nextPieceFinder.set(Integer.parseInt(data));
        logger.info("Next piece set to: " + data);
        spawnPiece();
      });
    }
  }
  public GamePiece spawnPiece(){
    communicator.send("PIECE");
    Random random = new Random();
    AtomicInteger piece = new AtomicInteger();
    Platform.runLater(()->{
      piece.set(nextPieceFinder.getValue());
    });
    int rotation = random.nextInt(4);
    return GamePiece.createPiece(piece.get(),rotation);
  }
}
