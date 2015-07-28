package meshTools;

import java.util.ArrayList;

import core.Link;
import core.MainApp;
import core.Plane3D;
import toxi.geom.Vec3D;
import toxi.geom.mesh.Face;
import toxi.geom.mesh.TriangleMesh;

public class MeshPipe {
	ArrayList<Link> sections;
	TriangleMesh mesh;
	MainApp parent;
	float rad;
	
	MeshPipe(ArrayList<Link> _sections, MainApp _parent, float _rad){
		sections = _sections;
		parent = _parent;
		mesh =new TriangleMesh("geom");
		rad = _rad;
	}
	
	public void buildMesh(){
		mesh =new TriangleMesh("geom");
		for (Link l: sections){
			Plane3D a = l.a;
			Plane3D b = l.b;
			createBox(a,b);
		}
	}
	
	public void createBox(Plane3D a, Plane3D b){
		Vec3D a1 = a;
		Vec3D a2 = a.add(a.xx);
		Vec3D b1 = b;
		Vec3D b2 = b.add(b.xx);
		mesh.addFace(a1, a2, b2);
		mesh.addFace(a1, b2, b1);
		a1 = a;
		a2 = a.add(a.yy);
		b1 = b;
		b2 = b.add(b.yy);
		mesh.addFace(a1, a2, b2);
		mesh.addFace(a1, b2, b1);
		a1 = a.add(a.yy);
		a2 = a1.add(a.xx);
		b1 = b.add(b.yy);
		b2 = b1.add(b.xx);
		mesh.addFace(a1, a2, b2);
		mesh.addFace(a1, b2, b1);
		a1 = a2.copy();
		a2 = a2.sub(a.yy);
		b1 = b2.copy();
		b2 = b2.sub(b.yy);
		mesh.addFace(a1, a2, b2);
		mesh.addFace(a1, b2, b1);
	}
	
	public void render(){
		parent.beginShape(parent.TRIANGLES);
		parent.fill(255);
		  // iterate over all faces/triangles of the mesh
		  for(java.util.Iterator<Face> i=mesh.faces.iterator(); i.hasNext();) {
		    Face f=(Face)i.next();
		    // create vertices for each corner point
		    
		    parent.vertex(f.a.x, f.a.y,f.a.z);
		    parent.vertex(f.b.x,f.b.y,f.b.z);
		    parent.vertex(f.c.x,f.c.y,f.c.z);
		    
		  }
		  parent.endShape();
	}
	
	
	
}