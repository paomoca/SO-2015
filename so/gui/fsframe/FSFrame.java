package so.gui.fsframe;

import javax.swing.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import so.filesystem.main.FileSystemController;
import so.gui.grid.Grid;
import so.gui.shell.*;

/**
 * Created by diegomartin on 12/1/15.
 */
public class FSFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private Grid section;
    private Shell shell;
    private Interpreter intr;
    private FileSystemController fileSystemController;
    private JFileChooser fileChooser;

    public FSFrame() {

        JPanel content = new JPanel();
        
        shell = new Shell();
        shell.getCurCommand().addKeyListener(new GetCurCommandKeyListener());
		shell.getHistory().addMouseListener(new ShellClickListener());
		shell.getCurCommand().setForeground(Color.GREEN.darker());
		shell.getCurCommand().setCaretColor(Color.GREEN.darker());
		shell.getCurCommand().setBackground(Color.BLACK);
		shell.getHistory().setForeground(Color.GREEN.darker());
		shell.getHistory().setBackground(Color.BLACK);
		
		fileChooser = new JFileChooser();

        section = new Grid(480,480,10,10);
        content.setLayout(new BorderLayout());
        content.add(section, BorderLayout.WEST);
        content.add(shell, BorderLayout.SOUTH);

        this.add(content, BorderLayout.CENTER);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("File System GUI");
        this.setResizable(false);
        this.pack();
        
        intr = new Interpreter();
        fileSystemController = new FileSystemController();
        	try {
				fileSystemController.init();
			} catch (ShellAnswerException e) {
				// TODO Auto-generated catch block
				this.shell.getHistory().append(e.toString());
			}
    }
    
    private void loadImport(){
    	int flag = fileChooser.showOpenDialog(this);
		if (flag == JFileChooser.APPROVE_OPTION) {
			
			File file = fileChooser.getSelectedFile();
			//JOptionPane.showMessageDialog(myWindow, file.getName()+ ": File loaded.\n");
			shell.getHistory().append("\nTo import: "+file.getAbsolutePath());
		}else{
			shell.getHistory().append("\nError loading FileChooser.");
		}
    }
    
    class ShellClickListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			shell.getCurCommand().requestFocus();

		}

		@Override
		public void mousePressed(MouseEvent e) {

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}

	}

	class GetCurCommandKeyListener implements KeyListener {

		public void keyPressed(KeyEvent key) {

			if (key.getKeyChar() == '\n')
				shell.getCurCommand().getInputMap()
						.put(KeyStroke.getKeyStroke("ENTER"), "doNothing");
		}

		public void keyReleased(KeyEvent key) {

		}

		public void keyTyped(KeyEvent key) {

			if (key.getKeyChar() == '\n') {
				if (shell.getCommand().equals(""))
					return;
				try {
					intr.readCommand(shell.getCommand(),fileSystemController);
				} catch (WrongCommandException e) {
					shell.getHistory().append("\n"+e.toString());
				} catch (ShellAnswerException e){
					shell.getHistory().append("\n"+e.toString());
				} catch (RequestImportException e){
					shell.getHistory().append("\n"+e.toString());
					loadImport();
				}
			}
		}
	}// CurCommandKeyListener
    
}
