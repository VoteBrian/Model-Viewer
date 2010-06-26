package com.votebrian.android.modelViewer;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class ModelViewer extends Activity {
	//Global variables
	ModelView modelView;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //Get rid of everything else on the screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        //instance ModelView
        modelView = new ModelView(this);
        
        //Set the content view
        setContentView(modelView);
    }
}