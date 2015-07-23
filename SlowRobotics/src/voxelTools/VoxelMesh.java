package voxelTools;
import processing.core.PApplet;
import toxi.geom.AABB;
import toxi.geom.Vec3D;
import toxi.geom.mesh.BoxSelector;
import toxi.geom.mesh.LaplacianSmooth;
import toxi.geom.mesh.WETriangleMesh;


public class VoxelMesh {
	  WETriangleMesh m;
	  int res;

	  public VoxelMesh(int _res) {
	    m = new WETriangleMesh();
	    res = _res;
	  } 

	  void createMesh(VoxelGrid v, float threshold, float cutoff, PApplet parent) {
	    m = new WETriangleMesh();
	    //loop through values
	    for (int x = 0; x<v.getW (); x++) {
	      for (int y = 0; y<v.getH (); y++) {
	        for (int z = 0; z<v.getD (); z++) {
	          //see if there are neighbouring values if not create the faces
	          float val = v.getValue(x,y,z);
	          if (parent.abs(val-threshold)>=cutoff) {
	            
	            if (v.getValue((x-res),y,z)== 0 ) {
	              addFace(x, y, z, x, y, z+res, x, y+res, z+res, x, y+res, z);
	            }
	            if (v.getValue((x+res),y,z)==0) {
	              addFace(x+res, y, z, x+res, y, z+res, x+res, y+res, z+res, x+res, y+res, z);
	            }
	            if (v.getValue(x,(y-res),z)==0) {
	              addFace(x, y, z, x, y, z+res, x+res, y, z+res, x+res, y, z);
	            }
	            if (v.getValue(x,(y+res),z)==0) {
	              addFace(x, y+res, z, x, y+res, z+res, x+res, y+res, z+res, x+res, y+res, z);
	            }
	            if (v.getValue(x,y,(z-res))==0) {
	              addFace(x, y, z, x, y+res, z, x+res, y+res, z, x+res, y, z);
	            }
	            if (v.getValue(x,y,(z+res))==0) {
	              addFace(x, y, z+res, x, y+res, z+res, x+res, y+res, z+res, x+res, y, z+res);
	            }
	          }
	        }
	      }
	    }
	    //m.scale(res);
	    parent.println("meshing complete");
	  }

	  //function to create faces from corner coords
	  void addFace(float ax, float ay, float az, float bx, float by, float bz, float cx, float cy, float cz, float dx, float dy, float dz) {
	    m.addFace(new Vec3D(ax, ay, az), new Vec3D(bx, by, bz), new Vec3D(dx, dy, dz));
	    m.addFace(new Vec3D(dx, dy, dz), new Vec3D(bx, by, bz), new Vec3D(cx, cy, cz));
	  }

	  void smooth() {
	    new LaplacianSmooth().filter(m, 1);
	  }

	  void smoothRandomSelection(int iter, float step) {
	    for (int i=1; i<iter; i++) {
	      BoxSelector sel = new BoxSelector(m, new AABB(new Vec3D (0, 0, 0), step*i));
	      sel.selectVertices();
	      new ScaledLaplacianSmooth().filter(sel, 1);
	    }
	  }
}
