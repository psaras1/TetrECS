package uk.ac.soton.comp1206.event;

import java.util.HashSet;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;

/**
 * LineClearedListener is an interface that is used to listen for when lines are cleared in the
 * game. Used to then update the score and game state accordingly.
 */
public interface LineClearedListener {

  void linesCleaned(HashSet<GameBlockCoordinate> linesCleared);
}

