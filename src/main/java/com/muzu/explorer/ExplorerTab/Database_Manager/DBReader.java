package com.muzu.explorer.ExplorerTab.Database_Manager;

import java.sql.*;

public class DBReader {
    public static Statement statement;
    private static Connection conn = null;

    public static void connectToDB(){
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:src\\main\\resources\\com\\muzu\\explorer\\MuzU_DB");
            statement = conn.createStatement();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void closeDB() {
        try {
            if (statement != null) statement.close();
            if (conn != null) conn.close();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static ResultSet getAll(int id) {  /** dont forget to close Db after use **/
        ResultSet rs = null;
        DBReader.connectToDB();
        try{
            rs = DBReader.statement.executeQuery(
                    "SELECT MuzU_List.ID, MuzU_List.FileName, FilePath.FilePath, MuzU_List.Midi\n" +
                            "FROM MuzU_List\n" +
                            "LEFT JOIN FilePath ON MuzU_List.FilePathID = FilePath.ID\n" +
                            "WHERE MuzU_List.ID = '"+id+"';");
            //MusicPlayer.play(new File(filePath));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }
}
