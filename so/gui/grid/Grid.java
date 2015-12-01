package so.gui.grid;

import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * Created by diegomartin on 12/1/15.
 */
public class Grid extends JPanel {


    private int width = -1;
    private int height = -1;
    private int pWidth = -1;
    private int pHeight = -1;
    private java.util.List<Point> fillCells;

    public Grid() {
        fillCells = new ArrayList<>();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Point fillCell : fillCells) {
            int cellX = 10 + (fillCell.x * 10);
            int cellY = 10 + (fillCell.y * 10);
            g.setColor(Color.RED);
            g.fillRect(cellX, cellY, 10, 10);
        }

        g.setColor(Color.BLACK);
        g.drawRect(10, 10, 800, 500);

        for (int i = 10; i <= 800; i += 10) {
            g.drawLine(i, 10, i, 510);
        }

        for (int i = 10; i <= 500; i += 10) {
            g.drawLine(10, i, 810, i);
        }
    }

    public void fillCell(int x, int y) {
        fillCells.add(new Point(x, y));
        repaint();
    }


}
