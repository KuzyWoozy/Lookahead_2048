package cs3822;

import java.util.ArrayList;
import java.util.List;
import java.lang.Math;
import java.util.LinkedList;

import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.event.EventHandler;
import javafx.scene.text.TextAlignment;
import javafx.geometry.VPos;
import javafx.scene.text.Font;
import javafx.animation.AnimationTimer;
import javafx.animation.TranslateTransition;
import javafx.animation.ParallelTransition; 
import javafx.util.Duration;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.animation.SequentialTransition;


/**
 * GUI representation of the game.
 *
 * @author Daniil Kuznetsov
 */
class GraphicsView implements View {
  private Stage stage;

  private Group group;
  private Group nodes;

  private Scene scene;
  private Canvas canvas;
  private GraphicsContext gc;

  final private Duration animationLength = new Duration(100);

  final private float paddingPercent = 5;
  final private float roundingPercent = 15;
  final private float textSizePercent = 35;

  final int grid_cols;
  final int grid_rows;

  private boolean animate = false;

  private ArrayList<String> input;

  private boolean canPressFlag = true;

  
  /** Constructor. */ 
  public GraphicsView(Grid grid, Stage stage, float width, float height) {
    this.stage = stage;

    // Parent group
    this.group = new Group();
    // Node to group together nodes
    this.nodes = new Group();

    this.canvas = new Canvas(width, height);
    this.group.getChildren().add(this.canvas);
    this.group.getChildren().add(this.nodes);

    this.grid_rows = grid.getRows();
    this.grid_cols = grid.getCols();

    this.scene = new Scene(this.group, Color.WHITE);
    this.stage.setScene(this.scene);
    
    this.gc = canvas.getGraphicsContext2D();

    this.input = new ArrayList<String>();

    gc.setTextBaseline(VPos.CENTER);
    gc.setTextAlign(TextAlignment.CENTER);
    
    stage.show();

    List<Grid> frames = new LinkedList<Grid>();
    frames.add(grid);
    display(frames);

    // Register a callback event on key press
    this.scene.setOnKeyPressed(
        new EventHandler<KeyEvent>() {
          public void handle(KeyEvent e) {
            if (canPressFlag) {
              // Simple boolean lock
              canPressFlag = false;
              String code = e.getCode().toString();
              if (!input.contains(code)) {
                input.add(code);
              }
            }
          }
        });

    // Register a callback even on key release
    this.scene.setOnKeyReleased(
        new EventHandler<KeyEvent>() {
          public void handle(KeyEvent e) {
            canPressFlag = true;
            String code = e.getCode().toString();
            input.remove(code);
          }
        });
  }


  /** 
   * Draw a rectangle of given size and colour. 
   *
   * @param gc Javafx GraphicsContext object
   * @param x Top left x coordinate of the rectangle
   * @param y Top left y coordinate of the rectangle
   * @param width Width of the rectangle
   * @param height Height of the rectangle
   * @param color Colour of the rectangle
   */
  private void drawRect(GraphicsContext gc, float x, float y, float width, float height, Color color) {
    gc.setFill(color);
    gc.fillRect(x, y, width, height);
  }

  /** 
   * Draw a round rectangle of given size and colour. 
   *
   * @param gc Javafx GraphicsContext object
   * @param x Top left x coordinate of the round rectangle
   * @param y Top left y coordinate of the round rectangle
   * @param width Width of the round rectangle
   * @param height Height of the round rectangle
   * @param arc_width Width radius of the corners
   * @param arc_height Height radius of the corners
   * @param color Colour of the round rectangle
   */
  private void drawRoundRect(GraphicsContext gc, float x, float y, float width, float height, float arc_width, float arc_height, Color color) {
    gc.setFill(color); 
    gc.fillRoundRect(x, y, width, height, arc_width, arc_height);
  }
  
  
  /** Computes x coordinate with respect to pad width. */
  private float node_canvas_x(int x, float node_width, float pad_width) {
    return ((x+1) * pad_width) + (x * node_width);
  }
  
