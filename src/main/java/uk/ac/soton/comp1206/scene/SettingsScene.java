package uk.ac.soton.comp1206.scene;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.game.Multimedia;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The Settings Scene displays the settings of the game Currently only the volume can be adjusted
 */
public class SettingsScene extends BaseScene {

  /**
   * Create a new scene, passing in the GameWindow the scene will be displayed in
   *
   * @param gameWindow the game window
   */
  private Slider volumeSlider;

  private static final Logger logger = LogManager.getLogger(SettingsScene.class);
  /*Multimedia object to play audio*/
  private Multimedia multimedia = new Multimedia();
  /*Default volume*/
  private static double volume = 0.3;
  private StackPane menuPane = new StackPane();
  /*Default theme*/
  public static StringProperty theme = new SimpleStringProperty("challenge-background1");


  public SettingsScene(GameWindow gameWindow) {
    super(gameWindow);
  }

  @Override
  public void initialise() {
    this.theme.addListener((e) -> {
      logger.info("Theme changed to {}", theme.get());
      menuPane.getStyleClass().clear();
      menuPane.getStyleClass().add(theme.get());
    });
    volumeSlider.setValue(volume * 100);
    keybindings();

  }

  @Override
  public void build() {
    this.root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

    menuPane.setMaxWidth(gameWindow.getWidth());
    menuPane.setMaxHeight(gameWindow.getHeight());
    menuPane.getStyleClass().add(theme.get());
    root.getChildren().add(menuPane);

    var mainPane = new BorderPane();
    menuPane.getChildren().add(mainPane);

    VBox settings = new VBox(10);
    settings.setAlignment(Pos.CENTER);


    /*title*/
    var title = new Text("Settings");
    title.getStyleClass().add("bigtitle1");
    var topBox = new VBox();
    topBox.setAlignment(Pos.CENTER);
    topBox.getChildren().add(title);
    mainPane.setTop(topBox);

    /*
    center
     */
    /*Sound volume*/
    var volumeLabel = new Text("Master Volume: ");
    volumeLabel.getStyleClass().add("option3-button");
    double volumeTemp = volume * 100;
    volumeSlider = new Slider(0, 100, volumeTemp);//min, max, default
    volumeSlider.setShowTickLabels(true);
    volumeSlider.setShowTickMarks(true);
    volumeSlider.setMajorTickUnit(50);
    volumeSlider.setMinorTickCount(4);
    volumeSlider.getStyleClass().add("slider");
    volumeSlider.setStyle("-fx-text-fill: white !important; -fx-font-size: 20px;");
    volumeSlider.setMaxWidth(400);

    volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
      volume = newValue.doubleValue() / 100;
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

    /*
    Each image is a clickable object that changes the theme of the game
    On hover, the image will enlarge and add a glow effect
    On click, sound effect will play
     */
    one.setFitWidth(240);
    one.setPreserveRatio(true);
    one.getStyleClass().add("setting-image");
    imageGrid.add(one, 0, 1);
    one.setOnMouseClicked(e -> {
      logger.info("Image 1 selected");
      multimedia.playAudio("/sounds/transition.wav");
      this.theme.set("challenge-background1");
    });
    two.setFitWidth(240);
    two.setPreserveRatio(true);
    two.getStyleClass().add("setting-image");
    imageGrid.add(two, 1, 1);
    two.setOnMouseClicked(e -> {
      logger.info("Image 2 selected");
      multimedia.playAudio("/sounds/transition.wav");
      this.theme.set("challenge-background2");
    });
    three.setFitWidth(240);
    three.setPreserveRatio(true);
    three.getStyleClass().add("setting-image");
    imageGrid.add(three, 2, 1);
    three.setOnMouseClicked(e -> {
      logger.info("Image 3 selected");
      multimedia.playAudio("/sounds/transition.wav");
      this.theme.set("challenge-background3");
    });

    four.setFitWidth(240);
    four.setPreserveRatio(true);
    four.getStyleClass().add("setting-image");
    imageGrid.add(four, 0, 2);
    four.setOnMouseClicked(e -> {
      logger.info("Image 4 selected");
      multimedia.playAudio("/sounds/transition.wav");
      this.theme.set("challenge-background4");
    });

    five.setFitWidth(240);
    five.setPreserveRatio(true);
    five.getStyleClass().add("setting-image");
    imageGrid.add(five, 1, 2);
    five.setOnMouseClicked(e -> {
      logger.info("Image 5 selected");
      multimedia.playAudio("/sounds/transition.wav");
      this.theme.set("challenge-background5");
    });

    six.setFitWidth(240);
    six.setPreserveRatio(true);
    six.getStyleClass().add("setting-image");
    imageGrid.add(six, 2, 2);
    six.setOnMouseClicked(e -> {
      logger.info("Image 6 selected");
      multimedia.playAudio("/sounds/transition.wav");
      this.theme.set("challenge-background6");
    });

    settings.getChildren().add(imageGrid);

    BorderPane.setMargin(title, new Insets(20, 0, 0, 0));
    mainPane.setCenter(settings);

    /*menu button, top left*/
    var escape = new Text("Menu");
    escape.setOnMouseClicked(e -> exit());
    escape.getStyleClass().add("option1-button");
    AnchorPane menuButton = new AnchorPane();
    menuButton.getChildren().add(escape);
    AnchorPane.setLeftAnchor(escape, 10.0);
    AnchorPane.setTopAnchor(escape, 5.0);

    mainPane.getChildren().add(menuButton);
  }

  /*
   * Keybinds for the scene
   */
  public void keybindings() {
    scene.setOnKeyPressed(e -> {
      switch (e.getCode()) {
        case ESCAPE:
          exit();
          break;
      }
    });
  }

  /**
   * Save the current configuration to a file called settings.txt
   */
  public static void writeSettings() {
    logger.info("Writing settings to file");
    try {
      if (new File("settings.txt").createNewFile()) {
        logger.info("Settings file created");
      }
    } catch (IOException e) {
      e.printStackTrace();
      logger.error("Error creating settings file");
    }
    try {
      BufferedWriter writer = new BufferedWriter(new FileWriter("settings.txt"));
      writer.write(volume + " ");
      writer.write(theme.get());
      writer.close();
      logger.info("Settings written to file");
    } catch (IOException e) {
      e.printStackTrace();
      logger.error("Error writing settings to file");
    }
  }

  /**
   * Read the settings from the file settings.txt Called when building the MenuScene
   */
  public static void readSettings() {
    logger.info("Reading settings from file");
    if (new File("settings.txt").exists()) {
      try {
        FileInputStream file = new FileInputStream("settings.txt");
        BufferedReader reader = new BufferedReader(new java.io.InputStreamReader(file));
        try {
          String line = reader.readLine();
          String[] settings = line.split(" ");
          volume = Double.parseDouble(settings[0]);
          Multimedia.adjustGlobalVolume(volume);
          theme.set(settings[1]);
          reader.close();
        } catch (IOException e) {
          e.printStackTrace();
          logger.error("Error reading settings from file");
        }
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
    } else {
      logger.info("Settings file does not exist,creating a new one");
      writeSettings();
    }
  }

  /**
   * On exiting the settings scene, write the settings to file and return to the menu
   */
  private void exit() {
    writeSettings();
    gameWindow.startMenu();
  }

}


