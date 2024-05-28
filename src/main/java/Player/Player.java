package Player;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

public class Player {
    public Tab tab;
    int life;
    int noteStreak;
    int multiplier;
    int powerPorcentage;
    int score;
    String chartPath;

    public Player() throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        this.tab = new Tab(this);
        this.life = 50;
        this.noteStreak = 0;
        this.multiplier = 1;
        this.powerPorcentage = 0;
        this.score = 0;
    }
    public Player(int playerNumber) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        this.tab = new Tab(this, playerNumber);
        this.life = 50;
        this.noteStreak = 0;
        this.multiplier = 1;
        this.powerPorcentage = 0;
        this.score = 0;
    }

    public void setChartPath(String chartPath) {
        this.chartPath = chartPath;
    }
    public String getChartPath() {
        return this.chartPath;
    }

    public void resetNoteStreak() {
        this.noteStreak = 0;
    }
    public void resetMultiplier() {
        this.multiplier = 1;
    }



}
