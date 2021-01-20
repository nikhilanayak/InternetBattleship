package src;


import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Renderer extends JPanel {
    public static final int BOARD_SIZE = 10;
    ArrayList<Rectangle> shipPositions = new ArrayList<>();




    boolean[][] cells = new boolean[BOARD_SIZE][BOARD_SIZE];

    public Renderer() {

    }

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);

        //drawing lines by width from top to bottom
        g.setColor(Color.BLACK);

        double width = this.getWidth() / (double) BOARD_SIZE;
        double height = this.getHeight() / (double) BOARD_SIZE;




        g.setColor(Color.GRAY);

        for(int x = 0; x < BOARD_SIZE; x++) {
            for (int y = 0; y < BOARD_SIZE; y++) {
                if (cells[x][y]) {
                    g.fillRect((int) Math.round(x * width), (int) Math.round(y * height), (int) Math.round(width + 1), (int) Math.round(height + 1));
                }
            }
        }

        for (int x = 0; x < BOARD_SIZE; x++) {
            g.drawLine((int) Math.round(x * width), 0, (int) Math.round(x * width), this.getHeight());
        }
        //drawing lines by height from side to side
        for (int y = 0; y < BOARD_SIZE; y++) {
            //need to have a double so the line is more precise
            g.drawLine(0, (int) Math.round(y * height), this.getWidth(), (int) Math.round(y * height));
        }

        g.setColor(Color.BLACK);
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(3));
        for(int i = 0; i < shipPositions.size(); i++){
            g2.drawRect((int) Math.round(shipPositions.get(i).x * width), (int) Math.round(shipPositions.get(i).y * height), (int) Math.round((width) * shipPositions.get(i).width) , (int) Math.round((height)* shipPositions.get(i).height));
        }




    }


}



