package com.muzu.explorer;

import com.muzu.explorer.ExplorerTab.InfoPane;
import com.muzu.explorer.ExplorerTab.MusicPlayer;
import com.muzu.explorer.ExplorerTab.MusicList_Manager.MusicListManager;
import com.muzu.explorer.MuzU.MuzU;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
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
    void trackPausePressed(ActionEvent event) {
        musicPlayer.pausePressed();
    }

    @FXML
    void trackPlayPressed(ActionEvent event) {
        musicPlayer.playPressed();
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

        musicPlayer = new MusicPlayer(trackName,trackImage,trackProgress,trackDuration,trackSeekSlider);
        infoPane.setMusicPlayer(musicPlayer);
        musicListManager.setQueryMenus(SelectMenu, SortMenu);
    }

}
