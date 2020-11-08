package com.muzu.explorer.midi;

import java.io.File;
import java.util.ArrayList;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.MidiDevice.Info;

public class MidiCore {

    private Sequencer sequencer;

    private Sequence sequence;

    private Info[] devs;

    public int devID, devDiv;

    private MidiDevice midiDevice;

    public File[] soundbanks;

    public String[] devx;

    public String[][] devd;

    public long midiPauseProg, midiPauseProgMs, midiLoopStart, midiLoopEnd;

    public enum cycleType {
        none, whole, partial
    }

    public cycleType repeat;

    public MidiCore() {
        repeat = cycleType.none;
        Info[] vdevs = MidiSystem.getMidiDeviceInfo();
        ArrayList<Info> xdevs = new ArrayList<Info>();
        ArrayList<String> src = new ArrayList<String>();
        ArrayList<String[]> dsc = new ArrayList<String[]>();
        for (Info dev : vdevs) {
            String s = dev.getName();
            try {
                MidiDevice vc = MidiSystem.getMidiDevice(dev);
                vc.getReceiver();
                vc.close();
            } catch (MidiUnavailableException e) {
                s = "$NORECEIVER";
            }
            if (s != "Real Time Sequencer" && s != "$NORECEIVER") {
                xdevs.add(dev);
                if (s == "Gervill")
                    s = "Internal";
                String dest = s;
                src.add(dest);
                String[] destc = new String[3];
                destc[0] = dev.getDescription();
                destc[1] = dev.getVendor();
                destc[2] = dev.getVersion();
                dsc.add(destc);
            }
        }
        devDiv = xdevs.size();
        Info[] arrw = new Info[xdevs.size()];
        devs = xdevs.toArray(arrw);
        String[] arrx = new String[src.size()];
        devx = src.toArray(arrx);
        String[][] arry = new String[dsc.size()][3];
        devd = dsc.toArray(arry);
        try {
            midiDevice = MidiSystem.getMidiDevice(devs[0]);
            midiDevice.open();
            sequencer = MidiSystem.getSequencer(false);
            sequencer.open();
            sequencer.getTransmitter().setReceiver(midiDevice.getReceiver());
        } catch (Exception e) {

        }
    }

    public void changeDev(int id) {
        midiPauseProg = sequencer.getTickPosition();
        midiPauseProgMs = sequencer.getMicrosecondPosition();
        devID = devFix(id);
        boolean running = sequencer.isRunning();
        try {
            sequencer.close();
            midiDevice.close();
            midiDevice = MidiSystem.getMidiDevice(devs[devID]);
            midiDevice.open();
            if (midiDevice.getDeviceInfo().getName().equals("Gervill"))
                initSoundbank();
            sequencer.open();
            sequencer.getTransmitter().setReceiver(midiDevice.getReceiver());
            sequencer.setSequence(sequence);
            changeCycleMethod();
            if (running)
                sequencer.start();

        } catch (Exception e) {

        }
        sequencer.setTickPosition(midiPauseProg);
    }

    public void setSoundbank(File[] sb) {
        soundbanks = sb;
        if (midiDevice.getDeviceInfo().getName().equals("Gervill"))
            changeDev(devID);
    }

    private void initSoundbank() {
        Synthesizer midis = (Synthesizer) midiDevice;
        if (soundbanks != null) {
            for (File sb : soundbanks) {
                try {
                    Soundbank sbx = MidiSystem.getSoundbank(sb);
                    midis.loadAllInstruments(sbx);
                } catch (Exception e) {

                }
            }
        }
    }

    public long getMidiProg() {
        return sequencer.getMicrosecondPosition();
    }

    public long getMidiTickProg() {
        return sequencer.getTickPosition();
    }

    public void changeMidi(File file, boolean playNow) {
        try {
            sequence = MidiSystem.getSequence(file);
            sequencer.setSequence(sequence);
            changeCycleMethod();
            midiPauseProg = 0;
            midiPauseProgMs = 0;
            if (playNow)
                sequencer.start();
        } catch (Exception e) {

        }
    }

