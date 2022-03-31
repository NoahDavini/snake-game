package edu.wsu;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class SnakePane extends AnchorPane {

    private static final double RADIUS = 10;
    private int snakeLength, realSnakeSpeed, numFruits, score;
    private Color bodyColor, headColor, fruitColor;
    private List<Circle> snake, fruits;
    private double dx, dy, xTailDir, yTailDir;
    private Timeline timeline;
    private String snakeSpeed, playerName;
    private enum TailDirection {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }
    private TailDirection tailDirection;
    Text text;

    public SnakePane(int snakeLength, Color bodyColor, Color headColor, String snakeSpeed, int numFruits, Color fruitColor, String playerName) {
        this.snakeLength = snakeLength;
        this.bodyColor = bodyColor;
        this.headColor = headColor;
        this.snakeSpeed = snakeSpeed;
        this.numFruits = numFruits;
        this.fruitColor = fruitColor;
        this.playerName = playerName;

        if (snakeSpeed.equals("Slow")) realSnakeSpeed = 130;
        if (snakeSpeed.equals("Normal")) realSnakeSpeed = 100;
        if (snakeSpeed.equals("Fast")) realSnakeSpeed = 70;
        if (snakeSpeed.equals("Warp Speed")) realSnakeSpeed = 40;

        this.dx = 2 * RADIUS;
        this.dy = 0;

        this.score = 0;

        snake = new LinkedList<>();

        fruits = new LinkedList<>();

        text = new Text();
        getChildren().add(text);
    }

    public void startGame() {
        createSnake();
        getChildren().addAll(snake);

        generateFruit();
        getChildren().addAll(fruits);

        timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);

        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(realSnakeSpeed),
        event -> handleGameEvent()));
        timeline.play();

        this.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case UP:
                    this.dx = 0; this.dy = -2 * RADIUS;
                    break;
                case DOWN:
                    this.dx = 0; this.dy = 2 * RADIUS;
                    break;
                case LEFT:
                    this.dx = -2 * RADIUS; this.dy = 0;
                    break;
                case RIGHT:
                    this.dx = 2 * RADIUS; this.dy = 0;
                    break;
            }
        });
        this.requestFocus();
    }

    private void handleGameEvent() {
        moveSnake();
        HUD();
        if (collision()) {
            gameOver();
        }
        if (fruitEncounter()) {
            consumeFruit();
            score++;
        }
    }

    private void createSnake() {
        double centerX = getWidth() / 2;
        double centerY = getHeight() / 2;
        Circle head = new Circle(centerX, centerY, RADIUS, this.headColor);
        snake.add(head);
        for (int i = 1; i <= snakeLength; i++) {
            Circle bodySegment = new Circle(centerX - i * 2 * RADIUS, centerY, RADIUS, this.bodyColor);
            snake.add(bodySegment);
        }
    }

    public void moveSnake() {
        Circle head = snake.get(0);
        Circle lastSegment = snake.remove(snake.size() - 1);

        lastSegment.setCenterX((head.getCenterX()+ dx));
        lastSegment.setCenterY((head.getCenterY()+ dy));
        lastSegment.setFill(this.headColor);

        head.setFill(this.bodyColor);

        snake.add(0, lastSegment);
    }

    public double generateRandXY(double getWH) {
        Random r = new Random();
        return r.nextInt((int) (getWH / (2 * RADIUS))) * 2 * RADIUS + RADIUS;
    }

    public void generateFruit() {
        for (int i = 0; i < numFruits; i++) {
            double fruitCenterX = generateRandXY(getWidth());
            double fruitCenterY = generateRandXY(getHeight());
            for (int j = 0; j < snakeLength; j++) {
                while (fruitCenterX == snake.get(j).getCenterX() && fruitCenterY == snake.get(j).getCenterY()) {
                    fruitCenterX = generateRandXY(getWidth());
                    fruitCenterY = generateRandXY(getHeight());
                }
            }
            Circle fruit = new Circle(fruitCenterX, fruitCenterY, RADIUS, this.fruitColor);
            fruits.add(fruit);
        }
    }

    public boolean collision() {
        double headPosX = snake.get(0).getCenterX();
        double headPosY = snake.get(0).getCenterY();
        for (int i = 1; i < snakeLength; i++) {
            if (Math.abs(headPosX - snake.get(i).getCenterX()) < 1e-7
                    && Math.abs(headPosY - snake.get(i).getCenterY()) < 1e-7)
                return true;
        }
        return headPosX >= getWidth() || headPosX <= 0
                || headPosY >= getHeight() || headPosY <= 0;
    }

    public boolean fruitEncounter() {
        double headPosX = snake.get(0).getCenterX();
        double headPosY = snake.get(0).getCenterY();
        for (Circle fruit : fruits) {
            if (headPosX == fruit.getCenterX()
                    && headPosY == fruit.getCenterY()) {
                return true;
            }
        }
        return false;
    }

    public void setTailDirection() {
        xTailDir = snake.get(snakeLength - 2).getCenterX() - snake.get(snakeLength - 1).getCenterX();
        yTailDir = snake.get(snakeLength - 2).getCenterY() - snake.get(snakeLength - 1).getCenterY();
        if (yTailDir < 0)
            tailDirection = TailDirection.UP;
        if (yTailDir > 0)
            tailDirection = TailDirection.DOWN;
        if (xTailDir < 0)
            tailDirection = TailDirection.LEFT;
        if (xTailDir > 0)
            tailDirection = TailDirection.RIGHT;
    }

    public void consumeFruit() {
        Circle newSnakeSegment = new Circle((snake.get(snakeLength - 1).getCenterX() + xTailDir),
                (snake.get(snakeLength - 1).getCenterY() + yTailDir), RADIUS, bodyColor);
        snake.add(newSnakeSegment);
        snakeLength++;
        for (int i = 0; i < snakeLength; i++) {
            for (Circle fruit : fruits) {
                while (snake.get(i).getCenterX() == fruit.getCenterX()
                        && snake.get(i).getCenterY() == fruit.getCenterY()) {
                    fruit.setCenterX(generateRandXY(getWidth()));
                    fruit.setCenterY(generateRandXY(getHeight()));
                }
            }
        }
        getChildren().add(newSnakeSegment);
    }

    public void HUD() {
        text.setText("Name: " + playerName + "\nScore: " + this.score);
        text.setY(RADIUS);
        requestFocus();
    }

    public void gameOver() {
        timeline.stop();
        StackPane stackPane = new StackPane();
        Rectangle rectangle = new Rectangle(300, 200, Color.WHITE);
        Text text;
        if (snakeLength + fruits.size() == getWidth() * getHeight() / 2 * RADIUS)
            text = new Text("YOU WON!\nName: " + playerName + "\nScore: " + score);
        else
            text = new Text("Game Over\nName: " + playerName + "\nScore: " + score);
        stackPane.getChildren().addAll(rectangle, text);
        stackPane.setLayoutX(150);
        stackPane.setLayoutY(100);
        getChildren().add(stackPane);
    }
}
