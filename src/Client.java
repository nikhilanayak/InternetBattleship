package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client implements ActionListener {
    Socket socket;
    DataOutputStream out;
    DataInputStream in;

    JFrame frame;
    JButton[][] buttons = new JButton[10][10];
    Container center;

    public static void main(String[] args) throws IOException {
        new Client();
    }

    public Client() throws IOException {
        frame = new JFrame();
        frame.setSize(600, 600);
        frame.setLayout(new BorderLayout());
        center = new Container();
        center.setLayout(new GridLayout(10,10));
        for(int x = 0; x < 10; x++){
            for(int y = 0; y < 10; y++){
                buttons[x][y] = new JButton("");
                center.add(buttons[x][y]);
                buttons[x][y].addActionListener(this);
                        
            }
        }
        frame.add(center, BorderLayout.CENTER);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        connect("127.0.0.1", 1337);
        run();

    }

    @Override
    public void actionPerformed(ActionEvent e){
        for(int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                if(e.getSource().equals(buttons[x][y])){
                    System.out.println(x + ", " + y);
                    try{
                        out.writeUTF(x + ", " + y);
                    }
                    catch(IOException exception){
                        exception.printStackTrace();
                    }
                }
            }
        }
    }

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

    public void run() throws IOException {
        boolean running = true;
        Scanner scanner = new Scanner(System.in);

        System.out.print(">");
        while(running) {
            System.out.println(in.readUTF());
        }

        try
        {
            in.close();
            out.close();
            socket.close();
        }
        catch(IOException i)
        {
            System.out.println(i);
        }
    }




}
