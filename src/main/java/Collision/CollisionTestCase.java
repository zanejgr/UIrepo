//******************************************************************************
// Copyright (C) 2016-2019 University of Oklahoma Board of Trustees.
//******************************************************************************
// Last modified: Sun Feb  3 12:42:53 2019 by Chris Weaver
//******************************************************************************
// Major Modification History:
//
// 20160209 [weaver]:	Original file.
// 20190129 [weaver]:	Updated to JOGL 2.3.2 and cleaned up.
// 20190203 [weaver]:	Additional cleanup and more extensive comments.
//
//******************************************************************************
// Notes:
//
// Warning! This code uses depricated features of OpenGL, including immediate
// mode vertex attribute specification, for sake of easier classroom learning.
// See www.khronos.org/opengl/wiki/Legacy_OpenGL
//
//******************************************************************************

package Collision;

//import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.gl2.GLUT;

public final class CollisionTestCase
	implements GLEventListener
{
	//**********************************************************************
	// Public Class Members
	//**********************************************************************

	public static final GLU	GLU = new GLU();
	public static final GLUT	GLUT = new GLUT();
	public static final Random	RANDOM = new Random();

	//**********************************************************************
	// Private Members
	//**********************************************************************

	// State (internal) variables
	private int				w;				// Canvas width
	private int				h;				// Canvas height
	private int				k = 0;			// Animation counter
	private TextRenderer		renderer;
	
	private int radius = 50;
	Velocity v = new Velocity(5, 5);
	Position p = new Position(500 + v.x, 200 + v.y);

	//**********************************************************************
	// Main
	//**********************************************************************

	public static void main(String[] args)
	{
		GLProfile		profile = GLProfile.getDefault();
		GLCapabilities	capabilities = new GLCapabilities(profile);
		GLCanvas		canvas = new GLCanvas(capabilities);	// Single-buffer
		//GLJPanel		canvas = new GLJPanel(capabilities);	// Double-buffer
		JFrame			frame = new JFrame("Application");

		// Specify the starting width and height of the canvas itself
		canvas.setPreferredSize(new Dimension(1280, 720));

		// Populate and show the frame
		frame.setBounds(50, 50, 600, 600);
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

		// Register this class to update whenever OpenGL needs it
		canvas.addGLEventListener(new CollisionTestCase());

		// Have OpenGL call display() to update the canvas 60 times per second
		FPSAnimator	animator = new FPSAnimator(canvas, 60);

		animator.start();
	}

	//**********************************************************************
	// Override Methods (GLEventListener)
	//**********************************************************************

	// Called immediately after the GLContext of the GLCanvas is initialized.
	public void	init(GLAutoDrawable drawable)
	{
		w = drawable.getSurfaceWidth();
		h = drawable.getSurfaceHeight();

		renderer = new TextRenderer(new Font("Serif", Font.PLAIN, 18),
									true, true);

		initPipeline(drawable);
	}

	// Notification to release resources for the GLContext.
	public void	dispose(GLAutoDrawable drawable)
	{
		renderer = null;
	}

	// Called to initiate rendering of each frame into the GLCanvas.
	public void	display(GLAutoDrawable drawable)
	{
		update(drawable);
		render(drawable);
	}

	// Called during the first repaint after a resize of the GLCanvas.
	public void	reshape(GLAutoDrawable drawable, int x, int y, int w, int h)
	{
		this.w = w;
		this.h = h;
	}

	//**********************************************************************
	// Private Methods (Rendering)
	//**********************************************************************

	// Update the scene model for the current animation frame.
	private void	update(GLAutoDrawable drawable)
	{
		k++;									// Advance animation counter
		if(p.x + v.x + radius > 1280 || p.x + v.x - radius < 0)
			v.x = -v.x;
		if(p.y + v.y + radius > 720 || p.y + v.y - radius < 0)
			v.y = -v.y;
		p.x = p.x + v.x;
		p.y = p.y + v.y;
	}

	// Render the scene model and display the current animation frame.
	private void	render(GLAutoDrawable drawable)
	{
		GL2	gl = drawable.getGL().getGL2();

		gl.glClear(GL.GL_COLOR_BUFFER_BIT);	// Clear the buffer
		
		setScreenProjection(gl);
		drawBall(gl, p.x, p.y);
	}

	//**********************************************************************
	// Private Methods (Pipeline)
	//**********************************************************************

	// www.khronos.org/registry/OpenGL-Refpages/es2.0/xhtml/glBlendFunc.xml
	private void	initPipeline(GLAutoDrawable drawable)
	{
		GL2	gl = drawable.getGL().getGL2();

		// Make the sky gradient easier by enabling alpha blending.
		// Translucency in 3-D is more difficult and expensive than in 2-D!

		// See com.jogamp.opengl.GL
		gl.glEnable(GL2.GL_POINT_SMOOTH);	// Turn on point anti-aliasing
		gl.glEnable(GL2.GL_LINE_SMOOTH);	// Turn on line anti-aliasing

		gl.glEnable(GL.GL_BLEND);			// Turn on color channel blending
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
	}

	// Position and orient the default camera to view in 2-D, in pixel coords.
	private void	setScreenProjection(GL2 gl)
	{
		GLU	glu = new GLU();

		gl.glMatrixMode(GL2.GL_PROJECTION);		// Prepare for matrix xform
		gl.glLoadIdentity();						// Set to identity matrix
		glu.gluOrtho2D(0.0f, 1280.0f, 0.0f, 720.0f);// 2D translate and scale
	}
	
	private void drawBall(GL2 gl, int x, int y)
	{
		setColor(gl, 255, 0, 0);
		drawOval(gl, x, y, radius, radius);
		setColor(gl, 255, 255, 255);
		fillOval(gl, x, y, radius, radius);
	}

	// Warning! Text is drawn in unprojected canvas/viewport coordinates.
	// For more on text rendering, the example on this page is long but helpful:
	// jogamp.org/jogl-demos/src/demos/j2d/FlyingText.java
	private void	drawText(GLAutoDrawable drawable)
	{
		renderer.beginRendering(w, h);
		renderer.setColor(0.75f, 0.75f, 0.75f, 1.0f);
		renderer.draw("Application", 2, h - 14);
		renderer.endRendering();
	}

	//**********************************************************************
	// Private Methods (Utility Functions)
	//**********************************************************************

	private void	setColor(GL2 gl, int r, int g, int b, int a)
	{
		gl.glColor4f(r / 255.0f, g / 255.0f, b / 255.0f, a / 255.0f);
	}

	private void	setColor(GL2 gl, int r, int g, int b)
	{
		setColor(gl, r, g, b, 255);
	}

	private void	fillOval(GL2 gl, int cx, int cy, int w, int h)
	{
		gl.glBegin(GL2.GL_POLYGON);

		for (int i=0; i<32; i++)
		{
			double	a = (2.0 * Math.PI) * (i / 32.0);

			gl.glVertex2d(cx + w * Math.cos(a), cy + h * Math.sin(a));
		}

		gl.glEnd();
	}

	private void	drawOval(GL2 gl, int cx, int cy, int w, int h)
	{
		gl.glBegin(GL.GL_LINE_LOOP);

		for (int i=0; i<32; i++)
		{
			double	a = (2.0 * Math.PI) * (i / 32.0);

			gl.glVertex2d(cx + w * Math.cos(a), cy + h * Math.sin(a));
		}

		gl.glEnd();
	}

}

//******************************************************************************
