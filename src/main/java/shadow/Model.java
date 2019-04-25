//******************************************************************************
// Copyright (C) 2019 University of Oklahoma Board of Trustees.
//******************************************************************************
// Last modified: Wed Feb 27 17:32:08 2019 by Chris Weaver
//******************************************************************************
// Major Modification History:
//
// 20190227 [weaver]:	Original file.
//
//******************************************************************************
//
// The model manages all of the user-adjustable variables utilized in the scene.
// (You can store non-user-adjustable scene data here too, if you want.)
//
// For each variable that you want to make interactive:
//
//   1. Add a member of the right type
//   2. Initialize it to a reasonable default value in the constructor.
//   3. Add a method to access a copy of the variable's current value.
//   4. Add a method to modify the variable.
//
// Concurrency management is important because the JOGL and the Java AWT run on
// different threads. The modify methods use the GLAutoDrawable.invoke() method
// so that all changes to variables take place on the JOGL thread. Because this
// happens at the END of GLEventListener.display(), all changes will be visible
// to the View.update() and render() methods in the next animation cycle.
//
//******************************************************************************

package shadow;

import java.awt.Color;
//import java.lang.*;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.*;
import com.jogamp.opengl.*;

import utilities.Utilities;

//******************************************************************************

/**
 * The <CODE>Model</CODE> class.
 *
 * @author  Chris Weaver
 * @version %I%, %G%
 */
public final class Model
{
	//**********************************************************************
	// Private Members
	//**********************************************************************

	// State (internal) variables
	private final View			light;

	// Model variables
	private Point2D.Double		origin;			// Current origin coords
	private float 				rotateX;			// Rotation about X-axis
	private float 				rotateY;			// Rotation about Y-axis
	private float 				rotateZ;			// Rotation about Z-axis
	
	private Velocity lightVelocity;
	private Position lightPosition;
	
	private Velocity lightVelocity2;
	private Position lightPosition2;
	
	private Velocity lightVelocity3;
	private Position lightPosition3;
	
	private Velocity lightVelocity4;
	private Position lightPosition4;
	
	private Velocity lightVelocity5;
	private Position lightPosition5;
	
	private Velocity lightVelocity6;
	private Position lightPosition6;
	
	private Velocity lightVelocity7;
	private Position lightPosition7;
	
	private float lightRadius;
	
	private LightColor lightColor;
	private LightColor lightColor2;
	private LightColor lightColor3;
	private LightColor lightColor4;
	private LightColor lightColor5;
	private LightColor lightColor6;
	private LightColor lightColor7;
	
	private boolean light1On;
	private boolean light2On;
	private boolean light3On;
	private boolean light4On;
	private boolean light5On;
	private boolean light6On;
	private boolean light7On;
	
	private LightColor[] colorList = {
			new LightColor(1.0f, 1.0f, 1.0f), new LightColor(1.0f, 2.0f, 1.0f), new LightColor(1.0f, 1.0f, 2.0f), new LightColor(2.0f, 1.0f, 1.0f),
			new LightColor(2.0f, 2.0f, 1.0f), new LightColor(2.0f, 1.0f, 2.0f), new LightColor(1.0f, 2.0f, 2.0f)
	};
	
	private int colorCounter;
	private int colorCounter2;
	private int colorCounter3;
	private int colorCounter4;
	private int colorCounter5;
	private int colorCounter6;
	private int colorCounter7;

	//**********************************************************************
	// Constructors and Finalizer
	//**********************************************************************

	public Model(View light)
	{
		this.light = light;

		// Initialize user-adjustable variables (with reasonable default values)
		origin = new Point2D.Double(0.0, 0.0);
		rotateX = -15;
		rotateY = -15;
		rotateZ = 0;
		lightVelocity = new Velocity(0.01, 0.03);
		lightPosition = new Position(0.5, 0.5);
		
		lightVelocity2 = new Velocity(0.03, 0.02);
		lightPosition2 = new Position(0.3, 0.3);
		
		lightVelocity3 = new Velocity(0.01, 0.04);
		lightPosition3 = new Position(0.2, 0.4);
		
		lightVelocity4 = new Velocity(0.01, 0.01);
		lightPosition4 = new Position(0.2, 0.3);
		
		lightVelocity5 = new Velocity(0.01, -0.02);
		lightPosition5 = new Position(-0.3, 0.4);
		
		lightVelocity6 = new Velocity(-0.01, -0.02);
		lightPosition6 = new Position(-0.5, -0.3);
		
		lightVelocity7 = new Velocity(-0.04, 0.02);
		lightPosition7 = new Position(-0.5, 0.4);
		
		lightColor = colorList[0];
		lightColor2 = colorList[1];
		lightColor3 = colorList[2];
		lightColor4 = colorList[3];
		lightColor5 = colorList[4];
		lightColor6 = colorList[5];
		lightColor7 = colorList[6];
		
		
		light1On = true;
		light2On = true;
		light3On = true;
		light4On = false;
		light5On = false;
		light6On = false;
		light7On = false;
		
		colorCounter = 0;
		colorCounter2 = 1;
		colorCounter3 = 2;
		colorCounter4 = 3;
		colorCounter5 = 4;
		colorCounter6 = 5;
		colorCounter7 = 6;
		
		lightRadius = 0.02f;
	}

