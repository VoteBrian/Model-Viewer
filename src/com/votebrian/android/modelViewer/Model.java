package com.votebrian.android.modelViewer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.os.Environment;

public class Model {
	public String filename = "models/standard.off";
	public File sdcard;
	public FloatBuffer	vertexBuffer;
	public FloatBuffer	colorBuffer;
	public ShortBuffer	triBuffer;
	
	float centX;
	float centY;
	float centZ;
	
	float[] coords;
	int numVertices;;
	
	short[] triangles;
	public int numTriangles;
	
	float[] colors;
	public int numColors;
	
	public Model(float x, float y, float z) {
		String line;
		String value;
		String remainder;
		String remainderZ;
		
		BufferedReader reader;
		
		int lineLength	= 0;
		int index		= 0;
		int vertices	= 0;
		int indices		= 0;
		
		float vertX		= 0f;
		float vertY		= 0f;
		float vertZ		= 0f;
		
		final int FILETYPE	= 0;
		final int LENGTHS	= 1;
		final int VERTICES	= 2;
		final int INDICES	= 3;
		final int DONE		= 4;
			  int type		= FILETYPE;
			  
		centX = x;
		centY = y;
		centZ = z;
		
		//build the default filename
		sdcard = Environment.getExternalStorageDirectory();
		File file = new File(sdcard, filename);
		
		//establish the reader
		try {
			reader = new BufferedReader(new FileReader(file));
			
			while((line = reader.readLine()) != null) {
				if(type == FILETYPE) {
					//the first line should just be "OFF\n"
					//ignore that line
					type = LENGTHS;
				} else if(type == LENGTHS) {
					//record the values for number of vertices and indices
					lineLength = line.length();
					
					index = line.indexOf(" ");
					value = line.substring(0, index-1);
					numVertices = vertices = Integer.parseInt(value);
					
					remainder = line.substring(index+1,lineLength);
					index = remainder.indexOf(" ");
					value = line.substring(0, index-1);
					numTriangles = indices = Integer.parseInt(value);
					
					type = VERTICES;
				} else if(type == VERTICES) {
					//store vertices values
					lineLength = line.length();
					
					index = line.indexOf(" ");
					vertX = Float.parseFloat( line.substring(0, index-1) );
					
					remainder = line.substring(index+1, lineLength);
					index = remainder.indexOf(" ");
					vertY = Float.parseFloat( remainder.substring(0, index-1) );
					
					remainderZ = remainder.substring(index+1, lineLength);
					index = remainderZ.indexOf("/n");
					vertZ = Float.parseFloat( remainderZ.substring(0, index-1) );
					
					coords[numVertices*3 - vertices*3] = vertX;
					coords[numVertices*3 - vertices*3 + 1] = vertY;
					coords[numVertices*3 - vertices*3 + 2] = vertZ;
					
					if(vertices == 1) {
						type = INDICES;
					}
					
					vertices -= 1;
				} else if(type == INDICES)	{
					//store indices values
					type = DONE;
				}
			}
		}
		catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
		
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
		ByteBuffer fbb = ByteBuffer.allocateDirect(numTriangles*2);
		fbb.order(ByteOrder.nativeOrder());
		triBuffer = fbb.asShortBuffer();
		
		//allocate memory for the index buffer
		//number of vertices * 4 bytes per float
	    ByteBuffer cbb = ByteBuffer.allocateDirect(numVertices*4*4);
	    cbb.order(ByteOrder.nativeOrder());
	    colorBuffer = cbb.asFloatBuffer();

		vertexBuffer.put(coords);
		triBuffer.put(triangles);
		colorBuffer.put(colors);
		
		vertexBuffer.position(0);
		triBuffer.position(0);
		colorBuffer.position(0);
	}
}
