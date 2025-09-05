package com.my.app.batch;

import com.my.app.filters.FilterFactory;
import com.my.app.filters.ImageFilter;
import com.my.app.io.ImageOperations;
import com.my.app.processor.ImageProcessor;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.application.Platform;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Optional;

public class BatchProcessor {
    private final ImageOperations imageOperations;
    private final ImageProcessor processor;
    
    public BatchProcessor(ImageOperations imageOperations) {
        this.imageOperations = imageOperations;
        this.processor = new ImageProcessor();
    }
    
    public Task<Void> createBatchTask(List<File> inputFiles, List<String> filterNames, 
                                     File outputDirectory, ProgressBar progressBar, TextArea logArea) {
        
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                int totalOperations = inputFiles.size() * filterNames.size();
                int completedOperations = 0;
                
                for (File inputFile : inputFiles) {
                    if (isCancelled()) break;
                    
                    Platform.runLater(() -> logArea.appendText("Processing: " + inputFile.getName() + "\n"));
                    
                    Optional<BufferedImage> imageOpt = imageOperations.readImage(inputFile.getAbsolutePath());
                    if (imageOpt.isEmpty()) {
                        Platform.runLater(() -> logArea.appendText("Failed to read: " + inputFile.getName() + "\n"));
                        continue;
                    }
                    
                    BufferedImage originalImage = imageOpt.get();
                    String baseName = inputFile.getName().replaceFirst("[.][^.]+$", "");
                    
                    for (String filterName : filterNames) {
                        if (isCancelled()) break;
                        
                        ImageFilter filter = FilterFactory.getFilter(filterName);
                        if (filter != null) {
                            Platform.runLater(() -> logArea.appendText("  Applying " + filterName + "...\n"));
                            
                            BufferedImage processedImage = processor.processImage(originalImage, 40, filter, true);
                            
                            String outputFileName = baseName + "_" + filterName.toLowerCase().replace(" ", "_") + ".png";
                            File outputFile = new File(outputDirectory, outputFileName);
                            
                            imageOperations.sendImage(processedImage, outputFile.getAbsolutePath());
                            Platform.runLater(() -> logArea.appendText("  Saved: " + outputFileName + "\n"));
                        }
                        
                        completedOperations++;
                        final double progress = (double) completedOperations / totalOperations;
                        Platform.runLater(() -> progressBar.setProgress(progress));
                    }
                }
                
                Platform.runLater(() -> logArea.appendText("Batch processing completed!\n"));
                return null;
            }
        };
    }
}