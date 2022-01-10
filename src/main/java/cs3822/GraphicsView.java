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


class GraphicsView implements View {
  private Stage stage;

  private Group group;
  private Scene scene;
  private Canvas canvas;
  private GraphicsContext gc;

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

    float x;
    float y;

    if (node_width < node_height) {
      gc.setFont(new Font((int) (node_width * (textSizePercent/100f))));
    } else {
      gc.setFont(new Font((int) (node_height * (textSizePercent/100f))));
    }

    gc.setFill(Color.WHITE);
    gc.fillRect(0, 0, width, height);

    for (Node node : grid.getNodes() ) { 
      
      x = node.getPos().getX();
      x = ((x+1) * pad_width) + (x * node_width);

      y = node.getPos().getY();
      y = ((y+1) * pad_height) + (y * node_height);

      
      if (node.getType() == NodeType.VALUE) {
        gc.setFill(Color.BLACK);
        gc.fillRoundRect(x, y, node_width, node_height, node_arc_width, node_arc_height);
        
        try {
          gc.setFill(Color.WHITE);
          gc.fillText(String.valueOf(node.getValue()), x+(node_width/2), y+(node_height/2), node_width);
        } catch(NoValueException e) {
          e.printStackTrace();
          System.exit(1);
        }
      } 
      else if (node.getType() == NodeType.EMPTY) {
        gc.setFill(Color.GREY);
        gc.fillRoundRect(x, y, node_width, node_height, node_arc_width, node_arc_height);
      }
    }
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
