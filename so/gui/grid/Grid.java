package so.gui.grid;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;

/**
 * Created by diegomartin on 12/1/15.
 */
public class Grid extends JPanel {


    private int gridWidth = 500;
    private int gridHeight = 500;

    private int cellWidth = 10;
    private int cellHeight = 10;

    private int gridOffset = 10;
    private int columns = gridWidth/cellWidth;
    private java.util.List<Point> fillCells;

    public Grid() {
        fillCells = new ArrayList<>();

        this.setPreferredSize(new Dimension(gridWidth + (gridOffset * 2), gridHeight + (gridOffset * 2)));

        this.addMouseListener(new BlockClickListener());

        System.out.print("Total blocks = " + (gridWidth/cellWidth)*(gridHeight/cellHeight) + "\n");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Point fillCell : fillCells) {
            int cellX = gridOffset + (fillCell.x * cellWidth);
            int cellY = gridOffset + (fillCell.y * cellHeight);
            g.setColor(Color.RED);
            g.fillRect(cellX, cellY, cellWidth, cellHeight);
        }

        g.setColor(Color.BLACK);
        g.drawRect(gridOffset, gridOffset, gridWidth, gridHeight);

        for (int i = gridOffset; i <= gridWidth + gridOffset; i += cellWidth) {
            g.drawLine(i, gridOffset, i, gridHeight + gridOffset);
        }

        for (int i = gridOffset; i <= gridHeight + gridOffset; i += cellHeight) {
            g.drawLine(gridOffset, i, gridWidth + gridOffset, i);
        }

    }

    public void fillCell(int x, int y) {
        fillCells.add(new Point(x, y));
        repaint();
    }

    class BlockClickListener implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mousePressed(MouseEvent e) {
            int row = (e.getY()/cellWidth) - 1;
            int column = (e.getX()/cellHeight) - 1;


            int section = (columns * row) + column ;

            if (section >= 0 && section < 2500) {
                System.out.println(section);
            }

            fillCell(column,row);

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }


}
