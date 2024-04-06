package uk.ac.soton.comp1206.scene;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.ScoreList;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.Multimedia;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The scores scene of the game. Holds the scores of the game for the player to see.
 */
public class ScoresScene extends BaseScene {
  /*
  Stores current game instance
   */

  private Game game;
  private Multimedia scoresMusic = new Multimedia();

  /*
List of scores (Observable means it can be observed for changes, have a listener attached to it)
 */
  private ObservableList<Pair<String, Integer>> localScoresObservable = new SimpleListProperty<>();
  /*
  Temporary score storage
   */
  private ScoreList scoreList;
  /*
  Name of the player
   */
  private StringProperty name = new SimpleStringProperty("");
  /*
  Stored as a pair
   */
  private Integer currentScore;
  private String username;
  /*
Online scores
   */
  private ScoreList remoteScoresList;
  public ObservableList<Pair<String, Integer>> remoteScores = new SimpleListProperty<>();
  private final Communicator communicator;
  private final SimpleStringProperty onlineNames = new SimpleStringProperty("");
  /*
  Used to store top 10 online scores, current score is compared to these
   */
  private ArrayList<Pair<String, Integer>> onlineScores;


  private static final Logger logger = LogManager.getLogger(ScoresScene.class);

  /*
  Constructor for ScoresScene
   */
  public ScoresScene(GameWindow gameWindow, Game game) {
    super(gameWindow);
    this.game = game;
    currentScore = game.score.get();
    logger.info("Current score: {}", currentScore);
    logger.info("Creating Scores Scene");
    communicator = gameWindow.getCommunicator();
  }

  /**
   * Second constructor for ScoresScene, allows it to be scene from menu scene No need to have a
   * game instance
   * TODO: Hasn't been implemented yet
   *
   * @param gameWindow
   */
  public ScoresScene(GameWindow gameWindow) {
    super(gameWindow);
    logger.info("Creating Scores Scene");
    communicator = gameWindow.getCommunicator();
  }

  public void loadOnlineScores(String s) {
    remoteScoresList.getChildren().clear();
   /*
   Message from the server is split into individual lines (indLines)
    */
    String[] indLines = s.split("\\R"); //line break, matches \n,\r, \r\n
    ArrayList<Pair<String, Integer>> toReturn = new ArrayList<>();

    for (String line : indLines) {
      String[] parts = line.split(":");
      if (parts.length == 2) {
        toReturn.add(new Pair<>(parts[0], Integer.parseInt(parts[1])));
      } else {
        logger.info("Invalid line in scores file");
      }
    }

    toReturn.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
    onlineScores = toReturn;
    remoteScores = FXCollections.observableArrayList(toReturn);
    SimpleListProperty<Pair<String, Integer>> remoteScoresWrapper = new SimpleListProperty<>(
        remoteScores);
    remoteScoresList.returnScores().bind(remoteScoresWrapper);
    remoteScoresList.returnName().bind(onlineNames);
    remoteScoresList.updateList();
  }


  /**
   * Method to handle the end of the game Loads the score scene
   */
  public void onGameEnd() {
    logger.info("Game has ended");
    gameWindow.showScores(game);
  }

  @Override
  public void initialise() {

    logger.info("Initialising " + this.getClass().getName());
    /*
    Return to menu on escape pressed
     */
    scene.setOnKeyPressed(e -> {
      logger.info("Key Pressed: {}", e.getCode());
      switch (e.getCode()) {
        case ESCAPE:
          logger.info("Escape pressed, returning to menu");
          gameWindow.startMenu();
          scoresMusic.stopBackgroundMusic();
          break;
      }
    });
    communicator.send("HISCORES");
    communicator.addListener(this::communicationListener);
  }

  /*
  Build the Scores Window
   */
  @Override
  public void build() {
    /*
    Logic: load scores from file, sort them, add new score if it is a high score
     */
    scoresMusic.playBackgroundMusic("/music/end.wav");
    localScoresObservable = FXCollections.observableArrayList(loadScores());
    scoreList = new ScoreList();
    remoteScoresList = new ScoreList();
    localScoresObservable.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
    AtomicReference<SimpleListProperty<Pair<String, Integer>>> scoresWrapper = new AtomicReference<>(
        new SimpleListProperty<>(localScoresObservable));
    scoreList = new ScoreList();
    scoreList.returnScores().bind(scoresWrapper.get());
    scoreList.returnName().bind(name);

    /**
     * ScorePane used as parent for all elements
     * MainPane used as parent for all elements, stored within ScorePane
     */

    logger.info("Building " + this.getClass().getName());
    root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());
    var scorePane = new StackPane();
    scorePane.setMaxWidth(gameWindow.getWidth());
    scorePane.setMaxHeight(gameWindow.getHeight());
    scorePane.getStyleClass().add("menu-background");
    root.getChildren().add(scorePane);

