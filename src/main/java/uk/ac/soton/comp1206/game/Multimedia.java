package uk.ac.soton.comp1206.game;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

/**
 * A utility class for playing sounds
 */
public class Multimedia {
  private  MediaPlayer audioPlayer;
  private  MediaPlayer musicPlayer;

  public Multimedia() {
    musicPlayer = new MediaPlayer(new Media(getClass().getResource("/music/menu.mp3").toExternalForm()));
//    The background music should loop
    musicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
  }
  /**
   * Play a sound from a file
   * @param file the file to play
   */
  public void playAudio(String file) {
    this.audioPlayer = new MediaPlayer(new Media(getClass().getResource(file).toExternalForm()));
    audioPlayer.play();
  }
  /**
   * Handles background music
   */
  public void playBackgroundMusic() {
    musicPlayer.play();
  }
  /**
   * Background music fades out instead of stopping abruptly
   */
  public void stopBackgroundMusic() {
    final Timeline timeline = new Timeline();
    KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.18), e -> {
      if (musicPlayer.getVolume() > 0.07) {
        musicPlayer.setVolume(musicPlayer.getVolume() - 0.07);
      } else {
        musicPlayer.stop();
        timeline.stop();
      }
    });
    timeline.getKeyFrames().add(keyFrame);
    timeline.setCycleCount(Timeline.INDEFINITE);
    timeline.play();
  }

}
