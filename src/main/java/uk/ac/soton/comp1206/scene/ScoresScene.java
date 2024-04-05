package uk.ac.soton.comp1206.scene;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The scores scene of the game. Holds the scores of the game for the player to see.

 */
public class ScoresScene extends BaseScene {
  private Game game;
  /*Add a localScores SimpleListProperty to hold the current list of scores in the Scene*/
  /*Wrapper around observableScoreList*/
  private ListProperty<Pair<String,Integer>> localScores;
  /*Created from scoreList. Capable of being observed/binded to the ListView*/
  private ObservableList<Pair<String,Integer>> observableScoreList;
  /*Holds the actual data of scores*/
  private List<Pair<String,Integer>> scoreList;
  /*Displays the list of scores*/
  private ListView<Pair<String,Integer>> scoreListView;
  private static final Logger logger = LogManager.getLogger(InstructionsScene.class);
  public ScoresScene(GameWindow gameWindow, Game game) {
    super(gameWindow);
    this.game = game;
    logger.info("Creating Scores Scene");
  }

  /**
   * Second constructor for ScoresScene, allows it to be scene from menu scene
   * No need to have a game instance
   * @param gameWindow
   */
  public ScoresScene(GameWindow gameWindow) {
    super(gameWindow);
    logger.info("Creating Scores Scene");
  }
  /*/
  Method to add scores
   */
  public void addScores(){
    IntegerProperty score = game.getScore();
    Pair<String,Integer> newScore = new Pair<>("Guest",score.getValue());
    observableScoreList.add(newScore);
    scoreList.add(newScore);
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
    logger.info("Final score: {}",game.score.get());
    controls();
  }
/*
Build the Scores Window
 */
  @Override
  public void build() {
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
    BorderPane.setMargin(topBox, new Insets(10,0,0,0));
    topBox.getChildren().add(title);
    mainPane.setTop(topBox);

    /*
    Center
     */
    /*score list*/
    scoreList = new ArrayList<>();
    scoreList.add(new Pair<>("Guest",game.score.get()));
    observableScoreList = FXCollections.observableList(scoreList);
    localScores = new SimpleListProperty<>(observableScoreList);

    scoreListView = new ListView<>(localScores);
    scoreListView.getStyleClass().add("score-list");
    mainPane.setCenter(scoreListView);

  }
  /*
  Method to handle the keyboard controls of the scene
   */
  private void controls(){
    scene.setOnKeyPressed(e -> {
      logger.info("Key Pressed: {}" ,e.getCode());
      switch (e.getCode()) {
        case ESCAPE:
          logger.info("Escape pressed, returning to menu");
          gameWindow.startMenu();
          break;
      }
    });
  }

}
