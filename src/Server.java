package src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

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

        while(true){
            try {
                if(readerA.available() > 0){
                    String messageA = readerA.readUTF();
                    writerA.writeUTF("you said: " + messageA);
                }
                if(readerB.available() > 0){
                    String messageB = readerB.readUTF();
                    writerB.writeUTF("you said: " + messageB);
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
