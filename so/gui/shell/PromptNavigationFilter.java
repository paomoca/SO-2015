package so.gui.shell;

import java.awt.event.*;  

import javax.swing.*;  
import javax.swing.text.*;

/**
 * Clase Prompt Navigation Filter
 * @extends NavigationFilter
 * @author JuanPTC
 * Es un filtro de navegaci�n personalisado para un JTextComponent.
 * Se utliza para limitar los lugares donde puede ingresar o quitar texto el usuario
 * esto es para que solo pueda escribir despues del prompt definido.
 */
public class PromptNavigationFilter extends NavigationFilter {
	
	private int promptLength;
	private Action backspace;

	/**
	 * Constructor
	 * @param Int promptLength - Tama�o en caracteres del prompt
	 * @param JTextoComponent prompt - Componente donde se encuentra el prompt.
	 * 
	 */
	public PromptNavigationFilter(int promptLength, JTextComponent prompt) {
		
		this.promptLength = promptLength;
		backspace = prompt.getActionMap().get("delete-previous");  
        prompt.getActionMap().put("delete-previous", new BackspaceAction());  
        prompt.setCaretPosition(promptLength); 
	}
	
	/**
	 * Limita la posici�n del carrete de texto.
	 */
	public void setDot(NavigationFilter.FilterBypass fb, int dot, Position.Bias bias) { 
		
        fb.setDot(Math.max(dot, promptLength), bias); 
    }  
  
	/**
	 * Limita los lugares que se pueden seleccionar del carrete de texto.
	 */
    public void moveDot(NavigationFilter.FilterBypass fb, int dot, Position.Bias bias) {
    	
        fb.moveDot(Math.max(dot, promptLength), bias); 
    }
    
    /**
     * Clase Backspace Action
     * @extends AbstractAction
     * @author JuanPTC
     * Detecta como acci�n la tecla "BACKSPACE" y limita su huso.
     */
    class BackspaceAction extends AbstractAction {
    	
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			
            JTextComponent component = (JTextComponent)e.getSource();  
            if (component.getCaretPosition() > promptLength)  
            {  
                backspace.actionPerformed( null );  
            }  
        }  
    }//BackspaceAction	
	
}
