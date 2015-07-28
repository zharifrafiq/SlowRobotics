package meshTools;

import java.util.ArrayList;
import java.util.HashMap;

import core.MainApp;
import processing.core.PApplet;
import toxi.geom.PointOctree;
import toxi.geom.Vec3D;
import toxi.geom.mesh.Vertex;
import toxi.geom.mesh.WETriangleMesh;

public class ColourWETriangleMesh extends WETriangleMesh {
	
	HashMap<Vertex, int[]> colours;
	PApplet parent;
	PointOctree tree;
	
	ColourWETriangleMesh(PApplet _parent, float bounds){
		colours = new HashMap<Vertex, int[]>();
		parent = _parent;
		tree = new PointOctree(new Vec3D(0,0,-bounds),bounds*2);
	}
	
	public void buildTree(){
		
		for (Vertex v : vertices.values()) {
            tree.addPoint(v); 
        }
		
	}
	
	
	
	public Vec3D getRandomVertex(){
		for (Vertex v : vertices.values()) {
		 int[] c= colours.get(v);
		 if(c!=null){
			 if((int)parent.random(getNumVertices())<=5 && c[0]>150)return v;
		 }
		}
		return new Vec3D(parent.random(700),parent.random(700),parent.random(400));
	}
	
	public void loadColours(String name){
		String[] txtLines = parent.loadStrings(name);
		for (int i = 0; i < txtLines.length-1; i+=1) {
				String[] sPt = parent.split(txtLines[i], '+');
				String[] cols = parent.split(sPt[1], ',');
				String[] pt = parent.split(sPt[0], ',');
				int[] c = new int[]{Integer.valueOf(cols[0]), Integer.valueOf(cols[1]), Integer.valueOf(cols[2])};
				Vec3D vert = new Vec3D(Float.valueOf(pt[0]), Float.valueOf(pt[1]), Float.valueOf(pt[2]));
				Vertex v = getClosestVertexToPoint(vert,5);
				colours.put(v,c);
		}
	}
	
	public Vertex getClosestVertexToPoint(Vec3D pt, float maxDist){
		ArrayList pts  = tree.getPointsWithinSphere(pt, maxDist);
		Vertex out = null;
		if(pts!=null){
			//System.out.println("findingpts");
			float d = maxDist+1;
			for(Vertex v:(ArrayList<Vertex>) pts){
				float td = v.distanceTo(pt);
				if(td<d){
					d = td;
					out = v;
				}
			}
		}
		return out;
	}
	
	public int[] getClosestColour(Vec3D pt, float rad){
			return getVertexColour(getClosestVertexToPoint(pt));
	}
		
	public int[] getVertexColour(Vertex v){
		return colours.get(v);
	}
	
	public void render(){
		parent.strokeWeight(5);
		for(Vertex v:colours.keySet()){
			int[]c = colours.get(v);
			parent.stroke(c[0],c[1],c[2]);
			parent.point(v.x, v.y,v.z);
		}
	}
}

