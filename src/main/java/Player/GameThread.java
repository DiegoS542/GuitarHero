
package Player;


import Components.Menu.Menu3D;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class GameThread extends Thread {
    private boolean paused = false;
    boolean exit;
    Tab tab;
    
    
    public GameThread(Tab tab) {
        System.out.println("GameThread creado");
        this.tab = tab;
        this.exit = false;
        this.paused = false;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setExit(boolean exit) {
        this.exit = exit;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }


    public Tab getTab() {
        return tab;
    }
    
    
    @Override
    public void run() {
        while (!exit) {
            tab.checkSongEnd();
            tab.repaint();
                try {
                    TimeUnit.NANOSECONDS.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
    }

}
