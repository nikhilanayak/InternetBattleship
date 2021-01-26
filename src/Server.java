package src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The server class that maintains game state and facilitates message sending.
 * @author Nikhil Nayak
 * @author Kyle Zhou
 * @version 1.0
 */
public class Server {


    /**
     * The main socket used to communicate with the clients.
     */
    ServerSocket socket;


    /**
     * The client socket for the first client.
     */
    Socket clientA = null;

    /**
     * The client socket for the second client.
     */
    Socket clientB = null;

    /**
     * The object used to send messages to clientA
     */
    DataOutputStream writerA = null;

    /**
     * The object used to send messages to clientB
     */
    DataOutputStream writerB = null;

    /**
     * The object used to receive messages from clientA
     */
    DataInputStream readerA = null;

    /**
     * The object used to receive messages from clientB
     */
    DataInputStream readerB = null;


    /**
     * Stores the state of whether clientA has pressed "Ready Up".
     *
     * @see Client
     */
    boolean readyA = false;

    /**
     * Stores the state of whether clientB has pressed "Ready Up".
     *
     * @see Client
     */
    boolean readyB = false;

    /**
     * Stores the state of whether clientA has finished placing all their ships.
     */
    boolean setShipsA = false;

    /**
     * Stores the state of whether clientB has finished placing all their ships.
     */
    boolean setShipsB = false;

    /**
     * Holds a local version of clientA's boards;
     */
    int[][] boardA = new int[10][10];

    /**
     * Holds a local version of clientB's boards;
     */
    int[][] boardB = new int[10][10];

    /**
     * Stores the current state of the game(eg. whether it has starting, whether it is running, etc.)
     */
    enum GameState {
        READYUP,
        SETSHIPS,
        BATTLE,
    }

    /**
     * Stores which player's turn it is.
     */
    enum TurnStatus {
        PlayerA,
        PlayerB,
        WAITING
    }


    /**
     * Initializes the server socket that {@link src.Client} objects can connect to.
     *
     * @param port the port the server will listen on
     * @see Client
     */
    public void init(int port) {
        try {
            socket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("Could Not Initialize Server Socket");
        }
    }

