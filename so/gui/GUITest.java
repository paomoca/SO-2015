package so.gui;

import so.gui.fsframe.FSFrame;

import javax.swing.*;
import java.awt.*;

class GUITest {

    public static void main(String[] a) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                }
                FSFrame fs = new FSFrame();
                fs.setVisible(true);
            }

        });
    }
}