package Editor;

import Player.*;
import Components.Menu.GameMenu;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import jnafilechooser.api.JnaFileChooser;

public class Editor extends JPanel{
    JnaFileChooser fileChooser = new JnaFileChooser();
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int width;
    int height;
    int ypos;
    int xpos; 
    String songName = "";
    String artist = " ";
    String difficulty = "0";
    String genre = " ";
    String fileName;
    JFrame frame;
    Editor panel;
    GameMenu mainMenu;
    JButton returnButton;
    JButton addSongButton;
    JButton songPropierties;
    JButton playButton;
    JButton stopButton;
    JButton pauseButton;
    JButton resumeButton;
    JButton remove;
    JButton removeAll;
    JButton backward;
    JButton forward;
    JButton save;
    JLabel timeLabel;
    JLabel durationLabel;
    SongPropierties propiertiesPanel;
    String songPath;
    Clip clip;
    CopyOnWriteArrayList<GameNote> notes;
    long elapsedTime;
    long startTime;
    long pausePosition;
    boolean exit;
    boolean paused;
    long dt;
    boolean skip;
    boolean back;
    PlayerNote greenNote = new PlayerNote(new Color(54, 58, 59), new Color(8, 200, 3), new Color(8, 200, 3));
    PlayerNote redNote = new PlayerNote(new Color(54, 58, 59), new Color(163, 24, 24), new Color(163, 24, 24));
    PlayerNote yellowNote = new PlayerNote(new Color(54, 58, 59), new Color(254, 254, 53), new Color(254, 254, 53));
    PlayerNote blueNote = new PlayerNote(new Color(54, 58, 59), new Color(63, 162, 211), new Color(63, 162, 211));
    PlayerNote orangeNote = new PlayerNote(new Color(54, 58, 59), new Color(217, 147, 53), new Color(217, 147, 53));
    ImageIcon pauseIcon = new ImageIcon("src/main/java/Resources/Icons/Pause.png");
    ImageIcon playIcon = new ImageIcon("src/main/java/Resources/Icons/Play.png");
    ImageIcon stopIcon = new ImageIcon("src/main/java/Resources/Icons/Stop.png");
    ImageIcon backwardIcon = new ImageIcon("src/main/java/Resources/Icons/Backward.png");
    ImageIcon forwardIcon = new ImageIcon("src/main/java/Resources/Icons/Forward.png");
    
