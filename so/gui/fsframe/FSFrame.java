package so.gui.fsframe;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import so.filesystem.cache.CacheControllerException;
import so.filesystem.disk.DeviceInitializationException;
import so.filesystem.disk.DiskFreeSpaceManager;
import so.filesystem.disk.IncorrectLengthConversionException;
import so.filesystem.disk.UnidentifiedMetadataTypeException;
import so.filesystem.filemanagment.InodeDirectPointerIndexOutOfRange;
import so.filesystem.main.FileSystemController;
import so.gui.fileWindow.FileWindow;
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
    private JFileChooser fileChooserImport;
    private JFileChooser fileChooserExport;
    private FileWindow openFileWindow;

    public FSFrame() {

        JPanel content = new JPanel();
        JPanel gridsHolder = new JPanel();

        openFileWindow = new FileWindow();
        openFileWindow.getSaveButton().addActionListener(new CreateWindowButtonListener());

        shell = new Shell();
        shell.getCurCommand().addKeyListener(new GetCurCommandKeyListener());
		shell.getHistory().addMouseListener(new ShellClickListener());
		shell.getCurCommand().setForeground(Color.GREEN.darker());
		shell.getCurCommand().setCaretColor(Color.GREEN.darker());
		shell.getCurCommand().setBackground(Color.BLACK);
		shell.getHistory().setForeground(Color.GREEN.darker());
		shell.getHistory().setBackground(Color.BLACK);

		fileChooserImport = new JFileChooser();
		fileChooserExport = new JFileChooser();

        section = SectionGrid.getInstance(480,480,10,10);
        blocks = BlockGrid.getInstance(480, 480, 10, 10);

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

    private void reqImport(String fileName){
    	int flag = fileChooserImport.showOpenDialog(this);
		if (flag == JFileChooser.APPROVE_OPTION) {

			File file = fileChooserImport.getSelectedFile();
			//JOptionPane.showMessageDialog(myWindow, file.getName()+ ": File loaded.\n");
			shell.getHistory().append("\nTo import: "+file.getAbsolutePath());
		try {
			fileSystemController.importFile(file.getAbsolutePath(),fileName);

		} catch (ShellAnswerException e){
			shell.getHistory().append("\n"+e.toString());
		} catch (IncorrectLengthConversionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InodeDirectPointerIndexOutOfRange e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnidentifiedMetadataTypeException e) {
            e.printStackTrace();
        } catch (DeviceInitializationException e) {
            e.printStackTrace();
        }
        }else{
			shell.getHistory().append("\nAction Canceled.");
		}
    }

    private void reqExport(String fileName){
    	int flag = fileChooserExport.showSaveDialog(this);
		if (flag == JFileChooser.APPROVE_OPTION) {
			byte[] dataTowrite = {'t','e','s','t'};
			try {
				//BufferedWriter outFile = new BufferedWriter(new FileWriter(fileChooserExport.getSelectedFile()));
				//outFile.write(new String(dataTowrite));
				//outFile.close();
				String path = fileChooserExport.getSelectedFile().getPath();
				fileSystemController.exportFile(fileName,path);
				}
			catch (Exception ex) {
				shell.getHistory().append("\n Error: " + ex.toString() + ex.getMessage());
			}
		}
    }

    private void reqCreate(String fileName){
    	if(openFileWindow != null){
			openFileWindow = new FileWindow();
	        openFileWindow.getSaveButton().addActionListener(new CreateWindowButtonListener());
			openFileWindow.getMyWindow().setTitle(fileName);
			openFileWindow.getScriptArea().setText(fileName);
			openFileWindow.getMyWindow().setVisible(true);
    	}else{
    		shell.getHistory().append("\nError creating "+fileName);
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
					shell.getHistory().append("\n Will import "+e.toString());
					reqImport(e.toString());
				} catch (RequestCreateFileExcpetion e) {
					reqCreate(e.toString());
                } catch (RequestExportException e) {
                    reqExport(e.toString());
                } catch (UnidentifiedMetadataTypeException e) {
                    e.printStackTrace();
                } catch (DeviceInitializationException e) {
                    e.printStackTrace();
                } catch (CacheControllerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
		}
	}// CurCommandKeyListener

	class CreateWindowButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent ae)
		{
		    if(ae.getActionCommand().equals("save")){
		    	try {
					intr.saveFile(openFileWindow.getMyWindow().getTitle(), fileSystemController);
				} catch (ShellAnswerException e) {
					shell.getHistory().append("\n"+e.toString());
				}
		    }else if(ae.getActionCommand().equals("quit")){
		    	openFileWindow.getMyWindow().setVisible(false);
		    }
		}
	}//BotonesListener


    class BlockClickListener implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mousePressed(MouseEvent e) {

            int row = (e.getY()/section.getCellWidth());
            int column = (e.getX()/section.getCellHeight());
            int blockSection = (section.getColumns() * row) + column ;
            if (blockSection >= 0 && blockSection < (section.getColumns() * section.getRows())) {
                section.getFillCells().clear();
                section.fillCell(column, row);

                // Funcion de pruebas
                blocks.getFillCells().clear();
                blocks.testBits(blockSection * section.getColumns() * section.getRows());

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
