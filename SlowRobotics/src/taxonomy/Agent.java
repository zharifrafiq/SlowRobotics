package taxonomy;
import java.util.ArrayList;

import dynamicTools.Environment;
import dynamicTools.Link;
import dynamicTools.MainApp;
import dynamicTools.Plane3D;
import toxi.geom.*;

/*------------------------------------

Class that handles flocking, neighbour search
and trails

------------------------------------*/

public class Agent extends Plane3D {
	public ArrayList neighbours = new ArrayList();
	public ArrayList<Link> trail = new ArrayList<Link>();

	//-------------------------------------------------------------------------------------

	//Constructors

	//-------------------------------------------------------------------------------------
	
	public Agent (Vec3D _o, Vec3D _x, Vec3D _y, boolean _f) {
		super(_o, _x, _y);
		f=_f;
		resetTrail();
	}

	public Agent (Vec3D _o, boolean _f){
		super (_o);
		f=_f;
		resetTrail();
	}

	Agent (Vec3D _o, Vec3D _x, boolean _f){
		super (_o,_x);
		f = _f;
		resetTrail();
	}

	Agent (Plane3D _b, boolean _f) {
		super(_b);
		f=_f;
		resetTrail();
	}

	//-------------------------------------------------------------------------------------

	//Run Function

	//-------------------------------------------------------------------------------------

	public void run() {

	}

	//-------------------------------------------------------------------------------------

	//Functions for reading and writing to the environment

	//-------------------------------------------------------------------------------------
	
	public void getNeighbours(Vec3D p, float rad, Environment environment) {
		neighbours = new ArrayList();
		ArrayList addList = environment.getWithinSphere(p, rad);
		if(addList!=null)neighbours.addAll(addList);
	}

	//-------------------------------------------------------------------------------------

	//Creating, destroying and manipulating Trails

	//-------------------------------------------------------------------------------------

	public void addToTrail(Plane3D p){
		Link lastLink = trail.get(trail.size()-1);
		if(lastLink.b==this){
			lastLink.setB(p);
			lastLink.updateLength();
		}else{
			trail.add(new Link(lastLink.b,p, true));
		}

	}

	public void removeFromTrail(int index){
		if(trail.size()!=1){
			trail.remove(index);
		}
	}
	
	public void resetTrail(){
		trail = new ArrayList<Link>();
		Link l =new Link(new Plane3D(this), this,true);
		trail.add(l);
	}
	
	public void updateTrail(){
		for(int i =0;i<trail.size();i++){
			Link l = trail.get(i);
			if(l.spr)l.spring();
			if(!l.a.locked())l.a.update();
			if(i==trail.size()-1){
				if(!l.b.locked())l.b.update();
			}
		}
	}
	
	public void addForceToTrail(Vec3D force){
		for(Link l:trail){
			l.a.addForce(force);
		}
	}

	void stiffenTrail(float bendResistance) {
		if(trail.size()>1){
			for(int i =1;i<trail.size();i++){
				Link prev = trail.get(i-1);
				Link next = trail.get(i);
				float currentAngle = prev.angleBetween(next,true);
				//if(currentAngle<parent.PI/3){
					
					Vec3D ab = prev.a.add(next.b).scale((float) 0.5);
					
					float targetAngle = prev.linkAngle;
					
					float diff = (targetAngle)-currentAngle/(float)Math.PI; //max is 1, min is -1
	
					//float sf = (float) (1+(0-1)*(0.5+0.5*parent.cos(diff*parent.PI))); //cosine interpolation
					
					Vec3D avg = ab.sub(prev.b).scale(bendResistance*diff);
					prev.b.addForce(avg);
				//}
			}
		}
	}

	//-------------------------------------------------------------------------------------

	//Interpolated Boid Behaviours

	//-------------------------------------------------------------------------------------

	public void seperate(Plane3D j, boolean inXY, float maxXY, float cutoff){
		Vec3D toPlane3D = j.sub(this);
		Plane p = new Plane(this, zz);
		Vec3D op = p.getProjectedPoint(j);
		op.subSelf(this);
		float d= toPlane3D.magnitude();
		float ratio = d/cutoff;
		if(d<cutoff){
			op.invert();
			ratio = 1-(d/(cutoff));
		}else{
			ratio = (d-cutoff)/(cutoff);
		}
		float f = interp.interpolate(0,maxXY,ratio);
		op.scaleSelf(f);
		toPlane3D.scaleSelf(f);
		if(inXY){
			addForce(op);
		}else{
			addForce(toPlane3D); 
		}
	}

	public void attract(Vec3D j, float max, float strength){
		Vec3D toPlane3D = j.sub(this);
		float ratio = 1-(toPlane3D.magnitude()/max);
		float f = interp.interpolate(0,strength,ratio);
		toPlane3D.scaleSelf(f);
		if(!toPlane3D.isZeroVector())addForce(toPlane3D);
	}
	/*

	  public void repel(Vec3D j, float max, float strength){
	    Vec3D toPlane3D = sub(j);
	    float ratio = 1-(toPlane3D.magnitude()/max);
	    float f = interp.interpolate(0,strength,ratio);
	    toPlane3D.scaleSelf(f);
	    if(!toPlane3D.isZeroVector())addForce(toPlane3D);
	  }*/

}