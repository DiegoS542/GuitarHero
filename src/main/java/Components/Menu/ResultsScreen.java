package Components.Menu;

import Player.Player;

import javax.swing.*;
import java.awt.*;

public class ResultsScreen extends JPanel {

    private final Player player;
    private final Player player2;
    private final int WIDTH;

    public ResultsScreen(GameMenu mainMenu, JFrame frame, int WIDTH, int HEIGHT, Player player, Player player2) {
        this.player = player;
        this.player2 = player2;
        this.WIDTH = WIDTH;

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setLayout(null);
        setBackground(new Color(5, 5, 5));

        Menu3D menu = new Menu3D();
        menu.addMenuItem("Continuar");
        int menuWidth = WIDTH / 4;
        int menuHeight = menu.getItemsSize() * menu.getMenuHeight() + 75;
        menu.setBounds((WIDTH - menuWidth) / 2, HEIGHT - menuHeight - 60, menuWidth, menuHeight);
        menu.addEvent(index -> mainMenu.resetMenu(frame));
        add(menu);

        SwingUtilities.invokeLater(menu::requestFocusInWindow);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        float[] fractions = {0.0f, 0.55f, 1.0f};
        Color[] colors = {new Color(5, 5, 5), new Color(20, 10, 35), new Color(60, 5, 20)};
        g2.setPaint(new LinearGradientPaint(0, 0, 0, getHeight(), fractions, colors));
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setFont(new Font("Arial Black", Font.BOLD, 60));
        FontMetrics titleFm = g2.getFontMetrics();
        String title = "¡Canción terminada!";
        int titleX = (getWidth() - titleFm.stringWidth(title)) / 2;
        int titleY = 160;
        g2.setColor(new Color(0, 0, 0, 200));
        g2.drawString(title, titleX + 3, titleY + 3);
        g2.setColor(Color.WHITE);
        g2.drawString(title, titleX, titleY);

        if (player2 == null) {
            drawScore(g2, "Puntaje: " + player.getScore(), WIDTH / 2, 260);
        } else {
            String winner = player.getScore() > player2.getScore() ? "Jugador 1"
                    : player2.getScore() > player.getScore() ? "Jugador 2" : "Empate";
            drawScore(g2, "Jugador 1 - Puntaje: " + player.getScore(), WIDTH / 2, 260);
            drawScore(g2, "Jugador 2 - Puntaje: " + player2.getScore(), WIDTH / 2, 310);
            g2.setFont(new Font("Verdana", Font.BOLD, 28));
            FontMetrics fm = g2.getFontMetrics();
            String winnerText = "Empate".equals(winner) ? "¡Empate!" : "Gana " + winner + "!";
            g2.setColor(new Color(255, 215, 0));
            g2.drawString(winnerText, (getWidth() - fm.stringWidth(winnerText)) / 2, 370);
        }
    }

    private void drawScore(Graphics2D g2, String text, int centerX, int y) {
        g2.setFont(new Font("Verdana", Font.BOLD, 26));
        FontMetrics fm = g2.getFontMetrics();
        g2.setColor(Color.WHITE);
        g2.drawString(text, centerX - fm.stringWidth(text) / 2, y);
    }
}
