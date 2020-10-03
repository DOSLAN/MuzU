package com.muzu.explorer.WaveSpectrum;

import com.muzu.explorer.WaveSpectrum.WaveFormService.WaveFormJob;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class Main extends Application {
	
	private WaveVisualization waveVisualization = new WaveVisualization(520, 32);
	
	@Override
	public void start(Stage primaryStage) {
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extensionFilterMp3 =
				new FileChooser.ExtensionFilter("mp3 files (*.mp3)", "*.mp3");
		fileChooser.getExtensionFilters().addAll(extensionFilterMp3);

		File file = fileChooser.showOpenDialog(null);

		try {
			
			//Root
			BorderPane root = new BorderPane();
			root.setCenter(waveVisualization);
			root.boundsInLocalProperty().addListener(l -> {
				waveVisualization.setWidth(root.getWidth());
				waveVisualization.setHeight(root.getHeight());
			});
			
			//PrimaryStage
			primaryStage.setTitle("Dark Side");
			primaryStage.setOnCloseRequest(c -> System.exit(0));
			
			//Scene
			Scene scene = new Scene(root, 600, 40);
			primaryStage.setScene(scene);
			
			//Show
			primaryStage.show();
			
			//
			waveVisualization.getWaveService().startService(file.getAbsolutePath(),WaveFormJob.AMPLITUDES_AND_WAVEFORM);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
