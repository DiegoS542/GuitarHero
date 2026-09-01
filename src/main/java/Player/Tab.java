package Player;

import Components.Menu.GameMenu;
import Components.Menu.Menu3D;
import Components.Menu.PauseMenu;
import Components.SongList.SongList;
import Utilities.Song;
import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerState;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Tab extends JPanel {

    JFrame frame;
    Player player;
    Player player2;
    Clip clip;
    Menu3D menu;
    GameMenu mainMenu;
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    ImageIcon stage;
    int ypos;
    int xpos;
    private final JLabel noteStreak = new JLabel("Note Streak: 0");
    private final JLabel multiplier = new JLabel("Multiplier: 1x");
    private final JLabel score = new JLabel("Score: 0");
    private final JLabel life = new JLabel("Life: 50");
    ArrayList<GameNote> notes;
    ArrayList<GameNote> notes2;
    NoteGenerator ng;
    NoteGenerator ng2;
    private boolean paused = false;
    long elapsedTime;
    long dt;
    String selectedSong;
    static boolean multiplayer;
    static boolean vsCPU;
    boolean exit;
    static int presition = 1;
    GameThread gameThread;
    boolean shouldPress;
    public volatile boolean running = true;
    ControllerManager controllers = new ControllerManager();

    public Tab(GameMenu mainMenu, Player player, Player player2, Song song, JFrame frame) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        setLayout(new GridLayout(1, 1));
        setSize(new Dimension((int) screenSize.getWidth(), (int) screenSize.getHeight()));
        setBackgroundImage(song.getDifficulty());
        shouldPress = false;
        this.player = player;
        this.player2 = player2;
        this.mainMenu = mainMenu;
        this.selectedSong = song.getName();
        this.exit = false;
        setLayout(null);
        setPreferredSize(new Dimension((int) (screenSize.getWidth()), (int) screenSize.getHeight()));
        ypos = (int) screenSize.getHeight() - 75;
        xpos = (int) (screenSize.getWidth() - 300) / 2;

        if (multiplayer) {
            xpos /= 2;
            this.player2.setXpos(xpos * 3);
            this.player2.setYpos(ypos);
            this.player2.addComponents(this, screenSize.width - 185);
        }
        this.player.setXpos(xpos);
        this.player.setYpos(ypos);
        this.player.addComponents(this, 35);

        KB();
        setFocusable(true);
        this.frame = frame;
    }

    //Setters

    public static void setVsCPU(boolean vsCPU) {
        Tab.vsCPU = vsCPU;
    }

    public static void setPresition(int presition) {
        Tab.presition = presition;
    }

    public void setYpos(int ypos) {
        this.ypos = ypos;
    }

    public void setXpos(int xpos) {
        this.xpos = xpos;
    }

    public static void setMultiplayer(boolean multiplayer) {
        Tab.multiplayer = multiplayer;
    }

    //Getters

    public int getYpos() {
        return this.ypos;
    }

    public int getXpos() {
        return this.xpos;
    }

    public ArrayList<GameNote> getNotes() {
        return notes;
    }

    public Dimension getScreenSize() {
        return screenSize;
    }

    public static boolean isMultiplayer() {
        return multiplayer;
    }

    // Methods
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (stage != null) {
            Image stageImage = stage.getImage();
            g.drawImage(stageImage, 0, 0, getWidth(), getHeight(), this);
        }
        drawLines(g, xpos, ypos);
        if (!notes.isEmpty()) {
            paintNotes(g, notes, player);
        }
        if (multiplayer) {
            if (!notes2.isEmpty())
                paintNotes(g, notes2, player2);
            drawLines(g, xpos, ypos);
            if (!notes.isEmpty()) {
                paintNotes(g, notes, player);
            }
            if (multiplayer) {
                if (!notes2.isEmpty())
                    paintNotes(g, notes2, player2);
            }
        }
    }

    private void drawLines(Graphics g, int xpos, int ypos) {
        g.setColor(Color.BLACK);
        g.drawLine(xpos - 25, 0, xpos - 25, ypos + 75);
        g.drawLine(xpos + 25, 0, xpos + 25, ypos);
        g.drawLine(xpos + 100, 0, xpos + 100, ypos);
        g.drawLine(xpos + 175, 0, xpos + 175, ypos);
        g.drawLine(xpos + 250, 0, xpos + 250, ypos);
        g.drawLine(xpos + 325, 0, xpos + 325, ypos);
        g.drawLine(xpos + 375, 0, xpos + 375, ypos + 75);

        g.setColor(new Color(128, 128, 128, 128));
        g.fillRect(xpos - 25, 0, 400, ypos + 75);
        if (multiplayer) {
            g.setColor(Color.BLACK);
            g.drawLine(xpos * 3 - 25, 0, xpos * 3 - 25, ypos + 75);
            g.drawLine(xpos * 3 + 25, 0, xpos * 3 + 25, ypos);
            g.drawLine(xpos * 3 + 100, 0, xpos * 3 + 100, ypos);
            g.drawLine(xpos * 3 + 175, 0, xpos * 3 + 175, ypos);
            g.drawLine(xpos * 3 + 250, 0, xpos * 3 + 250, ypos);
            g.drawLine(xpos * 3 + 325, 0, xpos * 3 + 325, ypos);
            g.drawLine(xpos * 3 + 375, 0, xpos * 3 + 375, ypos + 75);

            g.setColor(new Color(128, 128, 128, 128));
            g.fillRect(xpos * 3 - 25, 0, 400, ypos + 75);
        }
    }

    public void paintNotes(Graphics g, ArrayList<GameNote> notes, Player player) {
        Iterator<GameNote> iterator = notes.iterator();
        int oldNoteStreak = player.noteStreak;
        while (iterator.hasNext()) {
            GameNote element = iterator.next();
            if (element.isInScreen()) {
                g.setColor(element.getBorderColor());
                g.fillOval(element.getX(), element.getY(), 50, 35);
            }
            if (element.getY() >= ypos && element.getY() <= ypos + 100 && element.isInScreen()) {
                if(vsCPU && player.playerNumber == 2){
                    switch((int)(Math.random() * presition)) {
                            case 0: 
                                shouldPress = true;
                                switch(element.getButton()) {
                                    case 0: 
                                        player2.greenNote.setReleased(true);
                                        break;
                                    case 1:
                                        player2.redNote.setReleased(true);
                                        break;
                                    case 2:
                                        player2.yellowNote.setReleased(true);
                                        break;
                                    case 3:
                                        player2.blueNote.setReleased(true);
                                        break;
                                    case 4:
                                        player2.orangeNote.setReleased(true);
                                        break;
                                }
                                break;
                    }
                    
                }
                if ((player.greenNote.isReleased() && player.greenNote.isClicked() && element.getX() == player.greenNote.getX()) ||
                        (player.redNote.isReleased() && player.redNote.isClicked() && element.getX() == player.redNote.getX()) ||
                        (player.yellowNote.isReleased() && player.yellowNote.isClicked() && element.getX() == player.yellowNote.getX()) ||
                        (player.blueNote.isReleased() && player.blueNote.isClicked() && element.getX() == player.blueNote.getX()) ||
                        (player.orangeNote.isReleased() && player.orangeNote.isClicked() && element.getX() == player.orangeNote.getX())) {
                    if (element.getX() == player.greenNote.getX()) player.greenNote.setClicked(false);
                    if (element.getX() == player.redNote.getX()) player.redNote.setClicked(false);
                    if (element.getX() == player.yellowNote.getX()) player.yellowNote.setClicked(false);
                    if (element.getX() == player.blueNote.getX()) player.blueNote.setClicked(false);
                    if (element.getX() == player.orangeNote.getX()) player.orangeNote.setClicked(false);
                    element.setInScreen(false);
                    element.setScored(true);
                    player.score += 50 * player.multiplier;
                    player.noteStreak++;
                    if (player.noteStreak % 10 == 0 && player.multiplier <= 4) {
                        player.multiplier++;
                    }
                    if (player.life < 100) {
                        player.life += 5;
                    }
                }
            } else if (element.getY() >= screenSize.height && !element.isScored() && element.isInScreen()) {
                element.setInScreen(false);
                player.resetNoteStreak();
                player.resetMultiplier();
                if (player.life == 0) {
                    //System.exit(0);
                } else {
                    player.life -= 5;
                }
            }

        }
        if (oldNoteStreak != player.noteStreak) {
            player.noteStreakLabel.setText("Note Streak: " + player.noteStreak);
        }
        player.scoreLabel.setText("Score: " + player.score);
        player.multiplierLabel.setText("Multiplier: " + player.multiplier + "x");
        player.lifeLabel.setText("Life: " + player.life);
    }

    public void KB() {


        KeyListener kb = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_A:
                        player.greenNote.setReleased(true);
                        break;
                    case KeyEvent.VK_S:
                        player.redNote.setReleased(true);
                        break;
                    case KeyEvent.VK_D:
                        player.yellowNote.setReleased(true);
                        break;
                    case KeyEvent.VK_F:
                        player.blueNote.setReleased(true);
                        break;
                    case KeyEvent.VK_G:
                        player.orangeNote.setReleased(true);
                        break;
                    case KeyEvent.VK_ESCAPE:
                        togglePause();
                        break;
                }
                if (multiplayer && !vsCPU) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_Y:
                            player2.greenNote.setReleased(true);
                            break;
                        case KeyEvent.VK_U:
                            player2.redNote.setReleased(true);
                            break;
                        case KeyEvent.VK_I:
                            player2.yellowNote.setReleased(true);
                            break;
                        case KeyEvent.VK_O:
                            player2.blueNote.setReleased(true);
                            break;
                        case KeyEvent.VK_P:
                            player2.orangeNote.setReleased(true);
                            break;
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_A:
                        player.greenNote.setReleased(false);
                        player.greenNote.setClicked(true);
                        break;
                    case KeyEvent.VK_S:
                        player.redNote.setReleased(false);
                        player.redNote.setClicked(true);
                        break;
                    case KeyEvent.VK_D:
                        player.yellowNote.setReleased(false);
                        player.yellowNote.setClicked(true);
                        break;
                    case KeyEvent.VK_F:
                        player.blueNote.setReleased(false);
                        player.blueNote.setClicked(true);
                        break;
                    case KeyEvent.VK_G:
                        player.orangeNote.setReleased(false);
                        player.orangeNote.setClicked(true);
                        break;
                }
                if (multiplayer && !vsCPU) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_Y:
                            player2.greenNote.setReleased(false);
                            player2.greenNote.setClicked(true);
                            break;
                        case KeyEvent.VK_U:
                            player2.redNote.setReleased(false);
                            player2.redNote.setClicked(true);
                            break;
                        case KeyEvent.VK_I:
                            player2.yellowNote.setReleased(false);
                            player2.yellowNote.setClicked(true);
                            break;
                        case KeyEvent.VK_O:
                            player2.blueNote.setReleased(false);
                            player2.blueNote.setClicked(true);
                            break;
                        case KeyEvent.VK_P:
                            player2.orangeNote.setReleased(false);
                            player2.orangeNote.setClicked(true);
                            break;
                    }
                }
            }
        };
        this.addKeyListener(kb);


    }

    private void handleControllerInput(ControllerState currState, PlayerNote greenNote, PlayerNote redNote, PlayerNote yellowNote, PlayerNote blueNote, PlayerNote orangeNote) {
        if (currState.a) {  // Green fret
            greenNote.setReleased(true);
        } else {
            greenNote.setReleased(false);
            greenNote.setClicked(true);
        }
        if (currState.b) {  // Red fret
            redNote.setReleased(true);
        } else {
            redNote.setReleased(false);
            redNote.setClicked(true);
        }
        if (currState.y) {  // Yellow fret
            yellowNote.setReleased(true);
        } else {
            yellowNote.setReleased(false);
            yellowNote.setClicked(true);
        }
        if (currState.x) {  // Blue fret
            blueNote.setReleased(true);
        } else {
            blueNote.setReleased(false);
            blueNote.setClicked(true);
        }
        if (currState.lb) {  // Orange fret
            orangeNote.setReleased(true);
        } else {
            orangeNote.setReleased(false);
            orangeNote.setClicked(true);
        }
    }


    public void play(String selectedSong) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        gameThread = new GameThread(this);
        ng = new NoteGenerator("Note Generator", this, selectedSong, player, xpos, ypos);
        notes = ng.getNotes();

        if (multiplayer) {
            ng2 = new NoteGenerator("Note Generator", this, selectedSong, player2, xpos * 3, ypos);
            notes2 = ng2.getNotes();
            ng2.start();
        }
        if(multiplayer && vsCPU) {
            Thread CPUThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while(!exit) {
                        if(shouldPress) {
                            try {
                                TimeUnit.NANOSECONDS.sleep(50000000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            if(player2.greenNote.isReleased()){
                                player2.greenNote.setReleased(false);
                                player2.greenNote.setClicked(true);
                            }
                            if(player2.redNote.isReleased()){
                                player2.redNote.setReleased(false);
                                player2.redNote.setClicked(true);
                            }
                            if(player2.yellowNote.isReleased()){
                                player2.yellowNote.setReleased(false);
                                player2.yellowNote.setClicked(true);
                            }
                            if(player2.blueNote.isReleased()){
                                player2.blueNote.setReleased(false);
                                player2.blueNote.setClicked(true);
                            }
                            if(player2.orangeNote.isReleased()){
                                player2.orangeNote.setReleased(false);
                                player2.orangeNote.setClicked(true);
                            }
                            shouldPress = false;
                        }
                        try {
                                TimeUnit.NANOSECONDS.sleep(100000000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });
            CPUThread.start();
        }
        ng.start();
        playAudio();
        gameThread.start();
        SwingUtilities.invokeLater(this::requestFocusInWindow);
    }

    public void draw() {
        repaint();
    }

    public void playAudio() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        String audioFilePath = "src/main/java/Resources/Songs/" + selectedSong + ".wav";
        File audioFile = new File(audioFilePath);
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
        clip = AudioSystem.getClip();
        clip.open(audioStream);
        clip.start();
    }

    public void resumeAudio() {
        clip.start();
    }

    public void pauseAudio() {
        clip.stop();
    }

    void togglePause() {
        paused = !paused;
        if (paused) {

            ng.pauseG();
            if (multiplayer)
                ng2.pauseG();
            pauseAudio();
            menu = new PauseMenu();
            int menuWidth = 600;
            int menuHeight = 500;
            menu.setBounds(500, 50, menuWidth, menuHeight);
            this.add(menu);
            this.repaint();


            menu.addEvent(index -> {
                switch (index) {
                    case 0:
                        togglePause();
                        break;
                    case 1: {
                        try {
                            exit = true;
                            gameThread.setExit(true);
                            ng.setExit(true);
                            if (multiplayer)
                                ng2.setExit(true);
                            this.remove(menu);
                            paused = !paused;
                            this.repaint();
                            play(selectedSong);
                        } catch (Exception e) {
                        }
                    }
                    break;

                    case 2:
                        paused = false;
                        exit = true;
                        gameThread.setExit(true);
                        if (!multiplayer) {
                            ng.setExit(true);
                            //if(controllers != null)
                                //controllers.quitSDLGamepad();
                            running = false;
                            switchToGameMenu(mainMenu);

                        } else {
                            running = false;
                            ng2.setExit(true);
                            //if(controllers != null)
                                //controllers.quitSDLGamepad();
                            switchToGameMenu(mainMenu);
                            

                        }

                        break;
                }
            });
        } else {
            ng.resumeG();
            if (multiplayer)
                ng2.resumeG();
            resumeAudio();
            this.remove(menu);
        }
    }

    public void setBackgroundImage(int difficulty) {
        String imagePath = switch (difficulty) {
            case 0 -> "src/main/java/Resources/Stages/small_concert.gif/";
            case 1 -> "src/main/java/Resources/Stages/street.jpg";
            case 2 -> "src/main/java/Resources/Stages/garage.jpg";
            case 3 -> "src/main/java/Resources/Stages/little_concert.png";
            case 4 -> "src/main/java/Resources/Stages/big_concert.jpg";
            case 5 -> "src/main/java/Resources/Stages/stadium.jpg";
            default -> "";
        };
        System.out.println(imagePath);

        ImageIcon imageIcon = new ImageIcon(imagePath);
        if (imageIcon.getImageLoadStatus() == MediaTracker.ERRORED) {
            System.err.println("Error: Background image not found or failed to load at " + imagePath);
        } else {
            stage = imageIcon;
        }
        repaint();
    }

    public void switchToGameMenu(GameMenu mainMenu) {
        frame.getContentPane().removeAll();
        frame.add(mainMenu);
        mainMenu.resetMenu(frame);
        frame.revalidate();
        frame.repaint();

    }
}
