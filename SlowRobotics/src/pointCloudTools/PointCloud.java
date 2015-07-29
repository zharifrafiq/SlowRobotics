package pointCloudTools;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import javax.media.opengl.GL2;

import core.Agent;
import core.MainApp;
import processing.core.PApplet;
import processing.opengl.PJOGL;
import toxi.geom.AABB;
import toxi.geom.Triangle2D;
import toxi.geom.Vec2D;
import toxi.geom.Vec3D;
import toxi.geom.mesh.WETriangleMesh;
import toxi.geom.mesh2d.DelaunayTriangulation;
import toxi.geom.mesh2d.Voronoi;


public class PointCloud {
	
	float[] pts;
	float[] colours;
	public Plane3DOctree tree;
	FloatBuffer ptBuffer;
	FloatBuffer colourBuffer;
	HashMap<String, Float>heightField = new HashMap<String,Float>();
	HashMap<String, float[]>fieldColours = new HashMap<String,float[]>();
	PApplet parent;
	boolean loaded;
	int res = 2;
	
	public PointCloud(float[] _pts, PApplet _parent){
		parent = _parent;
		pts = _pts;
		loaded = true;
	}
	
	public PointCloud(PApplet _parent){
		pts = new float[0];
		colours = new float[0];
		parent = _parent;
		tree = new Plane3DOctree(new Vec3D(0,0,-500), 2000, parent);
		loaded = false;
	}
	
	//-------------------------------------------------------------------------------------

	//get/set/load

	//-------------------------------------------------------------------------------------
	
	public boolean ready(){
		return loaded;
	}
	
	public void loadPts(float[] _pts){
		pts = _pts;
		loaded = true;
	}
	
	public void loadColours(float[] _colours){
		colours = _colours;
	}
	
	public void loadSinWavePts(){
		int num = 100;
		pts = new float[num*num*3];
		colours = new float[num*num*3];
		int count =0;
		for(int i=0;i<num;i++){
			for(int j=0;j<num;j++){
				pts[count]=i*5;
				pts[count+1]=j*5;
				pts[count+2]=(parent.sin(i/10f)*(parent.sin(j/10f))*100);
				colours[count]=parent.random(1);
				colours[count+1]=parent.random(1);
				colours[count+2]=parent.random(1);
				count+=3;
			}
		}
		loaded = true;
		createBuffers(false);
		buildTree();
		
	}
	
	public void load(float[][] data){
		pts = data[0];
		colours = data[1];
		
		//reset heightfields
		heightField = new HashMap<String,Float>();
		fieldColours = new HashMap<String,float[]>();
		loaded = true;
		createBuffers(false);
	}
	
