package com.my.app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SimpleTestApp extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        System.out.println("JavaFX Application starting...");
        
        Label titleLabel = new Label("🎨 Advanced Image Processor - Test Mode");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        Label statusLabel = new Label("JavaFX is working correctly!");
        statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: green;");
        
        Button testButton = new Button("Test Button - Click Me!");
        testButton.setOnAction(e -> {
            statusLabel.setText("Button clicked! JavaFX events are working.");
            System.out.println("Test button clicked successfully!");
        });
        
        VBox root = new VBox(20);
        root.getChildren().addAll(titleLabel, statusLabel, testButton);
        root.setStyle("-fx-padding: 40; -fx-alignment: center; -fx-background-color: #f5f5f5;");
        
        Scene scene = new Scene(root, 400, 200);
        primaryStage.setTitle("Advanced Image Processor - JavaFX Test");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        System.out.println("JavaFX Application started successfully!");
    }
    
    public static void main(String[] args) {
        System.out.println("Starting JavaFX Test Application...");
        launch(args);
    }
}