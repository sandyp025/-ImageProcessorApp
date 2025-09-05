package com.my.app;

import com.my.app.filters.FilterFactory;
import com.my.app.filters.ImageFilter;
import com.my.app.io.ImageFileIO;
import com.my.app.io.ImageOperations;
import com.my.app.processor.ImageProcessor;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorkingHelloApplication extends Application {
    private BufferedImage currentImage;
    private String currentImageName;
    private ImageOperations imageIO;
    private ImageProcessor processor;
    private TextArea logArea;
    private ExecutorService executorService;
    
    @Override
    public void start(Stage primaryStage) {
        System.out.println("🎨 Advanced Image Processor - Starting...");
        
        this.imageIO = new ImageFileIO();
        this.processor = new ImageProcessor();
        this.executorService = Executors.newCachedThreadPool();
        
        try {
            // Load default image
            loadDefaultImage();
            
            // Create UI
            VBox root = createMainUI();
            
            Scene scene = new Scene(root, 800, 600);
            primaryStage.setTitle("🎨 Advanced Image Processor - Made in India ❤️ by Sandip Mandal ✨");
            primaryStage.setScene(scene);
            primaryStage.show();
            
            System.out.println("✅ Application started successfully!");
            logArea.appendText("🎨 Advanced Image Processor loaded successfully!\\n");
            logArea.appendText("✅ " + FilterFactory.getFilterNames().size() + " advanced filters available\\n");
            logArea.appendText("📸 Default image: " + currentImageName + "\\n");
            logArea.appendText("🚀 Ready for image processing!\\n");
            
        } catch (Exception e) {
            System.err.println("❌ Error starting application: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private VBox createMainUI() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #f5f5f5;");
        
        // Title
        Label titleLabel = new Label("🎨 Advanced Image Processor");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        // Current image info
        Label imageInfoLabel = new Label();
        if (currentImage != null) {
            imageInfoLabel.setText("📸 Current Image: " + currentImageName + " (" + 
                                 currentImage.getWidth() + "x" + currentImage.getHeight() + ")");
        } else {
            imageInfoLabel.setText("📸 No image loaded");
        }
        imageInfoLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
        
        // Filter selection
        Label filtersLabel = new Label("🎛️ Available Filters:");
        filtersLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        ComboBox<String> filterComboBox = new ComboBox<>();
        filterComboBox.getItems().addAll(FilterFactory.getFilterNames());
        filterComboBox.setValue(filterComboBox.getItems().get(0)); // Select first filter
        filterComboBox.setPrefWidth(300);
        
        // Processing options
        CheckBox asyncCheckBox = new CheckBox("Asynchronous Processing (Recommended)");
        asyncCheckBox.setSelected(true);
        
        Label tileSizeLabel = new Label("Tile Size:");
        Slider tileSizeSlider = new Slider(20, 100, 40);
        tileSizeSlider.setShowTickLabels(true);
        tileSizeSlider.setPrefWidth(200);
        Label tileSizeValueLabel = new Label("40");
        tileSizeSlider.valueProperty().addListener((obs, oldVal, newVal) -> 
            tileSizeValueLabel.setText(String.valueOf(newVal.intValue())));
        
        HBox tileSizeBox = new HBox(10);
        tileSizeBox.setAlignment(Pos.CENTER);
        tileSizeBox.getChildren().addAll(tileSizeLabel, tileSizeSlider, tileSizeValueLabel);
        
        // Process button
        Button processButton = new Button("🚀 Apply Filter");
        processButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10;");
        processButton.setPrefWidth(200);
        
        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(300);
        progressBar.setVisible(false);
        
        // Process button action
        processButton.setOnAction(e -> {
            if (currentImage == null) {
                showAlert("No Image", "Please load an image first.");
                return;
            }
            
            String selectedFilter = filterComboBox.getValue();
            boolean isAsync = asyncCheckBox.isSelected();
            int tileSize = (int) tileSizeSlider.getValue();
            
            processButton.setDisable(true);
            progressBar.setVisible(true);
            progressBar.setProgress(-1); // Indeterminate
            
            logArea.appendText("🔄 Applying " + selectedFilter + " filter " + (isAsync ? "asynchronously" : "synchronously") + "...\\n");
            
            // Process in background
            executorService.submit(() -> {
                try {
                    ImageFilter filter = FilterFactory.getFilter(selectedFilter);
                    BufferedImage processedImage = processor.processImage(currentImage, tileSize, filter, isAsync);
                    
                    // Save processed image
                    String outputFileName = "filtered_" + selectedFilter.toLowerCase().replace(" ", "_") + "_" + currentImageName;
                    imageIO.sendImage(processedImage, outputFileName);
                    
                    Platform.runLater(() -> {
                        progressBar.setProgress(1);
                        progressBar.setVisible(false);
                        processButton.setDisable(false);
                        logArea.appendText("✅ " + selectedFilter + " filter applied successfully!\\n");
                        logArea.appendText("💾 Saved as: " + outputFileName + "\\n");
                        showAlert("Success", "Filter applied successfully!\\nSaved as: " + outputFileName);
                    });
                    
                } catch (Exception ex) {
                    Platform.runLater(() -> {
                        progressBar.setVisible(false);
                        processButton.setDisable(false);
                        logArea.appendText("❌ Error applying filter: " + ex.getMessage() + "\\n");
                        showAlert("Error", "Error applying filter: " + ex.getMessage());
                    });
                }
            });
        });
        
        // Image selection buttons
        HBox imageButtonsBox = new HBox(10);
        imageButtonsBox.setAlignment(Pos.CENTER);
        
        Button loadSample1 = new Button("🏔️ Landscape");
        Button loadSample2 = new Button("🏙️ City");
        Button loadSample3 = new Button("👤 Portrait");
        Button loadOriginal = new Button("🎨 Original");
        
        loadSample1.setOnAction(e -> loadSampleImage("landscape.jpg"));
        loadSample2.setOnAction(e -> loadSampleImage("city.jpg"));
        loadSample3.setOnAction(e -> loadSampleImage("portrait.jpg"));
        loadOriginal.setOnAction(e -> loadSampleImage("quino-al-mBQIfKlvowM-unsplash.jpg"));
        
        imageButtonsBox.getChildren().addAll(loadSample1, loadSample2, loadSample3, loadOriginal);
        
        // Log area
        Label logLabel = new Label("📋 Processing Log:");
        logLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        logArea = new TextArea();
        logArea.setPrefRowCount(8);
        logArea.setEditable(false);
        logArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px;");
        
        // Add all components
        root.getChildren().addAll(
            titleLabel,
            new Separator(),
            imageInfoLabel,
            new Separator(),
            filtersLabel,
            filterComboBox,
            asyncCheckBox,
            tileSizeBox,
            processButton,
            progressBar,
            new Separator(),
            new Label("📷 Load Sample Images:"),
            imageButtonsBox,
            new Separator(),
            logLabel,
            logArea
        );
        
        return root;
    }
    
    private void loadDefaultImage() {
        try {
            URI resourceURI = getClass().getResource("/quino-al-mBQIfKlvowM-unsplash.jpg").toURI();
            Path path = Paths.get(resourceURI);
            loadImageFromPath(path.toString());
            System.out.println("✅ Default image loaded: " + currentImageName);
        } catch (Exception e) {
            System.err.println("⚠️ Could not load default image: " + e.getMessage());
        }
    }
    
    private void loadSampleImage(String imageName) {
        try {
            URI resourceURI = getClass().getResource("/" + imageName).toURI();
            Path path = Paths.get(resourceURI);
            loadImageFromPath(path.toString());
            logArea.appendText("📷 Loaded image: " + imageName + "\\n");
            
            // Update image info label
            ((VBox) logArea.getParent()).getChildren().stream()
                .filter(node -> node instanceof Label && ((Label) node).getText().startsWith("📸"))
                .findFirst()
                .ifPresent(label -> ((Label) label).setText("📸 Current Image: " + currentImageName + " (" + 
                    currentImage.getWidth() + "x" + currentImage.getHeight() + ")"));
                    
        } catch (Exception e) {
            logArea.appendText("❌ Error loading " + imageName + ": " + e.getMessage() + "\\n");
            showAlert("Error", "Could not load image: " + imageName);
        }
    }
    
    private void loadImageFromPath(String imagePath) {
        Optional<BufferedImage> imageOpt = imageIO.readImage(imagePath);
        if (imageOpt.isPresent()) {
            currentImage = imageOpt.get();
            currentImageName = Paths.get(imagePath).getFileName().toString();
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    @Override
    public void stop() throws Exception {
        super.stop();
        if (executorService != null) {
            executorService.shutdown();
        }
        System.out.println("🛑 Application stopped gracefully");
    }
    
    public static void main(String[] args) {
        System.out.println("🚀 Starting Advanced Image Processor...");
        launch(args);
    }
}