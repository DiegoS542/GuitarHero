import Components.Menu.GameMenu;
import Player.Tab;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main {
    public static void main(String[] args) throws Exception {
        Tab.loadKeyBindings();

        JFrame game = new JFrame("Game");
        game.setUndecorated(true);
        game.setExtendedState(JFrame.MAXIMIZED_BOTH);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Image image = toolkit.createImage(new byte[0]);
        Cursor transparentCursor = toolkit.createCustomCursor(image, new Point(0, 0), "invisibleCursor");
        game.setCursor(transparentCursor);


        game.setPreferredSize(new Dimension((int) screenSize.getWidth(), (int) screenSize.getHeight()));
        game.setResizable(false);
        game.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        GameMenu menu = new GameMenu(game,(int) screenSize.getWidth(),(int) screenSize.getHeight());

        game.getContentPane().add(menu);
        game.pack();
        game.setLocationRelativeTo(null);
        game.setVisible(true);
    }
}

