package com.muzu.explorer.ExplorerTab.MusicList_Manager;

import com.muzu.explorer.ExplorerTab.Database_Manager.DBReader;
import javafx.event.ActionEvent;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MusicListManager {
    private ListView<String> musicList;
    private SqliteQuery sqliteQuery = new SqliteQuery(this);

    public MusicListManager(ListView<String> musicList) {
        this.musicList = musicList;

        sqliteQuery.select("All");
    }

    ListView<String> getMusicList() {
        return musicList;
    }

    private void addMp3sInSameDirectoryToDB(List<File> mp3s){
        if(mp3s.size()==0) return;
        DBReader.connectToDB();
        String filePath = Paths.get(mp3s.get(0).getPath()).getParent().toString();
        try {
            DBReader.statement.execute(
                    "INSERT OR IGNORE INTO FilePath(FilePath)\n" +
                            "VALUES ('" + filePath + "');");
            ResultSet rs = DBReader.statement.executeQuery(
                    "SELECT FilePath.ID \n" +
                            "FROM FilePath\n" +
                            "WHERE FilePath = '" + filePath + "';");
            int filePathID = rs.getInt("ID");
            StringBuilder query = new StringBuilder("INSERT OR IGNORE INTO MuzU_List(FilePathID, FileName)\nVALUES");
            for (File file:mp3s){
                String fileName = file.getName().replaceAll("'", "''");
                query.append("(").append(filePathID).append(", '").append(fileName).append("'),");
            }
            query.deleteCharAt(query.length()-1).append(";");
            DBReader.statement.execute(query.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        DBReader.closeDB();
        sqliteQuery.select("All");
    }

    private void addMp3s_helperForAddFolder(File file){
        List<File> mp3s = new ArrayList<>();
        File[] files = file.listFiles();
        if (files!=null) {
            for (File f : files){
                if (f.isDirectory()){
                    addMp3s_helperForAddFolder(f);
                }
                else if(f.getPath().endsWith(".mp3")){
                    mp3s.add(f);
                }
            }
        }
        addMp3sInSameDirectoryToDB(mp3s);
    }

    public void addFolderMenuItemPressed(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File file = directoryChooser.showDialog(null);
        if (file==null) return;
        addMp3s_helperForAddFolder(file);
    }


    public void addMenuItemPressed(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilterMp3 =
                new FileChooser.ExtensionFilter("mp3 files (*.mp3)", "*.mp3");
        fileChooser.getExtensionFilters().addAll(extensionFilterMp3);

        List<File> mp3s = fileChooser.showOpenMultipleDialog(null);

        addMp3sInSameDirectoryToDB(mp3s);
    }

    public void setQueryMenus(Menu selectMenu, Menu sortMenu) {
        sqliteQuery.setQueryMenus(selectMenu, sortMenu);
    }
}
