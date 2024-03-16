package uk.ac.soton.comp1206.scene;

import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.game.Multimedia;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The main menu of the game. Provides a gateway to the rest of the game.
 */
public class MenuScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);
    private Multimedia menuMusic = new Multimedia();

    /**
     * Create a new menu scene
     * @param gameWindow the Game Window this will be displayed in
     */
    public MenuScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Menu Scene");
    }

    /**
     * Build the menu layout
     */
    @Override
    public void build() {

        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var menuPane = new StackPane();
        menuPane.setMaxWidth(gameWindow.getWidth());
        menuPane.setMaxHeight(gameWindow.getHeight());
        menuPane.getStyleClass().add("menu-background");
        root.getChildren().add(menuPane);

//        Implement background music on the Menu
        this.menuMusic.playBackgroundMusic("/music/menu.mp3");

        var mainPane = new BorderPane();
        menuPane.getChildren().add(mainPane);

        //Awful title
        var title = new Text("TetrECS");
        title.getStyleClass().add("title");
        HBox titleContainer = new HBox();
        titleContainer.setAlignment(Pos.CENTER);
        titleContainer.getChildren().add(title);
        mainPane.setTop(titleContainer);


        //For now, let us just add a button that starts the game. I'm sure you'll do something way better.
        //Added multiplayer and chat buttons
        var playButton = new Button("Play");
        var multiplayerButton = new Button("Multiplayer");
        var chatButton = new Button("Chat");
        multiplayerButton.getStyleClass().add("play-button");
        multiplayerButton.setMaxWidth(150);
        chatButton.getStyleClass().add("play-button");
        playButton.getStyleClass().add("play-button");

        VBox buttons = new VBox();
        buttons.getChildren().addAll(playButton, multiplayerButton, chatButton);
        buttons.setAlignment(Pos.CENTER);
        buttons.setSpacing(20);
        mainPane.setCenter(buttons);

        //Bind the button action to the startGame method in the menu
        playButton.setOnAction(this::startGame);
    }

    /**
     * Initialise the menu
     */
    @Override
    public void initialise() {

    }

    /**
     * Handle when the Start Game button is pressed
     *  Stops menu music and starts the game
     * @param event event
     */
    private void startGame(ActionEvent event) {
        this.menuMusic.stopBackgroundMusic();
        gameWindow.startChallenge();
    }

}
