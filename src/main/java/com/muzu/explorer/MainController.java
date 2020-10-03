package com.muzu.explorer;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class MainController {
    @FXML
    private Tab explorer;

    @FXML
    private Tab midiEditor;

    @FXML
    private Tab visualizer;


    @FXML
    private void initialize(){
        Node explorerFxml = null, midiEditorFxml = null, visualizerFxml = null;

        try {
            System.out.println("try start");
            explorerFxml = FXMLLoader.load(App.class.getResource("explorer.fxml"));
            midiEditorFxml = FXMLLoader.load(App.class.getResource("MidiEditor.fxml"));
            System.out.println("try end ");
        } catch (IOException e) {
            System.out.println("error : ");
            e.printStackTrace();
        }
        AnchorPane.setTopAnchor(explorerFxml, 0.0);
        AnchorPane.setRightAnchor(explorerFxml, 0.0);
        AnchorPane.setLeftAnchor(explorerFxml, 0.0);
        AnchorPane.setBottomAnchor(explorerFxml, 0.0);
        AnchorPane.setTopAnchor(midiEditorFxml, 0.0);
        AnchorPane.setRightAnchor(midiEditorFxml, 0.0);
        AnchorPane.setLeftAnchor(midiEditorFxml, 0.0);
        AnchorPane.setBottomAnchor(midiEditorFxml, 0.0);

        explorer.setContent(explorerFxml);
        midiEditor.setContent(midiEditorFxml);
    }
}