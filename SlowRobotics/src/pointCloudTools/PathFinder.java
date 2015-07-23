package pointCloudTools;
import java.util.ArrayList;
import java.util.Collections;
import toxi.geom.Vec3D;
import toxi.geom.mesh.WEFace;
import toxi.geom.mesh.WETriangleMesh;
import toxi.geom.mesh.WEVertex;
import voxelTools.Cell;

/*------------------------------------

Class that handles pathfinding within voxel
environment and drives robotic motion

------------------------------------*/


public class PathFinder {
	
	//-------------------------------------------------------------------------------------

	//Mesh Search 
	
	//-------------------------------------------------------------------------------------	
	
	public static Vec3D getClosestPoint(Vec3D pt, WETriangleMesh mesh){
		WEVertex cPt = (WEVertex) mesh.getClosestVertexToPoint(pt);
		Vec3D out = null;
		if(cPt!=null){
			out = getClosestPointOnSurface(cPt,pt);
		}
		return out;
		
	}
	
	public static Vec3D getClosestPointOnSurface(WEVertex v, Vec3D pt){
			float d = 1000000;
			Vec3D out = null;
			for (WEFace f: v.getRelatedFaces()){
				Vec3D tPt = f.toTriangle().closestPointOnSurface(pt);
				float tD = tPt.distanceTo(pt);
				if(tD<d){
					d = tD;
					out = tPt.copy();
				}
			}
		return out;
		
	}
	
	
	//-------------------------------------------------------------------------------------

	//Point cloud search

	//-------------------------------------------------------------------------------------	
	
	public static Vec3D getHighestPoint(Vec3D out, ArrayList<Vec3D> pts){
		for(Vec3D v:pts){
			if(v.z>out.z)out.set(v);
		}
		return out.copy();
	}
	
	public static Vec3D getLowestPoint(Vec3D out, ArrayList<Vec3D> pts){

		for(Vec3D v:pts){
			if(v.z<out.z)out.set(v);
		}
		return out.copy();
	}
	
	public static Vec3D getLowPoint(Vec3D out, ArrayList<Vec3D> pts){
		Collections.shuffle(pts);
		for(Vec3D v:pts){
			if(v.z<out.z)return v.copy();
		}
		return out.copy();
	}
	
	public static Vec3D getClosestPoint(Vec3D pt, ArrayList<Vec3D> pts){

		float d = 10000;
		Vec3D cPt = new Vec3D();
		for(Vec3D v:pts){
			if(v.distanceTo(pt)<d){
				d = v.distanceTo(pt);
				cPt.set(v);
			}
		}
		return cPt;
	}
	
	public static Vec3D getClosestPointFOV(Vec3D pt, Vec3D dir, float fov, ArrayList<Vec3D> pts){
	//	Collections.shuffle(pts);
		float d = 10000;
		Vec3D cPt = null;
		for(Vec3D v:pts){
			Vec3D ab = v.sub(pt);
			float angle = dir.angleBetween(ab,true);
			if(angle<fov){
				if(v.distanceTo(pt)>1){
					float dist = v.distanceTo(pt);
					if(dist<d){
						d = dist;
						cPt = v.copy();
					}
				}
			}
		}
		return cPt;
	}

	public static Vec3D getHighestPointFOV(Vec3D pt, Vec3D dir, float fov, float maxd, ArrayList<Vec3D> pts){
		//Collections.shuffle(pts);
		float d = -10000;
		Vec3D cPt = null;
		for(Vec3D v:pts){
			Vec3D ab = v.sub(pt);
			float angle = dir.angleBetween(ab,true);
			if(angle<fov){
				if(pt.distanceTo(v)<maxd){
					if(v.z>d){
						d = v.z;
						cPt = v.copy();
					}
				}
			}
		}
		return cPt;
	}
	
	public static Vec3D getNearZ(Vec3D pt, Vec3D dir, float fov, float targetZ, ArrayList<Vec3D> pts){
		//Collections.shuffle(pts);
		float d = 0;
		Vec3D cPt = null;
		for(Vec3D v:pts){
			Vec3D ab = v.sub(pt);
			float angle = dir.angleBetween(ab,true);
			float dist = pt.distanceTo(v);
			if(angle<fov){
				float diff = v.z*((fov-angle));
					if(diff>d){
						d = diff;
						cPt = v.copy();
					}
			}
		}
		return cPt;
	}
	
	public static Vec3D getLowestPointFOV(Vec3D pt, Vec3D dir, float fov,ArrayList<Vec3D> pts){
		//Collections.shuffle(pts);
		float d = 10000;
		Vec3D cPt = null;
		for(Vec3D v:pts){
			Vec3D ab = v.sub(pt);
			float angle = dir.angleBetween(ab,true);
			if(angle<fov){

					if(v.z<d){
						d = v.z;
						cPt = v.copy();
					}
			}
		}
		return cPt;
	}
}
