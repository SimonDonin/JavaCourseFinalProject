/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cyberpro.game.view;

import cyberpro.game.controller.ControllerInterface;
import cyberpro.game.controller.GameController;
import java.io.FileNotFoundException;
import java.io.IOException;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 *
 * @author mikhail
 */
public class MainMenu extends Application {
    
    private ControllerInterface controller;

	// this constructor is needed by JavaFX
//	public MainMenu() {
//
//	}
//
//	public MainMenu(ControllerInterface controller) {
//		this.controller = controller;
//	}

	@Override
	public void start(Stage primaryStage) {
		// Title
		Label title = new Label("My Game Title");
		title.getStyleClass().add("game-title");

		// Player mode selection
		Label modeLabel = new Label("Select Mode:");
		modeLabel.getStyleClass().add("mode-label");

		ToggleGroup playerModeGroup = new ToggleGroup();
		RadioButton onePlayer = new RadioButton("1 Player");
		RadioButton twoPlayers = new RadioButton("2 Players");
		onePlayer.setToggleGroup(playerModeGroup);
		twoPlayers.setToggleGroup(playerModeGroup);
		onePlayer.setSelected(true); // default

		VBox modeSelection = new VBox(5, modeLabel, onePlayer, twoPlayers);
		modeSelection.setAlignment(Pos.CENTER);

		// Buttons
		Button startButton = new Button("Start Game");
		Button mapEditorButton = new Button("Map Editor");
		Button exitButton = new Button("Exit");

		startButton.getStyleClass().add("menu-button");
		mapEditorButton.getStyleClass().add("menu-button");
		exitButton.getStyleClass().add("menu-button");

		// Event handling
		startButton.setOnAction(e -> {
			String selectedMode = onePlayer.isSelected() ? "1 Player" : "2 Players";
			System.out.println("Start Game clicked: " + selectedMode + " mode");
			// launch game logic here
		});

		mapEditorButton.setOnAction(e -> {
			System.out.println("Map Editor clicked");
			// launch map editor
		});

		exitButton.setOnAction(e -> primaryStage.close());

		// Layout
		VBox menuLayout = new VBox(20);
		menuLayout.setAlignment(Pos.CENTER);
		menuLayout.getChildren().addAll(title, modeSelection, startButton, mapEditorButton, exitButton);
		menuLayout.getStyleClass().add("menu-background");

		Scene scene = new Scene(menuLayout, 600, 400);
		scene.getStylesheets().add(getClass().getResource("menu-style.css").toExternalForm());

		primaryStage.setTitle("Game Menu");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
        
    public MainMenu(Stage stage, ControllerInterface controller) {
        this.controller = controller; // Get Controller interface to call its methods
        // Title
        Label title = new Label("Bombermen");
        title.getStyleClass().add("game-title");

        // Player mode selection
        Label modeLabel = new Label("Select Mode:");
        modeLabel.getStyleClass().add("mode-label");

        ToggleGroup playerModeGroup = new ToggleGroup();
        RadioButton onePlayer = new RadioButton("1 Player");
        RadioButton twoPlayers = new RadioButton("2 Players");
        onePlayer.setToggleGroup(playerModeGroup);
        twoPlayers.setToggleGroup(playerModeGroup);
        twoPlayers.setSelected(true); // default

        VBox modeSelection = new VBox(5, modeLabel, onePlayer, twoPlayers);
        modeSelection.setAlignment(Pos.CENTER);

        // Buttons
        Button startButton = new Button("Start Game");
        Button mapEditorButton = new Button("Map Editor");
        Button exitButton = new Button("Exit");

        startButton.getStyleClass().add("menu-button");
        mapEditorButton.getStyleClass().add("menu-button");
        exitButton.getStyleClass().add("menu-button");

        // Event handling
        startButton.setOnAction(e -> {
            String selectedMode = onePlayer.isSelected() ? "1 Player" : "2 Players";
            System.out.println("Start Game clicked: " + selectedMode + " mode");
            // launch game logic here
            try {
                controller.startGame();
            } catch (FileNotFoundException err) {
                System.err.println("File not found: " + err.getMessage());
                err.printStackTrace();
            } catch (IOException err) {
                System.err.println("I/O error: " + err.getMessage());
                err.printStackTrace();
            }
        });

        mapEditorButton.setOnAction(e -> {
            System.out.println("Map Editor clicked");
            // launch map editor
        });

        exitButton.setOnAction(e -> stage.close());

        // Layout
        VBox menuLayout = new VBox(20);
        menuLayout.setAlignment(Pos.CENTER);
        menuLayout.getChildren().addAll(title, modeSelection, startButton, mapEditorButton, exitButton);
        menuLayout.getStyleClass().add("menu-background");

        Scene scene = new Scene(menuLayout, 600, 400);
        scene.getStylesheets().add(getClass().getResource("menu-style.css").toExternalForm());


        stage.setScene(scene);
        stage.setTitle("Game menu");
        stage.show();
    }
}
