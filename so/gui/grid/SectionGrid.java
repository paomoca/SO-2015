package so.gui.grid;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Created by diegomartin on 12/2/15.
 */
public class SectionGrid extends Grid {

    public SectionGrid(int gridWidth, int gridHeight, int cellWidth, int cellHeight) {

        super(gridWidth, gridHeight, cellWidth, cellHeight);

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Point fillCell : getFillCells()) {
            int cellX = fillCell.x * getCellWidth();
            int cellY = fillCell.y * getCellHeight();
            g.setColor(Color.BLUE);
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


}
