package uk.ac.soton.comp1206.scene;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.game.Grid;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The instructions scene of the game. Provides a gateway to the rest of the game.
 */

public class InstructionsScene extends BaseScene {

  private static final Logger logger = LogManager.getLogger(InstructionsScene.class);

  private ImageView imageView;

  private BorderPane mainPane = new BorderPane();

  public InstructionsScene(GameWindow gameWindow) {
    super(gameWindow);
    logger.info("Creating Instructions Scene");
  }

  /**
   * Initialise the scene If escape is pressed, return to the menu
   */
  @Override
  public void initialise() {
    logger.info("Initialising " + this.getClass().getName());
    controls();

  }

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


  @Override
  public void build() {
    logger.info("Building " + this.getClass().getName());
    //Create the instructions scene
    imageView = new ImageView(
        InstructionsScene.class.getResource("/images/suggestedControls.png").toExternalForm());
    //Set the image to fit the top half of the window
    imageView.setPreserveRatio(true);
    imageView.setFitHeight(gameWindow.getHeight() / 2 - 40);


    root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());
    mainPane.setMaxWidth(gameWindow.getWidth());
    mainPane.setMaxHeight(gameWindow.getHeight());
    mainPane.getStyleClass().add("menu-background");

    VBox top = new VBox();
    top.setAlignment(Pos.CENTER);
    top.setSpacing(10);
    var controlsLabel = new Text("Controls:");
    controlsLabel.getStyleClass().add("heading");
    controlsLabel.setStyle("-fx-text-fill: white; -fx-font-family: 'Arial'; -fx-font-weight: bold;");


    //Add the instructions to the main pane

    var escape = new Text("Menu");
    escape.setOnMouseClicked(e -> gameWindow.startMenu());
    escape.getStyleClass().add("option1-button");
    AnchorPane menuButton = new AnchorPane();
    menuButton.getChildren().add(escape);
    AnchorPane.setLeftAnchor(escape,10.0);
    AnchorPane.setTopAnchor(escape,5.0);

    top.getChildren().addAll(menuButton, controlsLabel, imageView);
    top.setSpacing(10);
    double paneHeight = gameWindow.getHeight();
    double paneWidth = gameWindow.getWidth();
    top.setMaxHeight(paneHeight);
    top.setMaxWidth(paneWidth);
    mainPane.setTop(top);

    //Grid for piece options
    GridPane gridPane = new GridPane();
    gridPane.setAlignment(Pos.CENTER);
    gridPane.setHgap(10);
    gridPane.setVgap(10);

    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 5; j++) {
        var pieceBoard = new PieceBoard(55, 55);
        GamePiece gamePiece = GamePiece.createPiece(i * 5 + j);
        pieceBoard.displayPiece(gamePiece);
        gridPane.add(pieceBoard, j, i);
      }
    }
    gridPane.setPrefSize(paneWidth, paneHeight / 2);
    var  optionsLabel = new Text("Piece Options:");
    optionsLabel.getStyleClass().add("heading");
    optionsLabel.setStyle("-fx-text-fill: white; -fx-font-family: 'Arial'; -fx-font-weight: bold;");
    var bottom = new VBox();
    bottom.setAlignment(Pos.CENTER);
    bottom.fillWidthProperty().setValue(true);
    bottom.setMaxHeight(paneHeight / 2);
    bottom.setPadding(new javafx.geometry.Insets(10, 10, 10, 10));
    bottom.getChildren().addAll(optionsLabel, gridPane);

    gridPane.setPrefSize(paneWidth, paneHeight / 2);
    mainPane.setBottom(bottom);

    root.getChildren().add(mainPane);
  }

}
