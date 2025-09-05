package com.my.app;

import com.my.app.batch.BatchProcessor;
import com.my.app.filters.FilterFactory;
import com.my.app.filters.ImageFilter;
import com.my.app.image.DrawMultipleImagesOnCanvas;
import com.my.app.io.ImageFileIO;
import com.my.app.io.ImageOperations;
import com.my.app.processor.ImageProcessor;
import com.my.app.ui.ImageSelectionDialog;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HelloApplication extends Application {
    private Stage primaryStage;
    private BufferedImage currentImage;
    private String currentImageName;
    private ImageOperations imageIO;
    private ImageProcessor processor;
    private ImageView originalImageView;
    private ImageView processedImageView;
    private ProgressBar processingProgress;
    private TextArea logArea;
    private ExecutorService executorService;
    
    @Override
    public void start(Stage stage) throws IOException, URISyntaxException {
        this.primaryStage = stage;
        this.imageIO = new ImageFileIO();
        this.processor = new ImageProcessor();
        this.executorService = Executors.newCachedThreadPool();
        
        initializeUI();
        
        // Load default image
        loadDefaultImage();
    }
    
    private void initializeUI() {
        primaryStage.setTitle("Advanced Image Processor - Made in India ❤️ by Sandip Mandal ✨");
        
        // Create main layout
        BorderPane root = new BorderPane();
        
        // Top menu bar
        MenuBar menuBar = createMenuBar();
        root.setTop(menuBar);
        
        // Center content with image views
        HBox imageContainer = createImageContainer();
        root.setCenter(imageContainer);
        
        // Right control panel
        VBox controlPanel = createControlPanel();
        root.setRight(controlPanel);
        
        // Bottom status area
        VBox statusArea = createStatusArea();
        root.setBottom(statusArea);
        
        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        
        Menu fileMenu = new Menu("File");
        MenuItem openItem = new MenuItem("Open Image...");
        openItem.setOnAction(e -> openImageFile());
        MenuItem saveItem = new MenuItem("Save Processed Image...");
        saveItem.setOnAction(e -> saveProcessedImage());
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> Platform.exit());
        fileMenu.getItems().addAll(openItem, saveItem, new SeparatorMenuItem(), exitItem);
        
        Menu toolsMenu = new Menu("Tools");
        MenuItem batchItem = new MenuItem("Batch Processing...");
        batchItem.setOnAction(e -> showBatchProcessingDialog());
        toolsMenu.getItems().add(batchItem);
        
        Menu helpMenu = new Menu("Help");
        MenuItem aboutItem = new MenuItem("About");
        aboutItem.setOnAction(e -> showAboutDialog());
        helpMenu.getItems().add(aboutItem);
        
        menuBar.getMenus().addAll(fileMenu, toolsMenu, helpMenu);
        return menuBar;
    }
    
    private HBox createImageContainer() {
        HBox container = new HBox(20);
        container.setPadding(new Insets(20));
        container.setAlignment(Pos.CENTER);
        
        VBox originalContainer = new VBox(10);
        originalContainer.setAlignment(Pos.CENTER);
        Label originalLabel = new Label("Original Image");
        originalLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        originalImageView = new ImageView();
        originalImageView.setFitWidth(400);
        originalImageView.setFitHeight(300);
        originalImageView.setPreserveRatio(true);
        originalContainer.getChildren().addAll(originalLabel, originalImageView);
        
        VBox processedContainer = new VBox(10);
        processedContainer.setAlignment(Pos.CENTER);
        Label processedLabel = new Label("Processed Image");
        processedLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        processedImageView = new ImageView();
        processedImageView.setFitWidth(400);
        processedImageView.setFitHeight(300);
        processedImageView.setPreserveRatio(true);
        processedContainer.getChildren().addAll(processedLabel, processedImageView);
        
        container.getChildren().addAll(originalContainer, processedContainer);
        return container;
    }
    
    private VBox createControlPanel() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));
        panel.setPrefWidth(300);
        panel.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #cccccc;");
        
        Label titleLabel = new Label("Image Processing Controls");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        
        Button selectImageBtn = new Button("Select Image");
        selectImageBtn.setPrefWidth(200);
        selectImageBtn.setOnAction(e -> showImageSelectionDialog());
        
        Label filtersLabel = new Label("Available Filters:");
        filtersLabel.setStyle("-fx-font-weight: bold;");
        
        ListView<String> filterList = new ListView<>();
        filterList.getItems().addAll(FilterFactory.getFilterNames());
        filterList.setPrefHeight(200);
        filterList.getSelectionModel().selectFirst();
        
        Label processingLabel = new Label("Processing Options:");
        processingLabel.setStyle("-fx-font-weight: bold;");
        
        RadioButton asyncRadio = new RadioButton("Asynchronous (Parallel)");
        RadioButton syncRadio = new RadioButton("Synchronous (Sequential)");
        ToggleGroup processingGroup = new ToggleGroup();
        asyncRadio.setToggleGroup(processingGroup);
        syncRadio.setToggleGroup(processingGroup);
        asyncRadio.setSelected(true);
        
        Label tileSizeLabel = new Label("Tile Size:");
        Slider tileSizeSlider = new Slider(20, 100, 40);
        tileSizeSlider.setShowTickLabels(true);
        tileSizeSlider.setShowTickMarks(true);
        tileSizeSlider.setMajorTickUnit(20);
        Label tileSizeValue = new Label("40");
        tileSizeSlider.valueProperty().addListener((obs, oldVal, newVal) -> 
            tileSizeValue.setText(String.valueOf(newVal.intValue())));
        
        Button processBtn = new Button("Apply Filter");
        processBtn.setPrefWidth(200);
        processBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        processBtn.setOnAction(e -> {
            String selectedFilter = filterList.getSelectionModel().getSelectedItem();
            boolean isAsync = asyncRadio.isSelected();
            int tileSize = (int) tileSizeSlider.getValue();
            processImage(selectedFilter, isAsync, tileSize);
        });
        
        processingProgress = new ProgressBar(0);
        processingProgress.setPrefWidth(200);
        
        panel.getChildren().addAll(
            titleLabel, new Separator(),
            selectImageBtn, new Separator(),
            filtersLabel, filterList, new Separator(),
            processingLabel, asyncRadio, syncRadio, new Separator(),
            tileSizeLabel, tileSizeSlider, tileSizeValue, new Separator(),
            processBtn, processingProgress
        );
        
        return panel;
    }
    
    private VBox createStatusArea() {
        VBox statusArea = new VBox(5);
        statusArea.setPadding(new Insets(10));
        statusArea.setStyle("-fx-background-color: #f0f0f0;");
        
        Label logLabel = new Label("Processing Log:");
        logLabel.setStyle("-fx-font-weight: bold;");
        
        logArea = new TextArea();
        logArea.setPrefRowCount(4);
        logArea.setEditable(false);
        logArea.appendText("Welcome to Advanced Image Processor!\n");
        logArea.appendText("Select an image and apply filters to get started.\n");
        
        statusArea.getChildren().addAll(logLabel, logArea);
        return statusArea;
    }

    private void loadDefaultImage() {
        try {
            URI resourceURI = getClass().getResource("/quino-al-mBQIfKlvowM-unsplash.jpg").toURI();
            Path path = Paths.get(resourceURI);
            loadImageFromPath(path.toString());
            logArea.appendText("Default image loaded: " + path.getFileName().toString() + "\n");
        } catch (Exception e) {
            logArea.appendText("Error loading default image: " + e.getMessage() + "\n");
        }
    }
    
    private void showImageSelectionDialog() {
        Optional<String> selectedPath = ImageSelectionDialog.showImageSelectionDialog(primaryStage);
        if (selectedPath.isPresent()) {
            String path = selectedPath.get();
            if (path.startsWith("/")) {
                // Resource path
                try {
                    URI resourceURI = getClass().getResource(path).toURI();
                    loadImageFromPath(Paths.get(resourceURI).toString());
                } catch (Exception e) {
                    logArea.appendText("Error loading resource image: " + e.getMessage() + "\n");
                }
            } else {
                // File path
                loadImageFromPath(path);
            }
        }
    }
    
    private void openImageFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image File");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"),
            new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        
        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile != null) {
            loadImageFromPath(selectedFile.getAbsolutePath());
        }
    }
    
    private void loadImageFromPath(String imagePath) {
        Optional<BufferedImage> imageOpt = imageIO.readImage(imagePath);
        if (imageOpt.isPresent()) {
            currentImage = imageOpt.get();
            currentImageName = new File(imagePath).getName();
            
            // Convert BufferedImage to JavaFX Image
            Image fxImage = convertToFXImage(currentImage);
            originalImageView.setImage(fxImage);
            
            // Initialize canvas for drawing
            DrawMultipleImagesOnCanvas drawMultipleImagesOnCanvas = DrawMultipleImagesOnCanvas.getInstance();
            drawMultipleImagesOnCanvas.initialize(new Stage(), currentImage.getWidth(), currentImage.getHeight());
            
            logArea.appendText("Loaded image: " + currentImageName + " (" + 
                             currentImage.getWidth() + "x" + currentImage.getHeight() + ")\n");
        } else {
            logArea.appendText("Failed to load image from: " + imagePath + "\n");
        }
    }
    
    private void processImage(String filterName, boolean isAsync, int tileSize) {
        if (currentImage == null) {
            logArea.appendText("No image loaded. Please select an image first.\n");
            return;
        }
        
        ImageFilter filter = FilterFactory.getFilter(filterName);
        if (filter == null) {
            logArea.appendText("Filter not found: " + filterName + "\n");
            return;
        }
        
        logArea.appendText("Applying " + filterName + " filter " + (isAsync ? "asynchronously" : "synchronously") + "...\n");
        
        Task<BufferedImage> task = new Task<BufferedImage>() {
            @Override
            protected BufferedImage call() throws Exception {
                Platform.runLater(() -> processingProgress.setProgress(-1)); // Indeterminate
                BufferedImage result = processor.processImage(currentImage, tileSize, filter, isAsync);
                Platform.runLater(() -> processingProgress.setProgress(1)); // Complete
                return result;
            }
            
            @Override
            protected void succeeded() {
                BufferedImage processedImage = getValue();
                Image fxImage = convertToFXImage(processedImage);
                processedImageView.setImage(fxImage);
                
                // Save processed image
                String outputFileName = "filtered_" + currentImageName;
                imageIO.sendImage(processedImage, outputFileName);
                
                logArea.appendText("Filter applied successfully! Saved as: " + outputFileName + "\n");
                processingProgress.setProgress(0);
            }
            
            @Override
            protected void failed() {
                Throwable exception = getException();
                String errorMessage = exception.getMessage();
                
                logArea.appendText("Error processing image: " + errorMessage + "\n");
                processingProgress.setProgress(0);
                
                // Show user-friendly alert for tile size errors
                if (exception instanceof IllegalArgumentException && errorMessage.contains("Tile size")) {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Invalid Tile Size");
                        alert.setHeaderText("Tile size doesn't divide image dimensions evenly");
                        alert.setContentText(errorMessage);
                        alert.getDialogPane().setPrefWidth(600);
                        alert.showAndWait();
                    });
                }
            }
        };
        
        executorService.submit(task);
    }
    
    private void saveProcessedImage() {
        if (processedImageView.getImage() == null) {
            logArea.appendText("No processed image to save.\n");
            return;
        }
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Processed Image");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("PNG Files", "*.png"),
            new FileChooser.ExtensionFilter("JPEG Files", "*.jpg", "*.jpeg"),
            new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        
        File saveFile = fileChooser.showSaveDialog(primaryStage);
        if (saveFile != null) {
            try {
                BufferedImage bufferedImage = convertFromFXImage(processedImageView.getImage());
                String format = saveFile.getName().toLowerCase().endsWith(".png") ? "PNG" : "JPEG";
                ImageIO.write(bufferedImage, format, saveFile);
                logArea.appendText("Image saved as: " + saveFile.getName() + "\n");
            } catch (IOException e) {
                logArea.appendText("Error saving image: " + e.getMessage() + "\n");
            }
        }
    }
    
    private void showBatchProcessingDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Batch Processing");
        dialog.setHeaderText("Process multiple images with multiple filters");
        
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        
        Label inputLabel = new Label("Select Input Images:");
        ListView<File> inputFilesList = new ListView<>();
        inputFilesList.setPrefHeight(100);
        
        Button addImagesBtn = new Button("Add Images...");
        addImagesBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Images");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp")
            );
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                inputFilesList.getItems().add(selectedFile);
            }
        });
        
        Label filtersLabel = new Label("Select Filters:");
        ListView<String> filtersList = new ListView<>();
        filtersList.getItems().addAll(FilterFactory.getFilterNames());
        filtersList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        filtersList.setPrefHeight(120);
        
        Label outputLabel = new Label("Output Directory:");
        TextField outputField = new TextField();
        outputField.setPromptText("Select output directory...");
        
        Button browseOutputBtn = new Button("Browse...");
        browseOutputBtn.setOnAction(e -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select Output Directory");
            File selectedDir = directoryChooser.showDialog(primaryStage);
            if (selectedDir != null) {
                outputField.setText(selectedDir.getAbsolutePath());
            }
        });
        
        HBox outputRow = new HBox(10);
        outputRow.getChildren().addAll(outputField, browseOutputBtn);
        
        ProgressBar batchProgress = new ProgressBar(0);
        batchProgress.setPrefWidth(300);
        
        TextArea batchLogArea = new TextArea();
        batchLogArea.setPrefRowCount(5);
        batchLogArea.setEditable(false);
        
        content.getChildren().addAll(
            inputLabel, inputFilesList, addImagesBtn, new Separator(),
            filtersLabel, filtersList, new Separator(),
            outputLabel, outputRow, new Separator(),
            batchProgress, batchLogArea
        );
        
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setText("Start Batch Processing");
        okButton.setOnAction(e -> {
            List<File> inputFiles = new ArrayList<>(inputFilesList.getItems());
            List<String> selectedFilters = new ArrayList<>(filtersList.getSelectionModel().getSelectedItems());
            String outputDir = outputField.getText();
            
            if (inputFiles.isEmpty() || selectedFilters.isEmpty() || outputDir.isEmpty()) {
                batchLogArea.appendText("Please select input files, filters, and output directory.\n");
                return;
            }
            
            File outputDirectory = new File(outputDir);
            if (!outputDirectory.exists()) {
                outputDirectory.mkdirs();
            }
            
            BatchProcessor batchProcessor = new BatchProcessor(imageIO);
            Task<Void> batchTask = batchProcessor.createBatchTask(
                inputFiles, selectedFilters, outputDirectory, batchProgress, batchLogArea
            );
            
            executorService.submit(batchTask);
            dialog.close();
        });
        
        dialog.showAndWait();
    }
    
    private void showAboutDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Advanced Image Processor");
        alert.setContentText(
            "A powerful JavaFX image processing application with advanced filters and batch processing.\n\n" +
            "Features:\n" +
            "• Multiple advanced filters (Blur, Sharpen, Edge Detection, etc.)\n" +
            "• Asynchronous and synchronous processing\n" +
            "• Batch processing support\n" +
            "• Multiple image format support\n" +
            "• Real-time preview\n\n" +
            "Made in India ❤️ by Sandip Mandal ✨"
        );
        alert.showAndWait();
    }
    
    private Image convertToFXImage(BufferedImage bufferedImage) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "PNG", baos);
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            return new Image(bais);
        } catch (IOException e) {
            logArea.appendText("Error converting image: " + e.getMessage() + "\n");
            return null;
        }
    }
    
    private BufferedImage convertFromFXImage(Image fxImage) {
        int width = (int) fxImage.getWidth();
        int height = (int) fxImage.getHeight();
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        
        javafx.scene.image.PixelReader pixelReader = fxImage.getPixelReader();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = pixelReader.getArgb(x, y);
                bufferedImage.setRGB(x, y, argb);
            }
        }
        
        return bufferedImage;
    }
    
    @Override
    public void stop() throws Exception {
        super.stop();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
    
    public static void main(String[] args) {
        launch();
    }
}