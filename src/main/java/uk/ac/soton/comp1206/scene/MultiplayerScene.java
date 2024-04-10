package uk.ac.soton.comp1206.scene;

import java.util.Timer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
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
  private VBox chatBox = new VBox();
  private ScrollPane scroller = new ScrollPane();
  private static final Logger logger = LogManager.getLogger(MultiplayerScene.class);
  public MultiplayerScene(GameWindow gameWindow) {
    super(gameWindow);
    this.communicator = gameWindow.getCommunicator();
    logger.info("Creating Multiplayer Scene");
  }

  public void initialise(){
    super.initialise();
    logger.info("Initialising Multiplayer Scene");
    this.communicator.addListener((message) -> {
      this.handleCommunication(message.trim());
    });
  }
  @Override
  public void setupGame(){
    logger.info("Setting up Multiplayer Game");
    game = new MultiplayerGame(5,5,this.communicator);
  }
  public void build(){
    super.build();
    /*@override the title*/
    title.setText("Multiplayer");

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
    leftBox.setSpacing(10);
    leftBox.setPadding(new Insets(10));

    scroller.getStyleClass().add("scroller");
    chatBox.getStyleClass().add("messages");
    this.scroller.setContent(chatBox);

    scroller.setFitToWidth(true);
    scroller.setPrefViewportHeight(200);

    var sendText = new TextField();
    sendText.setMaxWidth(Double.MAX_VALUE);
    Button sendButton = new Button("Send");
    sendButton.getStyleClass().add("multiplayer-button");
    sendButton.setMinWidth(Button.USE_PREF_SIZE);
    sendButton.setMinHeight(28);

    HBox sendBox = new HBox();
    sendBox.getChildren().addAll(sendText,sendButton);
    HBox.setHgrow(sendText, Priority.ALWAYS);

    Text lobbyLabel = new Text("Current Lobby: "+LobbyScene.currentChannel);
    lobbyLabel.getStyleClass().add("multiplayer-game-label");
    Text chatHeading = new Text("Chat: ");
    chatHeading.getStyleClass().add("multiplayer-game-label");

    leftBox.setMaxWidth(200);
    leftBox.getChildren().addAll(lobbyLabel,chatHeading,scroller,sendBox);
    scroller.setStyle("-fx-border-color: white; -fx-border-width: 2px;-fx-background-color: rgba(0,0,0,0.5);");

    mainPane.setLeft(leftBox);
  }
  public void handleCommunication(String s){
    String[] parts = s.split(" ",2);
    String command = parts[0];
    if(command.equals("MSG")){
      recieveMessage(parts[1]);
    }
  }
  public void recieveMessage(String message){
    String[] data = message.split(":",2);
    String user = data[0];
    String text = data[1];
    chatBox.getChildren().add(new Text(user + ": " + text));
    scroller.setVvalue(scroller.getVmax());
  }
}
