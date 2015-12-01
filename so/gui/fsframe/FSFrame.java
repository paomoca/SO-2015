package so.gui.fsframe;

import javax.swing.*;
import java.awt.*;
import so.gui.grid.Grid;

/**
 * Created by diegomartin on 12/1/15.
 */
public class FSFrame extends JFrame {

    private Grid section;

    public FSFrame() {

        JPanel content = new JPanel();

        section = new Grid(480,480,10,10);
        content.setLayout(new BorderLayout());
        content.add(section, BorderLayout.WEST);

        this.add(content, BorderLayout.CENTER);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("File System GUI");
        this.pack();



    }
}
