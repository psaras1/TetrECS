package uk.ac.soton.comp1206.event;

import java.util.HashSet;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;

public interface LineClearedListener {
  void linesCleaned(HashSet<GameBlockCoordinate> linesCleared);
}
