package src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;

public class Server{

    // The server socket
    ServerSocket socket;



    // The client sockets and their writers/readers
    Socket clientA = null;
    Socket clientB = null;
    
    DataOutputStream writerA = null;
    DataOutputStream writerB = null;

    DataInputStream readerA = null;
    DataInputStream readerB = null;

    //boolean readied up
    boolean readyA = false;
    boolean readyB = false;

    boolean setShipsA = false;
    boolean setShipsB = false;

    int[][] boardA = new int[10][10];
    int[][] boardB = new int[10][10];

    StringTokenizer st;

    enum GameState{
        READYUP,
        SETSHIPS,
        BATTLE,
        GAMEOVER,
    }

    enum TurnStatus{
        PlayerA,
        PlayerB,
        WAITING
    }



    /**
     * Initializes the server socket that {@link src.Client} objects can connect to
     * @param port the port the server will listen on
     * @see Client
     */
    public void init(int port){
        try{
            socket = new ServerSocket(port);
        }
        catch(IOException e){
            System.out.println("Could Not Initialize Server Socket");
        }
    }

    /**
     * Starts the server socket, listens, and accepts more clients
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

        GameState gameState = GameState.READYUP;
        TurnStatus turnStatus = TurnStatus.PlayerA;

        while(true){
            try {
                if(readerA.available() > 0){
                    String messageA = readerA.readUTF();

                    if(messageA.charAt(0) == '-'){
                        String[] coordinates = messageA.split(" ");
                        boardA[Integer.parseInt(coordinates[1])][Integer.parseInt(coordinates[2])] = Integer.parseInt(coordinates[3]);
                        System.out.println(Integer.parseInt(coordinates[1]) + ", " + Integer.parseInt(coordinates[2]) + "=" + Integer.parseInt(coordinates[3]));
                    }

                    if(messageA.charAt(0) == '+'){
                        String[] guess = messageA.split(" ");
                        if(boardB[Integer.parseInt(guess[1])][Integer.parseInt(guess[2])] > 0){
                            writerA.writeUTF("HIT");
                        }
                        else if(boardB[Integer.parseInt(guess[1])][Integer.parseInt(guess[2])] < 0){
                            writerA.writeUTF("INVALID");
                        } else if(boardB[Integer.parseInt(guess[1])][Integer.parseInt(guess[2])] == 0){
                            writerA.writeUTF("MISS");
                        }
                    }

                    //is the player readied up?
                    switch(messageA){
                        case "READY":
                            readyA = true;
                            break;
                        case "NOTREADY":
                            readyA = false;
                            break;
                        case "DONESETSHIPS":
                            setShipsA = true;
                            break;
                    }
                }
                if(readerB.available() > 0){
                    String messageB = readerB.readUTF();

                    if(messageB.charAt(0) == '-'){
                        String[] coordinates = messageB.split(" ");
                        boardB[Integer.parseInt(coordinates[1])][Integer.parseInt(coordinates[2])] = Integer.parseInt(coordinates[3]);
                        System.out.println(Integer.parseInt(coordinates[1]) + ", " + Integer.parseInt(coordinates[2]) + "=" + Integer.parseInt(coordinates[3]));
                    }

                    if(messageB.charAt(0) == '+'){
                        String[] guess = messageB.split(" ");
                        if(boardA[Integer.parseInt(guess[1])][Integer.parseInt(guess[2])] > 0){
                            writerB.writeUTF("HIT");
                        }
                        else if(boardA[Integer.parseInt(guess[1])][Integer.parseInt(guess[2])] < 0){
                            writerB.writeUTF("INVALID");
                        } else if(boardA[Integer.parseInt(guess[1])][Integer.parseInt(guess[2])] == 0){
                            writerB.writeUTF("MISS");
                        }
                    }
                    //is the player readied up?
                    switch(messageB){
                        case "READY":
                            readyB = true;
                            break;
                        case "NOTREADY":
                            readyB = false;
                            break;
                        case "DONESETSHIPS":
                            setShipsB = true;
                            break;

                    }
                }
                //if both readied up then let them set ships
                if(readyA && readyB){
                    gameState = GameState.SETSHIPS;
                    writerA.writeUTF("SETSHIPS");
                    writerB.writeUTF("SETSHIPS");
                    readyB = false;
                    readyA = false;
                }

                if(setShipsB && setShipsA){
                    gameState = GameState.BATTLE;
                    writerA.writeUTF("BATTLE");
                    writerB.writeUTF("BATTLE");
                    setShipsA = false;
                    setShipsB = false;
                }

                if(gameState == GameState.BATTLE){
                    if(turnStatus == TurnStatus.PlayerA){
                        writerA.writeUTF("YOURTURN");
                        turnStatus = TurnStatus.WAITING;
                    } else if(turnStatus == TurnStatus.PlayerB){
                        writerB.writeUTF("YOURTURN");
                        turnStatus = TurnStatus.WAITING;
                    }
                }

            }
            catch(IOException e){
                System.out.println("IO Error: " + e.toString());
                return false;
            }
        }
    }



    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.init(1337);
        server.start();

    }

   
}
