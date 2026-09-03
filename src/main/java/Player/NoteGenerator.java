package Player;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class NoteGenerator extends Thread {
    final ArrayList<GameNote> notes;
    JPanel panel;
    Player player;
    int ypos;
    int xpos;
    Clip clip;
    long startTime;
    long elapsedTime;
    long dt;
    long pausePosition;
    boolean exit;
    boolean paused;
    public NoteGenerator(String name, JPanel panel, String selectedSong, Player player, int x, int y) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        super(name);
        this.panel = panel;
        this.player = player;
        notes = new ArrayList<>();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        ypos = y;
        xpos = x;
        readChartFile("src/main/java/Resources/Charts/"+selectedSong+".chart");
        loadAudio("src/main/java/Resources/Songs/"+selectedSong+".wav");
        exit = false;
        paused = false;
        dt = (long)(((ypos+35))/(GameNote.getSpeed())* 5) ;
        
    }

    public ArrayList<GameNote> getNotes() {
        return notes;
    }

    public void setExit(boolean exit) {
        this.exit = exit;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }
    
    public void readChartFile(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        boolean inNotesSection = false;
        boolean twoPlayers = false;

           while ((line = reader.readLine()) != null) {
            line = line.trim();
               //System.out.println(line);
            if(line.equals("Players = 2"))
                twoPlayers = true;
            if ((!twoPlayers && line.equals("[Player]")) || (player.playerNumber == 1 && line.equals("[Player]"))) {
                inNotesSection = true;
            } else if(twoPlayers && player.playerNumber == 2 && line.equals("[Player2]")) {
                inNotesSection = true;
            }else if (line.startsWith("{") && inNotesSection){
                continue;
            } else if(line.equals("}") && inNotesSection){
                inNotesSection = false;
            }else if (inNotesSection && !line.isEmpty()) {
                processNoteLine(line);
            }
        }
        reader.close();
    }

    private void processNoteLine(String line) {
        String[] parts = line.split(" = ");
        int time = (int)(Integer.parseInt(parts[0].trim()));
        String noteParts = parts[1];
        int track = Integer.parseInt(noteParts.trim());
        if (track >= 0 && track <= 4) {
            GameNote note = switch (track) {
                case 0 -> new GameNote(xpos - 5, 0, new Color(54, 58, 59), new Color(8, 200, 3), time, track);
                case 1 -> new GameNote(xpos + 75 - 5, 0, new Color(54, 58, 59), new Color(163, 24, 24), time, track);
                case 2 -> new GameNote(xpos + 150 - 5, 0, new Color(54, 58, 59), new Color(254, 254, 53), time, track);
                case 3 -> new GameNote(xpos + 225 - 5, 0, new Color(54, 58, 59), new Color(63, 162, 211), time, track);
                case 4 -> new GameNote(xpos + 300 - 5, 0, new Color(54, 58, 59), new Color(217, 147, 53), time, track);
                default -> null;
            };
            if (note != null) {
                notes.add(note);
            }
        }
    }

    private void loadAudio(String audioFilePath) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        File audioFile = new File(audioFilePath);
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
        clip = AudioSystem.getClip();
        clip.open(audioStream);
    }

    public void playAudio() {
        clip.start();
    }

    public void resumeG() {
        startTime = System.nanoTime() - pausePosition;
        paused = false;
    }

    public void pauseG() {
        pausePosition = (long) (elapsedTime * 1000000f);
        paused = true;
    }
    

    @Override
    public void run() {
        startTime = System.nanoTime();
     
        long loopStartTime;
        long loopEndTime;
        long loopDuration;
        long sleepTime;
        while(!exit) {
            loopStartTime = System.nanoTime();
            elapsedTime = (System.nanoTime() - startTime)/1000000;
            
            if(!paused) {
                for (GameNote element : notes) {
                    if(!element.isInScreen()) {
                        if((elapsedTime >= element.getTime() - dt)){
                            if(!element.isScored() && element.getY() < (ypos + 100))
                                element.setInScreen(true);
                        }
                    } else {
                      element.physics(ypos, dt);
                    }

                }
            }
            loopEndTime = System.nanoTime(); // Marca de tiempo al final del loop
            loopDuration = loopEndTime - loopStartTime;
            sleepTime = 5000000 - loopDuration; // 5 ms en nanosegundos menos la duración del loop
            try {
                if (sleepTime > 0) {
                    TimeUnit.NANOSECONDS.sleep(sleepTime);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("NOTEGENERATOR FINALIZADO");
    }
}
