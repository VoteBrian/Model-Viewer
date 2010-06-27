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
			0.391519f, 1.656085f, 0.933089f,
			1.410105f, 2.165212f, 1.222124f,
			1.639830f, 0.986414f, 0.624488f,
			0.240742f, 0.609021f, 0.583073f,
			0.026846f, 0.583785f, -0.323777f,
			-0.018535f, 0.583785f, -0.323777f,
			-0.232430f, 0.609021f, 0.583073f,
			-1.631519f, 0.986414f, 0.624488f,
			-1.401794f, 2.165212f, 1.222124f,
			-0.383207f, 1.656085f, 0.933090f,
			0.003756f, -2.377761f, -0.548851f,
			0.003755f, 1.622239f, -0.548851f
	};
	int numVertices = coords.length;
	
	short[] triIndices = {
			11, 0, 10,
			10, 0, 2,
			1, 2, 0,
			2, 3, 10,
			3, 4, 10,
			6, 5, 10,
			7, 6, 10,
			8, 7, 9,
			9, 7, 10,
			9, 10, 11
	};
	public int numTriIndices = triIndices.length;
	
	float[] colors = {
        0.5f, 0.5f, 0.5f, 1f,
        0.5f, 0.5f, 0.5f, 1f,
        0.5f, 0.5f, 0.5f, 1f,
        0.5f, 0.5f, 0.5f, 1f,
        0.5f, 0.5f, 0.5f, 1f,
        0.5f, 0.5f, 0.5f, 1f,
        0.5f, 0.5f, 0.5f, 1f,
        0.5f, 0.5f, 0.5f, 1f,
        0.5f, 0.5f, 0.5f, 1f,
        0.5f, 0.5f, 0.5f, 1f,
        0.5f, 0.5f, 0.5f, 1f,
        0.5f, 0.5f, 0.5f, 1f
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
