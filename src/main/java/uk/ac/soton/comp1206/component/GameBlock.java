package uk.ac.soton.comp1206.component;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.canvas.Canvas;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.paint.*;
import javafx.scene.effect.*;
import javafx.scene.paint.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Visual User Interface component representing a single block in the grid.
 * <p>
 * Extends Canvas and is responsible for drawing itself.
 * <p>
 * Displays an empty square (when the value is 0) or a coloured square depending on value.
 * <p>
 * The GameBlock value should be bound to a corresponding block in the Grid model.
 */
public class GameBlock extends Canvas {

  private static final Logger logger = LogManager.getLogger(GameBlock.class);


  /**
   * The set of colours for different pieces
   */
  public static final Color[] COLOURS = {
      Color.TRANSPARENT,
      Color.DEEPPINK,
      Color.RED,
      Color.ORANGE,
      Color.YELLOW,
      Color.YELLOWGREEN,
      Color.LIME,
      Color.GREEN,
      Color.DARKGREEN,
      Color.DARKTURQUOISE,
      Color.DEEPSKYBLUE,
      Color.AQUA,
      Color.AQUAMARINE,
      Color.BLUE,
      Color.MEDIUMPURPLE,
      Color.PURPLE
  };

  private final double width;
  private final double height;

  /**
   * The column this block exists as in the grid
   */
  private final int x;

  /**
   * The row this block exists as in the grid
   */
  private final int y;


  /**
   * The value of this block (0 = empty, otherwise specifies the colour to render as)
   */
  private final IntegerProperty value = new SimpleIntegerProperty(
      0); //each block has a unique IntegerProperty
  public Boolean center;


  /**
   * Create a new single Game Block
   *
   * @param gameBoard the board this block belongs to
   * @param x         the column the block exists in
   * @param y         the row the block exists in
   * @param width     the width of the canvas to render
   * @param height    the height of the canvas to render
   */
  public GameBlock(GameBoard gameBoard, int x, int y, double width, double height) {
    this.width = width;
    this.height = height;
    this.x = x;
    this.y = y;
    //A canvas needs a fixed width and height
    setWidth(width);
    setHeight(height);
    center = false;
    //Do an initial paint
    paint();

    //When the value property is updated, call the internal updateValue method
    value.addListener(
        this::updateValue); //When the value of a block changes, updateValue(From GameBoard) is called through listener
//        this.addEventHandler(MouseEvent.MOUSE_ENTERED, this::handleMouseEntered);
//        this.addEventHandler(MouseEvent.MOUSE_EXITED, this::handleMouseExited);
  }

  /**
   * When the value of this block is updated,
   *
   * @param observable what was updated
   * @param oldValue   the old value
   * @param newValue   the new value
   */
  private void updateValue(ObservableValue<? extends Number> observable, Number oldValue,
      Number newValue) {
    paint(); //after the listener calls updateValue, paint is called
  }

  /**
   * Handle painting of the block canvas
   */
  public void paint() {
    //If the block is empty, paint as empty
    if (value.get() == 0) {
      paintEmpty();
    } else {
      if(!center){
        paintColor(COLOURS[value.get()]);
      } else {
        paintColorWithCircle(COLOURS[value.get()]);
      }


    }

  }
  public void setCenter(){
    center = true;
    paint();
  }

  //TODO: Implement isPieceboard method to not color the pieceboards
  public boolean isPieceboard() {
    return false;
  }


  /**
   * Paint this canvas empty
   */
  private void paintEmpty() {
    var gc = getGraphicsContext2D();

    //Clear
    gc.clearRect(0, 0, width, height);

    //Fill
    gc.setFill(Color.rgb(255,255,255,0.5));
    gc.fillRect(0, 0, width, height);

    //Border
    gc.setStroke(Color.BLACK);
    gc.strokeRect(0, 0, width, height);
  }

  /**
   * Paint this canvas with the given colour
   *
   * @param colour the colour to paint
   */
  private void paintColor(Paint colour) {
    var gc = getGraphicsContext2D();

    //Clear
    gc.clearRect(0, 0, width, height);

    //Colour fill
    gc.setFill(colour);
    gc.fillRect(0, 0, width, height);

    Glow glow = new Glow();
    InnerShadow shadow = new InnerShadow();
    shadow.setColor(Color.BLACK);
    glow.setLevel(0.9);
    gc.applyEffect(glow);
    gc.applyEffect(shadow);

    //Border
    gc.setStroke(Color.BLACK);
    gc.strokeRect(0, 0, width, height);

  }

  private void paintColorWithCircle(Paint colour) {
    var gc = getGraphicsContext2D();
    //Clear
    gc.clearRect(0, 0, width, height);

    //Colour fill
    gc.setFill(colour);
    gc.fillRect(0, 0, width, height);

    Glow glow = new Glow();
    InnerShadow shadow = new InnerShadow();
    shadow.setColor(Color.BLACK);
    glow.setLevel(0.9);
    gc.applyEffect(glow);
    gc.applyEffect(shadow);

    //Border
    gc.setStroke(Color.BLACK);
    gc.strokeRect(0, 0, width, height);

    //Draw a gray circle in the middle of the block
    gc.setFill(Color.GRAY);
    gc.fillOval(width / 2 - 10, height / 2 - 10, 20, 20);
  }

  /**
   * Get the column of this block
   *
   * @return column number
   */
  public int getX() {
    return x;
  }

  /**
   * Get the row of this block
   *
   * @return row number
   */
  public int getY() {
    return y;
  }

  /**
   * Get the current value held by this block, representing it's colour
   *
   * @return value
   */
  public int getValue() {
    return this.value.get();
  }

  /**
   * Bind the value of this block to another property. Used to link the visual block to a
   * corresponding block in the Grid.
   *
   * @param input property to bind the value to
   */
  public void bind(ObservableValue<? extends Number> input) {
    value.bind(input);
  }

  @Override
  public String toString() {
    return "GameBlock{" +
        "x=" + x +
        ", y=" + y +
        ", value=" + value.get() +
        '}';
  }

  public void hoverBlock() {
    paint();
    var gc = getGraphicsContext2D();
    Light.Distant light = new Light.Distant();
    light.setAzimuth(-135.0);

    Lighting lighting = new Lighting();
    lighting.setLight(light);
    lighting.setSurfaceScale(5.0);
    gc.applyEffect(lighting);
  }


  /**
   * Handle mouse entered and exited events to change the block opacity
   * @param event
   */
//    public void handleMouseEntered(MouseEvent event){
//        if(value.get() == 0){
//            this.setOpacity(0.5);
//        }
//    }
//    public void handleMouseExited(MouseEvent event){
//        this.setOpacity(1);
//    }
}
