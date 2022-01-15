package cs3822;

import java.util.ArrayList;
import java.util.List;

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
import javafx.animation.ScaleTransition;
import javafx.animation.PauseTransition;

import java.lang.Math;
import java.util.LinkedList;

class GraphicsView implements View {
  private Stage stage;

  List<Group> oldFrameGroups;

  private Group group;
  private Group nodes;

  Integer i;

  private Scene scene;
  private Canvas canvas;
  private GraphicsContext gc;

  private Duration animationLength = new Duration(100);

  private float paddingPercent = 5;
  private float roundingPercent = 15;
  private float textSizePercent = 35;

  private boolean animate = true;

  private ArrayList<String> input;

  private boolean canPressFlag = true;
   

  private void drawRect(GraphicsContext gc, float x, float y, float width, float height, Color color) {
    gc.setFill(color);
    gc.fillRect(x, y, width, height);
  }

  private void drawRoundRect(GraphicsContext gc, float x, float y, float width, float height, float arc_width, float arc_height, Color color) {
    gc.setFill(color);
    gc.fillRoundRect(x, y, width, height, arc_width, arc_height);
  }

  private void drawText(GraphicsContext gc, String text, float x, float y, float node_width, float node_height, Color color) {
    gc.setFill(color);
    gc.fillText(text, x + node_width / 2, y + node_height / 2);
  }

  private float node_canvas_x(int x, float node_width, float pad_width) {
    return ((x+1) * pad_width) + (x * node_width);
  }

  private float node_canvas_y(int y, float node_height, float pad_height) {
    return ((y+1) * pad_height) + (y * node_height);
  }
  
  private Rectangle createRect(float node_width, float node_height, float node_arc_width, float node_arc_height, Color color) {
    Rectangle rect = new Rectangle(node_width, node_height);
    rect.setFill(color);
    rect.setArcWidth(node_arc_width);
    rect.setArcHeight(node_arc_height);
    
    return rect;
  }

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
  
  private ParallelTransition createTranslateAnimation(Rectangle rect, Text text, float fromX, float fromY, float toX, float toY, float node_width, float node_height, float node_arc_width, float node_arc_height, Color color) {
       
    rect.setX(toX);
    rect.setY(toY);

    TranslateTransition trans_node = new TranslateTransition(animationLength, rect);

    trans_node.setFromX(fromX-toX);
    trans_node.setFromY(fromY-toY);
    
    trans_node.setToX(0);
    trans_node.setToY(0);

    
    float textFromX = fromX + Math.abs(node_width - (float)text.getLayoutBounds().getWidth()) / 2;

    float textFromY = fromY + node_height / 2;

    float textToX = toX + Math.abs(node_width - (float)text.getLayoutBounds().getWidth()) / 2;
    float textToY = toY + node_height / 2;
    
    text.setX(textToX);
    text.setY(textToY);
    
    TranslateTransition trans_text = new TranslateTransition(animationLength, text);

    trans_text.setFromX(textFromX-textToX);
    trans_text.setFromY(textFromY-textToY);
    

    trans_text.setToX(0);
    trans_text.setToY(0);
    
    ParallelTransition trans = new ParallelTransition(trans_node, trans_text);

    return trans; 
  }

  
  private void display(Grid grid) {
      
    float width = (float)stage.getWidth();
    float height = (float)stage.getHeight();
    
    canvas.setWidth(width);
    canvas.setHeight(height);


    float width_padding = width * (paddingPercent / 100f);
    float height_padding = height * (paddingPercent / 100f);

    float node_width = (width - width_padding) / grid.getCols();
    float node_height = (height - height_padding) / grid.getRows();
    
    if (node_width < node_height) {
      gc.setFont(new Font((int) (node_width * (textSizePercent/100f))));
    } else {
      gc.setFont(new Font((int) (node_height * (textSizePercent/100f))));
    }

      
    float node_arc_width = node_width * (roundingPercent / 100f);
    float node_arc_height = node_height * (roundingPercent / 100f);
    float pad_width = width_padding / (grid.getCols() + 1);
    float pad_height = height_padding / (grid.getRows() + 1);

    float x_old;
    float y_old;

    float x;
    float y;


    drawRect(gc, 0, 0, width, height, Color.WHITE);

    this.i = 0;
          
    SequentialTransition animation = new SequentialTransition();
  
    List<Group> frameGroups = new LinkedList<Group>();

    ParallelTransition transitions;
    for (List<Node> frames : grid.getFrames()) {
      Group frameGroup = new Group();
      
      transitions = new ParallelTransition(); 
      for (Node node : frames) {
        x = node_canvas_x(node.getPos().getX(), node_width, pad_width); 
        y = node_canvas_y(node.getPos().getY(), node_height, pad_height);
        
        drawRoundRect(gc, x, y, node_width, node_height, node_arc_width, node_arc_height, Color.GREY);
        
        if (node.getType() == NodeType.VALUE) {
          Rectangle rect = createRect(node_width, node_height, node_arc_width, node_arc_height, Color.BLACK);

          Text text = null;
          x_old = 0;
          y_old = 0;
          try {
            text = createText(node.getValue(), node_width, node_height, Color.WHITE);
            x_old = node_canvas_x(node.getOldPos().getX(), node_width, pad_width); 
            y_old = node_canvas_y(node.getOldPos().getY(), node_height, pad_height);

          } catch(NoValueException | CantMoveException e) {
            e.printStackTrace();
            System.exit(1);
          }
         
          frameGroup.getChildren().add(rect);
          frameGroup.getChildren().add(text);


          ParallelTransition tran = createTranslateAnimation(rect, text, x_old, y_old, x, y, node_width, node_height, node_arc_width, node_arc_height, Color.BLACK);

          transitions.getChildren().add(tran);
        }
      }
      frameGroup.setVisible(false);
      this.nodes.getChildren().add(frameGroup);
      frameGroups.add(frameGroup);

      transitions.setOnFinished(e -> {frameGroups.get(i).setVisible(false); i++; frameGroups.get(i).setVisible(true);});
      animation.getChildren().add(transitions);
    }

    frameGroups.get(0).setVisible(true);

    grid.setDefaultFrame();
    lock();
    animation.setOnFinished(e -> unlock(grid));
    animation.getChildren().get(animation.getChildren().size()-1).setOnFinished(e -> {});
    this.nodes.getChildren().removeAll(oldFrameGroups);
    this.oldFrameGroups = frameGroups;
    animation.play();
  }

