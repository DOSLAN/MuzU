package com.muzu.explorer.ExplorerTab;

import com.muzu.explorer.Midi.MidiCore;
import com.muzu.explorer.Midi.MidiPlayer;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class MusicPlayer {
    private Label trackName, trackProgress, trackDuration;
    private Slider trackSeekBar;

    public MusicPlayer(Label trackName, ImageView trackImage, Label trackProgress,
                       Label trackDuration, Slider trackSeekBar) {
        this.trackName = trackName;
        this.trackProgress = trackProgress;
        this.trackDuration = trackDuration;
        this.trackSeekBar = trackSeekBar;
    }

    public void play(File file){
        switch (FilenameUtils.getExtension(file.getName())){
            case "mp3":
                playMp3(file);
                break;
            case "mid":
                playMidi(file);
                break;
            default:
                System.out.println("File format does not much: --> " + FilenameUtils.getExtension(file.getName()));
        }
    }

    private Media media;
    private MediaPlayer mediaPlayer;
    private boolean isMediaNotMidi;
    private void playMp3(File file){
        if (midiCore!=null)midiCore.midiStop();
        isMediaNotMidi = true;
        if (mediaPlayer != null) mediaPlayer.stop();
        media = new Media(file.toURI().toString());
        mediaPlayer = new MediaPlayer(media);

        trackName.setText(file.getName());
        mediaPlayer.setOnReady(() -> {
            Duration duration = media.getDuration();
            int dm = (int)duration.toMinutes();
            int ds = (int)duration.toSeconds()%60;
            trackDuration.setText(dm+":"+ds);

            mediaPlayer.play();
        });

        AtomicBoolean valueChanging = new AtomicBoolean(false);

        mediaPlayer.setOnEndOfMedia(() -> {
            mediaPlayer.seek(Duration.ZERO);
            mediaPlayer.pause();
            trackSeekBar.setValue(0.0);
        });

        mediaPlayer.currentTimeProperty().addListener(observable -> {
            Duration current = mediaPlayer.getCurrentTime();
            if (!valueChanging.get()&&isMediaNotMidi) {
                trackSeekBar.setValue(current.toMillis() / media.getDuration().toMillis() * 100.0);
                trackProgress.setText((int)current.toMinutes()+":"+(int)current.toSeconds()%60);
            }
        });

        trackSeekBar.valueChangingProperty().addListener((observableValue, aBoolean, t1) -> {
            if (isMediaNotMidi) {
                if (t1) {
                    valueChanging.set(true);
                } else {
                    mediaPlayer.seek(media.getDuration().multiply(trackSeekBar.getValue() / 100.0));
                    if (trackSeekBar.getValue() == 100) {
                        mediaPlayer.seek(Duration.ZERO);
                        mediaPlayer.pause();
                        trackSeekBar.setValue(0.0);
                    }
                    valueChanging.set(false);
                    System.out.println(trackSeekBar.isValueChanging());
                }
            }
        });

        trackSeekBar.valueProperty().addListener((ObservableValue<? extends Number> observableValue, Number number, Number t1) -> {
            if (isMediaNotMidi) {
                Duration d = media.getDuration().multiply(trackSeekBar.getValue() / 100.0);
                trackProgress.setText((int) d.toMinutes() + ":" + (int) d.toSeconds() % 60);
            }
        });
    }

    private Thread cycle;
    private MidiCore midiCore;
    private void playMidi(File file){
        if (mediaPlayer!=null) mediaPlayer.stop();
        isMediaNotMidi = false;
        midiCore = new MidiCore();
        midiCore.changeMidi(file,true);

        trackDuration.setText(MidiPlayer.microsecondsToTimeString(midiCore.getLength()));
        AtomicBoolean isDragging = new AtomicBoolean();
        cycle = new Thread(() -> {
            while (!isMediaNotMidi) {
                if (midiCore.isPlaying()) {
                    if (!isDragging.get())
                        trackSeekBar.setValue((int) (midiCore.getMidiProg() / 1000000));
                    if (!isDragging.get())
                        trackProgress.setText(MidiPlayer.microsecondsToTimeString(midiCore.getMidiProg()));
                    else
                        trackProgress.setText(MidiPlayer.millisecondsToTimeString((int)trackSeekBar.getValue()));
                } else {
                    if (!isDragging.get()) {
                        if (midiCore.getMidiProg() == midiCore.getLength())
                            midiCore.midiNavigate(0);
                        trackSeekBar.setValue((int) (midiCore.midiPauseProgMs / 1000000));
                    }
                    if (!isDragging.get())
                        trackProgress.setText(MidiPlayer.microsecondsToTimeString(midiCore.midiPauseProgMs));
                    else
                        trackProgress.setText(MidiPlayer.millisecondsToTimeString((int)trackSeekBar.getValue()));
                }
                try {
                    Thread.sleep(30);
                } catch (InterruptedException ignored) {}
            }
        });
        cycle.start();

        trackSeekBar.setOnMousePressed(mouseEvent -> {
            if (!isMediaNotMidi) isDragging.set(true);
        });

        trackSeekBar.setOnMouseReleased(mouseEvent -> {
            if (isDragging.get() && !isMediaNotMidi) {
                midiCore.midiNavigate(((long) trackSeekBar.getValue()) * 1000000);
                isDragging.set(false);
            }
        });
    }

    public void playPressed(){
        if (isMediaNotMidi) mediaPlayer.play();
        else midiCore.togglePause(true);
    }

    public void pausePressed(){
        if (isMediaNotMidi) mediaPlayer.pause();
        else midiCore.togglePause(false);
    }
}
