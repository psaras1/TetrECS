package uk.ac.soton.comp1206.scene;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
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
  /*
  Repeating timer requesting the list of channels
   */
  private ScheduledExecutorService executor;
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
    startChannelTimer();
    communicator.send("LIST");
    communicator.addListener(this::listenForList);
  }
  private void startChannelTimer() {
    logger.info("searching for channels");
    executor = Executors.newSingleThreadScheduledExecutor();
    executor.scheduleAtFixedRate(() -> {
      communicator.send("LIST");
    }, 0, 5, TimeUnit.SECONDS);
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
    mainPane.setMaxWidth(gameWindow.getWidth()-25);
    mainPane.setMaxHeight(gameWindow.getHeight()-25);
    menuPane.getChildren().add(mainPane);

    var createLobby = new Text("Create Lobby");
    buttons = new VBox();
    createLobby.getStyleClass().add("option-button");
    var channeLabel = new Text("Current lobbies: ");
    channeLabel.getStyleClass().add("option3-button");
    buttons.getChildren().addAll(createLobby,channeLabel);
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
    channelList.setPadding(new javafx.geometry.Insets(100,0,0,0));
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
