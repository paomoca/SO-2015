package so.gui.grid;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Created by diegomartin on 12/2/15.
 */
public class SectionGrid extends Grid {

    private static SectionGrid self = null;

    private SectionGrid(int gridWidth, int gridHeight, int cellWidth, int cellHeight) {
        super(gridWidth, gridHeight, cellWidth, cellHeight);

    }

    public static SectionGrid getInstance(int gridWidth, int gridHeight, int cellWidth, int cellHeight) {
        if (self == null) {
            self = new SectionGrid(gridWidth, gridHeight, cellWidth , cellHeight);
        }
        return self;
    }


}
