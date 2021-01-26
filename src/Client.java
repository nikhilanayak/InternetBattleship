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
/**
 * The client class that renders the Java Swing components and sends messages to the Server.
 * @see Server
 * @author Nikhil Nayak
 * @author Kyle Zhou
 * @version 1.0
 */
public class Client implements ActionListener, MouseListener {
    /**
     * Represents the width of the frame.
     */
    public static final int WIDTH = 1000;

    /**
     * Represents the height of the frame.
     */
    public static final int HEIGHT = 1000;

    /**
     * Holds a list of all the ships the player has placed
     */
    ArrayList<Integer> shipLengths = new ArrayList<Integer>();

    /**
     * Holds the x position of the last place the client guessed.
     */
    int lastGuessX = 0;

    /**
     * Holds the y position of the last place the client guessed.
     */
    int lastGuessY = 0;

    /**
     * Whether this click is the player's first or second click.
     */
    boolean firstClick = false;

    /**
     * The x position the player clicked on their first click.
     */
    int startingX = 0;

    /**
     * The y position the player clicked on their first click.
     */
    int startingY = 0;

    /**
     * The socket object used to connect to the server.
     */
    Socket socket;

    /**
     * The object used to send messages to the server.
     */
    DataOutputStream out;
    
    /**
     * The object used to receive messages from the server.
     */
    DataInputStream in;

    /**
     * The left-side panel that shows all of your ships.
     */
    MainPanel panel = new MainPanel();

    /**
     * The right-side panel that shows all of your hits/misses.
     */
    GuessingPanel guessingPanel = new GuessingPanel();

    /**
     * The main window that renders the game on the client.
     */
    JFrame frame;

    /**
     * Button for readying and unreadying up.
     */
    JButton readyUpButton = new JButton("Ready Up");

    /**
     * Represents if client is ready or not.
     */
    boolean readiedUp = false;

    /**
     * Represents if is this clients turn.
     */
    boolean myTurn = false;

    /**
     * Containers for holding west components.
     */
    Container west;

    /**
     * Containers for holding center components.
     */
    Container center;

    /**
     * Containers for holding panel components.
     */
    Container grids;

    /**
     * Holds the game state
     */
    enum GameState {
        READYUP,
        SETSHIPS,
        BATTLE,
        GAMEOVER,
    }

    /**
     * The current game state.
     */
    GameState gameState = GameState.READYUP;


    /**
     * The entry point of the program. Creates a new instance of client.
     *
     * @param args The command-line arguments created when the program is run.
     * @throws IOException
     */
    public static void main(String[] args) {
        new Client();
    }



    /**
     * The constructor for the client. Initializes Java Swing elements and connects to server.
     */
    public Client() {
        // Initialize Java Swing components
        frame = new JFrame();
        frame.setSize(WIDTH, HEIGHT);
        frame.setLayout(new BorderLayout());
        west = new Container();
        center = new Container();
        
        GridLayout layout = new GridLayout(1, 2);
        layout.setHgap(100);
        grids = new Container();
        grids.setLayout(layout);
        
        west.setLayout(new GridLayout(4, 1));
        west.add(readyUpButton);

        grids.add(panel);
        grids.add(guessingPanel);

        guessingPanel.addMouseListener(this);
        
        shipLengths.add(5);
        shipLengths.add(4);
        shipLengths.add(3);
        shipLengths.add(2);
        
        readyUpButton.setBackground(Color.RED);
        readyUpButton.addActionListener(this);
        readyUpButton.setPreferredSize(new Dimension(150, 25));
        panel.addMouseListener(this);
        guessingPanel.addMouseListener(this);
        panel.setSize(300, 300);
        panel.repaint();


        frame.add(grids, BorderLayout.CENTER);
        frame.add(west, BorderLayout.WEST);
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        connect("0.tcp.ngrok.io", 10573);
        run();
    }

