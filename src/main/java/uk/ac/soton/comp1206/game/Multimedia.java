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
  public void playBackgroundMusic(String file) {
    musicPlayer = new MediaPlayer(new Media(getClass().getResource(file).toExternalForm()));
//    The background music should loop
    musicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
    musicPlayer.play();
  }
  /**
   * Background music fades out instead of stopping abruptly
   */
  public void stopBackgroundMusic() {
    musicPlayer.stop();
  }

  /**
   * Check if the music is playing
   * @return
   */
  public boolean isPlaying() {
    return musicPlayer.getStatus() == MediaPlayer.Status.PLAYING;
  }

}
