//package so.gui.fsframe;
//
//
//import so.gui.drawpanel.*;
//import so.gui.drawpanel.Rectangle;
//
//import javax.swing.*;
//import java.awt.*;
//
///**
// * Created by diegomartin on 12/1/15.
// */
//public class FSFrame extends JFrame {
//
//    private DrawPanel dp;
//
//    public FSFrame() {
//
//        dp = DrawPanel.getInstance();
//        dp.setCanvasSize(500, 500);
//
//        JPanel dpHolder = new JPanel();;
//        dpHolder.setBorder(BorderFactory.createLineBorder(Color.BLACK));
//        dpHolder.setLayout(new BorderLayout(0, 0));
//        dpHolder.add(dp, BorderLayout.CENTER);
//        dpHolder.setPreferredSize(new Dimension(dp.getWidth() + 2, dp
//                .getHeight() + 2));
//
//        JPanel content = new JPanel();
//        content.setLayout(new BorderLayout());
//        content.add(dpHolder, BorderLayout.CENTER);
//
//
//        this.add(content, BorderLayout.CENTER);
//        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        this.setTitle("File System GUI");
//        this.pack();
//
//        this.drawTest();
//
//    }
//
//    public void drawTest() {
//        Rectangle.execute(0,0,100,100);
//    }
//}