    /**
     * Starts the server socket, listens, and accepts more clients.
     *
     * @return whether the loop gracefully exited
     */
    public boolean start() throws IOException {
        clientA = socket.accept();
        writerA = new DataOutputStream(clientA.getOutputStream());
        readerA = new DataInputStream((clientA.getInputStream()));
        System.out.println("accepted client a");

        clientB = socket.accept();
        writerB = new DataOutputStream(clientB.getOutputStream());
        readerB = new DataInputStream((clientB.getInputStream()));
        System.out.println("accepted client b");

        //initialize the gamestate
        GameState gameState = GameState.READYUP;
        TurnStatus turnStatus = TurnStatus.PlayerA;


        boolean running = true;

        while (running) {
            try {
                //if message from player A
                if (readerA.available() > 0) {
                    String messageA = readerA.readUTF();
                    //if was setting ships
                    if (messageA.charAt(0) == '-') {
                        String[] coordinates = messageA.split(" ");
                        boardA[Integer.parseInt(coordinates[1])][Integer.parseInt(coordinates[2])] = Integer.parseInt(coordinates[3]);
                    }
                    //if was a guess
                    if (messageA.charAt(0) == '+') {
                        String[] guess = messageA.split(" ");
                        //check if was a hit invalid or miss
                        if (boardB[Integer.parseInt(guess[1])][Integer.parseInt(guess[2])] > 0) {
                            writerA.writeUTF("HIT");
                            writerB.writeUTF("OPPHIT");
                            writerB.writeInt(Integer.parseInt(guess[1]));
                            writerB.writeInt(Integer.parseInt(guess[2]));
                            turnStatus = TurnStatus.PlayerB;
                        } else if (boardB[Integer.parseInt(guess[1])][Integer.parseInt(guess[2])] < 0) {
                            writerA.writeUTF("INVALID");
                        } else if (boardB[Integer.parseInt(guess[1])][Integer.parseInt(guess[2])] == 0) {
                            writerA.writeUTF("MISS");
                            turnStatus = TurnStatus.PlayerB;
                        }
                    }

                    //is the player readied up?
                    switch (messageA) {
                        case "READY":
                            readyA = true;
                            break;
                        case "NOTREADY":
                            readyA = false;
                            break;
                        case "DONESETSHIPS":
                            setShipsA = true;
                            break;

                        case "WIN":
                            writerA.writeUTF("WIN");
                            writerB.writeUTF("LOSE");
                            running = false;
                            break;
                    }
                }
                if (readerB.available() > 0) {
                    String messageB = readerB.readUTF();

                    if (messageB.charAt(0) == '-') {
                        String[] coordinates = messageB.split(" ");
                        boardB[Integer.parseInt(coordinates[1])][Integer.parseInt(coordinates[2])] = Integer.parseInt(coordinates[3]);
                    }

                    if (messageB.charAt(0) == '+') {
                        String[] guess = messageB.split(" ");
                        if (boardA[Integer.parseInt(guess[1])][Integer.parseInt(guess[2])] > 0) {
                            writerB.writeUTF("HIT");

                            writerA.writeUTF("OPPHIT");
                            writerA.writeInt(Integer.parseInt(guess[1]));
                            writerA.writeInt(Integer.parseInt(guess[2]));

                            turnStatus = TurnStatus.PlayerA;
                        } else if (boardA[Integer.parseInt(guess[1])][Integer.parseInt(guess[2])] < 0) {
                            writerB.writeUTF("INVALID");
                        } else if (boardA[Integer.parseInt(guess[1])][Integer.parseInt(guess[2])] == 0) {
                            writerB.writeUTF("MISS");
                            turnStatus = TurnStatus.PlayerA;
                        }
                    }
                    //is the player readied up?
                    switch (messageB) {
                        case "READY":
                            readyB = true;
                            break;
                        case "NOTREADY":
                            readyB = false;
                            break;
                        case "DONESETSHIPS":
                            setShipsB = true;
                            break;
                        case "WIN":
                            writerB.writeUTF("WIN");
                            writerA.writeUTF("LOSE");
                            running = false;
                            break;

                    }
                }
                //if both readied up then let them set ships
                if (readyA && readyB) {
                    gameState = GameState.SETSHIPS;
                    writerA.writeUTF("SETSHIPS");
                    writerB.writeUTF("SETSHIPS");
                    readyB = false;
                    readyA = false;
                }
                //if both players done setting ships
                if (setShipsB && setShipsA) {
                    gameState = GameState.BATTLE;
                    writerA.writeUTF("BATTLE");
                    writerB.writeUTF("BATTLE");
                    setShipsA = false;
                    setShipsB = false;
                }
                //logic for taking turns
                if (gameState == GameState.BATTLE) {
                    if (turnStatus == TurnStatus.PlayerA) {
                        writerA.writeUTF("YOURTURN");
                        turnStatus = TurnStatus.WAITING;
                    } else if (turnStatus == TurnStatus.PlayerB) {
                        writerB.writeUTF("YOURTURN");
                        turnStatus = TurnStatus.WAITING;
                    }
                }

            } catch (IOException e) {
                System.out.println("IO Error: " + e.toString());
                return false;
            }
        }

        socket.close();
        writerA.close();
        writerB.close();
        readerA.close();
        readerB.close();
        clientB.close();
        clientA.close();
        return true;
    }


    /**
     * The entry point to the Server; initializes the server socket and starts listening for messages.
     *
     * @param args the command-line arguments used when the program is run
     */

    public static void main(String[] args) {
        Server server = new Server();
        server.init(1337);
        try {
            server.start();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

    }


}
