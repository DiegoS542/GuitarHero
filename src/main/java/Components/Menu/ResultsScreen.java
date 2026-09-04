package Components.Menu;

import Components.Scenes.OnePlayerScene;
import Components.Scenes.TwoPlayerScene;
import Player.Player;
import Utilities.Song;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ResultsScreen extends JPanel {

    private final Player player;
    private final Player player2;
    private final Player loser;

    public ResultsScreen(GameMenu mainMenu, JFrame frame, int WIDTH, int HEIGHT, Player player, Player player2, Player loser, Song song) {
        this.player = player;
        this.player2 = player2;
        this.loser = loser;

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setLayout(null);
        setBackground(new Color(5, 5, 5));

        boolean pureDefeat = (player2 == null && loser != null);
        if (!pureDefeat) {
            playApplause();
        }

        Menu3D menu = new Menu3D();
        menu.addMenuItem("Reintentar");
        menu.addMenuItem("Continuar");
        int menuWidth = WIDTH / 3;
        int menuHeight = menu.getItemsSize() * menu.getMenuHeight() + 75;
        menu.setBounds((WIDTH - menuWidth) / 2, HEIGHT - menuHeight - 60, menuWidth, menuHeight);
        menu.addEvent(index -> {
            if (index == 0) {
                try {
                    frame.getContentPane().removeAll();
                    if (player2 == null) {
                        frame.add(new OnePlayerScene(mainMenu, frame, song));
                    } else {
                        frame.add(new TwoPlayerScene(mainMenu, frame, song));
                    }
                    frame.revalidate();
                    frame.repaint();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                mainMenu.resetMenu(frame);
                mainMenu.restartAudio();
            }
        });
        add(menu);

        SwingUtilities.invokeLater(menu::requestFocusInWindow);
    }

    private void playApplause() {
        try {
            File audioFile = new File("src/main/java/Resources/SoundFX/claps.wav");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        String title;
        Color titleColor;
        if (player2 == null) {
            if (loser != null) {
                title = "Perdiste";
                titleColor = new Color(220, 60, 60);
            } else {
                title = "¡Ganaste!";
                titleColor = new Color(120, 220, 120);
            }
        } else if (loser != null) {
            Player winner = (loser == player) ? player2 : player;
            title = "Gana " + (winner == player ? "Jugador 1" : "Jugador 2");
            titleColor = new Color(255, 215, 0);
        } else if (player.getScore() == player2.getScore()) {
            title = "¡Empate!";
            titleColor = new Color(255, 215, 0);
        } else {
            title = "Gana " + (player.getScore() > player2.getScore() ? "Jugador 1" : "Jugador 2");
            titleColor = new Color(255, 215, 0);
        }

        g2.setFont(new Font("Arial Black", Font.BOLD, 60));
        FontMetrics titleFm = g2.getFontMetrics();
        int titleX = (getWidth() - titleFm.stringWidth(title)) / 2;
        int titleY = 150;
        g2.setColor(new Color(0, 0, 0, 200));
        g2.drawString(title, titleX + 3, titleY + 3);
        g2.setColor(titleColor);
        g2.drawString(title, titleX, titleY);

        int y = 240;
        if (player2 == null) {
            drawPlayerStats(g2, player, null, getWidth() / 2, y);
        } else {
            y = drawPlayerStats(g2, player, "Jugador 1", getWidth() / 2, y);
            drawPlayerStats(g2, player2, "Jugador 2", getWidth() / 2, y + 20);
        }
    }

    private int drawPlayerStats(Graphics2D g2, Player p, String label, int centerX, int y) {
        g2.setFont(new Font("Verdana", Font.BOLD, 24));
        FontMetrics fm = g2.getFontMetrics();
        String line1 = (label != null ? label + " — " : "") + "Puntaje: " + p.getScore();
        g2.setColor(Color.WHITE);
        g2.drawString(line1, centerX - fm.stringWidth(line1) / 2, y);
        y += 32;

        g2.setFont(new Font("Verdana", Font.PLAIN, 18));
        fm = g2.getFontMetrics();
        String line2 = String.format("Racha máxima: %d    Precisión: %.2f%%", p.getMaxStreak(), p.getAccuracy());
        g2.setColor(new Color(210, 210, 210));
        g2.drawString(line2, centerX - fm.stringWidth(line2) / 2, y);
        y += 34;

        String stars = starString(p.getAccuracy());
        g2.setFont(new Font("Dialog", Font.PLAIN, 28));
        fm = g2.getFontMetrics();
        g2.setColor(new Color(255, 215, 0));
        g2.drawString(stars, centerX - fm.stringWidth(stars) / 2, y);
        y += 40;

        return y;
    }

    private String starString(double accuracy) {
        int stars = accuracy >= 95 ? 5 : accuracy >= 80 ? 4 : accuracy >= 60 ? 3 : accuracy >= 40 ? 2 : accuracy >= 20 ? 1 : 0;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            sb.append(i < stars ? "★" : "☆");
        }
        return sb.toString();
    }
}
