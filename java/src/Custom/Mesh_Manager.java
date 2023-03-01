package Custom;

import java.awt.Component;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.UIManager;

import ie.tudublin.Visual;
import processing.core.PShape;


// Will make loading of custom shapes easier
public class Mesh_Manager extends Visual
{
	public PShape Get_Shape_File(String path_to_file)
    {
        return loadShape(path_to_file); // will expand later
    } 
    


    public String Find_File()
    {
        try 
        {   UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");  } // found online... we want a windows look!
        catch (Exception e)
        {   System.out.println("\nLook and Feel not set... What happened???");    }
        
        JFileChooser shape_file_finder = new JFileChooser(System.getProperty("user.dir") ) //open a file explorer in the current folder that we're in
        {   // Make sure that the window is on top
            @Override
            protected JDialog createDialog(Component parent) 
            {
                // intercept the dialog created by JFileChooser
                JDialog dialog = super.createDialog(parent);
                dialog.setAlwaysOnTop(true); // here we go!
                return dialog;
            }
        };
        int status = shape_file_finder.showOpenDialog(shape_file_finder.getParent()); // Similar to threads in C, we use an int for our status
        

        if (status == JFileChooser.APPROVE_OPTION) // user selected something
        {
            return shape_file_finder.getSelectedFile().getAbsolutePath(); // get the chosen file path
        }
        else // User cancelled
        {
            return "No OBJ file selected";
        }
        
    }
}
