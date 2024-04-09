package uk.ac.soton.comp1206.scene;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

public class LobbyScene extends BaseScene {
  private static final Logger logger = LogManager.getLogger(InstructionsScene.class);
  protected static Communicator communicator;
  private Text createLobby;
  private TextField sendText;
  private ArrayList<String> channels = new ArrayList<>();
  private String userTemp = "";
  private boolean joinedChannel = false;
  private VBox messages = new VBox();
  private VBox rightPane;
  private ScrollPane scroller;
  private TextField messageField;
  private String currentChannel;
  private StringProperty channelInfo;
  private VBox buttons;
  private VBox mainBox;
  private boolean inChannel=false;
  private BorderPane mainPane;
  private TextField nameField;
  /*
  Repeating timer requesting the list of channels
   */
  private ScheduledExecutorService executor;
  public LobbyScene(GameWindow gameWindow) {
    super(gameWindow);
    communicator = gameWindow.getCommunicator();
    logger.info("Creating Lobby Scene");
  }

  @Override
  public void initialise() {
    logger.info("Initialising Lobby Scene");
    startChannelTimer();
    keyboardControls();
    communicator.send("LIST");
    communicator.addListener(this::listenForList);
  }

  /**
   * Start a timer to request the list of channels every 5 seconds
   * (By sending the LIST command to the server)
   */
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

    //menu pane
    var menuPane = new StackPane();
    menuPane.setMaxWidth(gameWindow.getWidth());
    menuPane.setMaxHeight(gameWindow.getHeight());
    menuPane.getStyleClass().add("menu-background");
    root.getChildren().add(menuPane);
    //main pane(within menu pane)
    mainPane = new BorderPane();
    mainPane.setMaxWidth(gameWindow.getWidth()-25);
    mainPane.setMaxHeight(gameWindow.getHeight()-25);
    menuPane.getChildren().add(mainPane);

    createLobby = new Text("Create Lobby");
    createLobby.setOnMouseClicked(e->{
      createLobby();
    });
    buttons = new VBox();
    createLobby.getStyleClass().add("option-button");
    var channelLabel = new Text("Current lobbies: ");
    channelLabel.getStyleClass().add("option3-button");
    buttons.getChildren().addAll(createLobby,channelLabel);
    buttons.setMinWidth(gameWindow.getWidth()/2);
    //Buttons vbox added in MainPane, within menuPane
    mainPane.getChildren().add(buttons);

