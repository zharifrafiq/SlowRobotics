package taxonomy;

import java.util.ArrayList;
import java.util.TreeMap;

import dynamicTools.MainApp;
import dynamicTools.Plane3D;
import toxi.geom.Triangle3D;
import toxi.geom.Vec3D;

public class TopologyAgent extends Agent{
	
	TopologyAgent (Vec3D _o, Vec3D _x, Vec3D _y, boolean _f, MainApp _p) {
	    super(_o, _x, _y, _f,_p);
	    f=_f;  
	  }
	  
	TopologyAgent (Vec3D _o, boolean _f, MainApp _p){
	   super (_o, _f,_p);
	   f=_f;
	  }
	  
	TopologyAgent (Vec3D _o, Vec3D _x, boolean _f, MainApp _p){
	    super (_o,_x, _f, _p);
	    f = _f;
	  }

	TopologyAgent (Plane3D _b, boolean _f, MainApp _p) {
	    super(_b, _f,_p);
	    f=_f;
	  }
	
	@Override
	public void run(){
		if (!f){
			update();
			alignWithNeighbours();
		}
		render();
		checkBounds(3000);

		age++;
	}

	
	//-------------------------------------------------------------------------------------
	  
	  //Topology functions
	  
	//-------------------------------------------------------------------------------------
	
	public void alignWithNeighbours() {
	    if (neighbours!=null) {
	      for (int i= 0; i<neighbours.size (); i++) {
	        Plane3D j = (Plane3D) neighbours.get(i);
	        if (j!=this) {
	          float d = j.distanceTo(this);
	         // interpolateToPlane3D(j,parent.maxDist);
	          matchZ(j);
	          seperate(j,true,parent.attractCutoff);
	        }
	      }
	      interpolateToZZ(normalFrom3Pts(neighbours), parent.maxPlane);
	    //  if (sumClose<minBeforeSpawn && age>25)sparsePop=true;
	    }
	  }
 
	
	public void matchZ(Plane3D j){
	    //move by normal
	    Vec3D toPlane3D = j.sub(this);
	    float ratio = toPlane3D.magnitude()/parent.maxDist;
	    float f = interp.interpolate(0,parent.maxZ,ratio);
	    Vec3D zt = zz.scale(f);
	    float ab = toPlane3D.angleBetween(zz,true);
	    if (ab>parent.PI/2)zt.invert();
	    addForce(zt);
	  }
	
	 
	  //creates a triangle from three points and returns the normal of the triangle...
	  public Vec3D normalFrom3Pts(ArrayList pts) {
	    TreeMap<Float, Vec3D> closestPts = new TreeMap<Float, Vec3D>();
	    Vec3D n = zz.copy();
	    for (int i=0; i<pts.size ()-1; i++) {
	      Vec3D p = (Vec3D)pts.get(i);
	      float d = p.distanceTo(this);
	      if (d>0)closestPts.put(d, p);
	    }
	    if (closestPts.size()>=3) {
	      Triangle3D tri = new Triangle3D(
	      (Vec3D)closestPts.pollFirstEntry().getValue(), 
	      (Vec3D)closestPts.pollFirstEntry().getValue(), 
	      (Vec3D)closestPts.pollFirstEntry().getValue());
	      n = tri.computeNormal();
	      if (zz.angleBetween(n)>parent.PI/2)n.invert();
	    }
	    return n;
	  }
}