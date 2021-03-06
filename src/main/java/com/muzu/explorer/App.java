package com.muzu.explorer;

import com.muzu.explorer.service.AudioService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    public static AudioService audioService = new AudioService();

    @Override
    public void start(Stage stage) throws IOException {
        stage.setMaximized(true);
        Parent root = loadFXML("main");
        stage.setScene(new Scene(root));
        stage.setTitle("MuzU Explorer");
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}