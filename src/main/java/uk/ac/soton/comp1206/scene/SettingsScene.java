package uk.ac.soton.comp1206.scene;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import uk.ac.soton.comp1206.game.Multimedia;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

public class SettingsScene extends BaseScene{

  /**
   * Create a new scene, passing in the GameWindow the scene will be displayed in
   *
   * @param gameWindow the game window
   */
  private Slider volumeSlider;

  public SettingsScene(GameWindow gameWindow) {
    super(gameWindow);
  }

  @Override
  public void initialise() {
    keybinds();

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


    VBox settings = new VBox();

    /*title*/
    var title = new Text ("Settings");
    title.getStyleClass().add("bigtitle");
    var topBox = new VBox();
    topBox.setAlignment(Pos.CENTER);
    topBox.getChildren().add(title);
    mainPane.setTop(topBox);

    var volumeLabel = new Text("Volume");
    volumeLabel.getStyleClass().add("setting");
    volumeSlider = new Slider(0, 100, 50);//min, max, default
    volumeSlider.setShowTickLabels(true);
    volumeSlider.setShowTickMarks(true);
    volumeSlider.setMajorTickUnit(50);
    volumeSlider.setMinorTickCount(4);
    volumeSlider.getStyleClass().add("slider");
    volumeSlider.setStyle("-fx-text-fill: white !important; -fx-font-size: 20px;");
    volumeSlider.setMaxWidth(400);

    volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
      double volume = newValue.doubleValue() / 100;

      Multimedia.adjustGlobalVolume(volume);
    });

    var volumeBox = new VBox();
    volumeBox.setSpacing(15);
    volumeBox.getChildren().addAll(volumeLabel, volumeSlider);
    volumeBox.setAlignment(Pos.CENTER);
    settings.getChildren().add(volumeBox);
    settings.setAlignment(Pos.CENTER);

    mainPane.setCenter(settings);


  }
  public void keybinds(){
    scene.setOnKeyPressed(e->{
      switch (e.getCode()){
        case ESCAPE:
          gameWindow.startMenu();
          break;
      }
    });
  }
}
