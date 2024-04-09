package uk.ac.soton.comp1206.scene;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.util.Duration;
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
    public ImageView muteImageView = new ImageView(unmuteImage);

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
        titleContainer.setPadding(new Insets(100));
        titleContainer.setAlignment(Pos.CENTER);
        titleContainer.getChildren().add(title);
        mainPane.setTop(titleContainer);
        //Animation for title image
        Timeline titleTimeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(title.rotateProperty(), 0)),
            new KeyFrame(Duration.seconds(1), new KeyValue(title.rotateProperty(), 10)),
            new KeyFrame(Duration.seconds(2), new KeyValue(title.rotateProperty(), -10)),
            new KeyFrame(Duration.seconds(3), new KeyValue(title.rotateProperty(), 0))
        );
        titleTimeline.setCycleCount(Timeline.INDEFINITE);
        titleTimeline.play();


        //For now, let us just add a button that starts the game. I'm sure you'll do something way better.
        //Added multiplayer and chat buttons
        /*
        *Play button
         */
        var playButton = new Text("Play");
        playButton.getStyleClass().add("option-button");
        playButton.setOnMouseClicked(e -> {
            menuMusic.stopBackgroundMusic();
            transitionSound.playAudio("/sounds/transition.wav");
            gameWindow.startChallenge();
        });
        /*
        *Instructions button
         */
        var instructionsButton = new Text("Instructions");
        instructionsButton.getStyleClass().add("option-button");
        instructionsButton.setOnMouseClicked(e->{
            menuMusic.stopBackgroundMusic();
            transitionSound.playAudio("/sounds/transition.wav");
            gameWindow.loadScene(new InstructionsScene(gameWindow));
        });

        /*
        *Multiplayer button
         */
        var multiplayerButton = new Text("Multiplayer");
        multiplayerButton.getStyleClass().add("option-button");
        multiplayerButton.setOnMouseClicked(e->{
            menuMusic.stopBackgroundMusic();
            transitionSound.playAudio("/sounds/transition.wav");
            gameWindow.loadScene(new LobbyScene(gameWindow));
        });

        /*
        *Chat button
         */
        /*
        *Scores button
         */
        var scoresButton = new Text("Scores");
        scoresButton.getStyleClass().add("option-button");
        scoresButton.setOnMouseClicked(e->{
            menuMusic.stopBackgroundMusic();
            transitionSound.playAudio("/sounds/transition.wav");
            gameWindow.loadScene(new ScoresScene(gameWindow));
        });

        /*
        *Button container
         */
        var buttonContainer = new VBox();
        buttonContainer.getChildren().addAll(playButton, instructionsButton, scoresButton, multiplayerButton);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setSpacing(20);
        mainPane.setCenter(buttonContainer);

        //Mute button implementation
        var muteButton = new Button("",muteImageView);
        muteImageView.setFitHeight(30);
        muteImageView.setFitWidth(30);
        muteImageView.styleProperty().setValue("-fx-effect: dropshadow(gaussian, aqua, 10, 0, 0, 0);");
        muteButton.setBackground(null);
        AnchorPane muteButtonPane = new AnchorPane();
        muteButtonPane.getChildren().add(muteButton);
        AnchorPane.setLeftAnchor(muteButton, 5.0);
        AnchorPane.setBottomAnchor(muteButton, 13.0);
        muteButtonPane.setPickOnBounds(false);
        muteButton.setOnMouseClicked(actionEvent -> {
            if(!menuMusic.isPlaying()){
                menuMusic.playBackgroundMusic("/music/menu.mp3");
                muteImageView.setImage(unmuteImage);
            }else{
                menuMusic.stopBackgroundMusic();
                muteImageView.setImage(muteImage);
            }
        });
        root.getChildren().add(muteButtonPane);

    }

    @Override
    String getMusic() {
        return null;
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
                        muteImageView.setImage(unmuteImage);
                    }else{
                        menuMusic.stopBackgroundMusic();
                        muteImageView.setImage(muteImage);
                    }
                    break;
            }
        });
    }

}
