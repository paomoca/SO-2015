package so.gui.fsframe;

import javax.swing.*;
import java.awt.*;
import so.gui.grid.Grid;

/**
 * Created by diegomartin on 12/1/15.
 */
public class FSFrame extends JFrame {

    private Grid grid;

    public FSFrame() {

        grid = new Grid();

        JPanel content = new JPanel();
        content.setLayout(new BorderLayout());
        content.add(grid, BorderLayout.CENTER);


        this.add(content, BorderLayout.CENTER);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("File System GUI");
        this.pack();



    }
}
