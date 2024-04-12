package uk.ac.soton.comp1206.scene;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The Local Challenge Scene is a single player challenge scene that allows the player to play
 * against the computer Extends the Challenge Scene Added some power ups to the game Changed the
 * layout of the scene
 */

public class LocalChallengeScene extends ChallengeScene {

  private static final Logger logger = LogManager.getLogger(LocalChallengeScene.class);
  private Text eraseBlock;
  private Text eraseBlockCost;

  /**
   * Create a new Single Player challenge scene
   *
   * @param gameWindow the Game Window
   */

  public LocalChallengeScene(GameWindow gameWindow) {
    super(gameWindow);
  }

  @Override
  public void build() {
    super.build();
    /*
    top override
     */
    var statsBox = new HBox();
    statsBox.setAlignment(Pos.CENTER);
    statsBox.setSpacing(20);
    statsBox.getChildren().addAll(scoreBox, levelBox, livesBox, multiplierBox);

    var topBox = new VBox();
    topBox.setAlignment(Pos.CENTER);
    topBox.getChildren().addAll(title, statsBox);

    mainPane.setTop(topBox);

        /*
    left override
     */
    var powerUp = new Text("Power Ups:");
    powerUp.getStyleClass().add("powerUp");
    /*lives*/
    var getLives = new Text("+1 Life");
    var getLivesCost = new Text("Cost: 100");
    getLivesCost.getStyleClass().add("option3-button");
    getLives.getStyleClass().add("option1-button");
    getLives.setOnMouseClicked(e -> {
      Boolean changed = game.powerLives();
      if (!changed) {
        getLives.getStyleClass().remove("option1-button");
        getLives.getStyleClass().add("noPower");
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), change -> {
          getLives.getStyleClass().remove("noPower");
          getLives.getStyleClass().add("option1-button");
        }));
        timeline.play();
      }
    });
    var getLivesContainer = new VBox();
    getLivesContainer.setSpacing(5);
    getLivesContainer.setAlignment(Pos.CENTER);
    getLivesContainer.getChildren().addAll(getLives, getLivesCost);

    /*piece*/
    var getPiece = new Text("New Piece");
    var getPieceCost = new Text("Cost: 300");
    getPieceCost.getStyleClass().add("option3-button");
    getPiece.getStyleClass().add("option1-button");
    getPiece.setOnMouseClicked(e -> {
      Boolean changed = game.powerPiece();

      if (!changed) {
        getPiece.getStyleClass().remove("option1-button");
        getPiece.getStyleClass().add("noPower");
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), change -> {
          getPiece.getStyleClass().remove("noPower");
          getPiece.getStyleClass().add("option1-button");
        }));
        timeline.play();
      }
    });
    var getPieceContainer = new VBox();
    getPieceContainer.setSpacing(5);
    getPieceContainer.setAlignment(Pos.CENTER);
    getPieceContainer.getChildren().addAll(getPiece, getPieceCost);

    /*Erase block*/
    eraseBlock = new Text("Erase Block");
    eraseBlockCost = new Text("Cost: 500");
    eraseBlockCost.getStyleClass().add("option3-button");
    eraseBlock.getStyleClass().add("option1-button");
    eraseBlock.setOnMouseClicked(e -> {
      powerErase();
    });
    var eraseBlockContainer = new VBox();
    eraseBlockContainer.setSpacing(5);
    eraseBlockContainer.setAlignment(Pos.CENTER);
    eraseBlockContainer.getChildren().addAll(eraseBlock, eraseBlockCost);

    var leftContainer = new VBox();
    leftContainer.setPadding(new Insets(10));
    leftContainer.setSpacing(20);
    leftContainer.setAlignment(Pos.CENTER);

    leftContainer.getChildren()
        .addAll(powerUp, getLivesContainer, getPieceContainer, eraseBlockContainer);

    mainPane.setLeft(leftContainer);

    /*
    right override
     */
    leftBox.setAlignment(Pos.CENTER);
    leftBox.setPadding(new Insets(20, 20, 20, 20));
    mainPane.setRight(leftBox);


  }

  /**
   * Activate the erase block power up If the player has enough points, the player can erase a block
   * from the board The player is then able to click on a block to erase it
   */
  public void powerErase() {
    if (this.game.getScore().get() >= 500) {
      powerUpSound();
      logger.info("Erase block power up activated");
      game.eraseMode = true;
      logger.info("Erase mode activated, eraseMode: {}", game.eraseMode);
      board.setOnMouseClicked(e -> {
        coordX = board.currentBlock.getX();
        coordY = board.currentBlock.getY();
        game.blockClicked(board.getBlock(coordX, coordY));
        this.game.setScore(this.game.getScore().get() - 500);
        game.eraseMode = false;
        board.setOnMouseClicked(null);
        logger.info("Erase mode deactivated, eraseMode: {}", game.eraseMode);
      });
    } else {
      logger.info("Not enough points to activate erase block power up");
      eraseBlock.getStyleClass().remove("option1-button");
      eraseBlock.getStyleClass().add("noPower");
      Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), change -> {
        eraseBlock.getStyleClass().remove("noPower");
        eraseBlock.getStyleClass().add("option1-button");
      }));
      timeline.play();
    }
  }

  private void powerUpSound() {
    String soundFile = getClass().getResource("/sounds/wow.mp3").toExternalForm();
    Media sound = new Media(soundFile);
    MediaPlayer mediaPlayer = new MediaPlayer(sound);
    mediaPlayer.play();
  }
}
