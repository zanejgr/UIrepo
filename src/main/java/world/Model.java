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

package World;
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
	private final World			world;

	// Model variables
	private Point2D.Double		origin;			// Current origin coords
	private float 				rotateX;			// Rotation about X-axis
	private float 				rotateY;			// Rotation about Y-axis
	private float 				rotateZ;			// Rotation about Z-axis

	//**********************************************************************
	// Constructors and Finalizer
	//**********************************************************************

	public Model(World world)
	{
		this.world = world;

		// Initialize user-adjustable variables (with reasonable default values)
		origin = new Point2D.Double(0.0, 0.0);
		rotateX = 0;
		rotateY = -165;
		rotateZ = -15;
	}

	//**********************************************************************
	// Public Methods (Access Variables)
	//**********************************************************************
	
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
	
	public void	setOriginInSceneCoordinates(Point2D.Double q)
	{
		world.getCanvas().invoke(false, new BasicUpdater() {
			public void	update(GL2 gl) {
				origin = new Point2D.Double(q.x, q.y);
			}
		});;
	}

	public void	setOriginInViewCoordinates(Point q)
	{
		world.getCanvas().invoke(false, new ViewPointUpdater(q) {
			public void	update(double[] p) {
				origin = new Point2D.Double(p[0], p[1]);
			}
		});;
	}
	
	public void setRotateX(float x)
	{
		rotateX += x % 360;
	}
	
	public void setRotateY(float y)
	{
		rotateY += y % 360;
	}
	
	public void setRotateZ(float z)
	{
		rotateZ += z % 360;
	}
	
	public void resetRotate()
	{
		rotateX = 0;
		rotateY = -165;
		rotateZ = -15;
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
			int		h = world.getHeight();
			double[]	p = Utilities.mapViewToScene(gl, q.x, h - q.y, 0.0);

			update(p);
		}

		public abstract void	update(double[] p);
	}
}

//******************************************************************************
