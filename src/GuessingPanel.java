package src;

import javax.swing.*;
import java.awt.*;


/**
 * The panel drawn on the right side of the screen that your hits/misses.
 * @author Nikhil Nayak
 * @author Kyle Zhou
 * @version 1.0
 */
public class GuessingPanel extends JPanel {
    /**
     * The size of the board in cells.
     */
    public static final int BOARD_SIZE = 10;

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

        //drawing lines by width from top to bottom
        g.setColor(Color.BLACK);

        double width = this.getWidth() / (double) BOARD_SIZE;
        double height = this.getHeight() / (double) BOARD_SIZE;

        for (int x = 0; x < BOARD_SIZE; x++) {
            g.drawLine((int) Math.round(x * width), 0, (int) Math.round(x * width), this.getHeight());
        }
        //drawing lines by height from side to side
        for (int y = 0; y < BOARD_SIZE; y++) {
            //need to have a double so the line is more precise
            g.drawLine(0, (int) Math.round(y * height), this.getWidth(), (int) Math.round(y * height));
        }


        for(int x = 0; x < BOARD_SIZE; x++) {
            for (int y = 0; y < BOARD_SIZE; y++) {
                if (cells[x][y] == 1) {
                    g.setColor(Color.BLUE); // coloring in the misses
                }
                else if(cells[x][y] == 2){
                    g.setColor(Color.RED); // coloring in the hits
                }
                else{
                    continue;
                }
                g.fillRect((int) Math.round(x * width), (int) Math.round(y * height), (int) Math.round(width + 1), (int) Math.round(height + 1)); //
            }
        }
    }

}
