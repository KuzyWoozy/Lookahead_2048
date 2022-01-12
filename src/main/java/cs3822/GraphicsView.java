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


class GraphicsView implements View {
  private Stage stage;

  private Group group;
  private Scene scene;
  private Canvas canvas;
  private GraphicsContext gc;

  private Duration animationLength = new Duration(1000);

  private float paddingPercent = 5;
  private float roundingPercent = 15;
  private float textSizePercent = 35;

  private ArrayList<String> input;

  private boolean canPressFlag = true;

  private void display(Grid grid) {
    
    float width = (float)stage.getWidth();
    float height = (float)stage.getHeight();

    canvas.setWidth(width);
    canvas.setHeight(height);

    float width_padding = width * (paddingPercent / 100f);
    float height_padding = height * (paddingPercent / 100f);

    float node_width = (width - width_padding) / grid.getCols();
    float node_height = (height - height_padding) / grid.getRows();
    float node_arc_width = node_width * (roundingPercent / 100f);
    float node_arc_height = node_height * (roundingPercent / 100f);
    float pad_width = width_padding / (grid.getCols() + 1);
    float pad_height = height_padding / (grid.getRows() + 1);

    float x_old;
    float y_old;

    float x;
    float y;

    float x_text_old;
    float y_text_old;

    float x_text;
    float y_text;

    if (node_width < node_height) {
      gc.setFont(new Font((int) (node_width * (textSizePercent/100f))));
    } else {
      gc.setFont(new Font((int) (node_height * (textSizePercent/100f))));
    }

    gc.setFill(Color.WHITE);
    gc.fillRect(0, 0, width, height);

    ParallelTransition transitions = new ParallelTransition();

    for (Node node : grid.getNodes() ) {
        
      x = node.getPos().getX();
      x = ((x+1) * pad_width) + (x * node_width);

      y = node.getPos().getY();
      y = ((y+1) * pad_height) + (y * node_height);

      if (node.getType() == NodeType.VALUE) {
        x_old = 0;
        y_old = 0;
        try {
          x_old = node.getOldPos().getX();
          y_old = node.getOldPos().getY();
        } catch(CantMoveException e) {
          e.printStackTrace();
          System.exit(1);
        }

        x_old = ((x_old+1) * pad_width) + (x_old * node_width);
        y_old = ((y_old+1) * pad_height) + (y_old * node_height);
       

        Rectangle rect = new Rectangle(width, height, Color.BLACK);
        rect.setArcWidth(node_arc_width);
        rect.setArcHeight(node_arc_height);
        
        TranslateTransition transition= new TranslateTransition(animationLength, rect);

        transition.setFromX(x_old);
        transition.setFromY(y_old);

        transition.setToX(x);
        transition.setToY(y);

        transitions.getChildren().add(transition);

        Text text = null;
        try {
          text = new Text(String.valueOf(node.getValue()));
        } catch(NoValueException e) {
          e.printStackTrace();
          System.exit(1);
        }

        x_text_old = x_old + node_width / 2;
        y_text_old = y_old + node_height / 2;

        x_text = x + node_width / 2;
        y_text = y + node_height / 2;

        TranslateTransition transitionText = new TranslateTransition(animationLength, text);

        transitionText.setFromX(x_text_old);
        transitionText.setFromY(y_text_old);

        transitionText.setToX(x_text);
        transitionText.setToY(y_text);

        transitions.getChildren().add(transitionText);

      } else if (node.getType() == NodeType.EMPTY) {
        gc.setFill(Color.GREY);
        gc.fillRoundRect(x, y, node_width, node_height, node_arc_width, node_arc_height);
      }
    }
    transitions.play();
  }


  public GraphicsView(Grid grid, Stage stage, float width, float height) {
    this.stage = stage;

    this.group = new Group();
    this.canvas = new Canvas(width, height);
    this.group.getChildren().add(this.canvas);
    
    this.scene = new Scene(this.group);
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
    
    gc.setTextAlign(TextAlignment.CENTER);
    gc.setTextBaseline(VPos.CENTER);
 
    stage.show();
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
        try {
          if (grid.won()) {
            stat.won();
            stop();
          } else if (grid.lost()) {
            stat.lost();
            stop();
          }
          display(grid);
          grid.process(algo.move(grid));

          input.clear();
        } catch(InvalidActionException e) {
          e.printStackTrace();
          System.exit(1);
        }
      }
    }.start();
    return stat;
  }

}
