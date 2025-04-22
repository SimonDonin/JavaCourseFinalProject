/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cyberpro.game.view;

import cyberpro.game.controller.ControllerInterface;
import cyberpro.game.controller.GameController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import java.io.FileNotFoundException;
import java.io.IOException;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.File;
import java.util.Arrays;

/**
 *
 * @author mikhail
 */
public class MainMenu extends Application {
    
    private final ControllerInterface controller;
    private Stage stage;

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("Game Menu");
        primaryStage.show();
    }
        
    public MainMenu(Stage stage, ControllerInterface controller) {
        this.controller = controller; // Get Controller interface to call its methods
        this.stage = stage;
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
        
        // ListView with .txt level files 
        Label levelsLabel = new Label("Available Levels:");
        levelsLabel.getStyleClass().add("mode-label");

        ListView<String> levelListView = new ListView<>();
        ObservableList<String> levelFileNames = FXCollections.observableArrayList();

        // Folder path (absolute patch)
        String patch = GameController.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        patch = patch + "cyberpro/game/model/";
        File folder = new File(patch);

        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));
            if (files != null) {
                Arrays.stream(files)
                        .map(File::getName)
                        .forEach(levelFileNames::add);
            }
        }
        levelListView.setItems(levelFileNames);
        levelListView.setMaxHeight(400);
        
        // Selection handler
        levelListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                File selectedFile = new File(folder, newVal);
                System.out.println("Selected file full path: " + selectedFile.getAbsolutePath());
                controller.setLevel("/cyberpro/game/model/" +  newVal);
            }
        });
        
        VBox levelSelection = new VBox(2, levelsLabel, levelListView);
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
                stage.hide();
                controller.startGame();
            } catch (FileNotFoundException err) {
                System.err.println("File not found: " + err.getMessage());
                err.printStackTrace();
            } catch (IOException err) {
                System.err.println("I/O error: " + err.getMessage());
                err.printStackTrace();
            }
        });

        exitButton.setOnAction(e -> stage.close());

        // Layout
        VBox menuLayout = new VBox(20);
        menuLayout.setAlignment(Pos.CENTER);
        menuLayout.getChildren().addAll(title, modeSelection, startButton, levelSelection, exitButton);
        menuLayout.getStyleClass().add("menu-background");

        Scene scene = new Scene(menuLayout, 600, 600);
        scene.getStylesheets().add(getClass().getResource("menu-style.css").toExternalForm());


        stage.setScene(scene);
        stage.setTitle("Game menu");
        stage.show();
    }
    
    public void showMainMenu() {
        stage.show();
        stage.requestFocus();
    }
}
