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
	
	/* ********************
	   Global Variables
	******************** */
	String filename = "models/tile.obj";
	String texFileName;
	
	//Store the sdcard path
	File sdcard_path;
	
	//Triangles
	FloatBuffer	trianglesBuffer;
	float[] triangles;
	int numTriangles = 0;
	
	float[] vertices;
	int numVertices = 0;
	
	short[] vertIndices;
	int numVertIndices = 0;
	
	//Texture
	FloatBuffer	texBuffer;
	float[] texture;
	float[] finalTexture;
	int numTex = 0;
	
	//Texture Indices
	ShortBuffer texIndexBuffer;
	short[] texIndices;
	int numTexIndices = 0;
	
	//Normals
	FloatBuffer normalBuffer;
	float[] normals;
	float[] finalNorm;
	int numNormals = 0;
	
	//Normal Indices
	ShortBuffer normIndexBuffer;
	short[] normIndices;
	int numNormIndices = 0;
	
	//Colors
	//This buffer will replace the texture buffer in the event that the obj file does
	//not specify a texture or if the texture image cannot be loaded.
	FloatBuffer	colorBuffer;
	float[] colors;
	int numColors = 0;
	
	//Center coordinates of the model
	float centX;
	float centY;
	float centZ;
	
	float rotAngleX;
	float rotAngleY;
	
	//TODO should this go with the texture variables above or is this separate?
	private int[] textures = new int[3];
	
	public Model(float x, float y, float z) {
		FileInputStream fileIS;
			  
		centX = x;
		centY = y;
		centZ = z;
		
		File file;
		
		//build the default filename
		sdcard_path = Environment.getExternalStorageDirectory();
		file = new File(sdcard_path, filename);
		
		try {
			fileIS = new FileInputStream(file);
		} catch (FileNotFoundException e1) {
			//TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//read the file
		readFile(file);
		
		BuildBuffers();
	}
	
	public void BuildBuffers() {
		int ind;
		
		triangles = new float [numVertIndices * 3 * 3];
		finalNorm = new float [numVertIndices * 3 * 3];
		finalTexture = new float [numVertIndices * 3 * 3];
		
		for(ind = 0; ind < numVertIndices * 3; ind++) {
			triangles[ind*3] = vertices[vertIndices[ind]*3];
			triangles[ind*3+1] = vertices[vertIndices[ind]*3+1];
			triangles[ind*3+2] = vertices[vertIndices[ind]*3+2];
			
			finalNorm[ind*3] = normals[normIndices[ind]*3];
			finalNorm[ind*3+1] = normals[normIndices[ind]*3+1];
			finalNorm[ind*3+2] = normals[normIndices[ind]*3+2];
			
			finalTexture[ind*3] = texture[texIndices[ind]*3];
			finalTexture[ind*3+1] = texture[texIndices[ind]*3+1];
			finalTexture[ind*3+2] = texture[texIndices[ind]*3+2];
		}
		numTriangles = triangles.length;
		
		//Vertex Buffer
		ByteBuffer vbb = ByteBuffer.allocateDirect(numTriangles*3*4);
		vbb.order(ByteOrder.nativeOrder());
		trianglesBuffer = vbb.asFloatBuffer();
		trianglesBuffer.put(triangles);
		trianglesBuffer.position(0);
		
		//Vertex Indices Buffer
		/*ByteBuffer ibb = ByteBuffer.allocateDirect(numVertIndices*3*2);
		ibb.order(ByteOrder.nativeOrder());
		vertIndexBuffer = ibb.asShortBuffer();
		vertIndexBuffer.put(vertIndices);
		vertIndexBuffer.position(0);
		*/
		
		//Texture Buffer
		ByteBuffer tbb = ByteBuffer.allocateDirect(numTriangles*3*4);
		tbb.order(ByteOrder.nativeOrder());
		texBuffer = tbb.asFloatBuffer();
		texBuffer.put(finalTexture);
		texBuffer.position(0);
		
		//Normal Buffer
		ByteBuffer nbb = ByteBuffer.allocateDirect(numTriangles*3*4);
		nbb.order(ByteOrder.nativeOrder());
		normalBuffer = nbb.asFloatBuffer();
		normalBuffer.put(finalNorm);
		normalBuffer.position(0);
		
		//TODO Color Buffer
		//if necessary
	}
	
	public void draw(GL10 gl) {
		//stuff
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
		
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
		
		gl.glFrontFace(GL10.GL_CCW);
		
		//Point to our buffers
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, trianglesBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texBuffer);
		gl.glNormalPointer(GL10.GL_FLOAT, 0, normalBuffer);
		
		
		//gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glTranslatef(centX, centY, -1*centZ);
		gl.glRotatef(rotAngleX, 0f, 1f, 0f);
		gl.glRotatef(rotAngleY, 1f, 0f, 0f);	
		
		gl.glDrawArrays(GL10.GL_TRIANGLES, 0, numTriangles);
		
		//Disable the client state before leaving
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
	}
	
	public void setAngleX(float angle) {
		rotAngleX += angle;
	}
	
	public void setAngleY(float angle) {
		rotAngleY += angle;
	}
	
	public void loadTexture(GL10 gl, Context context) {
		InputStream is = context.getResources().openRawResource(R.drawable.tex);
		Bitmap bitmap = null;
		
		try{
			bitmap = BitmapFactory.decodeStream(is);
		} finally {
			try {
				is.close();
				is = null;
			} catch(IOException e) {
				is = null;
			}
		}
		
		gl.glGenTextures(3, textures, 0);
		
		//Generate there texture pointer
		//gl.glGenTextures(3, textures, 0);

		//Create Nearest Filtered Texture and bind it to texture 0
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);

		//Create Linear Filtered Texture and bind it to texture 1
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[1]);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);

		/*
		//Create mipmapped textures and bind it to texture 2
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[2]);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR_MIPMAP_NEAREST);
		
		
		gl.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_GENERATE_MIPMAP, GL11.GL_TRUE);
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
		*/
		
		bitmap.recycle();
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
					numVertIndices++;
				}else {
					//ignore
				}
			}
			reader.close();
			
			vertices = new float [numVertices * 3];
			vertIndices = new short [numVertIndices * 3];
			normals = new float [numNormals * 3];
			normIndices = new short [numVertIndices * 3];
			texture = new float [numTex * 3];
			texIndices = new short [numVertIndices *3];
			
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
					
					//store first vertex index
					index = line.indexOf("/");
					value = line.substring(0, index);
					remainder = line.substring(index+1, lineLength);
					if(indicesInd != 0) {
						indicesInd++;
					}
					vertIndices[indicesInd] = (short) (Short.parseShort(value) - 1);
					line = remainder;
					lineLength = line.length();
					
					//store first texture index
					index = line.indexOf("/");
					value = line.substring(0, index);
					remainder = line.substring(index+1, lineLength);
					texIndices[indicesInd] = (short) (Short.parseShort(value) - 1);
					line = remainder;
					lineLength = line.length();
					
					//store first normal index
					index = line.indexOf(" ");
					value = line.substring(0, index);
					remainder = line.substring(index+1, lineLength);
					normIndices[indicesInd] = (short) (Short.parseShort(value) - 1);
					line = remainder;
					lineLength = line.length();
					
					
					//store second vertex index
					index = line.indexOf("/");
					value = line.substring(0, index);
					remainder = line.substring(index+1, lineLength);
					indicesInd++;
					vertIndices[indicesInd] = (short) (Short.parseShort(value) - 1);
					line = remainder;
					lineLength = line.length();
					
					//store second texture index
					index = line.indexOf("/");
					value = line.substring(0, index);
					remainder = line.substring(index+1, lineLength);
					texIndices[indicesInd] = (short) (Short.parseShort(value) - 1);
					line = remainder;
					lineLength = line.length();
					
					//store second normal index
					index = line.indexOf(" ");
					value = line.substring(0, index);
					remainder = line.substring(index+1, lineLength);
					normIndices[indicesInd] = (short) (Short.parseShort(value) - 1);
					line = remainder;
					lineLength = line.length();
					
					
					//store second vertex index
					index = line.indexOf("/");
					value = line.substring(0, index);
					remainder = line.substring(index+1, lineLength);
					indicesInd++;
					vertIndices[indicesInd] = (short) (Short.parseShort(value) - 1);
					line = remainder;
					lineLength = line.length();
					
					//store second texture index
					index = line.indexOf("/");
					value = line.substring(0, index);
					remainder = line.substring(index+1, lineLength);
					texIndices[indicesInd] = (short) (Short.parseShort(value) - 1);
					line = remainder;
					lineLength = line.length();
					
					//store second normal index
					value = line;
					normIndices[indicesInd] = (short) (Short.parseShort(value) - 1);
					line = remainder;
					lineLength = line.length();
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
}
