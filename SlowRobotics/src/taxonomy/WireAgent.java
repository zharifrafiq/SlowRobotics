package taxonomy;
import java.util.ArrayList;

import dynamicTools.MainApp;
import dynamicTools.Plane3D;
import meshTools.MeshPipe;
import toxi.geom.Vec3D;
import voxelTools.VoxelGrid;

/*------------------------------------

TODO wip class

------------------------------------*/

public class WireAgent extends Agent{
	float target=0;
	VoxelGrid volume;
	MeshPipe geom;

	  
	WireAgent (Vec3D _o, boolean _f,VoxelGrid _v){
	   super (_o, _f);
	   volume = _v;
	   f=_f;
	  }

	
	@Override
	public void run(){
		//grow phase
		/*
		if (!f){
			update();
			checkVoxels();
			if(age%5==0){
				alignWithVoxels();
				addToTrail(new Plane3D(this,parent));
			}
			if(age>25){
				//spawn();
				f=true;
			}

		}*/
		//if(!f){
		//bundle phase
		//updateLinks();
		//}
		//render();
		//renderGeom();
		//checkBounds(3000);

		//age++;
	}
	/*
	//-------------------------------------------------------------------------------------

	//Branching and spawning functions

	//-------------------------------------------------------------------------------------

	public void spawn(){
		if(trail.size()>3){
			int index = 0;
			Link l = trail.get(index);
			Plane3D start = new Plane3D(l.a);
			Vec3D n = l.b.sub(start).normalizeTo(1);
			Vec3D offset = volume.findValueInRadius(start, 1,0,255);
			if(!offset.isZeroVector()){
				start.addSelf(offset);
				// TODO rethink how agents spawn
				//parent.environment.addAgentDir(start, n, target);
			}else{
				//parent.environment.addAgentAtValue(200,10,500);
			}
		}

	}
	
	//-------------------------------------------------------------------------------------

		//Voxel Functions

	//-------------------------------------------------------------------------------------

	public void alignWithVoxels(){
		if(target==0){
			target= volume.getValue(this);
		}
		Vec3D n = volume.getVectorToBestInAngle(this, 2, parent.PI/5, vel,target);
		vel.set(n);
	}

	public void checkVoxels(){
		volume.get(this).set(-1);
	}
	
	//-------------------------------------------------------------------------------------

		//Linkage functions

	//-------------------------------------------------------------------------------------
	
	
	public void updateLinks(){
		//find other agents - this solves the 'parent' issue
		getNeighbours(this, 800);
			for (Link linkA:trail){
				for(WireAgent a:(ArrayList<WireAgent>)neighbours){
					if(a!=this)joinTrails(linkA, a,14f,1f,0.05f);
				}
				linkA.spring();
				linkA.update();
				linkA.alignEnds();
				
				// TODO pin implementation
				//linkA.renderPins(parent);
				//linkA.a.render(5);
			}
			stiffenTrail(0.4f);
	}
	
	public void initLinkAngles(){
		if(trail.size()>1){
			for(int i =1;i<trail.size();i++){
				Link prev = trail.get(i-1);
				Link next = trail.get(i);
				prev.setAngle(prev.angleBetween(next,true));
			}
		}
	}
	
	//TODO: Add bend resistance in 3d using planes
	//TODO: Constrain connections between links to in-plane
	//TODO: Play with falloff and inertia - seems to be breaking things.
	
	public void joinClosestTrails(Link linkA, WireAgent a, float maxDist, float minDist, float maxForce){
		//loop through links
		//only closest
		
		Link c = null;
		Vec3D c1 = new Vec3D();
		Vec3D c2 = new Vec3D();
		float dist = 1000000;
		
		for (Link linkB:a.trail){
			
			//attract
			Vec3D cptL1 = new Vec3D();
			Vec3D cptL2 = new Vec3D();
				
			if(closestPtBetweenLinks(linkA, linkB, cptL1,cptL2)){
				float d = cptL1.distanceTo(cptL2);
				if(d<maxDist){
					if(d<dist){
						dist = d;
						c1 = cptL1;
						c2 = cptL2;
						c = linkB;
					}
				}
			}
		}
		
		if(c!=null){
			if(!c.checkPinAtPt(c2)){
				cohere(linkA, c, c1, c2,dist, minDist,maxDist,maxForce); //cohere if no pin
				if(dist<1.5)linkA.tryPin(c,c1,c2);
			}
			
			//if(dist<2)linkA.a.addForce(alignOriginWithXX(c2, c.a.xx, 0.001f));
			//interpToAngle(linkA,c,c1,parent.PI/4,0.05f);
		}
	}
	
	public void joinTrails(Link linkA, WireAgent a, float maxDist, float minDist, float maxForce){
		//loop through links
		//only closest
		for (Link linkB:a.trail){
			
			//attract
			Vec3D cptL1 = new Vec3D();
			Vec3D cptL2 = new Vec3D();
				
			if(closestPtBetweenLinks(linkA, linkB, cptL1,cptL2)){
				float d = cptL1.distanceTo(cptL2);
				if(d<maxDist){
					if(!linkB.checkPinAtPt(cptL2)){
						if(d>minDist) cohere(linkA, linkB, cptL1, cptL2,d, minDist,maxDist,maxForce); //cohere if no pin
					}
					if(d<minDist){
						repel(linkA, linkB, cptL1, cptL2,d, minDist,maxDist,maxForce*4);
						if(d<1.5)linkA.tryPin(linkB,cptL1,cptL2);
					}
					align(linkA, linkB, cptL1, cptL2,d, minDist,maxDist,maxForce);
				}
			}
		}
	}

	
	public void cohere(Link linkA, Link linkB, Vec3D c1, Vec3D c2, float dist, float minDist, float maxDist, float maxForce){
		Vec3D to = c2.sub(c1);
		float sf = 0;
		float f = ((to.magnitude()-minDist/(maxDist-minDist))/2)+0.5f; //creates a range from 0.5-1 for the interpolation
		//scalars
		sf = (float) (maxForce+(0-maxForce)*(0.5+0.5*parent.cos(f*parent.PI))); //cosine interpolation
		linkA.addForce(to.normalizeTo(sf));
	}
	
	public void align(Link linkA, Link linkB, Vec3D c1, Vec3D c2, float dist, float minDist, float maxDist, float maxForce){
		float sf = (1-(dist/maxDist))*0.01f;
		if(dist<maxDist/4)linkA.alignWithLink(linkB,sf);
	}
	
	public void repel(Link linkA, Link linkB, Vec3D c1, Vec3D c2, float dist, float minDist, float maxDist, float maxForce){
		Vec3D to = c2.sub(c1);
		float f = (to.magnitude()/(minDist*2))+0.5f; //creates a range from 0-0.5 for the interpolation
		//scalars
		float sf = (float) (maxForce+(0-maxForce)*(0.5+0.5*parent.cos(f*parent.PI))); //cosine interpolation
		linkA.addForce(to.normalizeTo(-sf));
	}
	
	public boolean closestPtBetweenLinks(Link l1, Link l2, Vec3D a, Vec3D b){
		Vec3D[] out = new Vec3D[2];
		// Algorithm is ported from the C algorithm of 
		   // Paul Bourke at http://local.wasp.uwa.edu.au/~pbourke/geometry/lineline3d/
		   Vec3D resultSegmentPoint1 = new Vec3D();
		   Vec3D resultSegmentPoint2 = new Vec3D();
		 
		   Vec3D p1 = l1.a;
		   Vec3D p2 = l1.b;
		   Vec3D p3 = l2.a;
		   Vec3D p4 = l2.b;
		   Vec3D p13 = p1.sub(p3);
		   Vec3D p43 = p4.sub(p3);
		 
		   if (p43.magSquared() < Math.E) {
		      return false;
		   }
		   Vec3D p21 = p2.sub(p1);
		   if (p21.magSquared() < Math.E) {
		      return false;
		   }
		 
		   double d1343 = p13.x * (double)p43.x + (double)p13.y * p43.y + (double)p13.z * p43.z;
		   double d4321 = p43.x * (double)p21.x + (double)p43.y * p21.y + (double)p43.z * p21.z;
		   double d1321 = p13.x * (double)p21.x + (double)p13.y * p21.y + (double)p13.z * p21.z;
		   double d4343 = p43.x * (double)p43.x + (double)p43.y * p43.y + (double)p43.z * p43.z;
		   double d2121 = p21.x * (double)p21.x + (double)p21.y * p21.y + (double)p21.z * p21.z;
		 
		   double denom = d2121 * d4343 - d4321 * d4321;
		   if (Math.abs(denom) < Math.E) {
		      return false;
		   }
		   double numer = d1343 * d4321 - d1321 * d4343;
		 
		   float mua = parent.constrain((float)(numer / denom),0,1);
		   float mub = parent.constrain((float)((d1343 + d4321 * (mua)) / d4343),0,1);
		   
		  // if((mua==0 && mub != 0) || (mua == 1 && mub != 1))return false;
		   a.x = (p1.x + mua * p21.x);
		   a.y = (p1.y + mua * p21.y);
		   a.z = (p1.z + mua * p21.z);
		   b.x = (p3.x + mub * p43.x);
		   b.y = (p3.y + mub * p43.y);
		   b.z = (p3.z + mub * p43.z);


		return true;
	}
	
	public void renderGeom(){
		geom = new Loft(trail,parent,1);
		geom.buildMesh();
		geom.render();
	}
	
	//-------------------------------------------------------------------------------------

			//WIP / TODO

	//-------------------------------------------------------------------------------------
	

	public void interpToAngle(Link current, Link target, Vec3D rPt, float angle, float strength){
		float currentAngle = current.angleBetween(target,false);
		float diff = (currentAngle-angle)/parent.PI; //max is 1, min is -1
		
		//float sf = (float) (1+(0-1)*(0.5+0.5*parent.cos(diff*parent.PI))); //cosine interpolation
		Vec3D ac = rPt.sub(current.a); //get vec to a
		Vec3D rVec = ac.getRotatedAroundAxis(current.a.xx, strength*diff);
		Vec3D force = ac.sub(rVec);
		Vec3D bc = rPt.sub(current.b); //get vec to a
		Vec3D brVec = bc.getRotatedAroundAxis(current.a.xx, -strength*diff);
		Vec3D bforce = bc.sub(brVec);
		parent.println((diff*90)+"  force is "+force);
		current.a.addForce(force);
		current.b.addForce(bforce);
		//}
	}
	*/
	
}

