package Components.Menu;

import Components.SongList.SongList;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Settings extends JPanel {
    private GameMenu gameMenu;
    private JFrame frame;
    Menu3D menu;
    int WIDTH;
    int HEIGHT;
    public Settings(GameMenu mainMenu, JFrame frame, int WIDTH, int HEIGHT) {
        gameMenu = mainMenu;
        this.frame = frame;
        this.WIDTH = WIDTH;
        this.HEIGHT = HEIGHT;
        setBackground(new Color(43, 45, 48));
        setLayout(null);
        menu = new Menu3D();
        menu.items.clear();
        menu.addMenuItem("Modificar velocidad");
        menu.addMenuItem("Dificultad CPU");
        menu.addMenuItem("Mapeo de teclas");
        menu.setFocusable(true);
        menu.requestFocusInWindow();
        menu.addEvent(index -> {
            switch (index) {
                case 0:
                    switchToSpeedSettings(frame);
                    break;
                case 1:
                    switchToCPUSettings(frame);
                    break;
                case 2:
                    switchToKeyBindingSettings(frame);
                    break;
            }
        });

        int menuHeight = menu.getItemsSize() * menu.getMenuHeight() + 75;
        int menuWidth = WIDTH / 3;
        

        menu.setBounds((WIDTH - menuWidth) / 2, (HEIGHT - menuHeight) / 2, menuWidth, menuHeight);
        add(menu);

        menu.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    
                }
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    frame.getContentPane().removeAll();
                    gameMenu.resetMenu(frame);
                }
            }
        });
    }
    public void switchToSpeedSettings(JFrame frame) {
        SpeedSettings settings = new SpeedSettings(frame,gameMenu, WIDTH, HEIGHT); 
        frame.getContentPane().removeAll();
        frame.getContentPane().add(settings);
        frame.revalidate();
        frame.repaint();
    }
    
    public void switchToCPUSettings(JFrame frame) {
        CPUSettings settings = new CPUSettings(frame,gameMenu, WIDTH, HEIGHT);
        frame.getContentPane().removeAll();
        frame.getContentPane().add(settings);
        frame.revalidate();
        frame.repaint();
    }

    public void switchToKeyBindingSettings(JFrame frame) {
        KeyBindingSettings settings = new KeyBindingSettings(gameMenu, frame, WIDTH, HEIGHT);
        frame.getContentPane().removeAll();
        frame.getContentPane().add(settings);
        frame.revalidate();
        frame.repaint();
    }
}
