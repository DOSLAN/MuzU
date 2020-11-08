package com.muzu.explorer.model;

import com.muzu.explorer.ui.explorer_tab.Database_Manager.DBReader;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.io.FileUtils;

public class MuzU {
    private int id, numberOfMidi;
    private File file;

    public MuzU(int id) throws SQLException {
        ResultSet rs = DBReader.getAll(id);
        this.id = rs.getInt("ID");
        file = new File(rs.getString("FilePath") + "/" + rs.getString("FileName"));
        numberOfMidi = rs.getInt("midi");
        DBReader.closeDB();

        if (numberOfMidi!=0){

        }
    }

    public File getFile() {
        return file;
    }

    public File getMidi() {
        return new File("src\\main\\resources\\com\\muzu\\explorer\\midi Files\\"+
                id+"."+this.file.getName()+"-"+numberOfMidi+".mid");
    }

    public boolean doHaveMidi() {
        return numberOfMidi!=0;
    }

    public void setMidi(File file) {
        numberOfMidi++;
        DBReader.connectToDB();
        try{
            File midiFile = new File("src\\main\\resources\\com\\muzu\\explorer\\midi Files\\"+
                    id+"."+this.file.getName()+"-"+numberOfMidi+".mid");
            FileUtils.copyFile(file, midiFile);

            /** set midi **/
            DBReader.statement.execute("UPDATE MuzU_List SET Midi="+numberOfMidi+" WHERE ID="+id);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
        DBReader.closeDB();
    }
}
