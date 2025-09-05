package com.my.app.ui;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ImageSelectionDialog {
    private static final List<String> SAMPLE_IMAGES = Arrays.asList(
        "quino-al-mBQIfKlvowM-unsplash.jpg",
        "painting-mountain-lake-with-mountain-background.jpg",
        "landscape.jpg",
        "city.jpg",
        "portrait.jpg"
    );
    
    public static Optional<String> showImageSelectionDialog(Window owner) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Select Image");
        dialog.setHeaderText("Choose an image to process:");
        dialog.initOwner(owner);
        
        VBox content = new VBox(10);
        
        ListView<String> listView = new ListView<>();
        listView.getItems().addAll(SAMPLE_IMAGES);
        listView.getSelectionModel().selectFirst();
        listView.setPrefHeight(150);
        
        Button browseButton = new Button("Browse Custom Image...");
        browseButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Image File");
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
            );
            
            File selectedFile = fileChooser.showOpenDialog(owner);
            if (selectedFile != null) {
                dialog.setResult(selectedFile.getAbsolutePath());
                dialog.close();
            }
        });
        
        content.getChildren().addAll(
            new Label("Sample Images:"),
            listView,
            new Separator(),
            browseButton
        );
        
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                String selected = listView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    return "/" + selected; // Return as resource path
                }
            }
            return null;
        });
        
        return dialog.showAndWait();
    }
}