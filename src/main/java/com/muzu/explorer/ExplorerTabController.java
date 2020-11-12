package com.muzu.explorer;

import com.muzu.explorer.ui.explorer_tab.InfoPane;
import com.muzu.explorer.ui.explorer_tab.MusicPlayer;
import com.muzu.explorer.ui.explorer_tab.MusicList_Manager.MusicListManager;
import com.muzu.explorer.model.MuzU;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.layout.AnchorPane;

import java.sql.SQLException;

public class ExplorerTabController {
    @FXML
    private Menu SelectMenu;

    @FXML
    private Menu SortMenu;

    @FXML
    private ListView<String> musicList;

    @FXML
    private ImageView trackImage;

    @FXML
    private Slider trackSeekSlider;

    @FXML
    private Button playBtn;

    @FXML
    void trackPlayPausePressed(ActionEvent event) {
        musicPlayer.playPausePressed();
    }

    @FXML
    void onMouseDragReleased(DragEvent event){
        musicPlayer.onMouseDragReleased();
    }

    @FXML
    private Label trackProgress;

    @FXML
    private Label trackDuration;

    @FXML
    private Label trackName;

    @FXML
    private AnchorPane info;

    @FXML
    void addMp3MenuItemPressed(ActionEvent event) {
        musicListManager.addMenuItemPressed(event);
    }

    @FXML
    void addFolderMenuItemPressed(ActionEvent event){
        musicListManager.addFolderMenuItemPressed(event);
    }

    @FXML
    void openMenuItemPressed(ActionEvent event) {

    }

    private MusicListManager musicListManager;
    private InfoPane infoPane;
    private MusicPlayer musicPlayer;
    @FXML
    private void initialize()
    {
        musicList.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount()== 2) {
                String selected = musicList.getSelectionModel().getSelectedItem();
                int id = Integer.parseInt(selected.substring(0, selected.indexOf('.')));
                MuzU muzU = null;
                try {
                    muzU = new MuzU(id);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                infoPane.selectMuzU(muzU);
                assert muzU != null;
                musicPlayer.play(muzU.getFile());
            }
        });

        musicListManager = new MusicListManager(musicList);
        infoPane = new InfoPane(info);

        musicPlayer = new MusicPlayer(trackName,playBtn,trackImage,trackProgress,trackDuration,trackSeekSlider);
        infoPane.setMusicPlayer(musicPlayer);
        musicListManager.setQueryMenus(SelectMenu, SortMenu);
    }

}
