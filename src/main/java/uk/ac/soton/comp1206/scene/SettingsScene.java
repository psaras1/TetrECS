package uk.ac.soton.comp1206.scene;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.App;
import uk.ac.soton.comp1206.game.Multimedia;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The Settings Scene displays the settings of the game
 * Currently only the volume can be adjusted
 */
public class SettingsScene extends BaseScene{

  /**
   * Create a new scene, passing in the GameWindow the scene will be displayed in
   *
   * @param gameWindow the game window
   */
  private Slider volumeSlider;
  private static final Logger logger = LogManager.getLogger(SettingsScene.class);


  public SettingsScene(GameWindow gameWindow) {
    super(gameWindow);
  }

  @Override
  public void initialise() {
    volumeSlider.setValue(App.getCurrentVolume()*100);
    keybindings();

  }

  @Override
  public void build() {
    this.root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

    var menuPane = new StackPane();
    menuPane.setMaxWidth(gameWindow.getWidth());
    menuPane.setMaxHeight(gameWindow.getHeight());
    menuPane.getStyleClass().add("menu-background");
    root.getChildren().add(menuPane);

    var mainPane = new BorderPane();
    menuPane.setStyle("-fx-background-color: transparent;");
    menuPane.getChildren().add(mainPane);


    VBox settings = new VBox(10);
    settings.setAlignment(Pos.CENTER);


    /*title*/
    var title = new Text ("Settings");
    title.getStyleClass().add("bigtitle1");
    var topBox = new VBox();
    topBox.setAlignment(Pos.CENTER);
    topBox.getChildren().add(title);
    mainPane.setTop(topBox);

    /*
    center
     */
    /*volume*/
    var volumeLabel = new Text("Volume");
    volumeLabel.getStyleClass().add("option3-button");
    double volumeTemp = App.getCurrentVolume()*100;
    volumeSlider = new Slider(0, 100, volumeTemp);//min, max, default
    volumeSlider.setShowTickLabels(true);
    volumeSlider.setShowTickMarks(true);
    volumeSlider.setMajorTickUnit(50);
    volumeSlider.setMinorTickCount(4);
    volumeSlider.getStyleClass().add("slider");
    volumeSlider.setStyle("-fx-text-fill: white !important; -fx-font-size: 20px;");
    volumeSlider.setMaxWidth(400);

    volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
      double volume = newValue.doubleValue() / 100;
      App.setCurrentVolume(volume);
      Multimedia.adjustGlobalVolume(volume);
    });
    var volumeBox = new VBox();
    volumeBox.setSpacing(15);
    volumeBox.getChildren().addAll(volumeLabel, volumeSlider);
    volumeBox.setAlignment(Pos.CENTER);
    settings.getChildren().add(volumeBox);
    settings.setAlignment(Pos.CENTER);

    /*background image*/
    var imageGrid = new GridPane();
    imageGrid.setHgap(10);
    imageGrid.setVgap(10);
    imageGrid.setAlignment(Pos.CENTER);
    var imagesLabel = new Text("Challenge background Image:");
    imagesLabel.setTextAlignment(TextAlignment.CENTER);
    imagesLabel.getStyleClass().add("option3-button");
    settings.getChildren().add(imagesLabel);

    var one = new ImageView(new Image(getClass().getResource("/images/1.jpg").toString()));
    var two = new ImageView(new Image(getClass().getResource("/images/2.jpg").toString()));
    var three = new ImageView(new Image(getClass().getResource("/images/3.jpg").toString()));
    var four = new ImageView(new Image(getClass().getResource("/images/4.jpg").toString()));
    var five = new ImageView(new Image(getClass().getResource("/images/5.jpg").toString()));
    var six = new ImageView(new Image(getClass().getResource("/images/6.jpg").toString()));

    one.setFitWidth(240);
    one.setPreserveRatio(true);
    imageGrid.add(one, 0, 1);
    one.setOnMouseClicked(e->{
      logger.info("Image 1 selected");
      App.challengeBackground = 1;
    });
    two.setFitWidth(240);
    two.setPreserveRatio(true);
    imageGrid.add(two, 1, 1);
    two.setOnMouseClicked(e->{
      logger.info("Image 2 selected");
      App.challengeBackground = 2;
    });
    three.setFitWidth(240);
    three.setPreserveRatio(true);
    imageGrid.add(three, 2, 1);
    three.setOnMouseClicked(e->{
      logger.info("Image 3 selected");
      App.challengeBackground = 3;
    });

    four.setFitWidth(240);
    four.setPreserveRatio(true);
    imageGrid.add(four, 0, 2);
    four.setOnMouseClicked(e->{
      logger.info("Image 4 selected");
      App.challengeBackground = 4;
    });

    five.setFitWidth(240);
    five.setPreserveRatio(true);
    imageGrid.add(five, 1, 2);
    five.setOnMouseClicked(e->{
      logger.info("Image 5 selected");
      App.challengeBackground = 5;
    });

    six.setFitWidth(240);
    six.setPreserveRatio(true);
    imageGrid.add(six, 2, 2);
    six.setOnMouseClicked(e->{
      logger.info("Image 6 selected");
      App.challengeBackground = 6;
    });

    settings.getChildren().add(imageGrid);


    BorderPane.setMargin(title, new Insets(20, 0, 0, 0));
    mainPane.setCenter(settings);


  }
  /*
  * Keybinds for the scene
   */
  public void keybindings(){
    scene.setOnKeyPressed(e->{
      switch (e.getCode()){
        case ESCAPE:
          logger.info("Volume set to: " + App.getCurrentVolume()*100);
          gameWindow.startMenu();
          break;
      }
    });
  }
}