    var mainPane = new BorderPane();
    scorePane.getChildren().add(mainPane);

    /*
    Top
     */
    /*title*/
    var title = new Text("Scores:  ");
    title.getStyleClass().add("bigtitle");
    title.styleProperty().setValue("-fx-effect: dropshadow(gaussian, magenta, 40, 0, 0, 0);");
    var topBox = new HBox();
    topBox.setAlignment(Pos.CENTER);
    BorderPane.setMargin(topBox, new Insets(10, 0, 0, 0));
    topBox.getChildren().add(title);
    mainPane.setTop(topBox);

    /*
    Center
     */
    /**
     * Boolean newHS, gets triggered if the current score is higher than any of the scores in the list
     * If newHS is true, the user is prompted to enter their name, their score is displayed in top 10
     * If newHS is false, the user is prompted to proceed and view score board
     */
    boolean newHS = false;
    for (Pair<String, Integer> score : scoreList.returnScores()) {
      if (currentScore > score.getValue()) {
        newHS = true;
      }
    }
    if (newHS) {
      var nameLabel = new Text("Enter your name: ");
      nameLabel.getStyleClass().add("option3-button");
      TextField nameField = new TextField();
      nameField.setMaxWidth(gameWindow.getWidth() - 100);

      var display = new VBox();
      var submit = new Text("Submit");
      submit.getStyleClass().add("option-button");
      submit.setOnMouseClicked(e -> {
        username = nameField.getText();
        ArrayList<Pair<String, Integer>> newScores = new ArrayList<>();
        for (Pair<String, Integer> score : scoreList.returnScores()) {
          newScores.add(score);
        }
        newScores.add(new Pair<>(username, currentScore));
        writeScores(newScores);
        localScoresObservable = FXCollections.observableArrayList(loadScores());
        scoreList = new ScoreList();
        localScoresObservable.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        scoresWrapper.set(new SimpleListProperty<>(localScoresObservable));
        scoreList = new ScoreList();
        scoreList.returnScores().bind(scoresWrapper.get());
        scoreList.returnName().bind(name);
        display.getChildren().clear();
        finishBuild(mainPane);

      });
      display.getChildren().addAll(nameLabel, nameField, submit);
      display.setSpacing(20);
      mainPane.setCenter(display);
      display.setAlignment(Pos.CENTER);

    } else {
      var nameLabel = new Text("Your score: " + currentScore);
      nameLabel.getStyleClass().add("option3-button");
      var info = new VBox();
      Text submit = new Text("Proceed");
      submit.getStyleClass().add("option-button");
      Region spacer3 = new Region();
      spacer3.setPrefHeight(20);
      info.getChildren().addAll(nameLabel, spacer3, submit);
      mainPane.setCenter(info);
      info.setAlignment(Pos.CENTER);
      submit.setOnMouseClicked(e -> {
        ArrayList<Pair<String, Integer>> newScores = new ArrayList<>();
        for (Pair<String, Integer> score : scoreList.returnScores()) {
          newScores.add(score);
        }
        writeScores(newScores);
        localScoresObservable = FXCollections.observableArrayList(loadScores());
        scoreList = new ScoreList();
        localScoresObservable.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        scoresWrapper.set(new SimpleListProperty<>(localScoresObservable));
        scoreList = new ScoreList();
        scoreList.returnScores().bind(scoresWrapper.get());
        scoreList.returnName().bind(name);
        info.getChildren().clear();
        finishBuild(mainPane);
      });

    }
  }

  /**
   * Finish building the scores scene Takes in the mainPane and adds the scores to it in a VBox
   *
   * @param mainPane
   */
  public void finishBuild(BorderPane mainPane) {
    Boolean worthy = false;
    for (Pair<String, Integer> score : onlineScores) {
      if (currentScore > score.getValue()) {
        worthy = true;
      }
    }
    if (worthy) {
      logger.info("Worthy score{}", currentScore);
      writeOnlineScore(username, currentScore);
    }
    /*
    Local scores
     */
    var scoreBoxLocalLabel = new Text("Local Scores");
    Region spacer = new Region();
    spacer.setPrefHeight(20);
    scoreBoxLocalLabel.getStyleClass().add("heading");
    var scoreBoxLocal = new VBox();
    scoreBoxLocal.setAlignment(Pos.CENTER);
    scoreBoxLocal.getChildren().addAll(scoreBoxLocalLabel, spacer, scoreList);
  /*
  Remote scores
   */
    var scoreBoxRemoteLabel = new Text("Remote Scores");
    Region spacer1 = new Region();
    spacer1.setPrefHeight(20);
    scoreBoxRemoteLabel.getStyleClass().add("heading");
    var scoreBoxRemote = new VBox();
    scoreBoxRemote.setAlignment(Pos.CENTER);
    scoreBoxRemote.getChildren().addAll(scoreBoxRemoteLabel, spacer1, remoteScoresList);
    /*
    Scores container
     */
    var centralBox = new HBox();
    centralBox.setAlignment(Pos.CENTER);
    centralBox.getChildren().addAll(scoreBoxLocal, scoreBoxRemote);
    mainPane.setCenter(centralBox);
  }

  /**
   * Load scores from text file If file does not exist, create a new file and fill it with default
   * scores If file does exist, load first ten scores from file, add them to an ArrayList of paired
   * usernames and scores and return it
   *
   * @return
   */
  public ArrayList<Pair<String, Integer>> loadScores() {
    ArrayList<Pair<String, Integer>> scores = new ArrayList<>();
    File file = new File("Scores.txt");
    if (!file.exists()) {
      ArrayList<Pair<String, Integer>> scoresFiller = new ArrayList<>();
      scores.add(new Pair<>("Guest", 300));
      scores.add(new Pair<>("Guest", 250));
      scores.add(new Pair<>("Guest", 200));
      scores.add(new Pair<>("Guest: ", 150));
      scores.add(new Pair<>("Guest", 100));
      scores.add(new Pair<>("Guest", 50));
      scores.add(new Pair<>("Guest", 40));
      scores.add(new Pair<>("Guest", 30));
      scores.add(new Pair<>("Guest", 20));
      scores.add(new Pair<>("Guest", 10));
      writeScores(scoresFiller);
    }
    try {
      FileInputStream fis = new FileInputStream(file);
      BufferedReader br = new BufferedReader(new InputStreamReader(fis));
      try {
        String line;
        while ((line = br.readLine()) != null) {
          String[] parts = line.split(":");
          if (parts.length == 2) {
            scores.add(new Pair<>(parts[0], Integer.parseInt(parts[1])));
          } else {
            logger.info("Invalid line in scores file");
          }
        }
        br.close();
      } catch (IOException e) {
        logger.info("Error reading scores file");
      }
    } catch (FileNotFoundException e) {
      logger.info("Error opening scores file");
    }
    return scores;
  }

  /**
   * Write scores to text file Called when a new score is added to the list
   *
   * @param scores
   */
  public void writeScores(ArrayList<Pair<String, Integer>> scores) {
    scores.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
    try {
      if (new File("Scores.txt").createNewFile()) {
        logger.info("Scores file created");
      }
    } catch (IOException e) {
      logger.info("Error creating scores file");
    }
    try {
      BufferedWriter writer = new BufferedWriter(new FileWriter("Scores.txt"));
      int scoreCount = 0;
      for (Pair<String, Integer> score : scores) {
        writer.write(score.getKey() + ":" + score.getValue() + "\n");
        scoreCount++;
        if (scoreCount > 9) {
          break;
        }
      }
      writer.close();
    } catch (IOException e) {
      logger.error("Error writing scores file");
    }
  }

  /**
   * Listener for communication with the server
   * Takes in a string, splits it into parts, checks if the command is HISCORES
   * If it is, loads the online scores(calls loadOnlineScores())
   * @param s
   */
  public void communicationListener(String s) {
    String[] parts = s.split(" ", 2);
    String command = parts[0];

    if (command.equals("HISCORES")) {
      if (parts.length > 1) {
        String scores = parts[1];
        loadOnlineScores(scores);
      }
    }
  }

  /**
   * Called when a score is achieved > than the top 10 online scores from the server
   * @param name
   * @param score
   */
  public void writeOnlineScore(String name, Integer score) {
    onlineScores.add(0, new Pair<String, Integer>(name, score));
    communicator.send("HISCORE " + name + ":" + score);
  }
}
