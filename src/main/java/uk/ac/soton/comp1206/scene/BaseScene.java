package uk.ac.soton.comp1206.scene;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import uk.ac.soton.comp1206.game.Multimedia;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * A Base Scene used in the game. Handles common functionality between all scenes.
 */
public abstract class BaseScene {

    protected final GameWindow gameWindow;

    protected GamePane root;
    protected Scene scene;

    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     * @param gameWindow the game window
     */
    public BaseScene(GameWindow gameWindow) {
        this.gameWindow = gameWindow;
    }

    /**
     * Initialise this scene. Called after creation
     */
    public abstract void initialise();

    /**
     * Build the layout of the scene
     */
    public abstract void build();

    /**
     * Create a new JavaFX scene using the root contained within this scene
     * @return JavaFX scene
     */
    public Scene setScene() {
        var previous = gameWindow.getScene();
        Scene scene = new Scene(root, previous.getWidth(), previous.getHeight(), Color.BLACK);
        scene.getStylesheets().add(getClass().getResource("/style/game.css").toExternalForm());
        this.scene = scene;
        return scene;
    }

    /**
     * Get the JavaFX scene contained inside
     * @return JavaFX scene
     */
    public Scene getScene() {
        return this.scene;
    }
    /**
     * Create a mute button(more convenient to use as a method, to be used in other scenes too)
     * @return Button object
     */
    public Button createMuteButton(Multimedia music,String path){
        Image muteImage = new Image(getClass().getResource("/images/mute.png").toString());
        Image unmuteImage = new Image(getClass().getResource("/images/play.png").toString());
        ImageView muteImageView = new ImageView(muteImage);
        var muteButton = new Button("",muteImageView);
        muteImageView.setFitHeight(30);
        muteImageView.setFitWidth(30);
        muteButton.setOnAction(actionEvent -> {
            if(!music.isPlaying()){
                music.playBackgroundMusic(path);
                muteImageView.setImage(muteImage);
            }else{
                music.stopBackgroundMusic();
                muteImageView.setImage(unmuteImage);
            }
        });
        //Set the background of the button to be transparent
        muteButton.setBackground(null);
        return muteButton;
    }

}
