package Connection.Socket;

import Components.Menu.GameMenu;
import Components.Scenes.OnePlayerScene;
import Utilities.Button;
import Utilities.Song;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import Components.TextField.TextField;
import java.awt.*;

public class Client extends JPanel {
    int PUERTO_DEL_CLIENTE = 5000;
    int PUERTO_DEL_SERVIDOR = 5000;
    TextField server;
    Button connect;
    String ip;
    DatagramSocket dgSocket;
    DatagramPacket datagram;
    InetAddress destination = null;
    GameMenu gameMenu;
    JFrame frame;
    private volatile boolean gameRunning = true;

    public Client(GameMenu gameMenu, JFrame frame, int WIDTH, int HEIGHT) {
        this.frame = frame;
        this.gameMenu = gameMenu;
        frame.setCursor(Cursor.getDefaultCursor());
        setBackground(new Color(43, 45, 48));
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setLayout(null);
        ip = "";

        JLabel title = new JLabel("Ip del servidor:");
        title.setBounds(WIDTH / 2 - 50, (HEIGHT / 2) - 100, 200, 50);
        title.setForeground(Color.WHITE);
        add(title);

        server = new TextField();
        server.setBounds(WIDTH / 2 - 100, (HEIGHT / 2) - 50, 200, 50);
        add(server);

        connect = new Button("Conectar");
        connect.setBounds(WIDTH / 2 - 100, (HEIGHT / 2) + 25, 200, 50);
        add(connect);

        JLabel lblServer = new JLabel("Soy el servidor");
        lblServer.setBounds(WIDTH / 2 - 50, (HEIGHT / 2) + 125, 200, 50);
        lblServer.setForeground(Color.WHITE);
        add(lblServer);

        lblServer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleServer(frame);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                lblServer.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                lblServer.setCursor(Cursor.getDefaultCursor());
            }
        });

        connect.addActionListener(e -> {
            try {
                ip = server.getText();
                handleConnection(frame);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    gameMenu.resetMenu(frame);
                    gameMenu.restartAudio();
                    Toolkit toolkit = Toolkit.getDefaultToolkit();
                    Image image = toolkit.createImage(new byte[0]);
                    Cursor transparentCursor = toolkit.createCustomCursor(image, new Point(0, 0), "invisibleCursor");
                    frame.setCursor(transparentCursor);
                }
            }
        });

        SwingUtilities.invokeLater(this::requestFocusInWindow);
    }

    public void handleServer(JFrame frame) {
        try {
            JOptionPane.showMessageDialog(null, "Esperando al cliente...\n Tu ip es: " + InetAddress.getLocalHost().getHostAddress());
            new Server(frame, gameMenu);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleConnection(JFrame frame) throws Exception {
        dgSocket = new DatagramSocket(PUERTO_DEL_CLIENTE);
        destination = InetAddress.getByName(ip);

        System.out.println("Cliente UDP activo");

        String request = "REQUEST_SONG_LIST" + "\0";
        byte[] msg = request.getBytes();
        datagram = new DatagramPacket(msg, msg.length, destination, PUERTO_DEL_SERVIDOR);
        dgSocket.send(datagram);

        byte[] buffer = new byte[8192];
        datagram = new DatagramPacket(buffer, buffer.length);
        dgSocket.receive(datagram);
        ByteArrayInputStream bis = new ByteArrayInputStream(datagram.getData());
        ObjectInputStream ois = new ObjectInputStream(bis);
        Song song = (Song) ois.readObject();

        SwingUtilities.invokeLater(() -> {
            frame.getContentPane().removeAll();
            OnePlayerScene onePlayerScene = null;
            try {
                onePlayerScene = new OnePlayerScene(gameMenu, frame, song);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (UnsupportedAudioFileException e) {
                throw new RuntimeException(e);
            } catch (LineUnavailableException e) {
                throw new RuntimeException(e);
            }
            frame.add(onePlayerScene);
            frame.revalidate();
            frame.repaint();

            new Thread(() -> {
                while (gameRunning) {
                    try {
                        // Enviar el estado del juego al servidor
                        String estado = "CONTINUA" + "\0"; // Placeholder, reemplazar con lógica real
                        byte[] estadoMsg = estado.getBytes();
                        DatagramPacket estadoPaquete = new DatagramPacket(estadoMsg, estadoMsg.length, destination, PUERTO_DEL_SERVIDOR);
                        dgSocket.send(estadoPaquete);

                        // Recibir el estado del juego del servidor
                        byte[] estadoBuffer = new byte[1024];
                        DatagramPacket estadoRecibido = new DatagramPacket(estadoBuffer, estadoBuffer.length);
                        dgSocket.receive(estadoRecibido);
                        String estadoServidor = new String(estadoRecibido.getData()).split("\0")[0];
                        System.out.println("Estado del servidor: " + estadoServidor);

                        // Aquí puedes añadir la lógica para manejar el estado del juego según la respuesta del servidor

                        Thread.sleep(500); // Esperar 500 ms antes de enviar/recibir nuevamente
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        });
    }
}
