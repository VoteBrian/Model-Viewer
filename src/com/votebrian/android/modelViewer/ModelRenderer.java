package com.votebrian.android.modelViewer;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.widget.ImageView;

public class ModelRenderer implements GLSurfaceView.Renderer {
	//Tag for log messages
	private static final String TAG = "Background";
	
	//Global Variables
			Model model;
			Background bg;
	
			float viewW		= 0f;
			float viewH		= 0f;
			float nearW		= 0f;
			float nearH		= 0f;
	static	float nearZ		= 0.01f;
			float farH		= 0f;
			float farW		= 0f;
	static	float farZ		= 20f;
			float viewAngle	= 20f;
			float floorZ	= 10f;
			
			float rotAngleX	= 0f;
			float rotAngleY	= 0f;
			
	Context 	  context;
			
	public ModelRenderer(Context c){
		context = c;
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		//Set the background color to pink
		gl.glClearColor(0.8f, 0f, 0f, 1f);
		
		//Only draw front facing triangles
		//gl.glEnable(GL10.GL_CULL_FACE);
		//gl.glFrontFace(GL10.GL_CCW);
		//gl.glCullFace(GL10.GL_BACK);
		
		//no idea
		gl.glEnable(GL10.GL_ALPHA_TEST);
		setLighting(gl);
		
		//no idea
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		
		model = new Model(0f,0f,0f);
		bg = new Background();
	}

	public void onSurfaceChanged(GL10 gl, int w, int h) {
		viewW = w;
		viewH = h;
		
		projSettings(gl);
	}

	public void onDrawFrame(GL10 gl) {
		Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.tiletex);
		ByteBuffer bb = ByteBuffer.allocateDirect(bmp.getHeight() * bmp.getWidth() * 4); 
		
		//clear the color buffer to show the Clear Color
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		
		//reset the matrix
		gl.glLoadIdentity();
		
		//Draw the background
		Log.i(TAG, "width: " + farW);
		bg.updateSize(farW, farH, -1*(20-1));
		FloatBuffer bgVertexBuffer = bg.vertexBuffer;
		FloatBuffer bgColorBuffer = bg.colorBuffer;
		ShortBuffer bgTriBuffer = bg.triBuffer;
		int bgNumTriIndices = bg.numTriIndices;
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, bgVertexBuffer);
		gl.glColorPointer(4, GL10.GL_FLOAT, 0, bgColorBuffer);
		gl.glDrawElements(GL10.GL_TRIANGLES, bgNumTriIndices, GL10.GL_UNSIGNED_SHORT, bgTriBuffer);
		
		//Draw the model
		FloatBuffer vertexBuffer = model.vertexBuffer;
		FloatBuffer colorBuffer = model.colorBuffer;
		ShortBuffer triBuffer = model.triBuffer;
		FloatBuffer texBuffer = model.texBuffer;
		int numTriIndices = model.numTriangles * 3;
		
		//load texture
		gl.glTexCoordPointer(2, GL10.GL_FLAT, 0, texBuffer);
		
		gl.glTexImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_LUMINANCE, 256, 256, 0, GL10.GL_LUMINANCE, GL10.GL_UNSIGNED_BYTE, bb);
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glTranslatef(0f, 0f, -1*floorZ);
		gl.glRotatef(rotAngleX, 0f, 1f, 0f);
		gl.glRotatef(rotAngleY, 1f, 0f, 0f);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
		gl.glColorPointer(4, GL10.GL_FLOAT, 0, colorBuffer);
		gl.glDrawElements(GL10.GL_TRIANGLES, numTriIndices, GL10.GL_UNSIGNED_SHORT, triBuffer);
	}
	
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
	
	public void setAngleX(float angle) {
		rotAngleX += angle;
	}
	
	public void setAngleY(float angle) {
		rotAngleY += angle;
	}
	
}
