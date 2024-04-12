package uk.ac.soton.comp1206.scene;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.ui.GameWindow;

public class LocalChallengeScene extends ChallengeScene {
  private static final Logger logger  = LogManager.getLogger(LocalChallengeScene.class);

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
      if(!changed) {
        getLives.getStyleClass().remove("option1-button");
        getLives.getStyleClass().add("noPower");
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2),change->{
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
      if(!changed) {
        getPiece.getStyleClass().remove("option1-button");
        getPiece.getStyleClass().add("noPower");
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2),change->{
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
    var eraseBlock = new Text("Erase Block");
    var eraseBlockCost = new Text("Cost: 500");
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

    leftContainer.getChildren().addAll(powerUp, getLivesContainer,getPieceContainer,eraseBlockContainer);

    mainPane.setLeft(leftContainer);

    /*
    right override
     */
    leftBox.setAlignment(Pos.CENTER);
    leftBox.setPadding(new Insets(20, 20, 20, 20));
    mainPane.setRight(leftBox);


  }
  public Boolean powerErase(){
    if(this.game.getScore().get() >= 500){
      logger.info("Erase block power up activated");
      game.eraseMode = true;
      logger.info("Erase mode activated, eraseMode: {}", game.eraseMode);
      board.setOnMouseClicked(e->{
        coordX = board.currentBlock.getX();
        coordY = board.currentBlock.getY();
        game.blockClicked(board.getBlock(coordX,coordY));
        this.game.setScore(this.game.getScore().get()-500);
        game.eraseMode = false;
        board.setOnMouseClicked(null);
        logger.info("Erase mode deactivated, eraseMode: {}", game.eraseMode);
      });

      return true;
    }
    return false;
  }
}
