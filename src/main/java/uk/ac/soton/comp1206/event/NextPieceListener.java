package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.game.GamePiece;

/**
 * The Next Piece listener is used to handle the event when a new piece is spawned.
 */

public interface NextPieceListener {

  void nextPiece(GamePiece piece);

}