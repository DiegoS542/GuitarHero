package Components.Menu;

import Editor.Editor;
import Player.*;
import Scenes.OnePlayerScene;
import Scenes.SongList.SongList;
import Connection.Socket.Client;
import Player.Editor;
import Components.Scenes.ControllerSelection;

import Components.SongList.SongList;
import Utilities.Song;
import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerState;
import jnafilechooser.api.JnaFileChooser;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class GameMenu extends JPanel {

    JnaFileChooser fileChooser = new JnaFileChooser();
    int panelWidth;
    int panelHeight = 300;
    int WIDTH;
    int HEIGHT;
    String selectedSong;
    Menu3D menu;
    ControllerManager controllers = new ControllerManager();
    ArrayList<Song> songs = new ArrayList<>();

    SongList songList;
    SongList songList2;
    public Menu3D menu;
    Clip clip;
    public GameMenu(JFrame frame, int WIDTH, int HEIGHT) {

        this.WIDTH = WIDTH;
        this.HEIGHT = HEIGHT;
        controllers.initSDLGamepad();

        Menu3D menu = new Menu3D();

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setLayout(null);
        setBackground(new Color(43, 45, 48));
        panelWidth = frame.getWidth() / 4;

        menu = new Menu3D();
        menu.addMenuItem("Un jugador");
        menu.addMenuItem("Dos jugadores");
        menu.addMenuItem("En linea");
        menu.addMenuItem("Editar");
        menu.addMenuItem("Cerrar");

        int menuHeight = menu.getItemsSize() * menu.getMenuHeight() + 75;
        int menuWidth = WIDTH / 3;

        menu.setBounds((WIDTH - menuWidth) / 2, (HEIGHT - menuHeight) / 2, menuWidth, menuHeight);

        menu.addEvent(index -> {
            switch (index) {
                case 0:
                    switchToOnePlayerScene(frame);
                    break;
                case 1:
                    switchToTwoPlayerScene(frame);
                    break;
                case 2:
                    switchToOnline(frame);
                    break;
                case 3:
                    switchToEdit(frame);
                    break;
                case 4:
                    System.exit(0);
                    break;
            }
        });

        add(menu);
        try {
            playAudio();
        } catch(Exception e){}
    }

    public SongList getSongList(boolean multiplayer) {
        return songList;
    }

    private void switchToOnePlayerScene(JFrame frame) {
        ArrayList<Song> songs = new ArrayList<>();
        try {
            File folder = new File("src/main/java/Resources/Charts/");
            File[] listOfFiles = folder.listFiles();

            if (listOfFiles != null) {
                for (File file : listOfFiles) {
                    if (file.isFile()) {
                        try {
                            Song song = readSongFromFile(file);
                            songs.add(song);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                songList = new SongList(this, frame, songs, WIDTH, HEIGHT,1);
                songList.switchSongMenu();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }



    private void switchToTwoPlayerScene(JFrame frame) {
        ArrayList<Song> songs = new ArrayList<>();
        try {
        ControllerSelection controllerSelection = new ControllerSelection(frame, getWidth(), getHeight());

        ControllerState currState = controllers.getState(0);
        if (currState.isConnected) {
            frame.getContentPane().removeAll();
            frame.add(controllerSelection);
            frame.revalidate();
            frame.repaint();
        } else {
            try {
               songs = new ArrayList<>();

                File folder = new File("src/main/java/Resources/Charts/");
                File[] listOfFiles = folder.listFiles();

                if (listOfFiles != null) {
                    System.out.println("Number of files: " + listOfFiles.length);

                    for (File file : listOfFiles) {
                        if (file.isFile()) {
                            try {
                                Song song = readSongFromFile(file);
                                songs.add(song);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                // Aquí añadimos el SongList al content pane del frame
                frame.getContentPane().removeAll();
                frame.add(new SongList(frame,songs,WIDTH,HEIGHT,2,controllers));
                frame.revalidate();
                frame.repaint();

                songList = new SongList(this, frame, songs, WIDTH, HEIGHT,2);
                songList.switchSongMenu();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
                    frame.getContentPane().removeAll();
                    frame.add(new SongList(frame, songs, getWidth(), getHeight(), 2, controllers));
                    frame.revalidate();
                    frame.repaint();
                }

        }
    }


    private Song readSongFromFile(File file) throws IOException {
        Song song = new Song();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("Name = ")) {
                    song.setName(line.substring(8, line.length() - 1));
                } else if (line.startsWith("Artist = ")) {
                    song.setBand(line.substring(10, line.length() - 1));
                } else if (line.startsWith("Difficulty = ")) {
                    song.setDifficulty(Integer.parseInt(line.substring(13)));
                } else if (line.startsWith("Genre = ")) {
                    song.setGenre(line.substring(9, line.length() - 1));
                }
            }
        }
        return song;
    }

    private void switchToEdit(JFrame frame) {
        Editor editor = new Editor(frame,this);
        frame.getContentPane().removeAll();
        frame.getContentPane().add(editor);
        frame.revalidate();
        frame.repaint();

    }

    public void resetMenu(JFrame frame) {
        frame.getContentPane().removeAll();
        frame.add(menu);
        frame.add(this);
        frame.revalidate();
        frame.repaint();
        SwingUtilities.invokeLater(menu::requestFocusInWindow);
    }

    public void playAudio() throws LineUnavailableException, UnsupportedAudioFileException, IOException {
        String audioFilePath= "src/main/java/Resources/Songs/Silence.wav";
        File audioFile = new File(audioFilePath);
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
        clip = AudioSystem.getClip();
        clip.open(audioStream);
        clip.start();
    }

    public void switchToOnline(JFrame frame){

                Client client = new Client(frame,getWidth(),getHeight());
                frame.getContentPane().removeAll();
                frame.add(client);
                frame.revalidate();
                frame.repaint();
    }
}
