package taxonomy;

import java.util.ArrayList;

import dynamicTools.Environment;
import dynamicTools.Link;
import dynamicTools.MainApp;
import dynamicTools.Plane3D;
import pointCloudTools.PathFinder;
import meshTools.ColourWETriangleMesh;
import toxi.geom.Vec3D;
import toxi.geom.mesh.WEVertex;


public class MeshAgent extends Agent{
	
	float size;
	ColourWETriangleMesh mesh;
	int[] c;
	Vec3D p;
	WEVertex cPt;
	float a;
	Environment environment;
	
	MeshAgent(Vec3D _l, float _s, ColourWETriangleMesh _mesh, Environment _environment){
		super(_l,false);
		size = _s;
		mesh = _mesh;
		c = new int[]{255,255,255};
		environment = _environment;
	}
	
	
	public void run(){
		if(!f){
			getNeighbours(this,size, environment);
			repelNeighbours(0.5f * ((255-c[2])/255));
			cPt = (WEVertex) mesh.getClosestVertexToPoint(this);
			p = PathFinder.getClosestPointOnSurface(cPt, this);
			if(p!=null){
				attract(p, 50, 0.1f);
			}else{
				addForce(Vec3D.randomVector().scale(1f));
			}
			int [] nc = mesh.getClosestColour(this,200);
			if(nc!=null)c = nc;
			if(c!=null){
				size = map(c[0], 0, 255, 20, 5); //set size based on red chanel
				Vec3D toMesh = p.sub(this);
				a = toMesh.angleBetween(cPt.normal,true);
				if(distanceTo(p)<10 && a>(float)Math.PI/2 && size>5.5){
					addPtsToTrail();
					f=true;
					//if(size<10)parent.environment.addAgent(new MeshAgent(add(Vec3D.randomVector().scale(50)), 10, mesh, parent));
				}
			}
			
			update();
		}else{
			float hangFactor = map(c[1], 0, 255, -0.15f, 0.05f);
			trail.get(trail.size()-1).b.addForce(new Vec3D(0,0,hangFactor));
			stiffenTrail(0.25f);
			float repelFactor = (size-5)/80;
			repelTrailFromMesh(repelFactor);
			updateTrail();
		}

	}
	
	
	public void repelTrailFromMesh(float strength){
		for(Link l:trail){
			l.b.addForce(repel(l.a, p, strength,50));
		}
	}
	
	public void addPtsToTrail(){
		//float sf = (size-(c[0]/40)+((c[2]/255)*parent.random(10))+a)/4;
		float sf = (float) (((size*a)+((c[2]/255)*Math.random()*10)+Math.random()*3)/8);
		Vec3D normal = cPt.normal.copy().normalizeTo(sf);
		for(int i =1;i<8;i++){
			Plane3D p= new Plane3D(add(normal.scale(i)));
			
			addToTrail(p);
			if(i==1){
				trail.get(0).b.lock();
				trail.get(0).a.lock();
			}
		}
	}
	
	public void addToTrail(Plane3D p){
		Link lastLink = trail.get(trail.size()-1);
		if(lastLink.b==this){
			lastLink.setA(this);
			lastLink.setB(p);
			lastLink.updateLength();
		}else{
			trail.add(new Link(lastLink.b,p, true));
		}

	}
	
	public void repelNeighbours(float scale){
		if(neighbours!=null){
		for(Plane3D j:(ArrayList<Plane3D>)neighbours){
				addForce(repel(this, j,scale,size*2));
			}
		}
	}
	
	public Vec3D repel(Vec3D a, Vec3D j, float scale, float max){
		Vec3D toPlane3D = j.sub(a);
		float d= toPlane3D.magnitude();
		if(d<max && d>1){
		float ratio = 1-(d/(max));
		float f = interp.interpolate(0,scale,ratio);
		toPlane3D.invert();
		
		return toPlane3D.scale(f); 
		}else{
			return new Vec3D();
		}
	}
	
	
}