  /** Computes y coordinate with respect to pad height. */
  private float node_canvas_y(int y, float node_height, float pad_height) {
    return ((y+1) * pad_height) + (y * node_height);
  }
 
  /** 
   * Create a rectangle object of given size and colour. 
   *
   * @param node_width Width of the rectangle
   * @param node_height Height of the rectangle
   * @param arc_width Width radius of the corners
   * @param arc_height Height radius of the corners
   * @param color Colour of the rectangle
   * @return Rectangle object
   */
  private Rectangle createRect(float node_width, float node_height, float node_arc_width, float node_arc_height, Color color) {
    Rectangle rect = new Rectangle(node_width, node_height);
    rect.setFill(color);
    rect.setArcWidth(node_arc_width);
    rect.setArcHeight(node_arc_height);
    
    rect.setStroke(Color.BLACK);
    rect.setStrokeWidth(2);

    return rect;
  }
  
  /** 
   * Draw text of given size and colour. 
   *
   * @param value Value of node
   * @param node_width Width of the text block
   * @param node_height Height of the text block
   * @param color Colour of the round rectangle
   * @return Text block
   */
  private Text createText(int value, float node_width, float node_height, Color color) {
    Text text = new Text(String.valueOf(value));
    text.setFill(color);
    if (node_width < node_height) {
      text.setFont(new Font((int) (node_width * (textSizePercent/100f))));
    } else {
      text.setFont(new Font((int) (node_height * (textSizePercent/100f))));
    }
    text.setTextOrigin(VPos.CENTER);

    return text;
  }
 
  /**
   * Generates a movement animation to play out.
   * 
   * @param rect Node to animate
   * @param text Text to animate 
   * @param fromX Starting x coordinate
   * @param fromY Starting y coordinate
   * @param toX Destination x coordinate
   * @param toY Destination y coordinate
   * @param node_width Node width
   * @param node_height Node height
   * @return Animation object
   */
  private ParallelTransition createTranslateAnimation(Rectangle rect, Text text, float fromX, float fromY, float toX, float toY, float node_width, float node_height) {
       
    rect.setX(fromX);
    rect.setY(fromY);

    TranslateTransition trans_node = new TranslateTransition(animationLength, rect);

    trans_node.setFromX(0);
    trans_node.setFromY(0);
    
    trans_node.setToX(toX - fromX);
    trans_node.setToY(toY - fromY);

    
    float textFromX = fromX + Math.abs(node_width - (float)text.getLayoutBounds().getWidth()) / 2;

    float textFromY = fromY + node_height / 2;

    float textToX = toX + Math.abs(node_width - (float)text.getLayoutBounds().getWidth()) / 2;
    float textToY = toY + node_height / 2;
    
    text.setX(textFromX);
    text.setY(textFromY);
    
    TranslateTransition trans_text = new TranslateTransition(animationLength, text);

    trans_text.setFromX(0);
    trans_text.setFromY(0);

    trans_text.setToX(textToX - textFromX);
    trans_text.setToY(textToY - textFromY);
    
    ParallelTransition trans = new ParallelTransition(trans_node, trans_text);

    return trans; 
  }

  /** Lock the animation loop. */
  private void lock() {
    animate = true;
  }
  
  /** Unlock the animation loop. */
  private void unlock() {
    animate = false;
  }

