package Custom;

import java.awt.Component;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.UIManager;

import ie.tudublin.Visual;
import processing.core.PShape;

public class Mesh_Manager extends Visual
{
	public PShape Get_Shape_File(String path_to_file)
    {
        return loadShape(path_to_file);
    } 
    
    public String Find_File()
    {

        try 
        {   UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");  }
        catch (Exception e)
        {   System.out.println("Look and Feel not set");    }
        
        JFileChooser shape_file_finder = new JFileChooser(System.getProperty("user.dir") ) //open a file explorer in the current folder that we're in
        {   // Make sure that the window is on top
            @Override
            protected JDialog createDialog(Component parent) 
            {
                // intercept the dialog created by JFileChooser
                JDialog dialog = super.createDialog(parent);
                dialog.setAlwaysOnTop(true);
                return dialog;
            }
        };
        int status = shape_file_finder.showOpenDialog(shape_file_finder.getParent()); //
        

        if (status == JFileChooser.APPROVE_OPTION) // user selected something
        {
            return shape_file_finder.getSelectedFile().getAbsolutePath();
        }
        else // User cancelled
        {
            return "No OBJ file selected";
        }
        
    }
}
