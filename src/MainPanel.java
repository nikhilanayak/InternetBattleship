package src;


import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * The panel drawn on the left side of the screen that shows your ships.
 * @author Nikhil Nayak
 * @author Kyle Zhou
 * @version 1.0
 */
public class MainPanel extends JPanel{
    /**
     * The size of the board in cells
     */
    public static final int BOARD_SIZE = 10;

    /**
     * A set of rectangles that represents all the ships the client has placed.
     */
    ArrayList<Rectangle> shipPositions = new ArrayList<>();

    /**
     * An array that holds the state of all of the cells on.
     */
    int[][] cells = new int[BOARD_SIZE][BOARD_SIZE];


    /**
     * Calls the default paintComponent and draws lines and cells depending on the state of <code>cells</code>
     * @param g the <code>Graphics</code> object used to draw on the JPanel
     */
    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);

        //drawing the grid layout
        g.setColor(Color.BLACK);

        double width = this.getWidth() / (double) BOARD_SIZE;
        double height = this.getHeight() / (double) BOARD_SIZE;

        for (int x = 0; x <= BOARD_SIZE; x++) {
            g.drawLine((int) Math.round(x * width), 0, (int) Math.round(x * width), this.getHeight());
        }

        g.drawLine((int) this.getWidth() - 1, 0, (int) this.getWidth() - 1, this.getHeight() - 1);

        //drawing lines by height from side to side
        for (int y = 0; y < BOARD_SIZE; y++) {
            //need to have a double so the line is more precise
            g.drawLine(0, (int)Math.round(y * height), this.getWidth(), (int) Math.round(y * height));
        }



        //coloring in ships gray and hits as red
        for(int x = 0; x < BOARD_SIZE; x++) {
            for (int y = 0; y < BOARD_SIZE; y++) {
                if (cells[x][y] == 1) {
                    g.setColor(Color.GRAY);
                }
                else if(cells[x][y] == 2){
                    g.setColor(Color.RED);
                }
                else{
                    continue;
                }
                g.fillRect((int) Math.round(x * width), (int) Math.round(y * height), (int) Math.round(width + 1), (int) Math.round(height + 1));
            }
        }


        //going through the ships and boxing them as together
        g.setColor(Color.BLACK);
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(3));
        for(int i = 0; i < shipPositions.size(); i++){
            g2.drawRect((int) Math.round(shipPositions.get(i).x * width), (int) Math.round(shipPositions.get(i).y * height), (int) Math.round((width) * shipPositions.get(i).width) , (int) Math.round((height)* shipPositions.get(i).height));
        }
    }


}