  /** Render and animate the grid frames. */
  private void display(List<Grid> frames) {

    float width = (float)stage.getWidth();
    float height = (float)stage.getHeight();
    
    canvas.setWidth(width);
    canvas.setHeight(height);


    float width_padding = width * (paddingPercent / 100f);
    float height_padding = height * (paddingPercent / 100f);

    float node_width = (width - width_padding) / grid_cols;
    float node_height = (height - height_padding) / grid_rows;
    
    if (node_width < node_height) {
      gc.setFont(new Font((int) (node_width * (textSizePercent/100f))));
    } else {
      gc.setFont(new Font((int) (node_height * (textSizePercent/100f))));
    }

      
    float node_arc_width = node_width * (roundingPercent / 100f);
    float node_arc_height = node_height * (roundingPercent / 100f);
    float pad_width = width_padding / (grid_cols + 1);
    float pad_height = height_padding / (grid_rows + 1);

    float x_old;
    float y_old;

    float x;
    float y;

    // Refresh screen
    drawRect(gc, 0, 0, width, height, Color.WHITE);

    // Clear out the old rectangle objects
    this.nodes.getChildren().clear();
          
    SequentialTransition animation = new SequentialTransition();
    List<Group> frameGroups = new LinkedList<Group>(); 
    ParallelTransition transitions;
    
    for (Grid frame : frames) {

      Group frameGroup = new Group();
      
      transitions = new ParallelTransition(); 
      for (Node node : frame.getNodes()) {
        x = node_canvas_x(node.getPos().getX(), node_width, pad_width); 
        y = node_canvas_y(node.getPos().getY(), node_height, pad_height);
        
        drawRoundRect(gc, x, y, node_width, node_height, node_arc_width, node_arc_height, Color.GREY);
        
        if (node.getType() == NodeType.VALUE) {
          Color rectColor = null;
          Color textColor = null; 
          try {
            if (node.hasMerged() || (frame.getGeneratedNode() != null && frame.getGeneratedNode().getPos().equals(node.getPos()))) {
              rectColor = Color.WHITE;
              textColor = Color.BLACK;
            } else {
              rectColor = Color.BLACK;
              textColor = Color.WHITE;
            }
          } catch(NoMergeFlagException e) {
            e.printStackTrace();
            System.exit(1);
          }

          Rectangle rect = createRect(node_width, node_height, node_arc_width, node_arc_height, rectColor);

          Text text = null;
          x_old = 0;
          y_old = 0;
          try {
            text = createText(node.getValue(), node_width, node_height, textColor);
            x_old = node_canvas_x(node.getOldPos().getX(), node_width, pad_width); 
            y_old = node_canvas_y(node.getOldPos().getY(), node_height, pad_height);

          } catch(NoValueException | CantMoveException e) {
            e.printStackTrace();
            System.exit(1);
          }
         
          frameGroup.getChildren().add(rect);
          frameGroup.getChildren().add(text);

          transitions.getChildren().add(createTranslateAnimation(rect, text, x_old, y_old, x, y, node_width, node_height));
        }
      }
      
      frameGroups.add(frameGroup);
      transitions.setOnFinished(e -> {this.nodes.getChildren().clear(); if (!frameGroups.isEmpty()) this.nodes.getChildren().add(frameGroups.remove(0));});
      animation.getChildren().add(transitions);
    }
   
    this.nodes.getChildren().add(frameGroups.remove(0));
    
    animation.setOnFinished(e -> unlock());
    // Do not perform last group set
    animation.getChildren().get(animation.getChildren().size()-1).setOnFinished(e -> {});
    
    animation.play();
    lock();
  }

    
  @Override
  public List<Action> getInput() {
    List<Action> actions = null;
    // Default action
    if (input.isEmpty()) {
      actions = new ArrayList<Action>();
      actions.add(Action.NONE);
    } else {
      actions = View.convertStringToActions(String.join("", input));
      input.clear();
    }
    return actions;
  }
 
  @Override
  public GameStats play(GridManager manager, Algorithm algo) {
    GameStats stat = new GameStats();
    Boolean finished = false; 

    // Create and start the animation loop
    new AnimationTimer() {
      public void handle(long nanoTime) {
        if (!animate) {
          Grid grid = manager.show();
          
          if (grid.lost()) {
            stat.lost();
            stop();
            return;
          }
          // Pass the frames into the renderer 
          display(manager.process(algo.move(grid)));
        }
      }
    }.start();
    
    // Return post game stats
    return stat;
  }


}
