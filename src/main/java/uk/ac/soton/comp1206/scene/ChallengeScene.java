package uk.ac.soton.comp1206.scene;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;


/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the game.
 */
public class ChallengeScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);
    protected Game game;
    private Label scoreLabel;
    private Label levelLabel;
    private Label livesLabel;
    private Label multiplierLabel;
    private HBox stats;

    /**
     * Create a new Single Player challenge scene
     * @param gameWindow the Game Window
     */
    public ChallengeScene(GameWindow gameWindow) {
        super(gameWindow);
        this.scoreLabel = new Label();
        this.levelLabel = new Label();
        this.livesLabel = new Label();
        this.multiplierLabel = new Label();
        logger.info("Creating Challenge Scene");
    }
    public void bindProperties(){
        scoreLabel.textProperty().bind(Bindings.concat("Score: ",game.getScore().asString()));
        levelLabel.textProperty().bind(Bindings.concat("Level: ",game.getLevel().asString()));
        livesLabel.textProperty().bind(Bindings.concat("Lives: ",game.getLives().asString()));
        multiplierLabel.textProperty().bind(Bindings.concat("Multiplier: ",game.getMultiplier().asString()));
    }

    /**
     * Build the Challenge window
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());
        setupGame();
        bindProperties();
        //To set all text of labels to white, TODO shorten this
        scoreLabel.setStyle("-fx-text-fill: white;");
        levelLabel.setStyle("-fx-text-fill: white;");
        livesLabel.setStyle("-fx-text-fill: white;");
        multiplierLabel.setStyle("-fx-text-fill: white;");

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var mainPane = new BorderPane();

        var challengePane = new StackPane();
        challengePane.setMaxWidth(gameWindow.getWidth());
        challengePane.setMaxHeight(gameWindow.getHeight());
        challengePane.getStyleClass().add("menu-background");
        root.getChildren().add(challengePane);
//        Build the User Interface
        var stats = new HBox(135);
        stats.setAlignment(Pos.CENTER);
        BorderPane.setMargin(stats, new Insets(10,0,0,0));
        mainPane.setTop(stats);
        stats.getChildren().addAll(scoreLabel, levelLabel, livesLabel, multiplierLabel);




        var board = new GameBoard(game.getGrid(),gameWindow.getWidth()/2,gameWindow.getWidth()/2);
        mainPane.setCenter(board);
        challengePane.getChildren().add(mainPane);


        //Handle block on gameboard grid being clicked
        board.setOnBlockClick(this::blockClicked); //calls blockClicked from GameBoard class
    }

    /**
     * Handle when a block is clicked
     * @param gameBlock the Game Block that was clocked
     */
    private void blockClicked(GameBlock gameBlock) { //calls blockClicked method on game, passing through current gameBlock
        game.blockClicked(gameBlock);
    }

    /**
     * Setup the game object and model
     */
    public void setupGame() {
        logger.info("Starting a new challenge");

        //Start new game
        game = new Game(5, 5);
    }

    /**
     * Initialise the scene and start the game
     */
    @Override
    public void initialise() {
        logger.info("Initialising Challenge");
        game.start();
    }

}
