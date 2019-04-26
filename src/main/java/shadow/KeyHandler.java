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

package shadow;

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
				model.setRotateY(model.getRotateY() - 15);			break;

			case KeyEvent.VK_NUMPAD6:
			case KeyEvent.VK_RIGHT:
				model.setRotateY(model.getRotateY() + 15);			break;

			case KeyEvent.VK_NUMPAD2:
			case KeyEvent.VK_DOWN:
				model.setRotateX(model.getRotateX() + 15);			break;

			case KeyEvent.VK_NUMPAD8:
			case KeyEvent.VK_UP:
				model.setRotateX(model.getRotateX() - 15);			break;

			case KeyEvent.VK_A:
				model.setRotateZ(model.getRotateZ() - 15);			break;
			
			case KeyEvent.VK_Z:
				model.setRotateZ(model.getRotateZ() + 15);			break;
				
			case KeyEvent.VK_C:
				model.cycleLightColor();        break;
				
			case KeyEvent.VK_1:
				model.switchLight1();
				break;
				
			case KeyEvent.VK_2:
				model.switchLight2();
				break;
				
			case KeyEvent.VK_3:
				model.switchLight3();
				break; 
			
			case KeyEvent.VK_4:
				model.switchLight4();
				break; 
				
			case KeyEvent.VK_5:
				model.switchLight5();
				break; 
				
			case KeyEvent.VK_6:
				model.switchLight6();
				break; 
				
			case KeyEvent.VK_7:
				model.switchLight7();
				break; 
				
			case KeyEvent.VK_SPACE:
				model.jumbleLights();
				break;
				
			case KeyEvent.VK_R:
				model.setRadius(Math.min(model.getRadius() + 0.005f, 0.1f));
				break;
				
			case KeyEvent.VK_F:
				model.setRadius(Math.max(model.getRadius() - 0.005f, 0.01f));
				break;
				
			case KeyEvent.VK_E:
				model.setBrightness(Math.min(model.getBrightness() + 0.01f, 1.0f));
				break;
				
			case KeyEvent.VK_D:
				model.setBrightness(Math.max(model.getBrightness() - 0.01f, 0.0f));
				break;
			
			case KeyEvent.VK_ENTER:
				model.setRotateX(-15);
				model.setRotateY(-15);
				model.setRotateZ(0);				
				model.setRadius(0.02f);
				model.resetLights();
				break;
		}
	}
}

//******************************************************************************
