package light;

import java.awt.Dimension;
import java.awt.event.*;
import javax.swing.JFrame;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.*;

//******************************************************************************

/**
* The <CODE>Application</CODE> class.<P>
*
* @author  Skylar Smith
* @version %I%, %G%
*/
public final class Application
{
	//**********************************************************************
	// Public Class Members
	//**********************************************************************

	public static final String		DEFAULT_NAME = "Light";
	public static final Dimension	DEFAULT_SIZE = new Dimension(1000, 1000);

	//**********************************************************************
	// Main
	//**********************************************************************

	public static void	main(String[] args)
	{
		GLProfile		profile = GLProfile.getDefault();
		GLCapabilities	capabilities = new GLCapabilities(profile);
		//GLCanvas		canvas = new GLCanvas(capabilities);	// Single-buffer
		GLJPanel			canvas = new GLJPanel(capabilities);	// Double-buffer
		JFrame			frame = new JFrame(DEFAULT_NAME);

		// Specify the starting width and height of the canvas itself
		canvas.setPreferredSize(DEFAULT_SIZE);

		// Populate and show the frame
		frame.setBounds(50, 50, 200, 200);
		frame.getContentPane().add(canvas);
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// Exit when the user clicks the frame's close button
		frame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
			});

		// Create the disco ball light
		View view = new View(canvas);
		
	}
}

//******************************************************************************