	//**********************************************************************
	// Public Methods (Access Variables)
	//**********************************************************************
	
	public float getRadius()
	{
		return lightRadius;
	}
	
	public boolean isLight1On()
	{
		return light1On;
	}
	
	public boolean isLight2On()
	{
		return light2On;
	}
	
	public boolean isLight3On()
	{
		return light3On;
	}
	
	public boolean isLight4On()
	{
		return light4On;
	}
	
	public boolean isLight5On()
	{
		return light5On;
	}
	
	public boolean isLight6On()
	{
		return light6On;
	}
	
	public boolean isLight7On()
	{
		return light7On;
	}
	public LightColor getLightColor()
	{
		return lightColor;
	}
	
	public LightColor getLightColor2()
	{
		return lightColor2;
	}
	
	public LightColor getLightColor3()
	{
		return lightColor3;
	}
	
	public LightColor getLightColor4()
	{
		return lightColor4;
	}
	
	public LightColor getLightColor5()
	{
		return lightColor5;
	}
	
	public LightColor getLightColor6()
	{
		return lightColor6;
	}
	
	public LightColor getLightColor7()
	{
		return lightColor7;
	}
	
	public Velocity getLightVelocity()
	{
		return this.lightVelocity;
	}
	
	public Position getLightPosition()
	{
		return this.lightPosition;
	}
	
	public Velocity getLightVelocity2()
	{
		return this.lightVelocity2;
	}
	
	public Position getLightPosition2()
	{
		return this.lightPosition2;
	}
	
	public Velocity getLightVelocity3()
	{
		return this.lightVelocity3;
	}
	
	public Position getLightPosition3()
	{
		return this.lightPosition3;
	}
	
	public Velocity getLightVelocity4()
	{
		return this.lightVelocity4;
	}
	
	public Position getLightPosition4()
	{
		return this.lightPosition4;
	}
	
	public Velocity getLightVelocity5()
	{
		return this.lightVelocity5;
	}
	
	public Position getLightPosition5()
	{
		return this.lightPosition5;
	}
	
	public Velocity getLightVelocity6()
	{
		return this.lightVelocity6;
	}
	
	public Position getLightPosition6()
	{
		return this.lightPosition6;
	}
	
	public Velocity getLightVelocity7()
	{
		return this.lightVelocity7;
	}
	
	public Position getLightPosition7()
	{
		return this.lightPosition7;
	}
	
	public Point2D.Double	getOrigin()
	{
		return new Point2D.Double(origin.x, origin.y);
	}
	
	public float getRotateX()
	{
		return rotateX;
	}
	
	public float getRotateY()
	{
		return rotateY;
	}
	
	public float getRotateZ()
	{
		return rotateZ;
	}

	//**********************************************************************
	// Public Methods (Modify Variables)
	//**********************************************************************

	public void resetLights()
	{
		light1On = true;
		light2On = true;
		light3On = true;
		light4On = false;
		light5On = false;
		light6On = false;
		light7On = false;
		
		lightVelocity = new Velocity(0.01, 0.03);	
		lightVelocity2 = new Velocity(0.03, 0.02);
		lightVelocity3 = new Velocity(0.01, 0.04);
		lightVelocity4 = new Velocity(0.01, 0.01);
		lightVelocity5 = new Velocity(0.01, -0.02);
		lightVelocity6 = new Velocity(-0.01, -0.02);
		lightVelocity7 = new Velocity(-0.04, 0.02);
	}
	
	public void setRadius(float r)
	{
		lightRadius = r;
	}
	
	public void jumbleLights()
	{
		lightVelocity.x = Math.random() / 10.0;
		lightVelocity.y = Math.random() / 10.0;
		
		lightVelocity2.x = Math.random() / 10.0;
		lightVelocity2.y = Math.random() / 10.0;
		
		lightVelocity3.x = Math.random() / 10.0;
		lightVelocity3.y = Math.random() / 10.0;
		
		lightVelocity4.x = Math.random() / 10.0;
		lightVelocity4.y = Math.random() / 10.0;
		
		lightVelocity5.x = Math.random() / 10.0;
		lightVelocity5.y = Math.random() / 10.0;
		
		lightVelocity6.x = Math.random() / 10.0;
		lightVelocity6.y = Math.random() / 10.0;
		
		lightVelocity7.x = Math.random() / 10.0;
		lightVelocity7.y = Math.random() / 10.0;
	}
	
	public void switchLight1()
	{
		light1On = !light1On;
	}
	
	public void switchLight2()
	{
		light2On = !light2On;
	}
	
	public void switchLight3()
	{
		light3On = !light3On;
	}
	
	public void switchLight4()
	{
		light4On = !light4On;
	}
	
	public void switchLight5()
	{
		light5On = !light5On;
	}
	
