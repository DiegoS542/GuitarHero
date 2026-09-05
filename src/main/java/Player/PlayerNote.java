package Player;

import java.awt.*;

public class PlayerNote extends Note {
    private boolean released;
    private boolean clicked;
    private Color colorClick;
    private String keyLabel;
    private boolean showKeyLabel = true;

    public PlayerNote(Color color, Color borderColor, Color colorClick) {
        super(color, borderColor);
        this.released = false;
        this.colorClick = colorClick;
        this.clicked = false;
    }

    public Color getColorClick() {
        return colorClick;
    }

    public void setColorClick(Color colorClick) {
        this.colorClick = colorClick;
    }

    public boolean isReleased() {
        return released;
    }

    public void setReleased(boolean released) {
        this.released = released;
        if (released) {
            setBackground(colorClick);
        } else {
            setBackground(color);
        }
        repaint();
    }

    public boolean isClicked() {
        return clicked;
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }

    public void setKeyLabel(String keyLabel) {
        this.keyLabel = keyLabel;
    }

    public void setShowKeyLabel(boolean showKeyLabel) {
        this.showKeyLabel = showKeyLabel;
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //  Paint Border
        g2.setColor(borderColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        g2.setColor(getBackground());

        //  Border set 2 Pix
        g2.fillRoundRect(4, 4, getWidth() - 8, getHeight() - 8, radius, radius);
        super.paintComponent(g);

        if (keyLabel != null && showKeyLabel) {
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setFont(new Font("Verdana", Font.BOLD, 16));
            g2.setColor(Color.WHITE);
            FontMetrics fm = g2.getFontMetrics();
            int tx = (getWidth() - fm.stringWidth(keyLabel)) / 2;
            int ty = (getHeight() + fm.getAscent()) / 2 - 2;
            g2.drawString(keyLabel, tx, ty);
        }
    }
}
