package uk.ac.soton.comp1206.game;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Grid is a model which holds the state of a game board. It is made up of a set of Integer
 * values arranged in a 2D arrow, with rows and columns.
 * <p>
 * Each value inside the Grid is an IntegerProperty can be bound to enable modification and display
 * of the contents of the grid.
 * <p>
 * The Grid contains functions related to modifying the model, for example, placing a piece inside
 * the grid.
 * <p>
 * The Grid should be linked to a GameBoard for it's display.
 */
public class Grid {

  private static final Logger logger = LogManager.getLogger(Grid.class);

  /**
   * The number of columns in this grid
   */
  private final int cols;

  /**
   * The number of rows in this grid
   */
  private final int rows;

  /**
   * The grid is a 2D arrow with rows and columns of SimpleIntegerProperties.
   */
  private final SimpleIntegerProperty[][] grid;

  /**
   * Create a new Grid with the specified number of columns and rows and initialise them
   *
   * @param cols number of columns
   * @param rows number of rows
   */
  public Grid(int cols, int rows) {
    this.cols = cols;
    this.rows = rows;

    //Create the grid itself
    grid = new SimpleIntegerProperty[cols][rows];

    //Add a SimpleIntegerProperty to every block in the grid
    for (var y = 0; y < rows; y++) {
      for (var x = 0; x < cols; x++) {
        grid[x][y] = new SimpleIntegerProperty(0);
      }
    }
  }

  /**
   * Get the Integer property contained inside the grid at a given row and column index. Can be used
   * for binding.
   *
   * @param x column
   * @param y row
   * @return the IntegerProperty at the given x and y in this grid
   */
  public IntegerProperty getGridProperty(int x, int y) {
    return grid[x][y];
  }

  /**
   * Update the value at the given x and y index within the grid
   *
   * @param x     column
   * @param y     row
   * @param value the new value
   */
  public void set(int x, int y, int value) {
    grid[x][y].set(value);
  }

  /**
   * Get the value represented at the given x and y index within the grid
   *
   * @param x column
   * @param y row
   * @return the value
   */
  public int get(int x, int y) {
    try {
      //Get the value held in the property at the x and y index provided
      return grid[x][y].get();
    } catch (ArrayIndexOutOfBoundsException e) {
      //No such index
      return -1;
    }
  }

  /**
   * Get the number of columns in this game
   *
   * @return number of columns
   */
  public int getCols() {
    return cols;
  }

  /**
   * Get the number of rows in this game
   *
   * @return number of rows
   */
  public int getRows() {
    return rows;
  }

  /**
   * Check whether a piece can be played in the grid at given x,y
   *
   * @param gamePiece Piece to play
   * @param placeX    X-coordinate
   * @param placeY    Y-coordinate
   * @return
   */
  public boolean canPlayPiece(GamePiece gamePiece, int placeX,
      int placeY) { //should check if values of specified pieces is 0, so a block can be placed
    //in order to do that, we need to loop through rows/columns and check if the values are 0
    logger.info("Checking if we can play the piece {} at {},{}", gamePiece, placeX,
        placeY); //curly braces act as placeholders

    int topX = placeX - 1; //Dealing with click offset, now working around center of the piece
    int topY = placeY - 1; //rather than top edge

    int[][] blocks = gamePiece.getBlocks(); //return array/model of what piece looks like(2D array)

    for (var blockX = 0; blockX < blocks.length; blockX++) {
      for (var blockY = 0; blockY < blocks.length; blockY++) {
        var currentBlockValue = blocks[blockX][blockY];
        if (currentBlockValue > 0) { //if the block already has something in
          //Check if it's possible to place block
          var gridValue = get(topX + blockX, topY + blockY);//can only be placed if 0
          if (gridValue != 0) {
            logger.info("Unable to place {}, conflict at {},{}", gamePiece,placeX + blockX,
                placeY + blockY);
            return false;
          }

        }
      }

    }
    return true;
  }


  /**
   * Play a piece by updating the grid with the given coordinates
   *
   * @param gamePiece Piece to play
   * @param placeX    X-coordinate
   * @param placeY    Y-coordinate
   */
  public void playPiece(GamePiece gamePiece, int placeX, int placeY) {
    int topX = placeX - 1; //Dealing with click offset, now working around center of the piece
    int topY = placeY - 1; //rather than top edge

    logger.info("Placing the piece {} at {},{}", gamePiece, placeX,
        placeY); //curly braces act as placeholders
    int value = gamePiece.getValue(); //value of a piece represents what colour it is
    int[][] blocks = gamePiece.getBlocks(); //return array/model of what piece looks like(2D array)
    if (!canPlayPiece(gamePiece, placeX, placeY)) {
      return; //If piece can't be placed, return
    }
    //we've got a 5x5 grid
    //we've got a new piece of blocks to place(3x3 grid)
    //e.g:
    //0 1 0
    //0 1 0
    //0 1 0(line piece)

    //after we've checked we can place the piece:
    for (var blockX = 0; blockX < blocks.length; blockX++) {
      for (var blockY = 0; blockY < blocks.length; blockY++) {
        //in each iteration we're using an x and y coordinate of the 3x3 2d array
        var currentBlockValue = blocks[blockX][blockY];
        if (currentBlockValue > 0) { //Only updating the model if there's a block there
          set(topX + blockX, topY + blockY, value);
        }
        //Updating 5x5 grid from 3x3 grid, takes x,y coordinates where user clicked
        //plus where we currently are on the grid in the specific iteration

      }

    }

  }

  /**
   * Clean the grid by setting all values to 0
   */
  public void clean() {
    for (var y = 0; y < rows; y++) {
      for (var x = 0; x < cols; x++) {
        set(x, y, 0);
      }
    }
  }

}