	public void switchLight6()
	{
		light6On = !light6On;
	}
	
	public void switchLight7()
	{
		light7On = !light7On;
	}
	
	public void cycleLightColor()
	{
		++colorCounter;
		if(colorCounter > 6)
			colorCounter = 0;
		this.lightColor = colorList[colorCounter];
		
		++colorCounter2;
		if(colorCounter2 > 6)
			colorCounter2 = 0;
		this.lightColor2 = colorList[colorCounter2];
		
		++colorCounter3;
		if(colorCounter3 > 6)
			colorCounter3 = 0;
		this.lightColor3 = colorList[colorCounter3];
		
		++colorCounter4;
		if(colorCounter4 > 6)
			colorCounter4 = 0;
		this.lightColor4 = colorList[colorCounter4];
		
		++colorCounter5;
		if(colorCounter5 > 6)
			colorCounter5 = 0;
		this.lightColor5 = colorList[colorCounter5];
		
		++colorCounter6;
		if(colorCounter6 > 6)
			colorCounter6 = 0;
		this.lightColor6 = colorList[colorCounter6];
		
		++colorCounter7;
		if(colorCounter7 > 6)
			colorCounter7 = 0;
		this.lightColor7 = colorList[colorCounter7];
		
	}
	
	public void setLightVelocity(double x, double y)
	{
		this.lightVelocity.x = x;
		this.lightVelocity.y = y;
	}
	
	public void setLightPosition(double x, double y)
	{
		this.lightPosition.x = x;
		this.lightPosition.y = y;
	}
	
	public void setLightVelocity2(double x, double y)
	{
		this.lightVelocity2.x = x;
		this.lightVelocity2.y = y;
	}
	
	public void setLightPosition2(double x, double y)
	{
		this.lightPosition2.x = x;
		this.lightPosition2.y = y;
	}
	
	public void setLightVelocity3(double x, double y)
	{
		this.lightVelocity3.x = x;
		this.lightVelocity3.y = y;
	}
	
	public void setLightPosition3(double x, double y)
	{
		this.lightPosition3.x = x;
		this.lightPosition3.y = y;
	}
	
	public void setLightVelocity4(double x, double y)
	{
		this.lightVelocity4.x = x;
		this.lightVelocity4.y = y;
	}
	
	public void setLightPosition4(double x, double y)
	{
		this.lightPosition4.x = x;
		this.lightPosition4.y = y;
	}
	
	public void setLightVelocity5(double x, double y)
	{
		this.lightVelocity5.x = x;
		this.lightVelocity5.y = y;
	}
	
	public void setLightPosition5(double x, double y)
	{
		this.lightPosition5.x = x;
		this.lightPosition5.y = y;
	}
	
	public void setLightVelocity6(double x, double y)
	{
		this.lightVelocity6.x = x;
		this.lightVelocity6.y = y;
	}
	
	public void setLightPosition6(double x, double y)
	{
		this.lightPosition6.x = x;
		this.lightPosition6.y = y;
	}
	
	public void setLightVelocity7(double x, double y)
	{
		this.lightVelocity7.x = x;
		this.lightVelocity7.y = y;
	}
	
	public void setLightPosition7(double x, double y)
	{
		this.lightPosition7.x = x;
		this.lightPosition7.y = y;
	}
	
	public void	setOriginInSceneCoordinates(Point2D.Double q)
	{
		light.getCanvas().invoke(false, new BasicUpdater() {
			public void	update(GL2 gl) {
				origin = new Point2D.Double(q.x, q.y);
			}
		});;
	}

	public void	setOriginInViewCoordinates(Point q)
	{
		light.getCanvas().invoke(false, new ViewPointUpdater(q) {
			public void	update(double[] p) {
				origin = new Point2D.Double(p[0], p[1]);
			}
		});;
	}
	
	public void setRotateX(float x)
	{
		rotateX = x % 360;
	}
	
	public void setRotateY(float y)
	{
		rotateY = y % 360;
	}
	
	public void setRotateZ(float z)
	{
		rotateZ = z % 360;
	}
	
	//**********************************************************************
	// Inner Classes
	//**********************************************************************

	// Convenience class to simplify the implementation of most updaters.
	private abstract class BasicUpdater implements GLRunnable
	{
		public final boolean	run(GLAutoDrawable drawable)
		{
			GL2	gl = drawable.getGL().getGL2();

			update(gl);

			return true;	// Let animator take care of updating the display
		}

		public abstract void	update(GL2 gl);
	}

	// Convenience class to simplify updates in cases in which the input is a
	// single point in view coordinates (integers/pixels).
	private abstract class ViewPointUpdater extends BasicUpdater
	{
		private final Point	q;

		public ViewPointUpdater(Point q)
		{
			this.q = q;
		}

		public final void	update(GL2 gl)
		{
			int		h = light.getHeight();
			double[]	p = Utilities.mapViewToScene(gl, q.x, h - q.y, 0.0);

			update(p);
		}

		public abstract void	update(double[] p);
	}
}

//******************************************************************************
