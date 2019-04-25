package shadow;

//import java.lang.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.glu.*;
import com.jogamp.opengl.math.Matrix4;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.texture.*;
import org.lwjgl.opengl.*;
import com.jogamp.opengl.util.gl2.GLUT;



import shadow.KeyHandler;
import shadow.Model;
import utilities.Utilities;

//******************************************************************************

/**
 * The <CODE>Light</CODE> class.<P>
 *
 * @author  Andy Vu
 * @version %I%, %G%
 */
public final class View
	implements GLEventListener
{
	//**********************************************************************
	// Private Class Members
	//**********************************************************************

	private static final int			DEFAULT_FRAMES_PER_SECOND = 30;
	private static final DecimalFormat	FORMAT = new DecimalFormat("0.000");
	private static final GLUT	MYGLUT = new GLUT();
	
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
	
	private int crateTexture;
	private int brickTexture;
	private int metalTexture;
	private int grassTexture;
	private int feltTexture;
	private int roomTexture;
	
	private final Model				model;
	
	private float radius = 0.02f;

	private final KeyHandler			keyHandler;

	//**********************************************************************
	// Constructors and Finalizer
	//**********************************************************************

	public View(GLJPanel canvas)
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
		
		//gl.glOrtho(-1.0f, 1.0f, -1.0f, 1.0f, 0.01f, 100.0f);

		// See com.jogamp.opengl.GL
		gl.glEnable(GL2.GL_POINT_SMOOTH);		// Turn on point anti-aliasing
		gl.glEnable(GL2.GL_LINE_SMOOTH);			// Turn on line anti-aliasing
		gl.glEnable(GL2.GL_MULTISAMPLE);
		
		//gl.glShadeModel(GL2.GL_SMOOTH);
		
		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glDepthFunc(GL2.GL_LEQUAL);
		
		gl.glEnable(GL2.GL_NORMALIZE);
		
		gl.glEnable(GL2.GL_LIGHTING);
		gl.glEnable( GL2.GL_LIGHT1 );  
		gl.glEnable( GL2.GL_LIGHT2 );  
		gl.glEnable( GL2.GL_LIGHT3 );  
		gl.glEnable(GL2.GL_COLOR_MATERIAL);
		
		//gl.glColorMaterial(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE);
	     
	      try{
	         File image = new File("cratesmall.png");
	         Texture t = TextureIO.newTexture(image, true);
	         crateTexture = t.getTextureObject(gl);
	      }catch(IOException e){
	         e.printStackTrace();
	      }
	      
	      try{
	    	  File image = new File("bricks.jpg");
	    	  Texture t = TextureIO.newTexture(image, true);
	    	  brickTexture = t.getTextureObject(gl);
	      }catch(IOException e){
	    	  e.printStackTrace();
		  }
	      
	      try{
	    	  File image = new File("metal.jpg");
	    	  Texture t = TextureIO.newTexture(image, true);
	    	  metalTexture = t.getTextureObject(gl);
	      }catch(IOException e){
	    	  e.printStackTrace();
		  }
	      
	      try{
	    	  File image = new File("grass.bmp");
	    	  Texture t = TextureIO.newTexture(image, true);
	    	  grassTexture = t.getTextureObject(gl);
	      }catch(IOException e){
	    	  e.printStackTrace();
		  }
	      
	      try{
	    	  File image = new File("felt.bmp");
	    	  Texture t = TextureIO.newTexture(image, true);
	    	  feltTexture = t.getTextureObject(gl);
	      }catch(IOException e){
	    	  e.printStackTrace();
		  }
	      
	      try{
	    	  File image = new File("room.bmp");
	    	  Texture t = TextureIO.newTexture(image, true);
	    	  roomTexture = t.getTextureObject(gl);
	      }catch(IOException e){
	    	  e.printStackTrace();
		  }
	      
	}

	public void	display(GLAutoDrawable drawable)
	{
		GL2 gl = drawable.getGL().getGL2();

		render(drawable);
		update(drawable);
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
		
		if(model.getLightPosition().x + model.getLightVelocity().x + radius > 0.7f || model.getLightPosition().x + model.getLightVelocity().x - radius < -0.7f)
			model.setLightVelocity(-model.getLightVelocity().x, model.getLightVelocity().y);
		if(model.getLightPosition().y + model.getLightVelocity().y + radius > 0.5f || model.getLightPosition().y + model.getLightVelocity().y - radius < -0.5f)
			model.setLightVelocity(model.getLightVelocity().x, -model.getLightVelocity().y);
		
		model.setLightPosition(model.getLightPosition().x + model.getLightVelocity().x, model.getLightPosition().y + model.getLightVelocity().y);
		
		if(model.getLightPosition2().x + model.getLightVelocity2().x + radius > 0.7f || model.getLightPosition2().x + model.getLightVelocity2().x - radius < -0.7f)
			model.setLightVelocity2(-model.getLightVelocity2().x, model.getLightVelocity2().y);
		if(model.getLightPosition2().y + model.getLightVelocity2().y + radius > 0.5f || model.getLightPosition2().y + model.getLightVelocity2().y - radius < -0.5f)
			model.setLightVelocity2(model.getLightVelocity2().x, -model.getLightVelocity2().y);
		
		model.setLightPosition2(model.getLightPosition2().x + model.getLightVelocity2().x, model.getLightPosition2().y + model.getLightVelocity2().y);
		
		if(model.getLightPosition3().x + model.getLightVelocity3().x + radius > 0.7f || model.getLightPosition3().x + model.getLightVelocity3().x - radius < -0.7f)
			model.setLightVelocity3(-model.getLightVelocity3().x, model.getLightVelocity3().y);
		if(model.getLightPosition3().y + model.getLightVelocity3().y + radius > 0.5f || model.getLightPosition3().y + model.getLightVelocity3().y - radius < -0.5f)
			model.setLightVelocity3(model.getLightVelocity3().x, -model.getLightVelocity3().y);
		
		model.setLightPosition3(model.getLightPosition3().x + model.getLightVelocity3().x, model.getLightPosition3().y + model.getLightVelocity3().y);
		
		
	}

	private void	render(GLAutoDrawable drawable)
	{
		GL2	gl = drawable.getGL().getGL2();

		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);// Clear the buffer

		if(model.isLight1On())
			gl.glEnable(GL2.GL_LIGHT1);
		else
			gl.glDisable(GL2.GL_LIGHT1);
		
		if(model.isLight2On())
			gl.glEnable(GL2.GL_LIGHT2);
		else
			gl.glDisable(GL2.GL_LIGHT2);
		
		if(model.isLight3On())
			gl.glEnable(GL2.GL_LIGHT3);
		else
			gl.glDisable(GL2.GL_LIGHT3);
		
		
		
		//gl.glShadeModel(GL2.GL_SMOOTH);
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
		
		float[] white = {1.0f, 1.0f, 1.0f, 1.0f};
		//gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, white, 0);
		//gl.glMaterialf(GL2.GL_FRONT, GL2.GL_SHININESS, 16.0f);
		
		gl.glPushMatrix();
		
		gl.glRotatef(model.getRotateX(),1,0,0);
		gl.glRotatef(model.getRotateY(),0,1,0);
		gl.glRotatef(model.getRotateZ(),0,0,1);
		
		
		
		
		drawRoom(gl);
		drawRoomCube(gl, 0.0f, -0.25f, -0.1f);
		drawRoomCube1(gl);
		drawRoomCube2(gl);
		drawPyramid(gl);
		drawCylinder(gl);

		gl.glPopMatrix();
		//Creates a moving square that moves in a circular path mimic-ing the same path as the light
		
		float[] lightPos1 = {(float)model.getOrigin().x, (float)model.getOrigin().y, 0.0f , 1.0f};
		
		gl.glPushMatrix();
		
		gl.glRotatef(model.getRotateX(),1,0,0);
		gl.glRotatef(model.getRotateY(),0,1,0);
		gl.glRotatef(model.getRotateZ(),0,0,1);
		
		gl.glTranslatef((float)model.getLightPosition().x, (float)model.getLightPosition().y, -0.5f);
		
		// multicolor diffuse 
		float[] coloredLight = { model.getLightColor().red * 0.8f,model.getLightColor().green * 0.8f,model.getLightColor().blue * 0.8f, 1.0f };
		float[] dimLight = { 0.2f,0.2f,0.2f, 0.2f };
		gl.glLightfv( GL2.GL_LIGHT1, GL2.GL_POSITION, lightPos1, 0);
		gl.glLightfv( GL2.GL_LIGHT1, GL2.GL_AMBIENT, dimLight, 0);
		gl.glLightfv( GL2.GL_LIGHT1, GL2.GL_DIFFUSE, coloredLight, 0);
		
		gl.glLightfv( GL2.GL_LIGHT1, GL2.GL_SPECULAR, white, 0);
		float[] attenconst = {1.0f};
		gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_CONSTANT_ATTENUATION, attenconst, 0);
		float[] attenline = {2.5f};
		gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_LINEAR_ATTENUATION, attenline, 0);
		float[] attenquad = {3.8f};
		gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_QUADRATIC_ATTENUATION, attenquad, 0);
		
		
		gl.glColor3f(model.getLightColor().red,model.getLightColor().green,model.getLightColor().blue);
		if(model.isLight1On())
			MYGLUT.glutWireSphere(radius, 32, 8);

		gl.glPopMatrix();
		
		gl.glPushMatrix();
		
		gl.glRotatef(model.getRotateX(),1,0,0);
		gl.glRotatef(model.getRotateY(),0,1,0);
		gl.glRotatef(model.getRotateZ(),0,0,1);
		
		gl.glTranslatef((float)model.getLightPosition2().x, (float)model.getLightPosition2().y, -0.5f);
		
		// multicolor diffuse 
		float[] coloredLight2 = { model.getLightColor2().red * 0.8f,model.getLightColor2().green * 0.8f,model.getLightColor2().blue * 0.8f, 1.0f };
		gl.glLightfv( GL2.GL_LIGHT2, GL2.GL_POSITION, lightPos1, 0);
		gl.glLightfv( GL2.GL_LIGHT2, GL2.GL_AMBIENT, dimLight, 0);
		gl.glLightfv( GL2.GL_LIGHT2, GL2.GL_DIFFUSE, coloredLight2, 0);
		gl.glLightfv( GL2.GL_LIGHT2, GL2.GL_SPECULAR, white, 0);
		gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_CONSTANT_ATTENUATION, attenconst, 0);
		gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_LINEAR_ATTENUATION, attenline, 0);
		gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_QUADRATIC_ATTENUATION, attenquad, 0);
		
		gl.glColor3f(model.getLightColor2().red,model.getLightColor2().green,model.getLightColor2().blue);
		if(model.isLight2On())
			MYGLUT.glutWireSphere(radius, 32, 8);

		gl.glPopMatrix();
		
		gl.glPushMatrix();
		
		gl.glRotatef(model.getRotateX(),1,0,0);
		gl.glRotatef(model.getRotateY(),0,1,0);
		gl.glRotatef(model.getRotateZ(),0,0,1);
		
		gl.glTranslatef((float)model.getLightPosition3().x, (float)model.getLightPosition3().y, -0.5f);
		
		// multicolor diffuse 
		float[] coloredLight3 = { model.getLightColor3().red * 0.8f,model.getLightColor3().green * 0.8f,model.getLightColor3().blue * 0.8f, 1.0f };
		gl.glLightfv( GL2.GL_LIGHT3, GL2.GL_POSITION, lightPos1, 0);
		gl.glLightfv( GL2.GL_LIGHT3, GL2.GL_AMBIENT, dimLight, 0);
		gl.glLightfv( GL2.GL_LIGHT3, GL2.GL_DIFFUSE, coloredLight3, 0);
		gl.glLightfv( GL2.GL_LIGHT3, GL2.GL_SPECULAR, white, 0);
		gl.glLightfv(GL2.GL_LIGHT3, GL2.GL_CONSTANT_ATTENUATION, attenconst, 0);
		gl.glLightfv(GL2.GL_LIGHT3, GL2.GL_LINEAR_ATTENUATION, attenline, 0);
		gl.glLightfv(GL2.GL_LIGHT3, GL2.GL_QUADRATIC_ATTENUATION, attenquad, 0);
		
		
		gl.glColor3f(model.getLightColor3().red,model.getLightColor3().green,model.getLightColor3().blue);
		if(model.isLight3On())
			MYGLUT.glutWireSphere(radius, 32, 8);

		gl.glPopMatrix();
		

	}

	//**********************************************************************
	// Private Methods (Scene)
	//**********************************************************************

	
	private void drawRandomGeo(GL2 gl)
	{
		
	}
	
	private void drawRoom(GL2 gl)
	{
		
		gl.glEnable(GL2.GL_TEXTURE_2D);
		gl.glBindTexture(GL2.GL_TEXTURE_2D, roomTexture);
		
		
		gl.glColor3f(   0.8f,  0.8f, 0.8f );
		/* BACK */
		// Draw the square
		gl.glBegin(GL2.GL_POLYGON);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(  0.7f, -0.5f, 0.5f );
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(  0.7f,  0.5f, 0.5f );
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f( -0.7f,  0.5f, 0.5f );
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f( -0.7f, -0.5f, 0.5f );
		gl.glEnd();
		
		gl.glColor3f(   0.9f,  0.9f, 0.9f );
		/* LEFT */
		// Draw the square
		gl.glBegin(GL2.GL_POLYGON);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f( 0.7f, -0.5f, -0.5f );
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f( 0.7f,  0.5f, -0.5f );
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f( 0.7f,  0.5f,  0.5f );
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f( 0.7f, -0.5f,  0.5f );
		gl.glEnd();
		
		gl.glColor3f(   0.9f,  0.9f, 0.9f );
		/* RIGHT */
		// Draw the square
		gl.glBegin(GL2.GL_POLYGON);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f( -0.7f, -0.5f,  0.5f );
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f( -0.7f,  0.5f,  0.5f );
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f( -0.7f,  0.5f, -0.5f );
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f( -0.7f, -0.5f, -0.5f );
		gl.glEnd();
		
		gl.glColor3f(   0.9f,  0.9f, 0.9f );
		/* TOP */
		// Draw the square
		gl.glBegin(GL2.GL_POLYGON);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(  0.7f,  0.5f,  0.5f );
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(  0.7f,  0.5f, -0.5f );
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f( -0.7f,  0.5f, -0.5f );
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f( -0.7f,  0.5f,  0.5f );
		gl.glEnd();
		
		gl.glColor3f(   0.9f,  0.9f, 0.9f );
		/* BOTTOM */
		// Draw the square
		gl.glBegin(GL2.GL_POLYGON);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(  0.7f, -0.5f, -0.5f );
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(  0.7f, -0.5f,  0.5f );
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f( -0.7f, -0.5f,  0.5f );
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f( -0.7f, -0.5f, -0.5f );
		gl.glEnd();
		
		gl.glDisable(GL2.GL_TEXTURE_2D);
		
	}
	
	private void drawRoomCube1(GL2 gl)
	{
		gl.glColor3f(   1.0f,  1.0f, 1.0f );
		gl.glEnable(GL2.GL_TEXTURE_2D);
		gl.glBindTexture(GL2.GL_TEXTURE_2D, feltTexture);
	
		/* BACK */
		// Draw the square
		gl.glBegin(GL2.GL_POLYGON);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(  0.4f, -0.5f, 0.3f );
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(  0.4f, -0.3f, 0.3f );
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(  0.2f, -0.3f, 0.3f );
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(  0.2f, -0.5f, 0.3f );
		gl.glEnd();
		
		/* LEFT */
		// Draw the square
		gl.glBegin(GL2.GL_POLYGON);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(  0.4f, -0.5f, 0.3f );
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(  0.4f, -0.5f, 0.1f );
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(  0.4f, -0.3f, 0.1f );
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(  0.4f, -0.3f, 0.3f );
		gl.glEnd();
		
		/* RIGHT */
		// Draw the square
		gl.glBegin(GL2.GL_POLYGON);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(  0.2f, -0.5f, 0.3f );
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(  0.2f, -0.5f, 0.1f );
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(  0.2f, -0.3f, 0.1f );
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(  0.2f, -0.3f, 0.3f );
		gl.glEnd();
	
		/* TOP */
		// Draw the square
		gl.glBegin(GL2.GL_POLYGON);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(  0.4f, -0.3f, 0.3f );
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(  0.4f, -0.3f, 0.1f );
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(  0.2f, -0.3f, 0.1f );
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(  0.2f, -0.3f, 0.3f );
		gl.glEnd();
		
		/* BOTTOM */
		// Draw the square
		gl.glBegin(GL2.GL_POLYGON);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(  0.4f, -0.5f, 0.3f );
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(  0.4f, -0.5f, 0.1f );
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(  0.2f, -0.5f, 0.1f );
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(  0.2f, -0.5f, 0.3f );
		gl.glEnd();
	
		/* FRONT */
		// Draw the square
		gl.glBegin(GL2.GL_POLYGON);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(  0.4f, -0.5f, 0.1f );
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(  0.4f, -0.3f, 0.1f );
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(  0.2f, -0.3f, 0.1f );
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(  0.2f, -0.5f, 0.1f );
		gl.glEnd();
		
		gl.glDisable(GL2.GL_TEXTURE_2D);
		
	}
	
	private void drawRoomCube2(GL2 gl)
	{
		gl.glColor3f(   1.0f,  1.0f, 1.0f );
		
		gl.glEnable(GL2.GL_TEXTURE_2D);
		gl.glBindTexture(GL2.GL_TEXTURE_2D, grassTexture);
		
		/* BACK */
		// Draw the square
		gl.glBegin(GL2.GL_POLYGON);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(  0.4f, -0.3f, 0.3f );
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(  0.4f, -0.1f, 0.3f );
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(  0.2f, -0.1f, 0.3f );
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(  0.2f, -0.3f, 0.3f );
		gl.glEnd();
		
		/* LEFT */
		// Draw the square
		gl.glBegin(GL2.GL_POLYGON);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(  0.4f, -0.3f, 0.3f );
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(  0.4f, -0.3f, 0.1f );
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(  0.4f, -0.1f, 0.1f );
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(  0.4f, -0.1f, 0.3f );
		gl.glEnd();
		
		/* RIGHT */
		// Draw the square
		gl.glBegin(GL2.GL_POLYGON);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(  0.2f, -0.3f, 0.3f );
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(  0.2f, -0.3f, 0.1f );
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(  0.2f, -0.1f, 0.1f );
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(  0.2f, -0.1f, 0.3f );
		gl.glEnd();
		
		/* TOP */
		// Draw the square
		gl.glBegin(GL2.GL_POLYGON);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(  0.4f, -0.1f, 0.3f );
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(  0.4f, -0.1f, 0.1f );
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(  0.2f, -0.1f, 0.1f );
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(  0.2f, -0.1f, 0.3f );
		gl.glEnd();
		
		/* BOTTOM */
		// Draw the square
		gl.glBegin(GL2.GL_POLYGON);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(  0.4f, -0.3f, 0.3f );
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(  0.4f, -0.3f, 0.1f );
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(  0.2f, -0.3f, 0.1f );
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(  0.2f, -0.3f, 0.3f );
		gl.glEnd();
		
		/* FRONT */
		// Draw the square
		gl.glBegin(GL2.GL_POLYGON);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(  0.4f, -0.3f, 0.1f );
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(  0.4f, -0.1f, 0.1f );
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(  0.2f, -0.1f, 0.1f );
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(  0.2f, -0.3f, 0.1f );
		gl.glEnd();
		
		gl.glDisable(GL2.GL_TEXTURE_2D);

	}

	private void drawRoomCube(GL2 gl, float x, float y, float z)
	{
		gl.glEnable(GL2.GL_TEXTURE_2D);
	      
		gl.glBindTexture(GL2.GL_TEXTURE_2D, crateTexture);
		/* BACK */
		// Draw the square
		gl.glBegin(GL2.GL_POLYGON);
		gl.glColor4f(   1.0f,  1.0f, 1.0f, 0.25f );
		gl.glVertex3f(  x + 0.1f, y + 0.1f, z + 0.1f);
		gl.glVertex3f(  x - 0.1f, y + 0.1f, z + 0.1f);
		gl.glVertex3f(  x - 0.1f, y - 0.1f, z + 0.1f);
		gl.glVertex3f(  x + 0.1f, y - 0.1f, z + 0.1f);
		gl.glEnd();
		
		/* LEFT */
		// Draw the square
		gl.glBegin(GL2.GL_POLYGON);
		//gl.glColor4f(   1.0f,  0.0f, 0.0f, 0.25f );
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(  x - 0.1f, y + 0.1f, z - 0.1f);
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(  x - 0.1f, y + 0.1f, z + 0.1f);
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(  x - 0.1f, y - 0.1f, z + 0.1f);
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(  x - 0.1f, y - 0.1f, z - 0.1f);
		gl.glEnd();
		
		/* RIGHT */
		// Draw the square
		gl.glBegin(GL2.GL_POLYGON);
		//gl.glColor4f(   1.0f,  0.0f, 0.0f, 0.25f );
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(  x + 0.1f, y + 0.1f, z - 0.1f);
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(  x + 0.1f, y + 0.1f, z + 0.1f);
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(  x + 0.1f, y - 0.1f, z + 0.1f);
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(  x + 0.1f, y - 0.1f, z - 0.1f);
		
		gl.glEnd();
		
		/* TOP */
		// Draw the square
		gl.glBegin(GL2.GL_POLYGON);
		//gl.glColor4f(   1.0f,  0.0f, 0.0f, 0.25f );
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(  x + 0.1f, y + 0.1f, z + 0.1f);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(  x + 0.1f, y + 0.1f, z - 0.1f);
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(  x - 0.1f, y + 0.1f, z - 0.1f);
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(  x - 0.1f, y + 0.1f, z + 0.1f);
		gl.glEnd();
		
		/* BOTTOM */
		// Draw the square
		gl.glBegin(GL2.GL_POLYGON);
		//gl.glColor4f(   1.0f,  0.0f, 0.0f, 0.25f );
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(  x + 0.1f, y - 0.1f, z + 0.1f);
		gl.glTexCoord2f(0.0f, 1.0f);	
		gl.glVertex3f(  x + 0.1f, y - 0.1f, z - 0.1f);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(  x - 0.1f, y - 0.1f, z - 0.1f);
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(  x - 0.1f, y - 0.1f, z + 0.1f);
		gl.glEnd();
		
		/* FRONT */
		// Draw the square
		gl.glBegin(GL2.GL_POLYGON);
		gl.glTexCoord2f(0.0f, 0.0f); 
		gl.glVertex3f(  x + 0.1f, y + 0.1f, z - 0.1f);
		gl.glTexCoord2f(1.0f, 0.0f); 
		gl.glVertex3f(  x - 0.1f, y + 0.1f, z - 0.1f);
		gl.glTexCoord2f(1.0f, 1.0f); 
		gl.glVertex3f(  x - 0.1f, y - 0.1f, z - 0.1f);
		gl.glTexCoord2f(0.0f, 1.0f); 
		gl.glVertex3f(  x + 0.1f, y - 0.1f, z - 0.1f);
		gl.glEnd();
		
		gl.glDisable(GL2.GL_TEXTURE_2D);
		
		gl.glFlush();
		
	}

	
	private void drawPyramid(GL2 gl)
	{
		
		gl.glEnable(GL2.GL_TEXTURE_2D);
		gl.glBindTexture(GL2.GL_TEXTURE_2D, brickTexture);

		gl.glColor3f(   1.0f,  1.0f, 1.0f );
		/* BOTTOM */
		// Draw the square
		gl.glBegin(GL2.GL_POLYGON);
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(  0.1f, -0.5f, 0.3f );
		gl.glTexCoord2f(0.0f, 1.0f);	
		gl.glVertex3f(  0.1f, -0.5f, 0.1f );
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f( -0.1f, -0.5f, 0.1f );
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f( -0.1f, -0.5f, 0.3f );
		gl.glEnd();

		/* BACK */
		// Draw the triangle
		gl.glBegin(GL2.GL_POLYGON);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(  0.1f, -0.5f, 0.3f );
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(  0.0f, -0.3f, 0.2f );
		gl.glTexCoord2f(0.5f, 1.0f);
		gl.glVertex3f( -0.1f, -0.5f, 0.3f );
		gl.glEnd();
		
		/* LEFT */
		// Draw the triangle
		gl.glBegin(GL2.GL_POLYGON);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(  0.1f, -0.5f, 0.3f );
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(  0.0f, -0.3f, 0.2f );
		gl.glTexCoord2f(0.5f, 1.0f);
		gl.glVertex3f(  0.1f, -0.5f, 0.1f );
		gl.glEnd();
		
		/* RIGHT */
		// Draw the triangle
		gl.glBegin(GL2.GL_POLYGON);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f( -0.1f, -0.5f, 0.3f );
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(  0.0f, -0.3f, 0.2f );
		gl.glTexCoord2f(0.5f, 1.0f);
		gl.glVertex3f( -0.1f, -0.5f, 0.1f );
		gl.glEnd();
		
		/* FRONT */
		// Draw the triangle
		gl.glBegin(GL2.GL_POLYGON);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(  0.1f, -0.5f, 0.1f );
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(  0.0f, -0.3f, 0.2f );
		gl.glTexCoord2f(0.5f, 1.0f);
		gl.glVertex3f( -0.1f, -0.5f, 0.1f );
		gl.glEnd();
		
		gl.glDisable(GL2.GL_TEXTURE_2D);
		
		gl.glFlush();
		
	}
	
	
	private void drawCylinder(GL2 gl)
	{
		// BOTTOM
		drawOval(gl, -0.3f, -0.495f, 0.2f, 0.1, 0.1);
		
		// TOP
		drawOval(gl, -0.3f, -0.2f, 0.2f, 0.1, 0.1);
		
		// Connect them
		connectOvals(gl, -0.3f, -0.2f, 0.2f, 0.1, 0.1, -0.495f + 0.2f);
	}
	
	private void	 drawOval(GL2 gl, float cx, float cy, float cz, double w, double h)
	{
		gl.glEnable(GL2.GL_TEXTURE_2D);
		gl.glBindTexture(GL2.GL_TEXTURE_2D, metalTexture);
		
		gl.glBegin(GL2.GL_POLYGON);
		gl.glColor3f(1.0f, 1.0f, 1.0f);

		for (int i=0; i<32; i++)
		{
			double	a = (2.0 * Math.PI) * (i / 32.0);

			gl.glTexCoord2f((float)Math.cos(a), (float)Math.sin(a));
			gl.glVertex3f((float)(cx + w * Math.cos(a)), cy, (float)(cz + h * Math.sin(a)));
		}

		gl.glEnd();

		gl.glDisable(GL2.GL_TEXTURE_2D);
	}
	
	private void connectOvals(GL2 gl, float cx, float cy, float cz, double w, double h, double cylH)
	{
		gl.glEnable(GL2.GL_TEXTURE_2D);
		gl.glBindTexture(GL2.GL_TEXTURE_2D, metalTexture);
		
		gl.glBegin(GL2.GL_QUAD_STRIP);
		gl.glColor3f(1.0f, 1.0f, 1.0f);

		for (int i=0; i<32; i++)
		{
			double	a = (2.0 * Math.PI) * (i / 32.0);

			gl.glTexCoord2f(0.0f, 1.0f);
			gl.glVertex3f((float)(cx + w * Math.cos(a)), cy + (float)cylH, (float)(cz + h * Math.sin(a)));
			gl.glTexCoord2f(0.0f, 0.0f);
			gl.glVertex3f((float)(cx + w * Math.cos(a)), cy, (float)(cz + h * Math.sin(a)));
		}
		
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f((float)(cx + w * Math.cos(0)), cy + (float)cylH, (float)(cz + h * Math.sin(0)));
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f((float)(cx + w * Math.cos(0)), cy, (float)(cz + h * Math.sin(0)));
		
		gl.glEnd();
		
		gl.glDisable(GL2.GL_TEXTURE_2D);
	}

	//**********************************************************************
	// Private Helper Methods (Scene)
	//**********************************************************************
	private void drawSquare(GL2 gl, float r, float g, float b, float a) 
	{

		gl.glColor4f(r,g,b, a);         	// The color for the square.
		gl.glTranslatef(0,0,0.5f);    	// Move square 0.5 units forward.
		gl.glNormal3f(0,0,1);        	// Normal vector to square (this is actually the default).
		gl.glBegin(GL2.GL_POLYGON);
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
