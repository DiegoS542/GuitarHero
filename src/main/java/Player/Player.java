package Player;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.io.IOException;

public class Player {
    public Tab tab;
    int playerNumber;
    int life;
    int noteStreak;
    int multiplier;
    int powerPorcentage;
    int score;
    int maxStreak;
    int hits;
    int misses;
    int xpos;
    int ypos;
    String chartPath;
    String selectedSong;
    PlayerNote greenNote = new PlayerNote(new Color(54, 58, 59), new Color(8, 200, 3), new Color(8, 200, 3));
    PlayerNote redNote = new PlayerNote(new Color(54, 58, 59), new Color(163, 24, 24), new Color(163, 24, 24));
    PlayerNote yellowNote = new PlayerNote(new Color(54, 58, 59), new Color(254, 254, 53), new Color(254, 254, 53));
    PlayerNote blueNote = new PlayerNote(new Color(54, 58, 59), new Color(63, 162, 211), new Color(63, 162, 211));
    PlayerNote orangeNote = new PlayerNote(new Color(54, 58, 59), new Color(217, 147, 53), new Color(217, 147, 53));
    final JLabel noteStreakLabel = hudLabel("Racha: 0");
    final JLabel multiplierLabel = hudLabel("Multiplicador: 1x");
    final JLabel scoreLabel = hudLabel("Puntaje: 0");
    final JLabel accuracyLabel = hudLabel("Precisión: 100%");
    int hudX;
    String feedbackText = null;
    Color feedbackColor = Color.WHITE;
    long feedbackTimestamp = 0;

    private static JLabel hudLabel(String text) {
        return new JLabel(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(0, 0, 0, 150));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g);
            }
        };
    }

    public Player(String selectedSong, JFrame frame) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        this.life = 50;
        this.noteStreak = 0;
        this.multiplier = 1;
        this.powerPorcentage = 0;
        this.score = 0;
        this.selectedSong = selectedSong;
    }
    public Player(int playerNumber, String selectedSong, JFrame frame) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        this.playerNumber = playerNumber;
        this.life = 50;
        this.noteStreak = 0;
        this.multiplier = 1;
        this.powerPorcentage = 0;
        this.score = 0;
        this.selectedSong = selectedSong;
    }

    public int getScore() {
        return score;
    }

    public int getMaxStreak() {
        return maxStreak;
    }

    public double getAccuracy() {
        int total = hits + misses;
        return total == 0 ? 100.0 : (100.0 * hits / total);
    }

    public void setXpos(int xpos) {
        this.xpos = xpos;
    }

    public void setYpos(int ypos) {
        this.ypos = ypos;
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

    public void resetStats() {
        this.life = 50;
        this.score = 0;
        this.multiplier = 1;
        this.noteStreak = 0;
        this.maxStreak = 0;
        this.hits = 0;
        this.misses = 0;
    }

    public void addComponents(Tab tab, int x) {
        this.hudX = x;
        noteStreakLabel.setBounds(x, 15, 230, 50);
        labelDesign(noteStreakLabel);
        multiplierLabel.setBounds(x, 65, 230, 50);
        labelDesign(multiplierLabel);
        scoreLabel.setBounds(x, 115, 230, 50);
        labelDesign(scoreLabel);
        scoreLabel.setFont(new Font("Verdana", Font.BOLD, 22));
        accuracyLabel.setBounds(x, tab.getScreenSize().height - 70, 230, 50);
        labelDesign(accuracyLabel);
        greenNote.setBounds(xpos - 5, ypos, 60, 42);
        redNote.setBounds(xpos + 75 - 5, ypos, 60, 42);
        yellowNote.setBounds(xpos + 150 - 5, ypos, 60, 42);
        blueNote.setBounds(xpos + 225 - 5, ypos, 60, 42);
        orangeNote.setBounds(xpos + 300 - 5, ypos, 60, 42);
        tab.add(noteStreakLabel);
        tab.add(multiplierLabel);
        tab.add(scoreLabel);
        tab.add(accuracyLabel);
        tab.add(greenNote);
        tab.add(redNote);
        tab.add(yellowNote);
        tab.add(blueNote);
        tab.add(orangeNote);
    }
    
    public void labelDesign(JLabel label) {
        label.setFont(new Font("Verdana", Font.BOLD, 18));
        label.setForeground(new Color(255, 255, 255));
        label.setOpaque(false);
        label.setHorizontalAlignment(SwingConstants.CENTER);
    }
    
    public void removeComponents(Tab tab) {
        tab.remove(noteStreakLabel);
        tab.remove(multiplierLabel);
        tab.remove(scoreLabel);
        tab.remove(accuracyLabel);
        tab.remove(greenNote);
        tab.remove(redNote);
        tab.remove(yellowNote);
        tab.remove(blueNote);
        tab.remove(orangeNote);
    }
    
}
