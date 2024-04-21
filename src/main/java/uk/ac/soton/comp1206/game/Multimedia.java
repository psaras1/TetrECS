package uk.ac.soton.comp1206.game;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * A utility class for playing sounds
 */
public class Multimedia {

  private MediaPlayer audioPlayer;
  private MediaPlayer musicPlayer;
  public static double globalVolume = 1.0;
  public static double globalSoundVolume = 1.0;

  /**
   * Play a sound from a file
   *
   * @param file the file to play
   */
  public void playAudio(String file) {
    this.audioPlayer = new MediaPlayer(new Media(getClass().getResource(file).toExternalForm()));
    audioPlayer.setVolume(globalVolume);
    audioPlayer.play();
  }

  /**
   * Handles background music
   */
  public void playBackgroundMusic(String file) {
    musicPlayer = new MediaPlayer(new Media(getClass().getResource(file).toExternalForm()));
//    The background music should loop
    musicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
    musicPlayer.setVolume(globalVolume);
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
   *
   * @return
   */
  public boolean isPlaying() {
    return musicPlayer.getStatus() == MediaPlayer.Status.PLAYING;
  }

  /*
   *Adjust the global volume of any Multimedia object
   * (Intialises all objects to the same volume)
   */
  public static void adjustGlobalVolume(double volume) {
    globalVolume = volume;
  }
}
