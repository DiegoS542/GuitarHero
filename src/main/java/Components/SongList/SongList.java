package Components.SongList;

import Components.Menu.*;
import Components.Scenes.*;
import Utilities.Song;
import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import static java.awt.event.KeyEvent.VK_ENTER;

public class SongList extends JPanel {
    private Song selectedSong;
    private final Menu3D menu;
    private final Panel panel = new Panel();
    ImageIcon albumCover;
    private ControllerManager controllers;
    private volatile boolean running = true;

    private int players;
    private JFrame frame;
    private final ArrayList<Song> songs = new ArrayList<>();
    private GameMenu gameMenu;

    public SongList(GameMenu gameMenu, JFrame frame, int WIDTH, int HEIGHT, int players) {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(new Color(43, 45, 48));
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        this.frame = frame;
        this.gameMenu = gameMenu;
        this.players = players;

        //controllers = new ControllerManager();


        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.weightx = 1;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.VERTICAL;

        menu = new Menu3D();
        int menuWidth = (WIDTH / 4);
        int menuHeight = songs.size() * menu.getMenuHeight() + 75;
        menu.setPreferredSize(new Dimension(menuWidth, menuHeight));

        add(menu, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;

        panel.setPreferredSize(new Dimension((WIDTH / 4), HEIGHT-(HEIGHT / 3) ));

        add(panel, gbc);
        loadSongs();

        for (Song song : songs) {
            menu.addMenuItem(song.getName());
        }

        menu.setFocusable(true);
        menu.requestFocusInWindow();
        updatePanel(songs.get(menu.getPressedIndex()));

        menu.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleInput(e.getKeyCode());
            }
        });
        //startControllerListener();
    }


    public void handleInput(int key) {
        switch (key) {
            case VK_ENTER:
                gameMenu.getClip().stop();
                if (players == 1) {
                    try {
                running = false;
                        frame.getContentPane().removeAll();
                        frame.add(new OnePlayerScene(gameMenu, frame, songs.get(menu.getPressedIndex())));
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                } else {
                    try {
                running = false;
                        frame.getContentPane().removeAll();
                        frame.add(new TwoPlayerScene(gameMenu, frame, songs.get(menu.getPressedIndex())));
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
                frame.revalidate();
                frame.repaint();
                break;
            case KeyEvent.VK_ESCAPE:
                running = false;
                frame.getContentPane().removeAll();
                frame.getContentPane().add(gameMenu);
                frame.revalidate();
                frame.repaint();
                gameMenu.resetMenu(frame);
                gameMenu.setRunning(true);
                break;
            case KeyEvent.VK_UP, KeyEvent.VK_DOWN:
                updatePanel(songs.get(menu.getPressedIndex()));
                break;
        }
    }

    public Song getSelectedSong() {
        return selectedSong;
    }

    public void updatePanel(Song song) {
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 0, 10, 75);
        gbc.ipadx = 10;
        gbc.weightx = 1.0;
        panel.removeAll();

        ImageIcon albumCover = new ImageIcon("src/main/java/Resources/Images/" + song.getName() + ".jpg");
        Image imgCover = albumCover.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        JLabel cover = new JLabel(new ImageIcon(imgCover));

        JLabel songName = new JLabel("Nombre: " + song.getName());
        songName.setForeground(Color.WHITE);
        songName.setFont(menu.getFont());

        JLabel artist = new JLabel("Artista: " + song.getBand());
        artist.setForeground(Color.WHITE);
        artist.setFont(menu.getFont());

        JLabel genre = new JLabel("Género: " + song.getGenre());
        genre.setForeground(Color.WHITE);
        genre.setFont(menu.getFont());

        JLabel difficulty = new JLabel("Dificultad: " + song.getDifficulty());
        difficulty.setForeground(Color.WHITE);
        difficulty.setFont(menu.getFont());

        panel.add(cover, gbc);
        panel.add(songName, gbc);
        panel.add(artist, gbc);
        panel.add(genre, gbc);
        panel.add(difficulty, gbc);

        panel.revalidate();
        panel.repaint();
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

    private void startControllerListener() {
        new Thread(() -> {
            controllers.initSDLGamepad();
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
                    if (currState.bJustPressed){
                        frame.getContentPane().removeAll();
                        frame.getContentPane().add(gameMenu);
                        frame.revalidate();
                        frame.repaint();
                        gameMenu.resetMenu(frame);
                    }
                    if (currState.startJustPressed || currState.aJustPressed) {
                        if (menu.pressedIndex != -1) {
                            menu.items.get(menu.pressedIndex).getAnimator().show();
                            menu.hideMenu(menu.pressedIndex);
                            menu.runEvent();
                            if (players == 1) {
                                try {
                                    frame.getContentPane().removeAll();
                                    frame.add(new OnePlayerScene(gameMenu, frame, songs.get(menu.getPressedIndex())));
                                    controllers.quitSDLGamepad();
                                } catch (Exception exception) {
                                    exception.printStackTrace();
                                }
                            } else {
                                try {
                                    frame.getContentPane().removeAll();
                                    frame.add(new TwoPlayerScene(gameMenu, frame, songs.get(menu.getPressedIndex())));
                                    controllers.quitSDLGamepad();
                                } catch (Exception exception) {
                                    exception.printStackTrace();
                                }
                            }

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

    public void loadSongs() {
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
        }
    }
}
