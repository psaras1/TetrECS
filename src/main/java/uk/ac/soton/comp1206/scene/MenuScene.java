package uk.ac.soton.comp1206.scene;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
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
    private Multimedia transitionSound = new Multimedia();
    public Image muteImage = new Image(getClass().getResource("/images/mute.png").toString());
    public Image unmuteImage = new Image(getClass().getResource("/images/play.png").toString());
    public ImageView muteImageView = new ImageView(muteImage);

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
        menuMusic.playBackgroundMusic("/music/menu.mp3");

        var mainPane = new BorderPane();
        menuPane.setStyle("-fx-background-color: transparent;");
        menuPane.getChildren().add(mainPane);

        //TetrECS title image
        Image titleImage = new Image(getClass().getResource("/images/TetrECS.png").toString());
        ImageView title = new ImageView(titleImage);
        title.setFitWidth(400);
        title.setFitHeight(100);
        HBox titleContainer = new HBox();
        titleContainer.setPadding(new Insets(20));
        titleContainer.setAlignment(Pos.CENTER);
        titleContainer.getChildren().add(title);
        mainPane.setTop(titleContainer);


        //For now, let us just add a button that starts the game. I'm sure you'll do something way better.
        //Added multiplayer and chat buttons
        /*
        * The buttons are styled using the play-button class in the css file
         */
        var playButton = new Button("Play");
        var multiplayerButton = new Button("Multiplayer");
        var chatButton = new Button("Chat");
        var instructionsButton = new Button("Instructions");
        instructionsButton.getStyleClass().add("play-button");
        multiplayerButton.getStyleClass().add("play-button");
        multiplayerButton.setMaxWidth(150);
        instructionsButton.setMaxWidth(150);
        chatButton.getStyleClass().add("play-button");
        playButton.getStyleClass().add("play-button");

        VBox buttons = new VBox();
        buttons.getChildren().addAll(playButton, multiplayerButton, chatButton,instructionsButton);
        buttons.setAlignment(Pos.CENTER);
        buttons.setSpacing(20);
        mainPane.setCenter(buttons);


        //Calls startChallenge and stops the menu music
        playButton.setOnMouseClicked(e -> {
            menuMusic.stopBackgroundMusic();
            transitionSound.playAudio("/sounds/transition.wav");
            gameWindow.startChallenge();
        });
        //Bind the button action to the showInstructions method in the menu
        instructionsButton.setOnMouseClicked(e->{
            menuMusic.stopBackgroundMusic();
            transitionSound.playAudio("/sounds/transition.wav");
            gameWindow.loadScene(new InstructionsScene(gameWindow));
        });

        //Mute button implementation
        var muteButton = new Button("",muteImageView);
        muteImageView.setFitHeight(30);
        muteImageView.setFitWidth(30);
        muteButton.setBackground(null);
        AnchorPane muteButtonPane = new AnchorPane();
        muteButtonPane.getChildren().add(muteButton);
        AnchorPane.setLeftAnchor(muteButton, 5.0);
        AnchorPane.setBottomAnchor(muteButton, 5.0);
        muteButtonPane.setPickOnBounds(false);
        muteButton.setOnMouseClicked(actionEvent -> {
            if(!menuMusic.isPlaying()){
                menuMusic.playBackgroundMusic("/music/menu.mp3");
                muteImageView.setImage(muteImage);
            }else{
                menuMusic.stopBackgroundMusic();
                muteImageView.setImage(unmuteImage);
            }
        });
        root.getChildren().add(muteButtonPane);

    }


    /**
     * Initialise the menu
     */
    @Override
    public void initialise() {
        logger.info("Initialising " + this.getClass().getName());
        keyboardControlsMenu();
    }

    /**
     * Keyboard controls for the menu
     */
    public void keyboardControlsMenu(){
        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                //Mute/Unmute music
                case M :
                    if(!menuMusic.isPlaying()){
                        menuMusic.playBackgroundMusic("/music/menu.mp3");
                        muteImageView.setImage(muteImage);
                    }else{
                        menuMusic.stopBackgroundMusic();
                        muteImageView.setImage(unmuteImage);
                    }
                    break;
            }
        });
    }

}
