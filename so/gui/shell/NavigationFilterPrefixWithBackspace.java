package so.gui.shell;

import java.awt.event.*;  
import javax.swing.*;  
import javax.swing.text.*;  
 /**
  * 
  * @author JuanPTC
  *
  */
public class NavigationFilterPrefixWithBackspace extends NavigationFilter  
{  
    private int prefixLength;  
    private Action deletePrevious;  
  
    public NavigationFilterPrefixWithBackspace(int prefixLength, JTextComponent component)  
    {  
        this.prefixLength = prefixLength;  
        deletePrevious = component.getActionMap().get("delete-previous");  
        component.getActionMap().put("delete-previous", new BackspaceAction());  
        component.setCaretPosition(prefixLength);  
    }  
  
    public void setDot(NavigationFilter.FilterBypass fb, int dot, Position.Bias bias)  
    {  
        fb.setDot(Math.max(dot, prefixLength), bias); 
        System.out.println("setd");
    }  
  
    public void moveDot(NavigationFilter.FilterBypass fb, int dot, Position.Bias bias)  
    {  
        fb.moveDot(Math.max(dot, prefixLength), bias); 
        System.out.println("moved");
    }  
  
    class BackspaceAction extends AbstractAction  
    {  
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e)  
        {  
            JTextComponent component = (JTextComponent)e.getSource();  
            System.out.println("lel");
            if (component.getCaretPosition() > prefixLength)  
            {  
                deletePrevious.actionPerformed( null );  
            }  
        }  
    }
}
