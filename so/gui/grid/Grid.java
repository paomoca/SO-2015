package so.gui.grid;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;
import java.util.List;

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

    public void fillCells(boolean[] blocks) {

        for (int column = 0; column < columns; column++) {
            for (int row = 0; row < rows; row++) {
                if (blocks[(column * columns) + row]) {
                    fillCells.add(new Point(row, column));
                }
            }
        }
        repaint();
    }

    public int randInt(int min, int max) {

        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }

    public int getGridWidth() {
        return gridWidth;
    }

    public int getGridHeight() {
        return gridHeight;
    }

    public int getCellWidth() {
        return cellWidth;
    }

    public int getCellHeight() {
        return cellHeight;
    }

    public int getCells() {
        return cells;
    }

    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return rows;
    }

    public List<Point> getFillCells() {
        return fillCells;
    }
}
