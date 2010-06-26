package com.votebrian.android.modelViewer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Model {
	public FloatBuffer	vertexBuffer;
	public FloatBuffer	colorBuffer;
	public ShortBuffer	triBuffer;
	
	float centX;
	float centY;
	float centZ;
	
	float coords[] = {
			1, 1, -1,
			-1, 1, -1,
			0, -1, -1
	};
	int numVertices = coords.length;
	
	short[] triIndices = {
		1, 3, 2
	};
	public int numTriIndices = triIndices.length;
	
	float[] colors = {
        1f, 1f, 1f, 1f,
        1f, 1f, 1f, 1f,
        1f, 1f, 1f, 1f
	};
	public int numColors = colors.length;
	
	public Model(float x, float y, float z) {
		centX = x;
		centY = y;
		centZ = z;
		
		BuildBuffers();
	}
	
	public void BuildBuffers() {
		//allocate memory for the vertex buffer
		//number of vertices * three coordinates per vertex * 4 bytes per float
		ByteBuffer vbb = ByteBuffer.allocateDirect(numVertices*4);
		vbb.order(ByteOrder.nativeOrder());
		vertexBuffer = vbb.asFloatBuffer();
		
		//allocate memory for the index buffer
		//number of indices * 2 bytes per short
		ByteBuffer fbb = ByteBuffer.allocateDirect(numTriIndices*2);
		fbb.order(ByteOrder.nativeOrder());
		triBuffer = fbb.asShortBuffer();
		
		//allocate memory for the index buffer
		//number of vertices * 4 bytes per float
	    ByteBuffer cbb = ByteBuffer.allocateDirect(numColors*4);
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