	FloatBuffer allocateDirectFloatBuffer(int n) {
		int SIZEOF_FLOAT = Float.SIZE / 8;
		return ByteBuffer.allocateDirect(n * SIZEOF_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
	}
	
	
	public ArrayList<Vec3D> getPoints(){
		ArrayList<Vec3D> vectors = new ArrayList<Vec3D>();
		if(loaded){
			for (int i = 0;i<pts.length;i+=3){
				Vec3D pt = new Vec3D(pts[i], pts[i+1],pts[i+2]);
				vectors.add(pt);
			}
		}
		return vectors;
	}
	
	public Vec3D randomPoint(){
		ArrayList<Vec3D> vecs = getPoints();
		return vecs.get((int)parent.random(vecs.size()-1));
	}
	
	public HashMap<Vec2D, Vec3D> getProjectedPoints(){
		HashMap<Vec2D, Vec3D> projectedPoints = new HashMap<Vec2D, Vec3D>();
		for (int i = 0;i<pts.length;i+=3){
			Vec3D pt = new Vec3D(pts[i], pts[i+1],pts[i+2]);
			Vec2D pt2D = pt.to2DXY();
			projectedPoints.put(pt2D, pt);
		}
		return projectedPoints;
	}
	
	public void createBuffers(boolean useHeightField){
		if(!useHeightField){
			ptBuffer = allocateDirectFloatBuffer(pts.length);
			ptBuffer.rewind();
			ptBuffer.put(pts);
			ptBuffer.position(0);
			colourBuffer = allocateDirectFloatBuffer(colours.length);
			colourBuffer.rewind();
			colourBuffer.put(colours);
			colourBuffer.position(0);
		}else{
			pts = new float[heightField.size()*3];
			colours = new float[fieldColours.size()*3];
			int i=0;
			
			for(String k:heightField.keySet()){
				String[] s = k.split(",");
				pts[i]=(Float.valueOf(s[0]));
				pts[i+1]=(Float.valueOf(s[1]));
				pts[i+2]=(heightField.get(k));
				float[] col = fieldColours.get(k);
				colours[i]=(col[0]);
				colours[i+1]=(col[1]);
				colours[i+2]=(col[2]);
				i+=3;
			}
			createBuffers(false);
		}

	}

	public void renderHeightField(){
		for(String k:heightField.keySet()){
			String[] s = k.split(",");
			parent.point(Float.valueOf(s[0]),Float.valueOf(s[1]),heightField.get(k));
		}
	}
	
	public void appendPoints(PointCloud pcl){

		pts = concat(pts, pcl.pts);
		colours = concat(colours, pcl.colours);
		loaded = true;
		createBuffers(false);
		//insert new pts into tree
		for(Vec3D a:pcl.getPoints()){
			tree.addPoint(a);
		}
	}
	
	
	
	public float[] concat(float[] pts2, float[] pts3) {
		   int aLen = pts2.length;
		   int bLen = pts3.length;
		   float[] c= new float[aLen+bLen];
		   System.arraycopy(pts2, 0, c, 0, aLen);
		   System.arraycopy(pts3, 0, c, aLen, bLen);
		   return c;
		}
	
	public void buildTree(){
		tree = new Plane3DOctree(new Vec3D(-500,-500,-500), 2000, parent);
		ArrayList<Vec3D> tpts = getPoints();
		if(tpts.size()>0){
			System.out.println("building tree with "+ tpts.size() +" number of points");
			//each Plane3D has an index, this is a very sloppy way of letting me export out the structure lines...
			for(Vec3D a:tpts){
				tree.addPoint(a);
			}
			System.out.println("tree contains "+ tree.getPoints().size()+ " number of points");
		}
	}
	
	public void buildHeightField(){
		for (int i=0;i<pts.length;i+=3){
			String key = ""+((int)(pts[i]/res))*res+","+((int)(pts[i+1]/res))*res;
			heightField.put(key, pts[i+2]);
			float[] cols = new float[]{colours[i],colours[i+1],colours[i+2]};
			fieldColours.put(key,cols);
		}
	}
	
	public void appendHeightField(PointCloud pcl){
		parent.stroke(255);
		for (int i=0;i<pcl.pts.length;i+=3){
			String key = ""+((int)(pcl.pts[i]/res))*res+","+((int)(pcl.pts[i+1]/res))*res;
			heightField.put(key, pcl.pts[i+2]);
			float[] cols = new float[]{pcl.colours[i],pcl.colours[i+1],pcl.colours[i+2]};
			fieldColours.put(key,cols);
		}
		loaded = true;
		createBuffers(true);
		buildTree();
	}
	
	public void insertHeight(float x, float y, float z, float[] cols){
		String key = ""+(int)x+","+(int)y;
		heightField.put(key, z);
		fieldColours.put(key, cols);
	}
	
	public WETriangleMesh createDelaunayMesh(){
		
		WETriangleMesh mesh = new WETriangleMesh();
		Voronoi voronoi = new Voronoi();
		HashMap<Vec2D,Vec3D> ptMap = getProjectedPoints();
		voronoi.addPoints(ptMap.keySet());
		for(Triangle2D tri:voronoi.getTriangles()){
			//dont add massive faces
			//if(tri.getArea()<100){
				if (ptMap.containsKey(tri.a) && ptMap.containsKey(tri.b) && ptMap.containsKey(tri.c)){
					mesh.addFace(ptMap.get(tri.a), ptMap.get(tri.b), ptMap.get(tri.c));
				}
			//}
		}
		
		return mesh;
	}
	
	//-------------------------------------------------------------------------------------

	//modification

	//-------------------------------------------------------------------------------------

	public ArrayList<Vec3D> cropAABB(AABB box){
		ArrayList<Vec3D> inBox = new ArrayList<Vec3D>();
		for (int i = 0;i<pts.length;i+=3){
			Vec3D pt = new Vec3D(pts[i], pts[i+1],pts[i+2]);
			if(box.containsPoint(pt)){
				inBox.add(pt);
			}
		}
		return inBox;
	}
	
	public void cropArrayAABB(AABB box){
		int count = 0;
		float[] buffer = new float[pts.length];
		for (int i = 0;i<pts.length;i+=3){
			Vec3D pt = new Vec3D(pts[i], pts[i+1],pts[i+2]);
			if(box.containsPoint(pt)){
				buffer[count]=pts[i];
				buffer[count+1]=pts[i+1];
				buffer[count+2]=pts[i+2];
				count+=3;
			}
		}
		float[] newPts = new float[count];
		for(int i =0;i<count;i++){
			newPts[i]=buffer[i];
		}
		pts = newPts;
	}
	
	public void extractColourRange(float r, float g, float b, float range){

		int count = 0;
		float[][] buffer = new float[2][pts.length];
		for (int i = 0;i<pts.length;i+=3){
			/*float dr = Math.abs(r-(colours[i]*255));
			float dg = Math.abs(g-(colours[i+1]*255));
			float db = Math.abs(b-(colours[i+2]*255));*/
			
			float dr = (colours[i]*255);
			float dg = (colours[i+1]*255);
			float db = (colours[i+2]*255);
			
			//if(dr<range && db<range && dg<range){
			if(dr>r && db>b && dg>g){
				buffer[0][count]=pts[i];
				buffer[0][count+1]=pts[i+1];
				buffer[0][count+2]=pts[i+2];
				buffer[1][count]=colours[i];
				buffer[1][count+1]=colours[i+1];
				buffer[1][count+2]=colours[i+2];
				count+=3;
			}
		}
		float[][] newPts = new float[2][count];
		for(int i =0;i<count;i++){
			newPts[0][i]=buffer[0][i];
			newPts[1][i]=buffer[1][i];
		}
		
		pts = newPts[0];
		colours = newPts[1];
		loaded = true;
		//createBuffers(false);
		//buildTree();
	}

	
	//-------------------------------------------------------------------------------------

	//rendering

	//-------------------------------------------------------------------------------------
	
	public void renderPts(){
		if(pts.length>3){

		PJOGL pgl = (PJOGL)parent.beginPGL();
		  GL2 gl2 = pgl.gl.getGL2();

		  gl2.glEnable( GL2.GL_BLEND );
		  gl2.glEnable(GL2.GL_POINT_SMOOTH);      

		  gl2.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		  gl2.glVertexPointer(3, GL2.GL_FLOAT, 0, ptBuffer);
		  gl2.glDrawArrays(GL2.GL_POINTS, 0, pts.length/3);
		  gl2.glDisableClientState(GL2.GL_VERTEX_ARRAY);
		  gl2.glDisable(GL2.GL_BLEND);
		  parent.endPGL();
		}
	}
	
	public void renderColours(){
		if(pts.length>3){
			PJOGL pgl = (PJOGL)parent.beginPGL();
			GL2 gl2 = pgl.gl.getGL2();

			gl2.glEnable( GL2.GL_BLEND );
			//gl2.glEnable(GL2.GL_POINT_SMOOTH);      
			
			gl2.glEnableClientState(GL2.GL_VERTEX_ARRAY);
			gl2.glEnableClientState(GL2.GL_COLOR_ARRAY);
			gl2.glVertexPointer(3, GL2.GL_FLOAT, 0, ptBuffer);
			gl2.glColorPointer(3, GL2.GL_FLOAT, 0, colourBuffer);
			gl2.glDrawArrays(GL2.GL_POINTS, 0, pts.length/3);
			gl2.glDisableClientState(GL2.GL_VERTEX_ARRAY);
			gl2.glDisableClientState(GL2.GL_COLOR_ARRAY);
			gl2.glDisable(GL2.GL_BLEND);
			parent.endPGL();
		}
	}
	
	public void renderVecPts(){
		
	}
	
}
