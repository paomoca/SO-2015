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


    private int gridWidth;
    private int gridHeight;

    private int cellWidth ;
    private int cellHeight;

    private int cells;
    private int columns;
    private int rows;


    private java.util.List<Point> fillCells;

    public Grid(int gridWidth, int gridHeight, int cellWidth, int cellHeight) {
        fillCells = new ArrayList<>();

        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;

        this.columns = gridWidth/cellWidth;
        this.rows = gridHeight/cellHeight;
        this.cells = columns*rows;

        this.setPreferredSize(new Dimension(gridWidth, gridHeight));
        this.addMouseListener(new BlockClickListener());

        System.out.print("Total blocks = " + this.cells + "\n");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Point fillCell : fillCells) {
            int cellX = fillCell.x * cellWidth;
            int cellY = fillCell.y * cellHeight;
            g.setColor(Color.RED);
            g.fillRect(cellX, cellY, cellWidth, cellHeight);
        }

        g.setColor(Color.BLACK);
        g.drawRect(0, 0, gridWidth, gridHeight);

        for (int i = 0; i <= gridWidth; i += cellWidth) {
            g.drawLine(i, 0, i, gridHeight);
        }

        for (int i = 0; i <= gridHeight; i += cellHeight) {
            g.drawLine(0, i, gridWidth, i);
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
            System.out.println("Click detected.");

            int row = (e.getY()/cellWidth);
            System.out.println("Row = " + row);
            int column = (e.getX()/cellHeight);
            System.out.println("Column = " + column);
            int section = (columns * row) + column ;

            if (section >= 0 && section < (columns * rows)) {
                System.out.println(section);
                fillCell(column, row);
            }

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
