package Components.Menu;

import Player.Tab;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyBindingSettings extends JPanel {
    private final GameMenu gameMenu;
    private final JFrame frame;
    private final int WIDTH;
    private final int HEIGHT;
    private final String[] laneNames = {"Verde", "Rojo", "Amarillo", "Azul", "Naranja"};
    private int selected = 0;
    private boolean listening = false;

    public KeyBindingSettings(GameMenu mainMenu, JFrame frame, int WIDTH, int HEIGHT) {
        this.gameMenu = mainMenu;
        this.frame = frame;
        this.WIDTH = WIDTH;
        this.HEIGHT = HEIGHT;

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(new Color(43, 45, 48));
        setLayout(null);
        setFocusable(true);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (listening) {
                    if (e.getKeyCode() != KeyEvent.VK_ESCAPE) {
                        applyBinding(e.getKeyCode());
                    }
                    listening = false;
                    repaint();
                    return;
                }
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        selected = (selected + 9) % 10;
                        repaint();
                        break;
                    case KeyEvent.VK_DOWN:
                        selected = (selected + 1) % 10;
                        repaint();
                        break;
                    case KeyEvent.VK_ENTER:
                        listening = true;
                        repaint();
                        break;
                    case KeyEvent.VK_ESCAPE:
                        Settings settings = new Settings(gameMenu, frame, KeyBindingSettings.this.WIDTH, KeyBindingSettings.this.HEIGHT);
                        frame.getContentPane().removeAll();
                        frame.getContentPane().add(settings);
                        frame.revalidate();
                        frame.repaint();
                        break;
                }
            }
        });

        SwingUtilities.invokeLater(this::requestFocusInWindow);
    }

    private void applyBinding(int keyCode) {
        boolean isPlayer1 = selected < 5;
        int lane = selected % 5;
        int[] mine = isPlayer1 ? Tab.getPlayer1Keys() : Tab.getPlayer2Keys();
        int[] other = isPlayer1 ? Tab.getPlayer2Keys() : Tab.getPlayer1Keys();

        for (int k : other) {
            if (k == keyCode) return;
        }
        for (int i = 0; i < mine.length; i++) {
            if (i != lane && mine[i] == keyCode) return;
        }

        if (isPlayer1) {
            Tab.setPlayer1Key(lane, keyCode);
        } else {
            Tab.setPlayer2Key(lane, keyCode);
        }
        Tab.saveKeyBindings();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2.setFont(new Font("Arial Black", Font.BOLD, 40));
        g2.setColor(Color.WHITE);
        String title = "Mapeo de teclas";
        FontMetrics tfm = g2.getFontMetrics();
        g2.drawString(title, (getWidth() - tfm.stringWidth(title)) / 2, 90);

        int colWidth = 260;
        int startX = (getWidth() - colWidth * 2 - 40) / 2;
        int p1X = startX;
        int p2X = startX + colWidth + 40;
        int startY = 180;
        int rowHeight = 45;

        g2.setFont(new Font("Verdana", Font.BOLD, 18));
        g2.setColor(new Color(200, 200, 200));
        g2.drawString("Jugador 1", p1X, startY - 25);
        g2.drawString("Jugador 2", p2X, startY - 25);

        int[] p1Keys = Tab.getPlayer1Keys();
        int[] p2Keys = Tab.getPlayer2Keys();

        for (int i = 0; i < 5; i++) {
            drawRow(g2, p1X, startY + i * rowHeight, laneNames[i], KeyEvent.getKeyText(p1Keys[i]), selected == i, listening && selected == i);
            drawRow(g2, p2X, startY + i * rowHeight, laneNames[i], KeyEvent.getKeyText(p2Keys[i]), selected == 5 + i, listening && selected == 5 + i);
        }

        g2.setFont(new Font("Verdana", Font.PLAIN, 16));
        g2.setColor(new Color(180, 180, 180));
        String hint = "Flechas: moverse   Enter: reasignar   Esc: volver";
        g2.drawString(hint, (getWidth() - g2.getFontMetrics().stringWidth(hint)) / 2, getHeight() - 40);
    }

    private void drawRow(Graphics2D g2, int x, int y, String lane, String keyText, boolean isSelected, boolean isListening) {
        if (isSelected) {
            g2.setColor(new Color(46, 127, 255, 120));
            g2.fillRoundRect(x - 10, y - 26, 260, 36, 8, 8);
        }
        g2.setFont(new Font("Verdana", Font.PLAIN, 20));
        g2.setColor(Color.WHITE);
        g2.drawString(lane + ":", x, y);
        g2.setFont(new Font("Verdana", Font.BOLD, 20));
        g2.setColor(isListening ? new Color(255, 215, 0) : new Color(150, 220, 255));
        g2.drawString(isListening ? "..." : keyText, x + 140, y);
    }
}