    public void shutdown() {
        sequencer.close();
        midiDevice.close();
    }

    public boolean togglePause() {
        try {
            if (isPlaying()) {
                midiPauseProg = sequencer.getTickPosition();
                midiPauseProgMs = sequencer.getMicrosecondPosition();
                sequencer.stop();
            } else {
                sequencer.start();
                sequencer.setTickPosition(midiPauseProg);
            }
            return true;
        } catch (Exception e) {
            if (e instanceof java.lang.IllegalStateException)
                return false;
        }
        return false;
    }

    public void togglePause(boolean isPlaying) {
        if (isPlaying && !isPlaying()){
            sequencer.start();
            sequencer.setTickPosition(midiPauseProg);
        }
        else if(!isPlaying && isPlaying()){
            midiPauseProg = sequencer.getTickPosition();
            midiPauseProgMs = sequencer.getMicrosecondPosition();
            sequencer.stop();
        }
    }

    public void midiStop() {
        if (isPlaying())
            sequencer.stop();
        sequencer.setTickPosition(0);
        midiPauseProg = 0;
        midiPauseProgMs = 0;
    }

    public Boolean isPlaying() {
        return sequencer.isRunning();
    }

    public long getLength() {
        return sequencer.getMicrosecondLength();
    }

    public long getTickLength() {
        return sequencer.getTickLength();
    }

    public cycleType changeCycleMethod() {
        switch (repeat) {
            case none:
                sequencer.setLoopCount(0);
                sequencer.setLoopStartPoint(0);
                sequencer.setLoopEndPoint(-1);
                break;
            case whole:
                sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
                sequencer.setLoopStartPoint(0);
                sequencer.setLoopEndPoint(-1);
                break;
            case partial:
                sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
                if (midiLoopStart < midiLoopEnd) {
                    sequencer.setLoopStartPoint(midiLoopStart);
                    sequencer.setLoopEndPoint(midiLoopEnd);
                } else {
                    sequencer.setLoopStartPoint(0);
                    sequencer.setLoopEndPoint(-1);
                }
                break;
            default:
                break;
        }
        return repeat;
    }

    public void midiNavigate(long time) {
        long timeTicks = msToTicks(time);
        if (!(time != 0 && timeTicks == 0) && !((repeat == cycleType.partial && timeTicks >= midiLoopEnd)
                | time >= sequencer.getMicrosecondLength())) {
            if (time >= 0) {
                sequencer.setMicrosecondPosition(time);
                midiPauseProg = timeTicks;
                midiPauseProgMs = time;

            } else {
                sequencer.setMicrosecondPosition(0);
                midiPauseProg = 0;
                midiPauseProgMs = 0;
            }
        }
    }

    public void midiTickNavigate(long time) {
        if (!((repeat == cycleType.partial && time >= midiLoopEnd) | time >= sequencer.getTickLength())) {
            if (time >= 0) {
                sequencer.setTickPosition(time);
                midiPauseProg = time;
                midiPauseProgMs = sequencer.getMicrosecondPosition();
            } else {
                sequencer.setTickPosition(0);
                midiPauseProg = 0;
                midiPauseProgMs = 0;
            }
        }
    }

    private int devFix(int id) {
        if (id >= devDiv)
            return id + 1;
        else
            return id;
    }

    public long msToTicks(long ms) {
        if (ms == 0)
            return 0;
        float fps = sequence.getDivisionType();
        try {
            if (fps == Sequence.PPQ)
                return (long) (ms * sequencer.getTempoInBPM() * sequence.getResolution() / 60000000);
            else if (fps > Sequence.PPQ)
                return (long) (ms * fps * sequence.getResolution() / 1000000);
            else
                throw new Exception();
        } catch (Exception e) {
            return 0;
        }
    }
}
