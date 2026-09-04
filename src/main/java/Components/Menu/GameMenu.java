package Components.Menu;

import Editor.Editor;
import Components.SongList.SongList;
import Connection.Socket.Client;
import Components.Scenes.ControllerSelection;
import Player.Tab;
import Utilities.Song;
import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerState;
import jnafilechooser.api.JnaFileChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.LinearGradientPaint;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class GameMenu extends JPanel {

    private JnaFileChooser fileChooser = new JnaFileChooser();
    private int panelWidth;
    private int panelHeight = 300;
    private int WIDTH;
    private int HEIGHT;
    private String selectedSong;
    private Menu3D menu;
    private ArrayList<Song> songs = new ArrayList<>();
    private SongList songList;
    private Clip clip;
    private ControllerManager controllers;
    private volatile boolean running = true;

    public GameMenu(JFrame frame, int WIDTH, int HEIGHT) {

        this.WIDTH = WIDTH;
        this.HEIGHT = HEIGHT;

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setLayout(null);
        setBackground(new Color(5, 5, 5));
        panelWidth = frame.getWidth() / 4;
        controllers = new ControllerManager();
        controllers.initSDLGamepad();


        menu = new Menu3D();
        menu.addMenuItem("Un jugador");
        menu.addMenuItem("Dos jugadores");
        menu.addMenuItem("En linea");
        menu.addMenuItem("Vs. CPU");
        menu.addMenuItem("Editar");
        menu.addMenuItem("Configuración");
        menu.addMenuItem("Salir");

        int menuHeight = menu.getItemsSize() * menu.getMenuHeight() + 75;
        int menuWidth = WIDTH / 3;

        menu.setBounds((WIDTH - menuWidth) / 2, (HEIGHT - menuHeight) / 2, menuWidth, menuHeight);

        menu.addEvent(index -> {
            switch (index) {
                case 0:
                    switchToOnePlayerScene(frame);
                    break;
                case 1:
                    Tab.setVsCPU(false);
                    switchToTwoPlayerScene(frame);
                    break;
                case 2:
                    switchToOnline(frame);
                    break;
                case 3:
                    Tab.setVsCPU(true);
                    switchToTwoPlayerScene(frame);
                    break;
                case 4:
                    switchToEdit(frame);
                    break;
                case 5:
                    switchToSettings(frame);
                    break;
                case 6:
                    System.exit(0);
                    break;
            }
        });
        add(menu);
        if (controllers.getNumControllers() != 0){
        startControllerListener();
        }
        try {
            String audioFilePath = "src/main/java/Resources/Songs/Back in black.wav";
            File audioFile = new File(audioFilePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            //playAudio();
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

        g2.setFont(new Font("Arial Black", Font.BOLD, 80));
        FontMetrics fm = g2.getFontMetrics();
        String title = "Guitar Hero";
        int titleX = (getWidth() - fm.stringWidth(title)) / 2;
        int titleY = 130;

        g2.setColor(new Color(0, 0, 0, 200));
        g2.drawString(title, titleX + 4, titleY + 4);

        g2.setColor(Color.WHITE);
        g2.drawString(title, titleX, titleY);
    }

    public SongList getSongList(boolean multiplayer) {
        return songList;
    }

    public Clip getClip() {
        return clip;
    }

    private void switchToOnePlayerScene(JFrame frame) {
        try {
            //controllers.quitSDLGamepad();
            songList = new SongList(this, frame, WIDTH, HEIGHT, 1);
            frame.getContentPane().removeAll();
            frame.add(songList);
            frame.revalidate();
            frame.repaint();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void switchToTwoPlayerScene(JFrame frame) {
        try {
            //controllers.quitSDLGamepad();
            frame.getContentPane().removeAll();
            songList = new SongList(this, frame, WIDTH, HEIGHT, 2);
            frame.add(songList);
            frame.revalidate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void switchToEdit(JFrame frame) {
        clip.stop();
        Editor editor = new Editor(frame, this);
        frame.getContentPane().removeAll();
        frame.getContentPane().add(editor);
        frame.revalidate();
        frame.repaint();
    }

    public void resetMenu(JFrame frame) {
        try {
            frame.getContentPane().removeAll();
            frame.add(this);
            frame.revalidate();
            frame.repaint();
            SwingUtilities.invokeLater(menu::requestFocusInWindow);
        } catch (Exception e) {
        }
    }

    public void playAudio() throws LineUnavailableException, UnsupportedAudioFileException, IOException {
        clip.start();
    }

    public void restartAudio() {
        clip.stop();
        clip.setFramePosition(0);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void switchToOnline(JFrame frame) {
        clip.stop();
        Client client = new Client(this, frame, getWidth(), getHeight());
        frame.getContentPane().removeAll();
        frame.add(client);
        frame.revalidate();
        frame.repaint();
    }

    public void switchToSettings(JFrame frame) {
        Settings settings = new Settings(this, frame, getWidth(), getHeight());
        frame.getContentPane().removeAll();
        frame.getContentPane().add(settings);
        frame.revalidate();
        frame.repaint();
 
    }

    private void startControllerListener() {
        new Thread(() -> {
            while (running) {
                ControllerState currState = controllers.getState(0);
                if (currState.isConnected) {
                    if (currState.dpadUpJustPressed) {
                        if (menu.pressedIndex > 0) {
                            menu.pressedIndex--;
                            menu.repaint();
                        }
                    }
                    if (currState.dpadDownJustPressed) {
                        if (menu.pressedIndex < menu.items.size() - 1) {
                            menu.pressedIndex++;
                            menu.repaint();
                        }
                    }
                    if (currState.startJustPressed || currState.aJustPressed) {
                        if (menu.pressedIndex != -1) {
                            running = false;
                            menu.items.get(menu.pressedIndex).getAnimator().show();
                            menu.hideMenu(menu.pressedIndex);
                            menu.runEvent();
                        }
                    }
                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void setRunning(Boolean running) {
        this.running = running;
    }

}