    public Editor(JFrame frame, GameMenu mainMenu) {
        setLayout(null);
        setPreferredSize(screenSize);
        exit = false;
        skip = false;
        back = false;
        paused = false;
        panel = this;
        ypos = (int) screenSize.getHeight() - 75;
        xpos = (int) (screenSize.getWidth() - 300) / 2;
        dt = (long)(((ypos+35))/(GameNote.getSpeed())* 5) ;
        width = (int) screenSize.getWidth();
        height = (int) screenSize.getHeight();
        this.frame = frame;
        this.mainMenu = mainMenu;
        notes = new CopyOnWriteArrayList<>();
        frame.setCursor(Cursor.getDefaultCursor());
        returnButton = new JButton("Regresar");
        songPropierties = new JButton("Info");
        addSongButton = new JButton("Seleccionar Audio");
        remove = new JButton("Remover");
        removeAll = new JButton("Remover Todo");
        playButton = new JButton(playIcon);
        hover(playButton);
        playButton.setBounds(150,300,100,75);
        pauseButton = new JButton(pauseIcon);
        hover(pauseButton);
        pauseButton.setBounds(150,300,100,75);
        stopButton = new JButton(stopIcon);
        hover(stopButton);
        stopButton.setBounds(150,380,100,75);
        resumeButton = new JButton(playIcon);
        hover(resumeButton);
        resumeButton.setBounds(150,300,100,75);
        forward = new JButton(forwardIcon);
        hover(forward);
        forward.setBounds(150,460,100,75);
        backward = new JButton(backwardIcon);
        hover(backward);
        backward.setBounds(150,540,100,75);
        forward.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(clip != null) {
                    clip.stop();
                   
                    long newMicrosecondPosition = clip.getMicrosecondPosition() + 500000; 

                    if (newMicrosecondPosition > clip.getMicrosecondLength()) {
                        newMicrosecondPosition = clip.getMicrosecondLength();
                    }
                    clip.setMicrosecondPosition(newMicrosecondPosition);
                    if(!paused) {
                        clip.start();
                        skip = true;
                    }
                }
                SwingUtilities.invokeLater(panel::requestFocusInWindow);
            }
        });
        add(forward);
        backward.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(clip != null) {
                    clip.stop();
                   
                    long newMicrosecondPosition = clip.getMicrosecondPosition() - 500000; 

                    if (newMicrosecondPosition < 0) {
                        newMicrosecondPosition = 0;
                    }
                    clip.setMicrosecondPosition(newMicrosecondPosition);
                    if(!paused) {
                        clip.start();
                        back = true;
                    }
                }
                SwingUtilities.invokeLater(panel::requestFocusInWindow);
            }
        });
        add(backward);
        
        save = new JButton("Crear");
        save.setBounds(width - 300, ypos - 75 ,150,50);
        hover(save);
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exit = true;
                try {
                    writeChart();
                } catch (Exception ex) {}
                
                SwingUtilities.invokeLater(panel::requestFocusInWindow);
            }
        });
        add(save);
        
        durationLabel = new JLabel("Duracion: ");
        durationLabel.setBounds(width - 325, 50 ,300,100);
        durationLabel.setFont(new Font("Verdana", Font.BOLD, 18));
        durationLabel.setForeground(new Color(255, 255, 255)); 
        durationLabel.setOpaque(false);
        add(durationLabel);
        timeLabel = new JLabel("Tiempo transcurrido: ");
        timeLabel.setBounds(width - 400, 100 ,300,100);
        timeLabel.setFont(new Font("Verdana", Font.BOLD, 18));
        timeLabel.setForeground(new Color(255, 255, 255)); 
        timeLabel.setOpaque(false);
        add(timeLabel);
        initRetButton();
        initSongButton();
        initPropButton();
        initRemove(panel);
        initRemoveAll(panel);
        initPlayButton();
        initStopButton();
        initPauseButton();
        initResumeButton(panel);
        initPlayerButtons();
        KB();
    }

    public void setSongName(String name) {
        this.songName = name;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        drawLines(g, xpos, ypos);
        if(!notes.isEmpty()) {
            Iterator<GameNote> iterator = notes.iterator();
            while (iterator.hasNext()) {
                GameNote element = iterator.next();
                if(element.getY() > 0 && element.getY() <= height && element.isInScreen()) {
                    g.setColor(element.getBorderColor());
                    g.fillOval(element.getX(), element.getY(), 50, 35);
                }
            }
        }
    }

    private void drawLines(Graphics g, int xpos, int ypos) {
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 0, getWidth(), getHeight());
        
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
                            greenNote.setReleased(true);
                            notes.add(new GameNote(xpos, ypos, new Color(54, 58, 59), new Color(8, 200, 3), (int) elapsedTime,0));
                            System.out.println("Nota verde añadida en " + elapsedTime);
                            break;
                        case KeyEvent.VK_S:
                            redNote.setReleased(true);
                            notes.add(new GameNote(xpos + 75,ypos,  new Color(54, 58, 59), new Color(163, 24, 24), (int) elapsedTime,1));
                            System.out.println("Nota roja añadida en " + elapsedTime);
                            break;
                        case KeyEvent.VK_D:
                            yellowNote.setReleased(true);
                            notes.add(new GameNote(xpos + 150, ypos,  new Color(54, 58, 59), new Color(254, 254, 53), (int) elapsedTime,2));
                            System.out.println("Nota amarilla añadida en " + elapsedTime);
                            break;
                        case KeyEvent.VK_G:
                            blueNote.setReleased(true);
                            notes.add(new GameNote(xpos + 225, ypos, new Color(54, 58, 59), new Color(63, 162, 211), (int) elapsedTime,3));
                            System.out.println("Nota azul añadida en " + elapsedTime);
                            break;
                        case KeyEvent.VK_H:
                            orangeNote.setReleased(true);
                            notes.add(new GameNote(xpos + 300, ypos, new Color(54, 58, 59), new Color(217, 147, 53), (int) elapsedTime,4));
                            System.out.println("Nota naranja añadida en " + elapsedTime);
                            break;
                    }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                        case KeyEvent.VK_A:
                            greenNote.setReleased(false);
                            break;
                        case KeyEvent.VK_S:
                            redNote.setReleased(false);
                            break;
                        case KeyEvent.VK_D:
                            yellowNote.setReleased(false);
                            break;
                        case KeyEvent.VK_G:
                            blueNote.setReleased(false);
                            break;
                        case KeyEvent.VK_H:
                            orangeNote.setReleased(false);
                            break;
                    }
            }
        };
        this.addKeyListener(kb);
    }
    
    public void initPlayerButtons() {
        greenNote.setBounds(xpos, ypos, 50, 35);
        redNote.setBounds(xpos + 75, ypos, 50, 35);
        yellowNote.setBounds(xpos + 150, ypos, 50, 35);
        blueNote.setBounds(xpos + 225, ypos, 50, 35);
        orangeNote.setBounds(xpos + 300, ypos, 50, 35);
        add(greenNote);
        add(redNote);
        add(yellowNote);
        add(blueNote);
        add(orangeNote);
    }
    
    public void initIcon(JButton button, ImageIcon icon) {
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);;
        button.setOpaque(false);
    }
    
    public void initPauseButton() {
        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(clip != null) {
                    clip.stop();
                    pausePosition = (long) (elapsedTime * 1000000f);
                    remove(pauseButton);
                    add(resumeButton);
                    paused = true;
                }
            }
        });

    }
    
    public void initResumeButton(Editor editor) {
        resumeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    if(clip != null) 
                        clip.start();
                    startTime = System.nanoTime() - pausePosition;
                    remove(resumeButton);
                    add(pauseButton);
                    paused = false;
                    SwingUtilities.invokeLater(editor::requestFocusInWindow);
            }
        });
    }
    
    public void initPlayButton() {
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    File audioFile = new File(songPath);
                    AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
                    clip = AudioSystem.getClip();
                    clip.open(audioStream);
                    
                    clip.start();
                    remove(playButton);
                    add(pauseButton);
                    exit = false;
                    paused = false;
                    play();
                    
                } catch(Exception ex) {
                    System.out.println("Error with playing sound.");
                }
            }
        });
        add(playButton);
    }
    public void initStopButton() {
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(clip != null) {
                    clip.close();               
                    remove(resumeButton);
                    remove(pauseButton);
                    add(playButton);
                    exit = true;
                    for(GameNote element : notes) {
                        element.setY(0);
                        element.setInScreen(false);
                    }
                }
            }
        });
        add(stopButton);
    }
    
    public void initRemove(Editor editor) {
        remove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!notes.isEmpty()) 
                    notes.removeLast();
                SwingUtilities.invokeLater(editor::requestFocusInWindow);
            }
        });
        remove.setBounds( 50, ypos,150,50);
        hover(remove);
        add(remove);
    }
    
    public void initRemoveAll(Editor editor) {
        removeAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!notes.isEmpty()) 
                    notes.clear();
                SwingUtilities.invokeLater(editor::requestFocusInWindow);
            }
        });
        removeAll.setBounds(225,ypos,150,50);
        hover(removeAll);
        add(removeAll);
    }
    
    public void initPropButton() {
        songPropierties.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame frame = new JFrame("Song Propierties");
                propiertiesPanel = new SongPropierties(frame,panel);
                frame.setSize(400, 300);
                frame.add(propiertiesPanel);
                frame.setResizable(false);
                frame.setVisible(true);
            }
        });
        songPropierties.setBounds( 50, 125,150,50);
        hover(songPropierties);
        add(songPropierties);
    }
    
    public void initSongButton() {
        addSongButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    selectAudio();
                } catch (Exception ex) {
                }
            }
        });
        addSongButton.setBounds( 50, 50,150,50);
        hover(addSongButton);
        add(addSongButton);

    }
    
    public void initRetButton() {
        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exit = true;
                if(clip != null)
                    clip.stop();
                mainMenu.resetMenu(frame);
                mainMenu.restartAudio();
                Toolkit toolkit = Toolkit.getDefaultToolkit();
                Image image = toolkit.createImage(new byte[0]);
                Cursor transparentCursor = toolkit.createCustomCursor(image, new Point(0, 0), "invisibleCursor");
                
                frame.setCursor(transparentCursor);
            }
        });
        returnButton.setBounds(width - 300, ypos,150,50);
        hover(returnButton);
        add(returnButton);

    }
    
    public void hover(JButton button) {
 
        button.setFont(new Font("Verdana", Font.BOLD, 18));
        button.setForeground(new Color(255, 255, 255));

        button.setBackground(Color.GRAY);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255), 2));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(30, 144, 255));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.GRAY);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(new Color(0, 76, 153));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                button.setBackground(new Color(30, 144, 255)); 
            }
        });
    }
    
    public void selectAudio() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setTitle("Selecciona una cancion");

            if (fileChooser.showOpenDialog(null)) {
                File selectedFile = fileChooser.getSelectedFile();
                if (selectedFile.getName().toLowerCase().endsWith(".wav")) {
                    if (fileChooser.getSelectedFile() != null) {
                        songPath = fileChooser.getSelectedFile().getAbsolutePath();
                        fileName = fileChooser.getSelectedFile().getName();
                        File audioFile = new File(songPath);
                        AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
                        clip = AudioSystem.getClip();
                        clip.open(audioStream);
                        long duration = clip.getMicrosecondLength() /1000000;
                        durationLabel.setText("Duracion: " + duration + "s");

                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a WAV file.", "Invalid File Type", JOptionPane.ERROR_MESSAGE);
                }
            }
    }
    
    public void writeChart() throws IOException {
        System.out.println(fileName);
        String newName = fileName.replace(".wav", "");
        String newfile = newName + ".chart";
        String path = "src/main/java/Resources/Charts/" + newfile;
        String newSongPath = "src/main/java/Resources/Songs/" + fileName;
        System.out.println(path);
        
        try {
            java.nio.file.Files.copy(java.nio.file.Paths.get(songPath), java.nio.file.Paths.get(newSongPath), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        
        String songInfo = "[Song]\n"
                        + "{\n"
                        + " Name = \"" + newName + "\"\n"
                        + " Artist = \"" + artist + "\"\n"
                        + " Difficulty = " + difficulty + "\n"
                        + " Genre = \"" + genre + "\"\n"
                        + " Players = 1\n"
                        + "}\n\n";
        String notesInfo = "[Player]\n"
                     + "{\n";
        
        try {
            for(GameNote element : notes) {
                notesInfo += element.toString();
            }
            notesInfo += "}";
        }catch(Exception e) {
            System.out.println("No hay notas");
            
        }
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
           
            writer.write(songInfo);
            writer.write(notesInfo);
        } catch (IOException e) {}
    }
    
    public void play() {
        Thread gameThread = new Thread(new Runnable() {
            @Override
            public void run() {
                startTime = System.nanoTime();
                System.out.println(dt);
                long loopStartTime;
                long loopEndTime;
                long loopDuration;
                long sleepTime;
                while(!exit) {
                    loopStartTime = System.nanoTime();
                    elapsedTime = (System.nanoTime() - startTime)/1000000;
                    if (!paused) {
                        timeLabel.setText("Tiempo transcurrido: " + elapsedTime/1000f + "s" );
                        for (GameNote element : notes) {
                            if(!element.isInScreen()) { 
                                if (elapsedTime >= element.getTime() - dt) 
                                    element.setInScreen(true);
                                    
                            } else {
                                element.physics(ypos, dt);
                                if (skip) {
                                        element.setY(element.getY() + GameNote.getSpeed() * 100);
                                }
                                if (back) {
                                    element.setY(element.getY() - GameNote.getSpeed() * 100);
                                    if (element.getY() < 0){
                                        element.setInScreen(false);
                                    }
                                }
                            }
                        }
                        if (skip) {
                            startTime -= 500000000; 
                            elapsedTime += 500; 
                        }
                        if (back) {
                             startTime += 500000000; 
                             elapsedTime -= 500;
                            
                            if (elapsedTime < 0) {
                                startTime = System.nanoTime();
                            }
                        }
                        skip = false;
                        back = false;
                    }
                    draw();
                    loopEndTime = System.nanoTime();
                    loopDuration = loopEndTime - loopStartTime;
                    sleepTime = 5000000 - loopDuration;
                    try {
                        if (sleepTime > 0) {
                            TimeUnit.NANOSECONDS.sleep(sleepTime);
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        gameThread.start();
        SwingUtilities.invokeLater(this::requestFocusInWindow);
    }
    
    public void draw() {
        try{
            SwingUtilities.invokeAndWait(this::repaint);
        }catch(Exception e) {}
    }
     
}