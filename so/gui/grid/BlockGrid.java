package so.gui.grid;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by diegomartin on 12/2/15.
 */
public class BlockGrid extends Grid {

    public BlockGrid(int gridWidth, int gridHeight, int cellWidth, int cellHeight) {
        super(gridWidth, gridHeight, cellWidth, cellHeight);

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Random rand = new Random();
        for (Point fillCell : getFillCells()) {
            int cellX = fillCell.x * getCellWidth();
            int cellY = fillCell.y * getCellHeight();
            g.setColor(new Color(randInt(1,255),randInt(1,255),randInt(1,255)));
            g.fillRect(cellX, cellY, getCellWidth(), getCellHeight());
        }

        g.setColor(Color.BLACK);
        g.drawRect(0, 0, getGridWidth(), getGridHeight());

        for (int i = 0; i <= getGridWidth(); i += getCellWidth()) {
            g.drawLine(i, 0, i, getGridHeight());
        }

        for (int i = 0; i <= getGridHeight(); i += getCellHeight()) {
            g.drawLine(0, i, getGridWidth(), i);
        }

    }

    public void testBits() {
        ArrayList<Boolean> arrayBits = new ArrayList<Boolean>();
        boolean[] bits;

        Random n = new Random();
        for(int i = 0; i < getRows()*getColumns(); ++i) {
            arrayBits.add(n.nextBoolean());
        }
        bits = new boolean[arrayBits.size()];
        for(int i = 0; i < arrayBits.size(); ++i) {
            bits[i] = !arrayBits.get(i);
        }

        fillCells(bits);


    }
}
