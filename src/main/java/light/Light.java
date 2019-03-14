package light;

//import java.lang.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.util.List;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.glu.*;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.awt.TextRenderer;
import light.KeyHandler;
import light.Model;
import utilities.Utilities;

//******************************************************************************

/**
 * The <CODE>Light</CODE> class.<P>
 *
 * @author  Andy Vu
 * @version %I%, %G%
 */
public final class Light
	implements GLEventListener
{
	//**********************************************************************
	// Private Class Members
	//**********************************************************************

	private static final int			DEFAULT_FRAMES_PER_SECOND = 60;
	private static final DecimalFormat	FORMAT = new DecimalFormat("0.000");

	//**********************************************************************
	// Private Members
	//**********************************************************************

	// State (internal) variables
	private final GLJPanel			canvas;
	private int						w;				// Canvas width
	private int						h;				// Canvas height

	private final FPSAnimator		animator;
	private int						counter;			// Frame counter
	private int lightmove;

	private final Model				model;

	private final KeyHandler			keyHandler;

	//**********************************************************************
	// Constructors and Finalizer
	//**********************************************************************

	public Light(GLJPanel canvas)
	{
		this.canvas = canvas;

		// Initialize model (scene data and parameter manager)
		model = new Model(this);

		// Initialize controller (interaction handlers)
		keyHandler = new KeyHandler(this, model);

		// Initialize rendering
		counter = 0;
		lightmove = 0;
		canvas.addGLEventListener(this);

		// Initialize animation
		animator = new FPSAnimator(canvas, DEFAULT_FRAMES_PER_SECOND);
		animator.start();
	}

	//**********************************************************************
	// Getters and Setters
	//**********************************************************************

	public GLJPanel	getCanvas()
	{
		return canvas;
	}

	public int	getWidth()
	{
		return w;
	}

	public int	getHeight()
	{
		return h;
	}

	//**********************************************************************
	// Override Methods (GLEventListener)
	//**********************************************************************

	public void	init(GLAutoDrawable drawable)
	{
		w = drawable.getSurfaceWidth();
		h = drawable.getSurfaceHeight();

		GL2 gl = drawable.getGL().getGL2();

		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);	// Black background

		// See com.jogamp.opengl.GL
		gl.glEnable(GL2.GL_POINT_SMOOTH);		// Turn on point anti-aliasing
		gl.glEnable(GL2.GL_LINE_SMOOTH);			// Turn on line anti-aliasing

		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glDepthFunc(GL2.GL_LESS);

		gl.glEnable(GL.GL_BLEND);				// Turn on color channel blending
		gl.glEnable(GL2.GL_LIGHTING);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
	}

	public void	display(GLAutoDrawable drawable)
	{
		GL2 gl = drawable.getGL().getGL2();
		GLU glu = GLU.createGLU();

		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT); 
		Point2D.Double	origin = model.getOrigin();

		float			xmin = (float)(origin.x - 1.0);
		float			xmax = (float)(origin.x + 1.0);
		float			ymin = (float)(origin.y - 1.0);
		float			ymax = (float)(origin.y + 1.0);

		gl.glMatrixMode(GL2.GL_PROJECTION);		// Prepare for matrix xform
		gl.glLoadIdentity();						// Set to identity matrix
		glu.gluOrtho2D(xmin, xmax, ymin, ymax);	// 2D translate and scale

		update(drawable);

		gl.glEnable( GL2.GL_LIGHTING );  
		gl.glEnable( GL2.GL_LIGHT0 );  
		gl.glEnable( GL2.GL_NORMALIZE );  
		//gl.glEnable(GL2.GL_COLOR_MATERIAL);

		//gl.glVertexAttribPointer(0, 3, GL2.GL_FLOAT, GL2.GL_FALSE, 6 * 32, );

		
		gl.glFlush();
		render(drawable);
		
		//Creates a moving square that moves in a circular path mimic-ing the same path as the light
		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex3d(Math.cos((double)lightmove/10.0)* .5f + 0.01f, Math.sin((double)lightmove/10.0)* .5f + 0.01f, -0.5f);
		gl.glVertex3d(Math.cos((double)lightmove/10.0)* .5f + 0.01f, Math.sin((double)lightmove/10.0)* .5f - 0.01f, -0.5f);
		gl.glVertex3d(Math.cos((double)lightmove/10.0)* .5f - 0.01f, Math.sin((double)lightmove/10.0)* .5f - 0.01f, -0.5f);
		gl.glVertex3d(Math.cos((double)lightmove/10.0)* .5f - 0.01f, Math.sin((double)lightmove/10.0)* .5f + 0.01f, -0.5f);
		gl.glEnd();
		
		// multicolor diffuse 
		float[] diffuseLight = { 1f,1f,1f,0f };  
		gl.glLightfv( GL2.GL_LIGHT0, GL2.GL_DIFFUSE, diffuseLight, 0 ); 

		
		System.out.println("Light X: " + (float)Math.cos((double)lightmove/10.0));
		System.out.println("Light Y: " + (float)Math.sin((double)lightmove/10.0));
		
		//Moves the light around in a circular path
		float[] lightPos = {(float)Math.cos((double)lightmove/10.0) * .5f, (float)Math.sin((double)lightmove/10.0) * .5f, 1.0f , 1.0f};
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightPos, 0 );
		
	}

	public void	reshape(GLAutoDrawable drawable, int x, int y, int w, int h)
	{
		this.w = w;
		this.h = h;
	}

	@Override
	public void dispose(GLAutoDrawable drawable) 
	{
		// Nothing to do here until we add animation
	}

	//**********************************************************************
	// Private Methods (Rendering)
	//**********************************************************************
	private void	update(GLAutoDrawable drawable)
	{
		counter++;									// Advance animation counter
		if(counter % 10 == 0)
			lightmove++;
	}

	private void	render(GLAutoDrawable drawable)
	{
		GL2	gl = drawable.getGL().getGL2();

		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);// Clear the buffer

		// Draw the light cube				
		drawCube(gl);
	}

	//**********************************************************************
	// Private Methods (Scene)
	//**********************************************************************

	private void drawCube(GL2 gl)
	{

		/* Cube will be missing front face */

		// Rotate The Cube On X, Y & Z
		gl.glRotatef(model.getRotateX(),0,0,1);
		gl.glRotatef(model.getRotateY(),0,1,0);
		gl.glRotatef(model.getRotateZ(),1,0,0);

		// White side - BACK
		gl.glBegin(GL2.GL_POLYGON);
		gl.glColor3f(   1.0f,  1.0f, 1.0f );
		gl.glVertex3f(  0.5f, -0.5f, 0.5f );
		gl.glVertex3f(  0.5f,  0.5f, 0.5f );
		gl.glVertex3f( -0.5f,  0.5f, 0.5f );
		gl.glVertex3f( -0.5f, -0.5f, 0.5f );
		gl.glEnd();

		// Purple side - RIGHT
		gl.glBegin(GL2.GL_POLYGON);
		gl.glColor3f(  1.0f,  0.0f,  1.0f );
		gl.glVertex3f( 0.5f, -0.5f, -0.5f );
		gl.glVertex3f( 0.5f,  0.5f, -0.5f );
		gl.glVertex3f( 0.5f,  0.5f,  0.5f );
		gl.glVertex3f( 0.5f, -0.5f,  0.5f );
		gl.glEnd();

		// Green side - LEFT
		gl.glBegin(GL2.GL_POLYGON);
		gl.glColor3f(   0.0f,  1.0f,  0.0f );
		gl.glVertex3f( -0.5f, -0.5f,  0.5f );
		gl.glVertex3f( -0.5f,  0.5f,  0.5f );
		gl.glVertex3f( -0.5f,  0.5f, -0.5f );
		gl.glVertex3f( -0.5f, -0.5f, -0.5f );
		gl.glEnd();

		// Blue side - TOP
		gl.glBegin(GL2.GL_POLYGON);
		gl.glColor3f(   0.0f,  0.0f,  1.0f );
		gl.glVertex3f(  0.5f,  0.5f,  0.5f );
		gl.glVertex3f(  0.5f,  0.5f, -0.5f );
		gl.glVertex3f( -0.5f,  0.5f, -0.5f );
		gl.glVertex3f( -0.5f,  0.5f,  0.5f );
		gl.glEnd();

		// Red side - BOTTOM
		gl.glBegin(GL2.GL_POLYGON);
		gl.glColor3f(   1.0f,  0.0f,  0.0f );
		gl.glVertex3f(  0.5f, -0.5f, -0.5f );
		gl.glVertex3f(  0.5f, -0.5f,  0.5f );
		gl.glVertex3f( -0.5f, -0.5f,  0.5f );
		gl.glVertex3f( -0.5f, -0.5f, -0.5f );
		gl.glEnd();

	}
	
	private void drawLight(GL2 gl)
	{

		/* Cube will be missing front face */

		// Rotate The Cube On X, Y & Z
		gl.glRotatef(model.getRotateX(),0,0,1);
		gl.glRotatef(model.getRotateY(),0,1,0);
		gl.glRotatef(model.getRotateZ(),1,0,0);

		// BACK
		gl.glBegin(GL2.GL_POLYGON);
		gl.glColor3f(   1.0f,  1.0f, 1.0f );
		gl.glVertex3f(  0.5f, -0.5f, 0.5f );
		gl.glVertex3f(  0.5f,  0.5f, 0.5f );
		gl.glVertex3f( -0.5f,  0.5f, 0.5f );
		gl.glVertex3f( -0.5f, -0.5f, 0.5f );
		gl.glEnd();

		// Right
		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex3f( 0.5f, -0.5f, -0.5f );
		gl.glVertex3f( 0.5f,  0.5f, -0.5f );
		gl.glVertex3f( 0.5f,  0.5f,  0.5f );
		gl.glVertex3f( 0.5f, -0.5f,  0.5f );
		gl.glEnd();

		// LEFT
		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex3f( -0.5f, -0.5f,  0.5f );
		gl.glVertex3f( -0.5f,  0.5f,  0.5f );
		gl.glVertex3f( -0.5f,  0.5f, -0.5f );
		gl.glVertex3f( -0.5f, -0.5f, -0.5f );
		gl.glEnd();

		// TOP
		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex3f(  0.5f,  0.5f,  0.5f );
		gl.glVertex3f(  0.5f,  0.5f, -0.5f );
		gl.glVertex3f( -0.5f,  0.5f, -0.5f );
		gl.glVertex3f( -0.5f,  0.5f,  0.5f );
		gl.glEnd();

		// BOTTOM
		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex3f(  0.5f, -0.5f, -0.5f );
		gl.glVertex3f(  0.5f, -0.5f,  0.5f );
		gl.glVertex3f( -0.5f, -0.5f,  0.5f );
		gl.glVertex3f( -0.5f, -0.5f, -0.5f );
		gl.glEnd();

	}

	//**********************************************************************
	// Private Helper Methods (Scene)
	//**********************************************************************
	private void drawSquare(GL2 gl, float r, float g, float b, float a) 
	{

		gl.glColor4f(r,g,b, a);         	// The color for the square.
		gl.glTranslatef(0,0,0.5f);    	// Move square 0.5 units forward.
		gl.glNormal3f(0,0,1);        	// Normal vector to square (this is actually the default).
		gl.glBegin(GL2.GL_TRIANGLE_FAN);
		gl.glVertex2f(-0.5f,-0.5f);    	// Draw the square (before the
		gl.glVertex2f(0.5f,-0.5f);     	//   the translation is applied)
		gl.glVertex2f(0.5f,0.5f);      	//   on the xy-plane, with its
		gl.glVertex2f(-0.5f,0.5f);    	 //  at (0,0,0).
		gl.glEnd();
		gl.glColor4f(0, 0, 0, 1);
		gl.glBegin(GL2.GL_LINE_STRIP);  	// Draw the outline of the square (in black)
		gl.glVertex2f(-0.5f,-0.5f);    	
		gl.glVertex2f(0.5f,-0.5f);     	
		gl.glVertex2f(0.5f,0.5f);      	
		gl.glVertex2f(-0.5f,0.5f); 
		gl.glEnd();
	}
}

//******************************************************************************