    /**
     * Invoked when an action occurs.
     *
     * @param event the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        //ready up button
        //sends message to server if ready or not
        if (gameState == GameState.READYUP) {
            if (event.getSource().equals(readyUpButton)) {
                readiedUp = !readiedUp;
                if (readiedUp) {
                    try {
                        out.writeUTF("READY");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    readyUpButton.setBackground(Color.GREEN);
                    readyUpButton.setText("Cancel Ready Up");
                } else {
                    try {
                        out.writeUTF("NOTREADY");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    readyUpButton.setBackground(Color.RED);
                    readyUpButton.setText("Ready Up");
                }
            }
        }

    }

    /**
     * Tells the user it's their turn to guess.
     */
    public void guess(){
        JOptionPane.showMessageDialog(frame, "Your Turn!");
    }

    /**
     * Connects To The {@link Server} Socket.
     *
     * @param ip   the IP address that you want to connect to
     * @param port the port you want to connect to
     */
    public void connect(String ip, int port) {
        try {
            socket = new Socket(ip, port);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("Could Not Initialize Client Socket");
        }
    }

    /**
     * Listens for input from the server, parses it, and changes the state and UI accordingly.
     */
    public void run() {

        boolean running = true;
        //
        Scanner scanner = new Scanner(System.in);

        while (running) {
            try {
                //if there is a message avalible
                if (in.available() > 0) {
                    String message = in.readUTF();

                    switch (message) {
                        //if opponent hits one of the clients ships
                        case "OPPHIT":
                            int x = in.readInt();
                            int y = in.readInt();
                            panel.cells[x][y] = 2;
                            panel.repaint();
                            break;
                        //if server lets client setships
                        case "SETSHIPS":
                            gameState = GameState.SETSHIPS;
                            JOptionPane.showMessageDialog(frame, "Both Players Ready. Set Your Ships!");
                            break;
                            //if both players readied up
                        case "BATTLE":
                            gameState = GameState.BATTLE;
                            panel.setEnabled(false);
                            guessingPanel.setEnabled(false);
                            JOptionPane.showMessageDialog(frame, "Battle!");
                            break;
                            //server letting client take turn
                        case "YOURTURN":
                            myTurn = true;
                            guess();
                            break;
                            //if the guess was a hit
                        case "HIT":
                            guessingPanel.cells[lastGuessX][lastGuessY] = 2;
                            guessingPanel.repaint();
                            break;
                            //if the guess was a miss
                        case "MISS":
                            guessingPanel.cells[lastGuessX][lastGuessY] = 1;
                            guessingPanel.repaint();
                            break;
                            
                        case "INVALID":
                            break;
    
                            //letting player know if win or lose
                        case "WIN":
                            JOptionPane.showMessageDialog(frame, "You Won!");
                            running = false;
                            break;

                        case "LOSE":
                            JOptionPane.showMessageDialog(frame, "You Lost!");
                            running = false;
                            break;


                    }
                    //checking if win by checking if sunk all ships
                    int total = 0;
                    for(int x = 0; x < 10; x++) {
                        for(int y = 0; y < 10; y++){
                            if(guessingPanel.cells[x][y] == 2){
                                total++;
                            }
                        }
                    }
                    if(total >= 2 + 3 + 4 + 5){
                        out.writeUTF("WIN");
                    }

                }

                if (gameState != GameState.READYUP) {
                    readyUpButton.setEnabled(false);
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        try {
            in.close();
            out.close();
            socket.close();
            System.exit(0);
        } catch (IOException exception) {
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



        if (gameState == GameState.SETSHIPS) {
            //if starting new ship
            if (!firstClick) {
                //get the coordinate of click
                startingX = Math.min(panel.cells.length - 1, (int) (e.getX() / (panel.getWidth() / MainPanel.BOARD_SIZE)));
                startingY = Math.min(panel.cells[0].length - 1, (int) (e.getY() / (panel.getHeight() / MainPanel.BOARD_SIZE)));
                //if there is already a ship in the spot
                if(panel.cells[startingX][startingY] == 1){
                    JOptionPane.showMessageDialog(frame, "Invalid (Ships Overlapping) - Add another ship of length: " + shipLengths.toString());
                } else{
                    //put a ship at that position
                    panel.cells[startingX][startingY] = 1;
                    firstClick = true;
                }


            } else {
                //placing second end of the ship
                //getting the x and y of the click
                int endingX = Math.min(panel.cells.length - 1, (int) (e.getX() / (panel.getWidth() / MainPanel.BOARD_SIZE)));
                int endingY = Math.min(panel.cells[0].length - 1, (int) (e.getY() / (panel.getHeight() / MainPanel.BOARD_SIZE)));
                //boolean the check if placement is valid
                boolean isValid = true;

                //if the y values are the same and the length of the ship is valid
                if (startingY == endingY && shipLengths.contains(Math.abs(endingX - startingX) + 1)) {
                    //create a new rectangle
                    Rectangle shipToAdd = new Rectangle(Math.min(endingX, startingX), startingY, Math.abs(endingX - startingX) + 1, 1);
                    for (Rectangle rectangle : panel.shipPositions) {
                        //check if the rectangle intersects other rectangles
                        if (rectangle.intersects(shipToAdd)) {
                            isValid = false;
                            panel.cells[startingX][startingY] = 0;
                            JOptionPane.showMessageDialog(frame, "Invalid (Ships Overlapping) - Add another ship of length: " + shipLengths.toString());
                        }
                    }

                    if (isValid) {
                        //if is valid then add the ship to the panel
                        shipLengths.remove(shipLengths.indexOf(Math.abs(endingX - startingX) + 1));
                        //send the ships coordinates to the server
                        for (int i = Math.min(endingX, startingX); i <= Math.max(endingX, startingX); i++) {
                            panel.cells[i][endingY] = 1;
                            try {
                                out.writeUTF("- " + i + " " + endingY + " " +  Math.abs(endingX - startingX) + 1);
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }
                        }
                        frame.repaint();
                        if (!shipLengths.isEmpty())
                            JOptionPane.showMessageDialog(frame, "Add another ship of length: " + shipLengths.toString());
                        panel.shipPositions.add(shipToAdd);
                    }

                //if x's are the same and the length of the ship is valid
                } else if (startingX == endingX && shipLengths.contains(Math.abs(endingY - startingY) + 1)) {
                    Rectangle shipToAdd = new Rectangle(startingX, Math.min(endingY, startingY), 1, Math.abs(endingY - startingY) + 1);
                    for (Rectangle rectangle : panel.shipPositions) {
                        if (rectangle.intersects(shipToAdd)) {
                            isValid = false;
                            panel.cells[startingX][startingY] = 0;
                            JOptionPane.showMessageDialog(frame, "Invalid(Ships Overlapping) - Add another ship of length: " + shipLengths.toString());
                        }
                    }

                    if (isValid) {
                        shipLengths.remove(shipLengths.indexOf( Math.abs(endingY - startingY) + 1));
                        for (int i = Math.min(endingY, startingY); i <= Math.max(endingY, startingY); i++) {
                            panel.cells[endingX][i] = 1;
                            try {
                                out.writeUTF("- " + endingX + " " + i + " " +  Math.abs(endingY - startingY) + 1);
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }
                        }
                        frame.repaint();
                        if (!shipLengths.isEmpty()) {
                            JOptionPane.showMessageDialog(frame, "Add another ship of length: " + shipLengths.toString());
                        }
                        panel.shipPositions.add(shipToAdd);
                    }

                } else {
                    //ships not placed straight
                    panel.cells[startingX][startingY] = 0;
                    if (!shipLengths.isEmpty()) {
                        JOptionPane.showMessageDialog(frame, "Invalid(Not Straight Ship) - Add another ship of length: " + shipLengths.toString());
                    }
                }

                firstClick = false;
            }
            //let server know if finished setting ships
            if(shipLengths.isEmpty()){
                try {
                    out.writeUTF("DONESETSHIPS");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
            frame.repaint();
        }
        //if is clients turn and clicked on the guessing panel
        if (gameState == GameState.BATTLE && myTurn && e.getSource().equals(guessingPanel)) {
            //get x and y of the click
            int guessX = Math.min(guessingPanel.cells.length - 1, (int) (e.getX() / (guessingPanel.getWidth() / GuessingPanel.BOARD_SIZE)));
            int guessY = Math.min(guessingPanel.cells[0].length - 1, (int) (e.getY() / (guessingPanel.getHeight() / GuessingPanel.BOARD_SIZE)));

            //send message to the server
            String message = "+ " + guessX + " " + guessY;
            try {
                out.writeUTF(message);
                lastGuessX = guessX;
                lastGuessY = guessY;
                //end turn
                myTurn = false;
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }


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



