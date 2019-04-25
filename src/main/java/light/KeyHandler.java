//******************************************************************************
// Copyright (C) 2016 University of Oklahoma Board of Trustees.
//******************************************************************************
// Last modified: Wed Feb 27 17:33:04 2019 by Chris Weaver
//******************************************************************************
// Major Modification History:
//
// 20160225 [weaver]:	Original file.
// 20190227 [weaver]:	Updated to use model and asynchronous event handling.
//
//******************************************************************************
// Notes:
//
//******************************************************************************

package light;

//import java.lang.*;
import java.awt.Component;
import java.awt.event.*;
import java.awt.geom.Point2D;
import utilities.Utilities;

//******************************************************************************

/**
 * The <CODE>KeyHandler</CODE> class.<P>
 *
 * @author  Chris Weaver
 * @version %I%, %G%
 */
public final class KeyHandler extends KeyAdapter
{
	//**********************************************************************
	// Private Members
	//**********************************************************************

	// State (internal) variables
	private final View	light;
	private final Model model;

	//**********************************************************************
	// Constructors and Finalizer
	//**********************************************************************

	public KeyHandler(View light, Model model)
	{
		this.light = light;
		this.model = model;

		Component	component = light.getCanvas();

		component.addKeyListener(this);
	}

	//**********************************************************************
	// Override Methods (KeyListener)
	//**********************************************************************

	public void		keyPressed(KeyEvent e)
	{
		
		switch (e.getKeyCode())
		{
			case KeyEvent.VK_NUMPAD4:
			case KeyEvent.VK_LEFT:
				model.setRotateY(-15);			break;

			case KeyEvent.VK_NUMPAD6:
			case KeyEvent.VK_RIGHT:
				model.setRotateY(15);			break;

			case KeyEvent.VK_NUMPAD2:
			case KeyEvent.VK_DOWN:
				model.setRotateX(15);			break;

			case KeyEvent.VK_NUMPAD8:
			case KeyEvent.VK_UP:
				model.setRotateX(-15);			break;

			case KeyEvent.VK_A:
				model.setRotateZ(-15);			break;
			
			case KeyEvent.VK_Z:
				model.setRotateZ(15);			break;
				
			case KeyEvent.VK_C:
				model.cycleLightColor();        break;
				
			case KeyEvent.VK_ENTER:
				model.setRotateX(0);
				model.setRotateY(0);
				model.setRotateY(0);				break;
		}
	}
}

//******************************************************************************
