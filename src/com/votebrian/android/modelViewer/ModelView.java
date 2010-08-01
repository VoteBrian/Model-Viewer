package com.votebrian.android.modelViewer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;
import android.view.MotionEvent;

public class ModelView extends GLSurfaceView implements Renderer{
	//Tag for log messages
	private static final String TAG = "ModelView";
	
	//Global Variables
	float startX = 0;
	float startY = 0;
	
	Context context;
	
	Model			model;
	Background		bg;

			float	viewW		= 0f;
			float	viewH		= 0f;
			float	nearW		= 0f;
			float	nearH		= 0f;
	static	float	nearZ		= 0.01f;
			float	farH		= 0f;
			float	farW		= 0f;
	static	float	farZ		= 20f;
			float	viewAngle	= 20f;
			float	floorZ		= 10f;
		
			float	rotAngleX	= 0f;
			float	rotAngleY	= 0f;
			
	private float[]		lightAmbient	= {0.5f, 0.5f, 0.5f, 1.0f};
	private float[]		lightDiffuse	= {1.0f, 1.0f, 1.0f, 1.0f};
	private float[]		lightPosition	= {0.0f, 75.0f, -12.0f, 1.0f};
	private float[]		lightPosition2	= {0.0f, 1.0f, 1.0f, 0f};
			
	private FloatBuffer	lightAmbientBuffer;
	private FloatBuffer	lightDiffuseBuffer;
	private FloatBuffer	lightPositionBuffer;
	private FloatBuffer	lightPositionBuffer2;

	//Constructor
	public ModelView(Context context) {
		super(context);
		
		//set global context variable
		this.context = context;
		
		//set renderer
		this.setRenderer(this);
		
		//build light buffers
		ByteBuffer byteBuf = ByteBuffer.allocateDirect(lightAmbient.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		lightAmbientBuffer = byteBuf.asFloatBuffer();
		lightAmbientBuffer.put(lightAmbient);
		lightAmbientBuffer.position(0);
		
		byteBuf = ByteBuffer.allocateDirect(lightDiffuse.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		lightDiffuseBuffer = byteBuf.asFloatBuffer();
		lightDiffuseBuffer.put(lightDiffuse);
		lightDiffuseBuffer.position(0);
		
		byteBuf = ByteBuffer.allocateDirect(lightPosition.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		lightPositionBuffer = byteBuf.asFloatBuffer();
		lightPositionBuffer.put(lightPosition);
		lightPositionBuffer.position(0);
		
		byteBuf = ByteBuffer.allocateDirect(lightPosition.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		lightPositionBuffer2 = byteBuf.asFloatBuffer();
		lightPositionBuffer2.put(lightPosition2);
		lightPositionBuffer2.position(0);
		
		//instance the background and model
		bg = new Background();
		model = new Model(0f, 0f, floorZ);
		
		//Log Message
		Log.i(TAG, "Constructor finished");
	}

	//Surface Created
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		//set up lighting
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, lightAmbientBuffer);		//Setup The Ambient Light ( NEW )
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, lightDiffuseBuffer);		//Setup The Diffuse Light ( NEW )
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPositionBuffer);	//Position The Light ( NEW )
		gl.glEnable(GL10.GL_LIGHT0);
		
		gl.glLightfv(GL10.GL_LIGHT2, GL10.GL_AMBIENT, lightAmbientBuffer);		//Setup The Ambient Light ( NEW )
		gl.glLightfv(GL10.GL_LIGHT2, GL10.GL_DIFFUSE, lightDiffuseBuffer);		//Setup The Diffuse Light ( NEW )
		gl.glLightfv(GL10.GL_LIGHT2, GL10.GL_POSITION, lightPositionBuffer2);	//Position The Light ( NEW )
		gl.glEnable(GL10.GL_LIGHT2);
		
		//Only draw front facing triangles
//		gl.glEnable(GL10.GL_CULL_FACE);
//		gl.glFrontFace(GL10.GL_CCW);
//		gl.glCullFace(GL10.GL_BACK);
		
		//gl settings
		//##BF
		//gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		//##
//		gl.glEnable(GL10.GL_COLOR_MATERIAL);
//		gl.glEnable(GL10.GL_DITHER);
//		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glClearColor(1.0f, 0.8f, 0.8f, 1.0f);
		gl.glClearDepthf(1.0f);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDepthFunc(GL10.GL_LEQUAL);

		//load texture
		//model.loadTexture(gl, this.context);
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
	}

	public void onDrawFrame(GL10 gl) {
		//clear screen
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
		
//		gl.glEnable(GL10.GL_LIGHTING);
		gl.glDisable(GL10.GL_LIGHTING);
		
		//Draw the background
		bg.updateSize(farW, farH, -1*(20-1));
//		bg.draw(gl);
//		gl.glColor4f(0.8f, 0.8f, 1f, 1f);
		model.draw(gl);
	}

	public void onSurfaceChanged(GL10 gl, int w, int h) {
		viewW = w;
		viewH = h;
		
		projSettings(gl);
		bg.updateSize(farW, farH, -1*(farZ-1));
	}
	
	/*
	private void setLighting(GL10 gl) {
		float lightAmbient0[]	= new float[] { 0.3f, 0.3f, 0.3f, 1f };
		float lightDiffuse0[]	= new float[] { 0.7f, 0.7f, 0.7f, 1f };
		float specular0[]		= new float[] { 1.0f, 1.0f, 1.0f, 1f };
		float specref[]			= new float[] { 1.0f, 1.0f, 1.0f, 1f };
		float[] lightPos0 = new float[] {5,5,3,1};

		gl.glEnable(GL10.GL_COLOR_MATERIAL);
//		gl.glEnable(GL10.GL_LIGHTING);
//		gl.glEnable(GL10.GL_LIGHT0);

//		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, lightAmbient0, 0);
//		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, lightDiffuse0, 0);
//		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPos0, 0);

//		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDepthFunc(GL10.GL_LEQUAL);
		
//		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glShadeModel(GL10.GL_SMOOTH);
		
		gl.glClearDepthf(1.0f);

//		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPos0, 0);
	}
	*/
	
	public boolean onTouchEvent(final MotionEvent event) {		
		queueEvent(new Runnable() {
			public void run() {				
				float x = event.getX();
				float y = event.getY();
				
				switch(event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					startX = x;
					startY = y;
					break;
				case MotionEvent.ACTION_UP:
					startX = 0;
					startY = 0;
					break;
				case MotionEvent.ACTION_MOVE:
					model.setAngleX(x-startX);
					startX = x;
					
					model.setAngleY(y-startY);
					startY = y;
					break;
				}
			}
		});
		return true;
	}
	
	private void projSettings(GL10 gl) {
		float ratio = viewW/viewH;
		nearH = (float) (nearZ*(Math.tan(Math.toRadians(viewAngle))));
		nearW = nearH*ratio;
		
		farH = (float) (farZ * (Math.tan(Math.toRadians(viewAngle))));
		farW = farH*ratio;
		
		gl.glMatrixMode(GL10.GL_PROJECTION);
//		gl.glOrthof(-3, 3, -3/ratio, 3/ratio, nearZ, farZ);
		gl.glFrustumf(-nearW, nearW, -nearH, nearH, nearZ, farZ);
		gl.glViewport(0, 0, (int) viewW, (int) viewH);
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
	}

}
