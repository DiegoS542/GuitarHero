package Components.Menu;

import Components.Scenes.OnePlayerScene;
import Components.Scenes.TwoPlayerScene;
import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerState;

import java.awt.*;

public class PauseMenu extends Menu3D{
    public PauseMenu() {
        super();
        setBackground(new Color(0, 0, 0, 0.5f));
        items.clear();
        addMenuItem("Reanudar");
        addMenuItem("Reiniciar");
        addMenuItem("Lista de Canciones");
        addMenuItem("Salir");
        controllers = new ControllerManager();

        /*if (controllers.getNumControllers() != 0){
            startControllerListener();
        }*/

    }
    private void startControllerListener() {
        new Thread(() -> {
            controllers.initSDLGamepad();
            while (true) {
                ControllerState currState = controllers.getState(0);
                if (currState.isConnected) {
                    if (currState.dpadUpJustPressed) {
                        if (pressedIndex > 0) {
                            pressedIndex--;
                            repaint();
                        }
                    }
                    if (currState.dpadDownJustPressed) {
                        if (pressedIndex < items.size() - 1) {
                            pressedIndex++;
                            repaint();
                        }
                    }
                    if (currState.startJustPressed || currState.aJustPressed) {
                        items.get(pressedIndex).getAnimator().show();
                        hideMenu(pressedIndex);
                        runEvent();
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
}
