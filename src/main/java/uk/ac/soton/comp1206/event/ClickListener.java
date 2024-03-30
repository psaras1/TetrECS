package uk.ac.soton.comp1206.event;

import javafx.scene.input.MouseEvent;

/**
 * Add a RightClicked listener and corresponding setOnRightClicked method to the GameBoard class.
 */

public interface ClickListener{
  void onClick(MouseEvent event);
}
