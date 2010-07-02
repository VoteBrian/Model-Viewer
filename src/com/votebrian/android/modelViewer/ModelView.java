package com.votebrian.android.modelViewer;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;

public class ModelView extends GLSurfaceView{
	//Tag for log messages
	private static final String TAG = "ModelView";
	
	//Global Variables
	ModelRenderer renderer;
	float startX = 0;
	float startY = 0;

	public ModelView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		
		renderer = new ModelRenderer();
		setRenderer(renderer);
		
		//Log Message
		Log.i(TAG, "Constructor finished");
	}
	
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
					renderer.setAngleX(x-startX);
					startX = x;
					
					renderer.setAngleY(y-startY);
					startY = y;
					break;
				}
			}
		});
		return true;
	}
	
	

}
