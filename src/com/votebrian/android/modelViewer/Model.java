package com.votebrian.android.modelViewer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

import javax.microedition.khronos.opengles.GL10;

import android.os.Environment;

public class Model {
	public String filename = "models/standard.off";
	public File sdcard;
	public FloatBuffer	vertexBuffer;
	public FloatBuffer	colorBuffer;
	public FloatBuffer	texBuffer;
	public ShortBuffer	triBuffer;
	
	float centX;
	float centY;
	float centZ;
	
	float rotAngleX;
	float rotAngleY;
	
	float[] coords;
	int numVertices;;
	
	short[] triangles;
	public int numTriangles;
	
	float[] colors;
	public int numColors;
	
	float[] texture = {
			0.750000f, 0.750000f,
			0.500000f, 0.750000f,
			0.500000f, 0.500000f,
			0.750000f, 0.500000f,
			0.250000f, 0.500000f,
			0.250000f, 0.250000f,
			0.500000f, 0.250000f,
			0.250000f, 0.750000f,
			0.000000f, 0.750000f,
			0.000000f, 0.500000f,
			0.500000f, 1.000000f,
			0.250000f, 1.000000f,
			0.500000f, 0.000000f,
			0.250000f, 0.000000f
	};
	public int numTex;
	
	public Model(float x, float y, float z) {
		String line;
		String value;
		String remainder;
		String remainderZ;
		
		BufferedReader reader;
		FileInputStream fileIS;
		
		int lineLength	= 0;
		int index		= 0;
		int vertices	= 0;
		int indices		= 0;
		
		float vertX		= 0f;
		float vertY		= 0f;
		float vertZ		= 0f;
		
		short ind1		= 0;
		short ind2		= 0;
		short ind3		= 0;
		
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
		
		try {
			fileIS = new FileInputStream(file);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
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
					value = line.substring(0, index);
					numVertices = vertices = Integer.parseInt(value);
					coords = new float[numVertices * 3];
					colors = new float[numVertices * 4];
					
					remainder = line.substring(index+1,lineLength);
					index = remainder.indexOf(" ");
					value = remainder.substring(0, index);
					numTriangles = indices = Integer.parseInt(value);
					triangles = new short[numTriangles * 3];
					
					type = VERTICES;
				} else if(type == VERTICES) {
					//store vertices values
					lineLength = line.length();
					
					index = line.indexOf(" ");
					vertX = Float.parseFloat( line.substring(0, index) );
					remainder = line.substring(index+1, lineLength);
					
					line = remainder;
					lineLength = line.length();
					index = line.indexOf(" ");
					vertY = Float.parseFloat( line.substring(0, index) );
					remainder = line.substring(index+1, lineLength);

					line = remainder;
					vertZ = Float.parseFloat( line );
					
					coords[numVertices*3 - vertices*3] = vertX;
					coords[numVertices*3 - vertices*3 + 1] = vertY;
					coords[numVertices*3 - vertices*3 + 2] = vertZ;
					
					if(vertices == 1) {
						type = INDICES;
					}
					
					vertices -= 1;
				} else if(type == INDICES)	{
					//store indices values
					lineLength = line.length();
					
					//ignore first value
					index = line.indexOf(" ");
					remainder = line.substring(index+1, lineLength);
					
					line = remainder;
					lineLength = line.length();
					index = remainder.indexOf(" ");
					remainder = line.substring(index+1, lineLength);
					ind1 = Short.parseShort( line.substring(0, index) );
					
					line = remainder;
					lineLength = line.length();
					index = line.indexOf(" ");
					remainder = line.substring(index+1, lineLength);
					ind2 = Short.parseShort( line.substring(0, index) );
					ind3 = Short.parseShort( line.substring(index+1, lineLength) );
					
					triangles[numTriangles*3 - indices*3] = ind1;
					triangles[numTriangles*3 - indices*3 + 1] = ind2;
					triangles[numTriangles*3 - indices*3 + 2] = ind3;
					
					if(indices == 1) {
						type = DONE;
					}
					
					indices -= 1;
				}
			}
		}
		catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
		
		Arrays.fill(colors, 0.8f);
		
//		//color the model
//		for(int a = 0; a < colors.length; a++) {
//			if(a%4 == 0) {
//				colors[a] = 1.0f;
//			}
//		};
		
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
		ByteBuffer fbb = ByteBuffer.allocateDirect(numTriangles*3*2);
		fbb.order(ByteOrder.nativeOrder());
		triBuffer = fbb.asShortBuffer();
		
		//allocate memory for the index buffer
		//number of vertices * 4 bytes per float
	    ByteBuffer cbb = ByteBuffer.allocateDirect(numVertices*4*4);
	    cbb.order(ByteOrder.nativeOrder());
	    colorBuffer = cbb.asFloatBuffer();
	    
	    ByteBuffer tbb = ByteBuffer.allocateDirect(numTex*4);
	    tbb.order(ByteOrder.nativeOrder());
	    texBuffer = tbb.asFloatBuffer();

		vertexBuffer.put(coords);
		triBuffer.put(triangles);
		colorBuffer.put(colors);
		
		vertexBuffer.position(0);
		triBuffer.position(0);
		colorBuffer.position(0);
	}
	
	public void draw(GL10 gl) {
		//stuff
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glTranslatef(0f, 0f, -10f);
		gl.glRotatef(rotAngleX, 0f, 1f, 0f);
		gl.glRotatef(rotAngleY, 1f, 0f, 0f);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
		gl.glColor4f(0.8f, 0.8f, 0.8f, 1.0f);
//		gl.glColorPointer(4, GL10.GL_FLOAT, 0, colorBuffer);
		gl.glDrawElements(GL10.GL_TRIANGLES, numTriangles*3, GL10.GL_UNSIGNED_SHORT, triBuffer);
	}
	

	
	public void setAngleX(float angle) {
		rotAngleX += angle;
	}
	
	public void setAngleY(float angle) {
		rotAngleY += angle;
	}
}
