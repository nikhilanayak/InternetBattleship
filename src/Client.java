package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Client implements ActionListener, MouseListener {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 800;
    ArrayList<Integer> shipLengths = new ArrayList<Integer>();



    boolean firstClick = false;

    int startingX = 0;
    int startingY = 0;

    // Socket variables
    Socket socket;
    DataOutputStream out;
    DataInputStream in;

    Renderer panel = new Renderer();

    // Java Swing components
    JFrame frame;

    JButton readyUpButton = new JButton("Ready Up");
    boolean readiedUp = false;

    Container west;

    enum GameState{
        READYUP,
        SETSHIPS,
        BATTLE,
        GAMEOVER,
    }

    GameState gameState = GameState.READYUP;


    /**
     * The entry point of the program. Creates a new instance of client
     * @param args The command-line arguments created when the program is run.
     * @throws IOException
     */
    public static void main(String[] args){
        new Client();
    }

    /**
     * The constructor for the client. Initializes Java Swing elements and connects to server
     */
    public Client(){
        // Initialize Java Swing components
        frame = new JFrame();
        frame.setSize(WIDTH, HEIGHT);
        frame.setLayout(new BorderLayout());
        west = new Container();
        west.setLayout(new GridLayout(4, 1));
        west.add(readyUpButton);


        shipLengths.add(5);
        shipLengths.add(4);
        shipLengths.add(3);
        shipLengths.add(3);
        shipLengths.add(2);

        readyUpButton.setBackground(Color.RED);
        readyUpButton.addActionListener(this);
        readyUpButton.setPreferredSize(new Dimension(150,25));
        panel.addMouseListener(this);
        panel.setSize(300,300);
        panel.repaint();

        frame.add(panel, BorderLayout.CENTER);
        frame.add(west, BorderLayout.WEST);


        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        connect("127.0.0.1", 1337);
        run();
    }

    /**
     * Invoked when an action occurs.
     *
     * @param event the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent event){
        if(gameState == GameState.READYUP) {
            if (event.getSource().equals(readyUpButton)) {
                readiedUp = !readiedUp;
                if (readiedUp) {
                    readyUpButton.setBackground(Color.GREEN);
                    readyUpButton.setText("Cancel Ready Up");
                } else {
                    readyUpButton.setBackground(Color.RED);
                    readyUpButton.setText("Ready Up");
                }
            }
        }

    }

    /**
     * Connects To The {@link Server} Socket
     * @param ip the IP address that you want to connect to
     * @param port the port you want to connect to
     *
     */
    public void connect(String ip, int port){
        try{
            socket = new Socket(ip, port);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
        }
        catch(IOException e){
            System.out.println("Could Not Initialize Client Socket");
        }
    }

    /**
     * Listens for input from the server, parses it, and changes the state and UI accordingly
     */
    public void run(){
        boolean running = true;
        //
        Scanner scanner = new Scanner(System.in);

        while(running) {
            try{
                if(in.available() > 0){
                    String message = in.readUTF();
                    switch(message){
                        case "SETSHIPS":
                            gameState = GameState.SETSHIPS;
                            break;

                    }
                }
                //let server know if readied up
                if(readiedUp && readyUpButton.isEnabled()){
                    out.writeUTF("READY");
                }
                if (!readiedUp && readyUpButton.isEnabled()){
                    out.writeUTF("NOTREADY");
                }

                if(gameState != GameState.READYUP){
                    readyUpButton.setEnabled(false);
                }
            }
            catch(IOException exception){
                exception.printStackTrace();
            }
        }

        try
        {
            in.close();
            out.close();
            socket.close();
        }
        catch(IOException exception)
        {
            exception.printStackTrace();
        }
    }

    /**
     * Invoked when the mouse button has been clicked (pressed
     * and released) on a component.
     *
     * @param e the event to be processed
     */
    @Override
    public void mouseClicked(MouseEvent e) {

    }

    /**
     * Invoked when a mouse button has been pressed on a component.
     *
     * @param e the event to be processed
     */
    @Override
    public void mousePressed(MouseEvent e) {
        if(gameState == GameState.SETSHIPS) {
            if (!firstClick) {
                startingX = Math.min(panel.cells.length - 1, (int) (e.getX() / (panel.getWidth() / Renderer.BOARD_SIZE)));
                startingY = Math.min(panel.cells[0].length - 1, (int) (e.getY() / (panel.getHeight() / Renderer.BOARD_SIZE)));

                panel.cells[startingX][startingY] = true;

                firstClick = true;
            } else {
                int endingX = Math.min(panel.cells.length - 1, (int) (e.getX() / (panel.getWidth() / Renderer.BOARD_SIZE)));
                int endingY = Math.min(panel.cells[0].length - 1, (int) (e.getY() / (panel.getHeight() / Renderer.BOARD_SIZE)));

                if (startingY == endingY && shipLengths.contains(Math.abs(endingX - startingX) + 1)) {
                    shipLengths.remove((Integer) Math.abs(endingX - startingX + 1));
                    for (int i = Math.min(endingX, startingX); i <= Math.max(endingX, startingX); i++) {
                        panel.cells[i][endingY] = true;
                    }
                    frame.repaint();
                    if (!shipLengths.isEmpty())
                        JOptionPane.showMessageDialog(frame, "Add another ship of length: " + shipLengths.toString());
                    panel.shipPositions.add(new Rectangle(Math.min(endingX, startingX), startingY, Math.abs(endingX - startingX) + 1, 1));

                } else if (startingX == endingX && shipLengths.contains(Math.abs(endingY - startingY) + 1)) {
                    shipLengths.remove((Integer) Math.abs(endingY - startingY + 1));
                    for (int i = Math.min(endingY, startingY); i <= Math.max(endingY, startingY); i++) {
                        panel.cells[endingX][i] = true;
                    }
                    frame.repaint();
                    if (!shipLengths.isEmpty())
                        JOptionPane.showMessageDialog(frame, "Add another ship of length: " + shipLengths.toString());
                    panel.shipPositions.add(new Rectangle(startingX, Math.min(endingY, startingY), 1, Math.abs(endingY - startingY) + 1));
                } else {
                    panel.cells[startingX][startingY] = false;
                    if (!shipLengths.isEmpty())
                        JOptionPane.showMessageDialog(frame, "Invalid - Add another ship of length: " + shipLengths.toString());
                }

                firstClick = false;
            }
            frame.repaint();
        }
    }



    /**
     * Invoked when a mouse button has been released on a component.
     *
     * @param e the event to be processed
     */
    @Override
    public void mouseReleased(MouseEvent e) {


    }

    /**
     * Invoked when the mouse enters a component.
     *
     * @param e the event to be processed
     */
    @Override
    public void mouseEntered(MouseEvent e) {

    }

    /**
     * Invoked when the mouse exits a component.
     *
     * @param e the event to be processed
     */
    @Override
    public void mouseExited(MouseEvent e) {

    }


}



