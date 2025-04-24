/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cyberpro.game.view;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class GameOverWindow {

    public GameOverWindow(String message) {

        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Game Over");
        window.setMinWidth(300);
        window.setMinHeight(200);

        Label label = new Label(message);
        label.getStyleClass().add("game-over-label");

        Button closeButton = new Button("OK");
        closeButton.getStyleClass().add("game-over-button");
        closeButton.setOnAction(e -> window.close());

        VBox layout = new VBox(20);
        layout.getChildren().addAll(label, closeButton);
        layout.setAlignment(Pos.CENTER);
        layout.getStyleClass().add("game-over-layout");

        Scene scene = new Scene(layout, 400, 200);
        scene.getStylesheets().add(GameOverWindow.class.getResource("gameover.css").toExternalForm());

        window.setScene(scene);
        window.setIconified(false);
        window.toFront();
        window.setAlwaysOnTop(true);   
        // All this magic like to send the "Game over" window on the top of other windows
        window.showAndWait();
        window.requestFocus();

    }
}