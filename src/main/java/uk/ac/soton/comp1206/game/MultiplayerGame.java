package uk.ac.soton.comp1206.game;


import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
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
import uk.ac.soton.comp1206.scene.LobbyScene;

public class MultiplayerGame extends Game{
  private static final Logger logger = LogManager.getLogger(MultiplayerGame.class);
  private Communicator communicator;
  private Queue<GamePiece> pieceQueue = new LinkedList<>();

  /**
   * Create a new game with the specified rows and columns. Creates a corresponding grid model.
   *
   * @param cols number of columns
   * @param rows number of rows
   */
  public MultiplayerGame(int cols, int rows,Communicator communicator) {
    super(cols, rows);
    this.communicator = communicator;
    communicator.addListener(this::handleCommunication);
  }
  public void initialiseGame(){
    while (pieceQueue.size() < 2){
      communicator.send("PIECE");
    }
    this.followingPiece = pieceQueue.poll();
    nextPiece();
    logger.info("Initialising Multiplayer Game");
  }
  private void handleCommunication(String message){
    if(message.startsWith("PIECE")){
      String[] parts = message.split(" ");
      GamePiece piece = GamePiece.createPiece(Integer.parseInt(parts[1]));
      pieceQueue.add(piece);
      logger.info("Queue size: " + pieceQueue.size());
    }
  }
  @Override
  public GamePiece nextPiece(){
    while (pieceQueue.size() < 2){
      communicator.send("PIECE");
    }
    currentPiece = followingPiece;
    followingPiece = pieceQueue.poll();
    return currentPiece;
  }

}
