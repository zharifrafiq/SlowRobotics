package pointCloudTools;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.media.opengl.GL2;

import controlP5.Println;
import dynamicTools.MainApp;
import KinectPV2.KinectPV2;
import processing.opengl.PJOGL;
import toxi.geom.AABB;
import toxi.geom.Vec3D;

public class KinectHandler {
	
	KinectPV2 kinect;
	MainApp parent;
	float maxD = 1.8f; //meters
	float minD = 0f;
	float[] m = new float[16];
	
	public KinectHandler(MainApp _parent){
		parent = _parent;
		kinect = new KinectPV2(parent);
		//kinect.enableDepthImg(true);
		kinect.enableColorChannel(true);
		//kinect.enablePointCloud(true);
		kinect.enablePointCloudColor(true);
		
		//kinect.activateRawDepth(true);
		//kinect.setLowThresholdPC(minD);
		//kinect.setHighThresholdPC(maxD);
		
		kinect.init();

	}
	
	public float[] copyPtBuffer (){
		
		//setup transform matrix
		setOpenGlTransformMatrix();
		FloatBuffer original = kinect.getPointCloudColorPos();

		float[] pts = new float[original.capacity()];
		for(int i = 0;i<original.capacity();i+=3){
			float[] p= transform(original.get(i), original.get(i+1), original.get(i+2));
			//float f = original.get(i);
			pts[i]=p[0];
			pts[i+1]=p[1];
			pts[i+2]=p[2];
		}
		
	    return pts;

	}
	
	public float[] copyColourBuffer (){
		
		FloatBuffer original = kinect.getColorChannelBuffer();
		float[] colours = new float[original.capacity()];
		for(int i = 0;i<original.capacity();i+=3){
			//float f = original.get(i);
			colours[i]=original.get(i);
			colours[i+1]=original.get(i+1);
			colours[i+2]=original.get(i+2);
		}
		
	    return colours;

	}
	
	public float[][] copyAABB(AABB box){
		//setup transform matrix
				setOpenGlTransformMatrix();
				FloatBuffer original = kinect.getPointCloudColorPos();
				FloatBuffer colours = kinect.getColorChannelBuffer();
				int c =0;
				
				float[][] pts = new float[2][original.capacity()];
				for(int i = 0;i<original.capacity();i+=3){
					float[] p= transform(original.get(i), original.get(i+1), original.get(i+2));
					if(box.containsPoint(new Vec3D(p[0],p[1],p[2]))){
						pts[0][c]=p[0];
						pts[0][c+1]=p[1];
						pts[0][c+2]=p[2];
						pts[1][c]=colours.get(i);
						pts[1][c+1]=colours.get(i+1);
						pts[1][c+2]=colours.get(i+2);
						c+=3;
					}
				}
				float[][] cropPts = new float[2][c];
				for(int i =0;i<c;i++){
					cropPts[0][i]=pts[0][i];
					cropPts[1][i]=pts[1][i];
				}
				
			    return cropPts;
		
	}
	
	
	public void renderDepth(){
		FloatBuffer pointCloudBuffer = kinect.getPointCloudDepthPos();
		
		PJOGL pgl = (PJOGL)parent.beginPGL();
		GL2 gl2 = pgl.gl.getGL2();

		gl2.glEnable( GL2.GL_BLEND );
		gl2.glEnable(GL2.GL_POINT_SMOOTH);      

		gl2.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		gl2.glVertexPointer(3, GL2.GL_FLOAT, 0, pointCloudBuffer);
		
		gl2.glTranslatef(parent.kinectTranslateX, parent.kinectTranslateY, parent.kinectTranslateZ);
		gl2.glRotatef(parent.kinectRotateA, parent.kinectRotateAxisX, parent.kinectRotateAxisY, parent.kinectRotateAxisY);
		gl2.glScalef(parent.kinectScale, -parent.kinectScale, -parent.kinectScale);

		gl2.glDrawArrays(GL2.GL_POINTS, 0, (kinect.WIDTHDepth * kinect.HEIGHTDepth)-1);
		gl2.glDisableClientState(GL2.GL_VERTEX_ARRAY);
		gl2.glDisable(GL2.GL_BLEND);
		parent.endPGL();
	}
	
	
	public void renderColours(){
		FloatBuffer pointCloudBuffer = kinect.getPointCloudColorPos();
		FloatBuffer colorBuffer      = kinect.getColorChannelBuffer();
		
		PJOGL pgl = (PJOGL)parent.beginPGL();
		GL2 gl2 = pgl.gl.getGL2();

		gl2.glEnable( GL2.GL_BLEND );
		//gl2.glEnable(GL2.GL_POINT_SMOOTH);      

		gl2.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		gl2.glEnableClientState(GL2.GL_COLOR_ARRAY);
		gl2.glVertexPointer(3, GL2.GL_FLOAT, 0, pointCloudBuffer);
		gl2.glColorPointer(3, GL2.GL_FLOAT, 0, colorBuffer);
		
		gl2.glTranslatef(parent.kinectTranslateX, parent.kinectTranslateY, parent.kinectTranslateZ);
		gl2.glRotatef(parent.kinectRotateA, parent.kinectRotateAxisX, parent.kinectRotateAxisY, parent.kinectRotateAxisY);
		gl2.glScalef(parent.kinectScale, -parent.kinectScale, -parent.kinectScale);
	
		gl2.glDrawArrays(GL2.GL_POINTS, 0, (kinect.WIDTHColor * kinect.HEIGHTColor)-1);
		gl2.glDisableClientState(GL2.GL_VERTEX_ARRAY);
		gl2.glDisableClientState(GL2.GL_COLOR_ARRAY);
		gl2.glDisable(GL2.GL_BLEND);
		parent.endPGL();
	}
	
	public void setOpenGlTransformMatrix(){
		PJOGL pgl = (PJOGL)parent.beginPGL();
		GL2 gl2 = pgl.gl.getGL2();
		gl2.glPushMatrix();
		gl2.glLoadIdentity();
		gl2.glTranslatef(parent.kinectTranslateX, parent.kinectTranslateY, parent.kinectTranslateZ);
		gl2.glRotatef(parent.kinectRotateA, parent.kinectRotateAxisX, parent.kinectRotateAxisY, parent.kinectRotateAxisY);
		gl2.glScalef(parent.kinectScale, -parent.kinectScale, -parent.kinectScale);
		m = new float[16];
		gl2.glGetFloatv(gl2.GL_MODELVIEW_MATRIX, m,0);
		gl2.glPopMatrix();
	}
	
	float[] transform(float x, float y, float z){
		float[] tp = new float[3];
		tp[0] = x*m[0] + y*m[4] + z*m[8] + m[12];
		tp[1] = x*m[1] + y*m[5] + z*m[9] + m[13];
		tp[2] = x*m[2] + y*m[6] + z*m[10] + m[14];
		return tp;
	}
	
	

}
