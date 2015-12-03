package so.gui.grid;

import so.filesystem.disk.DiskFreeSpaceManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by diegomartin on 12/2/15.
 */
public class BlockGrid extends Grid {

    private static BlockGrid self = null;

    private BlockGrid(int gridWidth, int gridHeight, int cellWidth, int cellHeight) {
        super(gridWidth, gridHeight, cellWidth, cellHeight);

    }

    public static BlockGrid getInstance(int gridWidth, int gridHeight, int cellWidth, int cellHeight) {
        if (self == null) {
            self = new BlockGrid(gridWidth, gridHeight, cellWidth,cellHeight);
        }
        return self;
    }

    public static BlockGrid getInstance() {
        return self;
    }

    public void testBits(int blocksRange) {
        //ArrayList<Boolean> arrayBits = new ArrayList<Boolean>();
        boolean[] bits;
        //System.out.println("Start = " + (blocksRange + 1));
        //System.out.println("End = " + (blocksRange + getCells()));
//
//
//        Random n = new Random();
//        for(int i = 0; i < getRows()*getColumns(); ++i) {
//            arrayBits.add(n.nextBoolean());
//        }
        bits = new boolean[getCells() - 1];
        for(int i = 0; i < getCells() - 1; ++i) {
            bits[i] = !DiskFreeSpaceManager.getInstance().printbits(blocksRange + 1, blocksRange + getCells())[i];
        }

        fillCells(bits);
    }

}
