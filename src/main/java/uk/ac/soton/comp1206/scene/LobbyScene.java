package uk.ac.soton.comp1206.scene;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

public class LobbyScene extends BaseScene {
  private static final Logger logger = LogManager.getLogger(InstructionsScene.class);
  private final Communicator communicator;
  private Timer timer;
  private ArrayList<String> channels = new ArrayList<>();
  private String userTemp = "";
  private boolean joinedChannel = false;
  private VBox messageContainer = new VBox();
  private ScrollPane container = new ScrollPane(messageContainer);
  private String currentChannel;
  private StringProperty channelInfo;
  private VBox buttons;
  private VBox mainBox;
  private boolean inChannel;
  private BorderPane mainPane;
  public LobbyScene(GameWindow gameWindow) {
    super(gameWindow);
    communicator = gameWindow.getCommunicator();
    inChannel = false;
    logger.info("Creating Lobby Scene");
  }

  @Override
  public void initialise() {
    logger.info("Initialising Lobby Scene");
    this.timer = new Timer();
//    timer.scheduleAtFixedRate(new TimerTask() {
//      @Override
//      public void run() {
//        communicator.send("LIST");
//      }
//    }, 0, 1000);
    communicator.send("LIST");
    communicator.addListener(this::listenForList);
  }

  @Override
  public void build() {
    root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

    var menuPane = new StackPane();
    menuPane.setMaxWidth(gameWindow.getWidth());
    menuPane.setMaxHeight(gameWindow.getHeight());
    menuPane.getStyleClass().add("menu-background");
    root.getChildren().add(menuPane);

    mainPane = new BorderPane();
    menuPane.getChildren().add(mainPane);

    var createLobby = new Text("Create Lobby");
    buttons = new VBox(createLobby);
    createLobby.getStyleClass().add("option-button");
    var channeLabel = new Text("Channels: ");
    channeLabel.getStyleClass().add("option3-button");
    buttons.getChildren().add(channeLabel);
    buttons.setMinWidth(gameWindow.getWidth()/2);
//    createLobby.setTextAlignment(Pos.TOP_LEFT);
    mainPane.getChildren().add(buttons);

  }

  public void listenForList(String message) {
    logger.info("Received list: " + message);
    String[] parts = message.split(" ",2);
    String command = parts[0];
    String data = parts[1];
    if (command.equals("CHANNELS")){
      Platform.runLater(()->
          getChannels(data));
    }
    if(command.equals("USERS")){
      Platform.runLater(()->
          getUsers(data));
    }
    if(command.equals("MSG")){
      Platform.runLater(()->
          getMessage(data));
    }
  }

  public void getChannels(String data){
    logger.info("Channels: " + data);
    if(channels!=null){
      channels.clear();
    }

    var channelList = new VBox();
    channelList.setAlignment(Pos.BOTTOM_LEFT);
    String[] channelArray = data.split("\\R");
    channels.addAll(Arrays.asList(channelArray));
    mainPane.setLeft(channelList);
    for(String channel : channels){
      var channelID = new Text(channel);
      channelID.getStyleClass().add("channelItem");
      channelList.getChildren().add(channelID);
      channelList.setAlignment(Pos.TOP_LEFT);
      channelID.setOnMouseClicked(e->{
//        join(channel, false);
      });
    }

  }

  public void getUsers(String data) {
    logger.info("Users: " + data);
    String[] userArray = data.split("\\R");
    for (String user : userArray) {
      userTemp += (user + "\n");
    }
    if (joinedChannel) {
      logger.info("User List new: {}", userTemp);
      channelInfo.set(currentChannel + "Users: " + userTemp);
    }
  }

  public void getMessage(String data){
    messageContainer.getChildren().add(new Text(data));
    container.setVvalue(container.getVmax());
  }

  public void join(String channel, boolean host){
    currentChannel = channel;
    messageContainer.getChildren().clear();
    communicator.send("JOIN " + channel);
    inChannel = true;
    mainBox = new VBox();
    mainPane.setRight(mainBox);

  }

}
