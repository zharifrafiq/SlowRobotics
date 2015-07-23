package voxelTools;
import java.util.HashMap;

import controlP5.Println;
import dynamicTools.MainApp;
import toxi.geom.Vec3D;

/*------------------------------------
 
 Class containing a 3d array of bytes and
 functions for searching, reading and writing 
 to this array
 
 ------------------------------------*/

public class VectorField {
	  Vec3D vals[];      // The array of bytes containing the pixels.
	  int w, h, d;
	  float s;
	  MainApp parent;
	  
	  VectorField(int _w, int _h, int _d, int _s, MainApp _parent) {
		parent = _parent;
	    w = _w;
	    h = _h;
	    d = _d;
	    s = _s;
	    initGrid();
	    interpolateFieldFromRandomVectors(30);
	    
	  }
	  
	  //-------------------------------------------------------------------------------------

	  //Functions for initialisation

	  //-------------------------------------------------------------------------------------

	  void initGrid() {
		    vals = new Vec3D[w*h*d];

		  }
	  
	  //-------------------------------------------------------------------------------------

	  //Functions for writing to the voxel array
	  
	  //-------------------------------------------------------------------------------------

	  
	   void interpolateFieldFromRandomVectors(int numInitialVectors) {
		HashMap<Vec3D,Vec3D>initial = new HashMap<Vec3D,Vec3D>();
		   for(int i=0;i<numInitialVectors;i++){
			   initial.put(new Vec3D(parent.random(w),parent.random(h),parent.random(d)), new Vec3D(parent.random(-1,1),parent.random(-1,1),parent.random(-1,1)) );
		   }
		   
	    for (int i = 0; i<w; i++) {
	      for (int j = 0; j<h; j++) {
	        for (int k =0; k <d; k++){
	          int index = i + w * (j + h * k);
	          vals[index]= getFieldAtPoint(new Vec3D(i,j,k),initial,30);
	          
	        }
	      }
	    }
	  }
	  
	   Vec3D getFieldAtPoint(Vec3D pt, HashMap<Vec3D, Vec3D>field, float cutoff){
		   Vec3D val = new Vec3D();
		   
		   for(Vec3D v:field.keySet()){
			   float d = pt.distanceTo(v);
			  
			   if(d>0 && d<cutoff){
				   Vec3D vec = field.get(v).copy();
				   vec.scaleSelf(1/d);
				   val.addSelf(vec);
			   }
		   }

		   return val.normalizeTo(1); 
		   
	   }
	   
	  
	  //-------------------------------------------------------------------------------------

	  //Functions for modifying the voxel array
	  
	  //-------------------------------------------------------------------------------------


	  //-------------------------------------------------------------------------------------

	  //Functions for reading the voxel array
	  
	  //-------------------------------------------------------------------------------------

	  Vec3D getValue(int x, int y, int z) {
	    if(x>w || x<0 || y<0 || y>h || z<0 || z>d)return new Vec3D();
	    int index = x + w * (y + h * z);
	    Vec3D val = vals[index];
	    return val;
	  }

	  int getW(){
	    return w;
	  }
	  
	  int getH(){
	    return h;
	  }
	  
	  int getD(){
	    return d;
	  }
	  
	  //-------------------------------------------------------------------------------------

	  //Functions for drawing
	  
	  //-------------------------------------------------------------------------------------

	  void render(int res, float length) {
	    int index = 0;
	    parent.strokeWeight(1);
	    parent.stroke(255);
	    for (int z=0; z<d; z+=res) {
	      for (int y=0; y<h; y+=res) {
	        for (int x=0; x<w; x+=res) {
	          index = x + w * (y + h * z);
	          Vec3D v = vals[index];
	          parent.line(x*s,y*s,z*s,x*s+(v.x*length),y*s+(v.y*length),z*s+(v.z*length));
	        }
	      }
	    }
	  }
}
