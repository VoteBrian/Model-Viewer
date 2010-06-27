package com.votebrian.android.modelViewer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.util.Log;

public class Background {
	//Tag for log messages
	private static final String TAG = "Background";
	
	FloatBuffer vertexBuffer;
	FloatBuffer colorBuffer;
	ShortBuffer triBuffer;
	
	float coords[] = {
			1f, 1f, -1f,
			1f, -1f, -1f,
			-1f, -1f, -1f,
			-1f, 1f, -1f
			
	};
	int numVertices = coords.length;
	
	short triIndices[] = {
			2, 1, 0,
			0, 3, 2
	};
	public int numTriIndices = triIndices.length;
	
	float[] colors = {
	        0.6f, 0.6f, 0.8f, 1f,
	        1f, 1f, 1f, 1f,
	        1f, 1f, 1f, 1f,
	        0.6f, 0.6f, 0.8f, 1f
	};
	public int numColors = colors.length;
	
	public Background() {
		BuildBuffers();
	}
	
	public void updateSize(float w, float h, float z) {
		coords[0] = w;
		coords[1] = h;
		coords[2] = z;
		
		coords[3] = w;
		coords[4] = -h;
		coords[5] = z;

		coords[6] = -w;
		coords[7] = -h;
		coords[8] = z;
		
		coords[9] = -w;
		coords[10] = h;
		coords[11] = z;
		
		BuildBuffers();
		
		Log.i(TAG, "width: " + w);
	}
	
	public void BuildBuffers() {
		//allocate memory for the vertex buffer
		//number of vertices * three coordinates per vertex * 4 bytes per float
		ByteBuffer vbb = ByteBuffer.allocateDirect(numVertices*3*4);
		vbb.order(ByteOrder.nativeOrder());
		vertexBuffer = vbb.asFloatBuffer();
		
		//allocate memory for the index buffer
		//number of indices * 2 bytes per short
		ByteBuffer fbb = ByteBuffer.allocateDirect(numTriIndices*2);
		fbb.order(ByteOrder.nativeOrder());
		triBuffer = fbb.asShortBuffer();
		
		//allocate memory for the index buffer
		//number of vertices * 4 bytes per float
	    ByteBuffer cbb = ByteBuffer.allocateDirect(numVertices*4*4);
	    cbb.order(ByteOrder.nativeOrder());
	    colorBuffer = cbb.asFloatBuffer();

		vertexBuffer.put(coords);
		triBuffer.put(triIndices);
		colorBuffer.put(colors);
		
		vertexBuffer.position(0);
		triBuffer.position(0);
		colorBuffer.position(0);
	}
}