    /*
    Chat/lobby messages
     */


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
        communicator.send("JOIN " + channel);
        buttons.getChildren().remove(createLobby);
        join(channel, false);
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
    Text newMessage = new Text(data);
    messages.getChildren().add(newMessage);
    scroller.setVvalue(scroller.getVmax());
  }

  public void join(String channel, boolean host){
    currentChannel = channel;
    if(!inChannel) {
      messages.getChildren().clear();
      communicator.send("JOIN " + channel);
      inChannel = true;


      messages.getChildren().clear();
      rightPane = new VBox();
      mainPane.setRight(rightPane);
      communicator.send("USERS");
      channelInfo = new SimpleStringProperty();
      channelInfo.set("Lobby: "+channel +"\n"+ "Users: "+"\n" + userTemp);
      Label channelLabel = new Label();
      channelLabel.textProperty().bind(channelInfo);
      channelLabel.getStyleClass().add("channelLabel");

      scroller = new ScrollPane();
      messages.getStyleClass().add("messages");
      scroller.getStyleClass().add("scroller");
//      scroller.setStyle("-fx-border-color: white; -fx-border-width: 2px;");
      scroller.setContent(messages);

      /*
      Option Bar
       */
      sendText = new TextField();
      Button sendButton = new Button("Send");
      sendButton.getStyleClass().add("lobby-button");
      sendButton.setAlignment(Pos.BOTTOM_RIGHT);
      sendButton.setOnMouseClicked(e -> {
        textFieldAction();
      });
      sendText.setOnKeyPressed(e -> {
        if (e.getCode().getName().equals("Enter")) {
          textFieldAction();
        }
      });


      Text leaveButton = new Text("Leave");
      leaveButton.getStyleClass().add("option2-button");
      leaveButton.setOnMouseClicked(e->{
        communicator.send("PART");
        //So user can join another lobby
        inChannel = false;
        mainPane.getChildren().remove(rightPane);
        buttons.getChildren().add(createLobby);
      });
      HBox messageField = new HBox();
      messageField.getChildren().addAll(sendText,sendButton);
      HBox chatButtons = new HBox();
      HBox.setHgrow(sendText, javafx.scene.layout.Priority.ALWAYS);
      chatButtons.getChildren().addAll(leaveButton);

      scroller.setPrefHeight(gameWindow.getHeight()-100);
      scroller.setFitToWidth(true);


      rightPane.prefWidthProperty().bind(mainPane.widthProperty().divide(2));
      rightPane.setStyle("-fx-border-color: white; -fx-border-width: 2px;-fx-background-color: rgba(0,0,0,0.5);");
      rightPane.setSpacing(20);
      Region spacer = new Region();
      spacer.setPrefHeight(60);
      rightPane.setPadding(new javafx.geometry.Insets(10,10,10,10));
      rightPane.getChildren().addAll(channelLabel,spacer,scroller,messageField, chatButtons);

      if(host){
        Text startButton = new Text("Start Game");
        startButton.getStyleClass().add("option2-button");
        startButton.setOnMouseClicked(e->{
          communicator.send("START");
          gameWindow.startChallenge();
        });
        chatButtons.getChildren().add(startButton);
        chatButtons.setAlignment(Pos.CENTER);
        chatButtons.setSpacing(10);
      }

    }
    else{
      alreadyChanneled();
    }

  }

  public void textFieldAction(){
      if(sendText.getText().startsWith("/nick")){
        String[] parts = sendText.getText().split(" ");
        String newUsername = parts[1];
        communicator.send("NICK " + newUsername);
        sendText.clear();
      }
      else if(sendText.getText().startsWith("/leave")){
        communicator.send("PART");
        //So user can join another lobby
        inChannel = false;
        mainPane.getChildren().remove(rightPane);
        buttons.getChildren().add(createLobby);
      }
      else {
        communicator.send("MSG " + sendText.getText());
        sendText.clear();
      }
  }

  public void createLobby(){
    if(!inChannel){
      buttons.getChildren().remove(createLobby);
      nameField = new TextField();
      nameField.setPromptText("Enter lobby name");
      nameField.setMinWidth(200);
      nameField.setMinHeight(30);
      buttons.getChildren().add(nameField);
      nameField.setOnKeyPressed(e->{
        if(e.getCode().getName().equals("Enter")){
          communicator.send("CREATE " + nameField.getText());
          join(nameField.getText(), true);
          inChannel = true;
          buttons.getChildren().remove(nameField);
        }
      });
    }
    else{
      alreadyChanneled();
    }
  }

  public void alreadyChanneled(){
    var errMessage = new Text("You are already in a lobby");
    errMessage.getStyleClass().add("option3-button");
    buttons.getChildren().add(errMessage);
    // Set up a fade-in transition
    FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), errMessage);
    fadeIn.setFromValue(0.0);
    fadeIn.setToValue(1.0);

    // Set up a fade-out transition
    FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), errMessage);
    fadeOut.setFromValue(1.0);
    fadeOut.setToValue(0.0);
    fadeOut.setDelay(Duration.seconds(1)); // Delay fade-out by 2 seconds

    // Play the fade-in transition followed by the fade-out transition
    fadeIn.play();
    fadeIn.setOnFinished(e -> {
      fadeOut.play();
    });
    fadeOut.setOnFinished(e -> {
      buttons.getChildren().remove(errMessage);
    });
  }

  private void keyboardControls(){
    gameWindow.getScene().setOnKeyPressed(e->{
      switch (e.getCode()){
        case ESCAPE:
          communicator.send("PART");
          communicator.send("QUIT");
          inChannel = false;
          gameWindow.startMenu();
          break;
      }
  });
  }

}
