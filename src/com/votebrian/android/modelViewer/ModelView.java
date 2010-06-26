package com.votebrian.android.modelViewer;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;

public class ModelView extends GLSurfaceView{
	//Tag for log messages
	private static final String TAG = "ModelView";
	
	//Global Variables
	ModelRenderer renderer;

	public ModelView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		
		renderer = new ModelRenderer();
		setRenderer(renderer);
		
		//Log Message
		Log.i(TAG, "Constructor finished");
	}

}
