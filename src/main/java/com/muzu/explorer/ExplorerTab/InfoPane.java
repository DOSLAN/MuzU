package com.muzu.explorer.ExplorerTab;

import com.muzu.explorer.MuzU.MuzU;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;

import java.io.File;

public class InfoPane {
    private AnchorPane base;
    private GridPane gridPane;
    private Label fileName, filePath;
    private Button midi;

    public InfoPane(AnchorPane base) {
        this.base = base;
        gridPane = new GridPane(); base.getChildren().add(gridPane);
        AnchorPane.setBottomAnchor(gridPane, 0.0);
        AnchorPane.setLeftAnchor(gridPane, 0.0);
        AnchorPane.setRightAnchor(gridPane, 0.0);
        AnchorPane.setTopAnchor(gridPane, 0.0);
    }

    public void selectMuzU(MuzU muzU){
        fileName = new Label(muzU.getFile().getName());
        filePath = new Label(muzU.getFile().getPath());
        midi = new Button();
        if (muzU.doHaveMidi()){
            midi.setText("Play midi file");
        }
        else{
            midi.setText("Select midi");
        }
        midi.setOnMousePressed(mouseEvent -> {
            if(muzU.doHaveMidi()){
                musicPlayer.play(muzU.getMidi());
            }
            else{
                FileChooser fileChooser = new FileChooser();
                FileChooser.ExtensionFilter extensionFilterMidi =
                        new FileChooser.ExtensionFilter("midi files (*.mid)", "*.mid", "*.midi");
                fileChooser.getExtensionFilters().addAll(extensionFilterMidi);
                File file = fileChooser.showOpenDialog(null);

                muzU.setMidi(file);
                midi.setText("Play midi file");
            }
        });

        gridPane.getChildren().clear();
        gridPane.add(new Label("File name"),0,0);    gridPane.add(fileName,1,0);
        gridPane.add(new Label("File Path"), 0,1);   gridPane.add(filePath,1,1);
        gridPane.add(new Label("Midi"), 0,2);        gridPane.add(midi,1,2);
//        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("main.fxml"));
//        try {
//            gridPane.add(fxmlLoader.load(),0,3);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private MusicPlayer musicPlayer;

    public void setMusicPlayer(MusicPlayer musicPlayer) {
        this.musicPlayer = musicPlayer;
    }
}
