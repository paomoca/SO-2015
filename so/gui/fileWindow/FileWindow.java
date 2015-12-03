package so.gui.fileWindow;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

/**
 * 
 * @author JuanPTC
 *
 */
public class FileWindow {

	private JFrame myWindow;
	// Components
	private JPanel buttonPanel;
	private JPanel textHolder;
	private JTextArea scriptArea;
	
	private JButton loadButton;
	private JButton saveButton;
	private JButton runButton;
	private JButton cancelButton;
	private JButton clearButton;
	
	//@SuppressWarnings("unused")
	//private LogoMenuBar menuBar;
	private JFileChooser fileChooser;


	// Constructor
	public FileWindow() {
		
		myWindow = new JFrame("New File");
		myWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Container contentPane = myWindow.getContentPane();
		contentPane.setLayout(new BorderLayout());
		
		saveButton = new JButton("Save File");
		saveButton.setSelected(false);
		saveButton.setActionCommand("save");
		saveButton.setMnemonic('S');
		//saveButton.addActionListener(new ScriptWindowButtonListener());
		
		clearButton = new JButton("Clear Contents");
		clearButton.setSelected(false);
		clearButton.setActionCommand("clear");
		clearButton.setMnemonic('B');
		clearButton.addActionListener(new ScriptWindowButtonListener());
		
		cancelButton = new JButton("Quit");
		cancelButton.setSelected(false);
		cancelButton.setActionCommand("quit");
		cancelButton.setMnemonic('Q');
		cancelButton.addActionListener(new ScriptWindowButtonListener());
		
//		loadButton = new JButton("Load Script");
//		loadButton.setSelected(false);
//		loadButton.setActionCommand("load");
//		loadButton.setMnemonic('L');
//		loadButton.addActionListener(new ScriptWindowButtonListener());
//		
//		runButton = new JButton("Run Script");
//		runButton.setSelected(false);
//		runButton.setActionCommand("run");
//		runButton.setMnemonic('L');
//		runButton.addActionListener(new ScriptWindowButtonListener());
		
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1,0));
		//buttonPanel.add(runButton);
		buttonPanel.add(saveButton);
		//buttonPanel.add(loadButton);
		buttonPanel.add(clearButton);
		//buttonPanel.add(cancelButton);
		
		fileChooser = new JFileChooser();
		
		scriptArea = new JTextArea(35,30);
		JScrollPane scroller = new JScrollPane(scriptArea);
		
		textHolder = new JPanel();
		textHolder.setLayout(new BorderLayout());
		textHolder.add(scroller,BorderLayout.CENTER);
		
		contentPane.add(buttonPanel,BorderLayout.NORTH);
		contentPane.add(textHolder,BorderLayout.CENTER);
		myWindow.pack();
		
		
	}
	
	// Listener
	class ScriptWindowButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent ae) 
		{
			
		    if(ae.getActionCommand().equals("clear")){
		    	scriptArea.setText("");
		    }else if(ae.getActionCommand().equals("save")){
		    	//fc.setCurrentDirectory(new File("/Users/jc/Documents/LOGO"));
		    	int bandera = fileChooser.showSaveDialog(myWindow);
				if (bandera == JFileChooser.APPROVE_OPTION) {
					String cadena1 = scriptArea.getText();
				    String cadena2 = cadena1.replace("\r","\n");
				    System.out.println(cadena1);
					try {
						BufferedWriter script = new BufferedWriter(new FileWriter(fileChooser.getSelectedFile()+".txt"));
						script.write(cadena2);
						script.close();
						} 
					catch (Exception ex) {
		            ex.printStackTrace();}
					File file = fileChooser.getSelectedFile();
					JOptionPane.showMessageDialog(myWindow, "File: " + file.getName() + " Saved.\n"); 
					} 
					scriptArea.setCaretPosition(scriptArea.getDocument().getLength()); 
		    }else if(ae.getActionCommand().equals("quit")){
		    	myWindow.setVisible(false);
		    }else if(ae.getActionCommand().equals("load")){
		    	//String arreglo[] = new String[100];
				//int i = 0;
				//fc.setCurrentDirectory(new File("/Users/jc/Documents/LOGO"));
				int bandera = fileChooser.showOpenDialog(myWindow);
				if (bandera == JFileChooser.APPROVE_OPTION) {
					try {
						BufferedReader script = new BufferedReader(new FileReader(fileChooser.getSelectedFile()));
					    scriptArea.read(script,null);
					    script.close();
					    scriptArea.requestFocus();	
						} 
					catch (Exception ex) {
		            ex.printStackTrace();}
					File file = fileChooser.getSelectedFile();
					myWindow.setTitle(file.getName());
					JOptionPane.showMessageDialog(myWindow,file.getName() + ": File loaded.\n");
					scriptArea.setCaretPosition(scriptArea.getDocument().getLength());
				} 
		    }else if(ae.getActionCommand().equals("run")){
		    	System.out.println("LEL");
		    }
		}
	}//BotonesListener
	
	// Getters & Setters
	
	/*public void setMenuBar(LogoMenuBar menuBar) {
		myWindow.setJMenuBar(menuBar);
		this.menuBar = menuBar;
	}*/
	public JFrame getMyWindow() {
		return myWindow;
	}
	public JTextArea getScriptArea() {
		return scriptArea;
	}

	public void setScriptArea(JTextArea scriptArea) {
		this.scriptArea = scriptArea;
	}

	public JButton getLoadButton() {
		return loadButton;
	}

	public void setLoadButton(JButton loadButton) {
		this.loadButton = loadButton;
	}

	public JButton getSaveButton() {
		return saveButton;
	}

	public void setSaveButton(JButton saveButton) {
		this.saveButton = saveButton;
	}

	public JButton getRunButton() {
		return runButton;
	}
	public void setRunButton(JButton runButton) {
		this.runButton = runButton;
	}
	public JButton getCancelButton() {
		return cancelButton;
	}
	public void setCancelButton(JButton cancelButton) {
		this.cancelButton = cancelButton;
	}
	public JButton getClearButton() {
		return clearButton;
	}
	public void setClearButton(JButton clearButton) {
		this.clearButton = clearButton;
	}
}
