package uk.ac.soton.comp1206.scene;

import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import uk.ac.soton.comp1206.component.Leaderboard;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.ui.GameWindow;

public class ScoresSceneMultiplayer extends ScoresScene{
  private Leaderboard leaderboard;
  public ScoresSceneMultiplayer(GameWindow gameWindow,
      Game game, Leaderboard leaderboard) {
    super(gameWindow, game);
    this.leaderboard = leaderboard;
  }
@Override
  public void build(){
    super.build();
    title.setText("Game Over!");
}
@Override
  public void finishBuild(BorderPane mainPane){
    super.finishBuild(mainPane);
    scoreBoxLocalLabel.setText("Game Scores: ");
    Region spacer = new Region();
    spacer.setPrefHeight(20);
    scoreBoxLocal.getChildren().clear();
    scoreBoxLocal.getChildren().addAll(scoreBoxLocalLabel,spacer,leaderboard);
    centralBox.setAlignment(Pos.CENTER);
    centralBox.getChildren().clear();
    centralBox.getChildren().addAll(scoreBoxLocal, scoreBoxRemote);
    mainPane.setCenter(centralBox);
  }
}

