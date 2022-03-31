package edu.wsu;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class PrimaryController {

    @FXML
    TextField playerName;
    @FXML
    ComboBox<Integer> snakeLength;
    @FXML
    ColorPicker snakeColorPicker;
    @FXML
    ComboBox<String> snakeSpeed;
    @FXML
    ComboBox<Integer> numFruits;
    @FXML
    ColorPicker fruitColorPicker;
    @FXML
    Button startButton;

    public void handleButtonAction(ActionEvent actionEvent) {
        Node src = (Node) actionEvent.getSource();
        Stage stage = (Stage) src.getScene().getWindow();
        SnakePane sp = new SnakePane(snakeLength.getValue(), snakeColorPicker.getValue(), Color.RED,
                snakeSpeed.getValue(), numFruits.getValue(), fruitColorPicker.getValue(), playerName.getText());
        Scene scene = new Scene(sp, 620, 420);
        stage.setScene(scene);
        sp.startGame();
    }

    public void initialize() {
        for (int i = 1; i <= 10; i++) {
            snakeLength.getItems().add(i);
            numFruits.getItems().add(i);
        }
        snakeSpeed.getItems().add("Slow");
        snakeSpeed.getItems().add("Normal");
        snakeSpeed.getItems().add("Fast");
        snakeSpeed.getItems().add("Warp Speed");
        snakeLength.setValue(1);
        snakeSpeed.setValue("Normal");
        numFruits.setValue(1);
        snakeColorPicker.setValue(Color.GREEN);
        fruitColorPicker.setValue(Color.PURPLE);
    }
}
