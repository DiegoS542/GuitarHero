package Components.Menu;

import Connection.Socket.Client;
import Player.Editor;
import Components.Scenes.ControllerSelection;
import Components.Scenes.OnePlayerScene;
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

public class GameMenu extends JPanel {

    JnaFileChooser fileChooser = new JnaFileChooser();
    int panelWidth;
    int panelHeight = 300;
    int WIDTH;
    int HEIGHT;
    String selectedSong;
    Menu3D menu;
    ControllerManager controllers = new ControllerManager();

    public GameMenu(JFrame frame, int WIDTH, int HEIGHT) {

        this.WIDTH = WIDTH;
        this.HEIGHT = HEIGHT;
        controllers.initSDLGamepad();

        Menu3D menu = new Menu3D();

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setLayout(null);
        setBackground(new Color(43, 45, 48));

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

    }

    private void switchToOnePlayerScene(JFrame frame) {
        try {
            ArrayList<Song> songs = new ArrayList<>();
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

                SongList songList = new SongList(frame, songs, getWidth(), getHeight(), 1, controllers);
                SongList songList = new SongList(this, frame, songs, WIDTH, HEIGHT,1);
                frame.getContentPane().removeAll();
                frame.add(songList);
                frame.revalidate();
                frame.repaint();
                //SwingUtilities.invokeLater(menu::requestFocusInWindow);
                //SwingUtilities.invokeLater(this::requestFocusInWindow);
                //this.repaint();
                // Aquí movemos la obtención de la canción seleccionada
                //selectedSong = songList.getSelectedSong();
                /*if (selectedSong != null) {
                    OnePlayerScene onePlayerPanel = new OnePlayerScene(frame,selectedSong);
                    frame.getContentPane().removeAll();
                    frame.getContentPane().add(onePlayerPanel);
                    frame.revalidate();
                    frame.repaint();
                    System.out.println("sdjasijdisajdfsafasfasfasfasdas");
                }*/
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void switchToTwoPlayerScene(JFrame frame) {
        try {
            ArrayList<Song> songs = new ArrayList<>();
            }
         catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void switchToTwoPlayerScene(JFrame frame) {
        ControllerSelection controllerSelection = new ControllerSelection(frame, getWidth(), getHeight());

        ControllerState currState = controllers.getState(0);
        if (currState.isConnected) {
            frame.getContentPane().removeAll();
            frame.add(controllerSelection);
            frame.revalidate();
            frame.repaint();
        } else {
            try {
                ArrayList<Song> songs = new ArrayList<>();

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
                frame.add(new SongList(this, frame, songs, WIDTH, HEIGHT,2));
                frame.revalidate();
                frame.repaint();

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
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setTitle("Selecciona una canción");

        if (fileChooser.showOpenDialog(null)) {
            File selectedFile = fileChooser.getSelectedFile();
            if (selectedFile.getName().toLowerCase().endsWith(".wav")) {
                if (fileChooser.getSelectedFile() != null) {
                    String path = fileChooser.getSelectedFile().getAbsolutePath();
                    String name = fileChooser.getSelectedFile().getName();
                    String newPath = "src/main/java/Resources/Songs/" + name;
                    try {
                        java.nio.file.Files.copy(java.nio.file.Paths.get(path), java.nio.file.Paths.get(newPath), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                    Editor editor = new Editor(newPath);
                    frame.getContentPane().removeAll();
                    frame.getContentPane().add(editor);
                    frame.revalidate();
                    frame.repaint();
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please select a WAV file.", "Invalid File Type", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void switchToOnline(JFrame frame){

                Client client = new Client(frame,getWidth(),getHeight());
                frame.getContentPane().removeAll();
                frame.add(client);
                frame.revalidate();
                frame.repaint();
    }
}
