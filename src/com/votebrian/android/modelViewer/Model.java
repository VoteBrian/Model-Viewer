package com.votebrian.android.modelViewer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import android.os.Environment;

public class Model {
	String filename = "models/tile.obj";
	String texFileName;
	
	File sdcard;
	FloatBuffer	vertexBuffer;
	FloatBuffer	colorBuffer;
	FloatBuffer	texBuffer;
	ShortBuffer	indexBuffer;
	
	float centX;
	float centY;
	float centZ;
	
	float rotAngleX;
	float rotAngleY;
	
	float[] vertices;
	int numVertices = 0;
	
	float[] texture;
	int numTex = 0;
	
	float[] normals;
	int numNormals = 0;
	
	short[] indices;
	int numIndices = 0;
	
	private int[] tex = new int[3];
	
	public Model(float x, float y, float z) {
		FileInputStream fileIS;
			  
		centX = x;
		centY = y;
		centZ = z;
		
		File file;
		
		//build the default filename
		sdcard = Environment.getExternalStorageDirectory();
		file = new File(sdcard, filename);
		
		try {
			fileIS = new FileInputStream(file);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//read the file
		readFile(file);
		
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
		ByteBuffer ibb = ByteBuffer.allocateDirect(numIndices*3*2);
		ibb.order(ByteOrder.nativeOrder());
		indexBuffer = ibb.asShortBuffer();

		vertexBuffer.put(vertices);
		indexBuffer.put(indices);
		
		vertexBuffer.position(0);
		indexBuffer.position(0);
	}
	
	public void draw(GL10 gl) {
		//stuff
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glTranslatef(centX, centY, -1*centZ);
		gl.glRotatef(rotAngleX, 0f, 1f, 0f);
		gl.glRotatef(rotAngleY, 1f, 0f, 0f);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
		gl.glColor4f(0.8f, 0.8f, 0.8f, 1.0f);
//		gl.glColorPointer(4, GL10.GL_FLOAT, 0, colorBuffer);
		gl.glDrawElements(GL10.GL_TRIANGLES, numIndices*3, GL10.GL_UNSIGNED_SHORT, indexBuffer);
	}
	
	void readFile(File file) {
		String line;
		String value;
		String remainder;
		String firstTwo;
		String block;		//substring of line
		
		int lineLength	= 0;
		int index		= 0;
		
		int vertInd	= 0;
		int textureInd	= 0;
		int normalInd	= 0;
		int indicesInd	= 0;
		
		BufferedReader reader;
		
		//read the file
		try {
			//Create BufferedReader to determine the length of each array.
			reader = new BufferedReader(new FileReader(file));
			while((line = reader.readLine()) != null) {
				firstTwo = line.substring(0, 2);
				
				
				if(firstTwo.compareTo("v ") == 0) {
					//increment vertices
					numVertices++;
				}else if(firstTwo.compareTo("vt") == 0) {
					//increment texture
					numTex++;
				}else if(firstTwo.compareTo("vn") == 0) {
					//increment normals
					numNormals++;
				}else if(firstTwo.compareTo("f ") == 0) {
					//increment indices
					numIndices++;
				}else {
					//ignore
				}
			}
			reader.close();
			
			vertices = new float [numVertices * 3];
			indices = new short [numIndices * 3];
			normals = new float [numNormals * 3];
			texture = new float [numTex * 3];
			
			reader = new BufferedReader(new FileReader(file));
			while((line = reader.readLine()) != null) {
				//number of characters in string
				lineLength = line.length();
				
				//check the first two characters to determine what to do 
				firstTwo = line.substring(0,2);
				
				if(firstTwo.compareTo("mt") == 0) {			//mtllib
					//ignore
				}else if(firstTwo.compareTo("v ") == 0) {	//vertices coordinates
					////get rid of prefix
					index = line.indexOf(" ");
					remainder = line.substring(index+1, lineLength);
					line = remainder;
					lineLength = line.length();
					
					//store x coordinate
					index = line.indexOf(" ");
					value = line.substring(0, index);
					if(vertInd != 0) {
						vertInd++;
					}
					vertices[vertInd] = Float.parseFloat(value);
					remainder = line.substring(index+1, lineLength);
					line = remainder;
					lineLength = line.length();
					
					//store y coordinate
					index = line.indexOf(" ");
					value = line.substring(0, index);
					vertInd++;
					vertices[vertInd] = Float.parseFloat(value);
					
					//store z coordinate
					value = line.substring(index+1, lineLength);
					vertInd++;
					vertices[vertInd] = Float.parseFloat(value);
				}else if(firstTwo.compareTo("vt") == 0) {	//texture coordinates
					//get rid of prefix
					index = line.indexOf(" ");
					remainder = line.substring(index+1, lineLength);
					line = remainder;
					lineLength = line.length();
					
					//store u coordinate
					index = line.indexOf(" ");
					value = line.substring(0, index);
					if(textureInd != 0) {
						textureInd++;
					}
					texture[textureInd] = Float.parseFloat(value);
					
					//store v coordinate
					value = line.substring(index+1, lineLength);
					textureInd++;
					texture[textureInd] = Float.parseFloat(value);
				}else if(firstTwo.compareTo("vn") == 0) {	//vector normals
					//get rid of prefix
					index = line.indexOf(" ");
					remainder = line.substring(index+1, lineLength);
					line = remainder;
					lineLength = line.length();
					
					//store x coordinate
					index = line.indexOf(" ");
					value = line.substring(0, index);
					if(normalInd != 0) {
						normalInd++;
					}
					normals[normalInd] = Float.parseFloat(value);
					remainder = line.substring(index+1, lineLength);
					line = remainder;
					lineLength = line.length();
					
					//store y coordinate
					index = line.indexOf(" ");
					value = line.substring(0, index);
					normalInd++;
					normals[normalInd] = Float.parseFloat(value);
					
					//store z coordinate
					value = line.substring(index+1, lineLength);
					normalInd++;
					normals[normalInd] = Float.parseFloat(value);
				}else if(firstTwo.compareTo("us") == 0) {	//usemtl (filename of the texture to use)
					//get rid of prefix
					index = line.indexOf(" ");
					remainder = line.substring(index+1, lineLength);
					texFileName = remainder;
				}else if(firstTwo.compareTo("s ") == 0) {	//shading
					//ignore
				}else if(firstTwo.compareTo("f ") == 0) {	//face definitions  (vertex/texture-coordinate/normal)
					//format:		f 5/1/1 1/2/1 4/3/1
					//get rid of prefix
					index = line.indexOf(" ");
					remainder = line.substring(index+1, lineLength);
					line = remainder;
					lineLength = line.length();
					
					//store index 1
					index = line.indexOf(" ");
					block = line.substring(0, index);
					remainder = line.substring(index+1, lineLength);
					index = block.indexOf("/");
					value = block.substring(0, index);
					if(indicesInd != 0) {
						indicesInd++;
					}
					indices[indicesInd] = (short) (Short.parseShort(value) - 1);
					line = remainder;
					lineLength = line.length();
					
					//store index 2
					index = line.indexOf(" ");
					block = line.substring(0, index);
					remainder = line.substring(index+1, lineLength);
					index = block.indexOf("/");
					value = block.substring(0, index);
					indicesInd++;
					indices[indicesInd] = (short) (Short.parseShort(value) - 1);
					line = remainder;
					lineLength = line.length();
					
					//store index 3
					index = line.indexOf("/");
					value = line.substring(0, index);
					indicesInd++;
					indices[indicesInd] = (short) (Short.parseShort(value) - 1);
				}else {
					//ignore
				}
			}
			reader.close();
		}
		catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
	}
	

	
	public void setAngleX(float angle) {
		rotAngleX += angle;
	}
	
	public void setAngleY(float angle) {
		rotAngleY += angle;
	}
	
	public void loadTexture(GL10 gl, Context context) {
		sdcard = Environment.getExternalStorageDirectory();
		
		File modelsDir = new File(sdcard, "models/tile_tex.png");
		
		String temp = "/mnt/sdcard/models/tile_tex.png";
		
		Bitmap bitmap = BitmapFactory.decodeFile(temp);
		if(bitmap == null ) {
			//TODO some error handling goes here
		}
		int[] textures = new int[3];
		gl.glGenTextures(3, textures, 0);
		int textureID = textures[0];
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureID);

        // no mipmaps
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);

        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
		
        bitmap.recycle();
	}
}
