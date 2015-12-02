package so.gui.shell;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/**
 * Clase Shell
 * @extends JPanel
 * @author JuanPTC
 * Es un panel con texto que simula el ambiente de una terminal de comandos.
 * Se utiliza para leer los commandos del usuario por linea.
 */
public class Shell extends JPanel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String command = "";
	private String promptCTE = ">_";
	private int promptLength = 2;
	//componentes
	private JTextArea history;
	private JTextArea curCommand;
	private JPanel textHolder;
	private JScrollPane Scroller;
	
	

	/**
	 * Constructor
	 * Crea todos los componentes del Panel.
	 */
	public Shell() {
		
		this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		history = new JTextArea(10,50);
		history.append("\n\n\n\n\n\n\n\n\n\n");
		history.setEditable(false);
		history.setFont(new Font("Menlo", Font.PLAIN, 14));
		curCommand = new JTextArea(promptCTE,1,50);
		curCommand.setNavigationFilter(new PromptNavigationFilter(promptLength,curCommand));
		curCommand.addKeyListener(new CurCommandKeyListener());
		curCommand.setFont(new Font("Menlo", Font.PLAIN, 14));
		
		
		textHolder = new JPanel();
		textHolder.setLayout(new BoxLayout(textHolder,BoxLayout.Y_AXIS));
		textHolder.add(history);
		textHolder.add(curCommand);
		
		Scroller = new JScrollPane(textHolder);
		Scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		Scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		Scroller.getVerticalScrollBar().addAdjustmentListener(new ScrollerListener());
		Scroller.getHorizontalScrollBar().setPreferredSize (new Dimension(0,0));
		
		//this.g
		this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		this.add(Scroller);
		
	}//constructor
	
	
	/**
	 * Clase Current Command key listener.
	 * @implements KeyListner.
	 * @author JuanPTC
	 * Escucha a un JTextComponent para saber que se teclea en el.
	 * Se utiliza para detectar el evento de la tecla "ENTER" y detectar el comando del usuario.
	 */
	class CurCommandKeyListener implements KeyListener {

		@Override
		public void keyPressed(KeyEvent key) {
			
			if(key.getKeyChar()=='\n')
				curCommand.getInputMap().put(KeyStroke.getKeyStroke("ENTER"),"doNothing");
		}

		@Override
		public void keyReleased(KeyEvent key) {
			
		}

		@Override
		public void keyTyped(KeyEvent key) {
			
			if(key.getKeyChar()=='\n')
			{
				curCommand.getInputMap().put(KeyStroke.getKeyStroke("ENTER"),"doNothing");
				command = curCommand.getText();
				curCommand.setText(promptCTE);
				
				command = command.replaceFirst(promptCTE,"");
				history.append("\n"+promptCTE+command);
				//System.out.println(command);
			}
		}	
	}//CurCommandKeyListener
	
	/**
	 * Clase Scroll Listener
	 * @implements AdjustmentLister
	 * @author JuanPTC
	 * Escucha a un JScrollBar para saber si se mueve.
	 * Se utiliza para mantener al JScrollBar abajo si este se ajusta sin ser movido por el usuario.
	 */
	class ScrollerListener implements AdjustmentListener {

		@Override
		public void adjustmentValueChanged(AdjustmentEvent e) {
			if (!e.getValueIsAdjusting()) 
				return;
			e.getAdjustable().setValue(e.getAdjustable().getMaximum());
		}
	}//ScrollerListener
	
	//Getters y setters------------------------
	
	public JTextArea getCurCommand() {
		return curCommand;
	}
	
	public JScrollPane getScroller() {
		return Scroller;
	}

	public void setScroller(JScrollPane scroller) {
		Scroller = scroller;
	}

	public JTextArea getHistory() {
		return history;
	}
	/**
	 * Resgresa command cuando una clase externa lo pide.
	 * @return command String que contiene el commando del usuario.
	 */
	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getPromptCTE() {
		return promptCTE;
	}

	public void setPromptCTE(String promptCTE) {
		this.promptCTE = promptCTE;
	}

	public int getPromptLength() {
		return promptLength;
	}

	public void setPromptLength(int promptLength) {
		this.promptLength = promptLength;
	}	
	//Getters y setters------------------------
		
}
