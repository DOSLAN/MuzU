module com.muzu.explorer {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.sql;
    requires org.apache.commons.io;
    requires java.desktop;
    requires jave.core;
    requires jave.nativebin.win64;

    opens com.muzu.explorer to javafx.fxml;
    opens com.muzu.explorer.WaveSpectrum to javafx.graphics;
    exports com.muzu.explorer;
}