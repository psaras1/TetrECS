package uk.ac.soton.comp1206.component;

import uk.ac.soton.comp1206.game.GamePiece;

/**
 * A PieceBoard represents the current piece the player is controlling Or the next piece that will
 * be spawned
 */


public class PieceBoard extends GameBoard {

  /**
   * Create a new PieceBoard with the specified width and height
   *
   * @param width
   * @param height
   */
  public PieceBoard(int width, int height) {
    super(3, 3, width, height);
    build();
  }

  /**
   * Display the piece on the board
   *
   * @param piece
   */
  public void displayPiece(GamePiece piece) {
    this.grid.clean();
    int[][] blocks = piece.getBlocks();
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        if (blocks[i][j] != 0) {
          this.grid.set(i, j, blocks[i][j]);

        }
      }
    }
  }

}
