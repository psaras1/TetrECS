package uk.ac.soton.comp1206.scene;

import java.util.Timer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.game.MultiplayerGame;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GameWindow;

public class MultiplayerScene extends ChallengeScene{

  /**
   * Create a new Single Player challenge scene
   *
   * @param gameWindow the Game Window
   */
  private final Communicator communicator;
  private static final Logger logger = LogManager.getLogger(MultiplayerScene.class);
  public MultiplayerScene(GameWindow gameWindow) {
    super(gameWindow);
    this.communicator = gameWindow.getCommunicator();
    logger.info("Creating Multiplayer Scene");
  }
  public String getMusic(){
    return "";
  }
  public void initialise(){
    super.initialise();
    logger.info("Initialising Multiplayer Scene");
  }
  @Override
  public void setupGame(){
    logger.info("Setting up Multiplayer Game");
    game = new MultiplayerGame(5,5,this.communicator);
  }
  public void build(){
    super.build();
    /* Right overwrite*/
    var nextPieceLabel = new Text("Next Piece: ");
    var currentPieceLabel = new Text("Current Piece: ");
    currentPieceLabel.getStyleClass().add("heading");
    nextPieceLabel.getStyleClass().add("heading");
    var pieceDisplay = new VBox();
    pieceDisplay.setAlignment(Pos.CENTER);
    pieceDisplay.setPadding(new Insets(10,10,10,10));
    pieceDisplay.getChildren().addAll(currentPieceLabel,currentPiece,nextPieceLabel,followingPiece);

    var rigthBox = new VBox();
    rigthBox.getChildren().addAll(scoreBox,livesBox,pieceDisplay);
    mainPane.setRight(rigthBox);

    /* Left overwrite*/
    var leftBox = new VBox();

    mainPane.setLeft(leftBox);

  }
}
