package com.votebrian.android.modelViewer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView;
import android.util.Log;

public class ModelRenderer implements GLSurfaceView.Renderer {
	//Global Variables
			Model model;
	
			float viewW		= 0f;
			float viewH		= 0f;
			float nearW		= 0f;
			float nearH		= 0f;
	static	float nearZ		= 0.01f;
			float farZ		= 40f;
			float viewAngle	= 45f;
			float floorZ	= 20f;

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		//Set the background color to red
		gl.glClearColor(1f, 0f, 0f, 1f);
		
		//Only draw front facing triangles
//		gl.glEnable(GL10.GL_CULL_FACE);
//		gl.glFrontFace(GL10.GL_CCW);
//		gl.glCullFace(GL10.GL_BACK);
		
		//no idea
		gl.glEnable(GL10.GL_ALPHA_TEST);
		setLighting(gl);
		
		//no idea
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		
		model = new Model(0f,0f,0f);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int w, int h) {
		viewW = w;
		viewH = h;
		
		projSettings(gl);
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		//clear the color buffer to show the Clear Color
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		
		//reset the matrix
		gl.glLoadIdentity();
		
		//Draw the background
		//gl.glMatrixMode(GL10.GL_MODELVIEW);
		
		//Draw the model
		gl.glMatrixMode(GL10.GL_MODELVIEW);
//		gl.glTranslatef(0f, 0f, floorZ);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, model.vertexBuffer);
		gl.glColorPointer(4, GL10.GL_FLOAT, 0, model.colorBuffer);
		gl.glDrawElements(GL10.GL_TRIANGLES, model.numTriIndices, GL10.GL_UNSIGNED_SHORT, model.triBuffer);
	}
	
	private void setLighting(GL10 gl) {
		float lightAmbient[] = new float[] { 0.2f, 0.3f, 0.6f, 0.9f };
		float lightDiffuse[] = new float[] { 0.9f, 0.9f, 0.7f, 0.9f };
		float[] lightPos = new float[] {5,5,3,1};

		gl.glMatrixMode(GL10.GL_TEXTURE);
		gl.glEnable(GL10.GL_COLOR_MATERIAL);
		gl.glEnable(GL10.GL_LIGHTING);
		gl.glEnable(GL10.GL_LIGHT0);

		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, lightAmbient,	0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, lightDiffuse,	0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPos, 0);
	}
	
	private void projSettings(GL10 gl) {
		float ratio = viewW/viewH;
		nearH = (float) (nearZ*(Math.tan(Math.toRadians(viewAngle))));
		nearW = nearH*ratio;
		
		gl.glMatrixMode(GL10.GL_PROJECTION);
		//gl.glOrthof(-3, 3, -3/ratio, 3/ratio, nearZ, farZ);
		gl.glFrustumf(-nearW, nearW, -nearH, nearH, nearZ, farZ);
		gl.glViewport(0, 0, (int) viewW, (int) viewH);
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
	}
	
}
