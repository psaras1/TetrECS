package uk.ac.soton.comp1206.event;

/**
 * A listener for when the game ends Takes care of any cleanup that needs to be done
 */
public interface GameEndListener {

  void onGameEnd();
}