  private void lock() {
    animate = false;
  }
  
  private void unlock(Grid grid) {
    animate = true;
  }
  

  public GraphicsView(Grid grid, Stage stage, float width, float height) {
    this.stage = stage;

    this.group = new Group();
    this.nodes = new Group();

    this.i = new Integer(0);
   
    this.oldFrameGroups = new LinkedList<Group>();

    this.canvas = new Canvas(width, height);
    this.group.getChildren().add(this.canvas);
    this.group.getChildren().add(this.nodes);

    
    this.scene = new Scene(this.group, Color.WHITE);
    this.stage.setScene(this.scene);
    
    this.gc = canvas.getGraphicsContext2D();

    this.input = new ArrayList<String>();

    this.scene.setOnKeyPressed(
        new EventHandler<KeyEvent>() {
          public void handle(KeyEvent e) {
            if (canPressFlag) {
              canPressFlag = false;
              String code = e.getCode().toString();
              if (!input.contains(code)) {
                input.add(code);
              }
            }
          }
        });

    this.scene.setOnKeyReleased(
        new EventHandler<KeyEvent>() {
          public void handle(KeyEvent e) {
            canPressFlag = true;
            String code = e.getCode().toString();
            input.remove(code);
          }
        });
    
    gc.setTextBaseline(VPos.CENTER);
    gc.setTextAlign(TextAlignment.CENTER);
    
    stage.show();
    display(grid);
  }
  
  @Override
  public List<Action> getInput() {
    if (input.isEmpty()) {
      List<Action> list = new ArrayList<Action>();
      list.add(Action.NONE);
      return list;
    } else {
      return View.convertStringToActions(String.join("", input));
    }
  }
 
  @Override
  public GameStats play(Grid grid, Algorithm algo) {
    GameStats stat = new GameStats();

    new AnimationTimer() {
      public void handle(long nanoTime) {
        if (animate) {
          try {
            if (grid.won()) {
              stat.won();
              stop();
            } else if (grid.lost()) {
              stat.lost();
              stop();
            }
            grid.process(algo.move(grid));
            display(grid);
            input.clear();
          } catch(InvalidActionException e) {
            e.printStackTrace();
            System.exit(1);
          }
        }
      }
    }.start();
    
    
    return stat;
  }


}
