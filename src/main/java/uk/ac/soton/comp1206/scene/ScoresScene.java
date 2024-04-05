package uk.ac.soton.comp1206.scene;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.ScoreList;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The scores scene of the game. Holds the scores of the game for the player to see.
 */
public class ScoresScene extends BaseScene {

  private Game game;

  /*
List of scores (Observable means it can be observed for changes, have a listener attached to it)
 */
  private ObservableList<Pair<String, Integer>> localScores = new SimpleListProperty<>();
  private ScoreList scoreList;

  private StringProperty name = new SimpleStringProperty("");
  Integer currentScore;
  String username;


  private static final Logger logger = LogManager.getLogger(InstructionsScene.class);

  public ScoresScene(GameWindow gameWindow, Game game) {
    super(gameWindow);
    this.game = game;
    currentScore = game.score.get();
    logger.info("Current score: {}", currentScore);
    logger.info("Creating Scores Scene");
  }

  /**
   * Second constructor for ScoresScene, allows it to be scene from menu scene No need to have a
   * game instance
   *
   * @param gameWindow
   */
  public ScoresScene(GameWindow gameWindow) {
    super(gameWindow);
    logger.info("Creating Scores Scene");
  }


  /*
  Method to handle the end of the game
   */
  public void onGameEnd() {
    logger.info("Game has ended");
    gameWindow.showScores(game);
  }

  @Override
  public void initialise() {
    logger.info("Initialising " + this.getClass().getName());
//    logger.info("Final score: {}",game.score.get());
    scene.setOnKeyPressed(e -> {
      logger.info("Key Pressed: {}", e.getCode());
      switch (e.getCode()) {
        case ESCAPE:
          logger.info("Escape pressed, returning to menu");
          gameWindow.startMenu();
          break;
      }
    });
  }

  /*
  Build the Scores Window
   */
  @Override
  public void build() {
    localScores = FXCollections.observableArrayList(loadScores());
    scoreList = new ScoreList();
    localScores.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
    AtomicReference<SimpleListProperty<Pair<String, Integer>>> scoresWrapper = new AtomicReference<>(
        new SimpleListProperty<>(localScores));
    scoreList = new ScoreList();
    scoreList.returnScores().bind(scoresWrapper.get());
    scoreList.returnName().bind(name);

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
    /*score list*/
    boolean newHS = false;
    for (Pair<String, Integer> score : scoreList.returnScores()) {
      if (currentScore > score.getValue()) {
        newHS = true;
      }
    }
    if (newHS) {
      var nameLabel = new Text("Enter your name: ");
      nameLabel.getStyleClass().add("heading");
      TextField nameField = new TextField();

      var display = new VBox();
      var submit = new Button("Submit");
      submit.setOnMouseClicked(e -> {
        username = nameField.getText();
        ArrayList<Pair<String, Integer>> newScores = new ArrayList<>();
        for (Pair<String, Integer> score : scoreList.returnScores()) {
          newScores.add(score);
        }
        newScores.add(new Pair<>(username, currentScore));
        writeScores(newScores);
        localScores = FXCollections.observableArrayList(loadScores());
        scoreList = new ScoreList();
        localScores.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        scoresWrapper.set(new SimpleListProperty<>(localScores));
        scoreList = new ScoreList();
        scoreList.returnScores().bind(scoresWrapper.get());
        scoreList.returnName().bind(name);
        display.getChildren().clear();
        finishBuild(mainPane);

      });
      display.setPadding(new Insets(10));
      display.getChildren().addAll(nameLabel, nameField, submit);
      mainPane.setCenter(display);
      display.setAlignment(Pos.CENTER);

    } else {
      var nameLabel = new Text("Your score: " + currentScore);
      nameLabel.getStyleClass().add("heading");
      var info = new VBox();
      Text submit = new Text("Proceed");
      submit.getStyleClass().add("option-button");
      info.getChildren().addAll(nameLabel, submit);
      mainPane.setCenter(info);
      info.setAlignment(Pos.CENTER);
      submit.setOnMouseClicked(e -> {
        ArrayList<Pair<String, Integer>> newScores = new ArrayList<>();
        for (Pair<String, Integer> score : scoreList.returnScores()) {
          newScores.add(score);
        }
        writeScores(newScores);
        localScores = FXCollections.observableArrayList(loadScores());
        scoreList = new ScoreList();
        localScores.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        scoresWrapper.set(new SimpleListProperty<>(localScores));
        scoreList = new ScoreList();
        scoreList.returnScores().bind(scoresWrapper.get());
        scoreList.returnName().bind(name);
        info.getChildren().clear();
        finishBuild(mainPane);
      });

    }
  }

  public void finishBuild(BorderPane mainPane) {
//    var localScores = new Text("Local Scores");
//    localScores.getStyleClass().add("heading");
//    localScores.setTextAlignment(TextAlignment.CENTER);
//
//    Region spacer = new Region();
//    spacer.setPrefHeight(20);

    var scoreBox = new VBox(scoreList);
    scoreBox.setAlignment(Pos.CENTER);

    mainPane.setCenter(scoreBox);
  }

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
          if(parts.length == 2) {
            scores.add(new Pair<>(parts[0], Integer.parseInt(parts[1])));
          }
          else{
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

}
