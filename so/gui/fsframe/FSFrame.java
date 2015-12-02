package so.gui.fsframe;

import javax.swing.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import so.filesystem.main.FileSystemController;
import so.gui.grid.BlockGrid;
import so.gui.grid.Grid;
import so.gui.grid.SectionGrid;
import so.gui.shell.*;

/**
 * Created by diegomartin on 12/1/15.
 */
public class FSFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private SectionGrid section;
	private BlockGrid blocks;
    private Shell shell;
    private Interpreter intr;
    private FileSystemController fileSystemController;
    private JFileChooser fileChooser;

    public FSFrame() {

        JPanel content = new JPanel();
        JPanel gridsHolder = new JPanel();
        
        
        shell = new Shell();
        shell.getCurCommand().addKeyListener(new GetCurCommandKeyListener());
		shell.getHistory().addMouseListener(new ShellClickListener());
		shell.getCurCommand().setForeground(Color.GREEN.darker());
		shell.getCurCommand().setCaretColor(Color.GREEN.darker());
		shell.getCurCommand().setBackground(Color.BLACK);
		shell.getHistory().setForeground(Color.GREEN.darker());
		shell.getHistory().setBackground(Color.BLACK);
		
		fileChooser = new JFileChooser();

        section = new SectionGrid(480,480,10,10);
        blocks = new BlockGrid(480, 480, 10, 10);

        section.addMouseListener(new BlockClickListener());

		section.setBackground(Color.white);
		blocks.setBackground(Color.white);
        
        gridsHolder.setLayout(new BorderLayout());
        gridsHolder.add(section,BorderLayout.WEST);
        gridsHolder.add(Box.createHorizontalStrut(20));
        gridsHolder.add(blocks, BorderLayout.EAST);
		gridsHolder.setBackground(new Color(78, 78, 78));
        
        content.setLayout(new BorderLayout());        
        content.add(gridsHolder, BorderLayout.NORTH);
		content.add(Box.createVerticalStrut(17));
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

    class BlockClickListener implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mousePressed(MouseEvent e) {

            int row = (e.getY()/section.getCellWidth());
            int column = (e.getX()/section.getCellHeight());
            int blocksRange = (section.getColumns() * row) + column ;
            if (blocksRange >= 0 && blocksRange < (section.getColumns() * section.getRows())) {
                System.out.println(blocksRange);
                section.getFillCells().clear();
                section.fillCell(column, row);
                blocks.getFillCells().clear();
                blocks.testBits();
            }

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }
    
}
