package com.muzu.explorer.ExplorerTab.MusicList_Manager;

import com.muzu.explorer.ExplorerTab.Database_Manager.DBReader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;

class SqliteQuery {
    private String filePath = "src\\main\\resources\\com\\muzu\\explorer\\Sqlite_Queries\\";
    private Menu selectMenu, sortMenu;
    private MusicListManager musicListManager;

    SqliteQuery(MusicListManager musicListManager) {
        this.musicListManager = musicListManager;
    }

    void select(String select){
        ObservableList<String> observableList = FXCollections.observableArrayList();
        DBReader.connectToDB();
        String query = null;
        try {
            query = String.valueOf(Files.readString(Paths.get(filePath+select+"")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            ResultSet rs = DBReader.statement.executeQuery(query);
            while(rs.next()) {
                String trackName = rs.getString("FileName");
                int id = rs.getInt("ID");
                observableList.add(id+"."+trackName);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        DBReader.closeDB();
        musicListManager.getMusicList().setItems(observableList);
    }

    void setQueryMenus(Menu selectMenu, Menu sortMenu) {
        this.selectMenu = selectMenu;
        this.sortMenu = sortMenu;

        MenuItem selectAll = new MenuItem("All"), selectMidi = new MenuItem("Midi");
        selectAll.setOnAction(event -> select("All"));
        selectMidi.setOnAction(event -> select("Midi"));

        selectMenu.getItems().addAll(selectAll, selectMidi);
    }
}
