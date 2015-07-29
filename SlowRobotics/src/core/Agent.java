package core;
import java.util.ArrayList;

import toxi.geom.*;
import toxi.math.InterpolateStrategy;

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

	//Default Run Function - mostly for testing / demos

	//-------------------------------------------------------------------------------------

	public void run(Environment environment) {
		getNeighbours(this, 50, environment); //get nearby agents
		for(Plane3D a:(ArrayList<Plane3D>)neighbours){
			repel(a, 0, 30, 0.03f, "sigmoid");
			cohere(a, 20, 50, 0.01f, "sigmoid");
			align(this, a, 0, 50, 0.1f, "exponential");
		}
		interpolateToXX(vel, 0.5f); //align the plane of the agent with its velocity for funsies
		addForce(xx.scale(0.01f)); //then push in that dir
		update(); //moves the agent
		if(age%10==0)addToTrail(this);
		if(!inBounds(200)){
			resetTrail();
			set(Vec3D.randomVector().scale(100)); //reset if outa bounds
		}
		
		//meaningless change
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

	protected void stiffenTrail(float bendResistance) {
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

	//Interpolation

	//-------------------------------------------------------------------------------------
	
	//toxi circ interpolator
	
	public float interpCirc(float a, float b, float f, boolean isFlipped) {
        if (isFlipped) {
                return a - (b - a) * ((float) Math.sqrt(1 - f * f) - 1);
        } else {
                f = 1 - f;
                return a + (b - a) * ((float) Math.sqrt(1 - f * f));
        }
	}
	
	//toxi cosine interpolator 
	
	public float interpCos(float a, float b, float f) {
        return b+(a-b)*(float)(0.5+0.5*Math.cos(f*Math.PI));
	}
	
	//toxi sigmoid interpolator
	
	public float interpSigmoid(float a, float b, float f, float sharpPremult) {
        f = (f * 2 - 1) * sharpPremult * 5;
        f = (float) (1.0f / (1.0f + Math.exp(-f)));
        return a + (b - a) * f;
	}
	
	public float interpExp(float a, float b, float f){
		return(a+(b-a)*(f*f));
	}
	
	public float getInterpolatedVal(String type, float a, float b, float f){
		float sf = 0;
		switch(type){
			case "exponential": sf = interpExp(a, b, f);
			case "circle": sf = interpCirc(a, b, f,false);
			case "cosine": sf = interpCos(a, b, f);
			case "sigmoid": sf = interpSigmoid(a, b, f,1);
		}
		return sf;
	}
	
	//-------------------------------------------------------------------------------------

	//Interpolated Boid Behaviours

	//-------------------------------------------------------------------------------------
	public void cohere(Vec3D c2, float minDist, float maxDist, float maxForce, String interpolatorType){
		Vec3D to = c2.sub(this);
		float dist = to.magnitude();
		if(dist>minDist && dist<maxDist){	
			float f = ((dist-minDist)/(maxDist-minDist)); //creates a range from 1 to 0
			float sf = getInterpolatedVal(interpolatorType,0,maxForce,f);
			addForce(to.normalizeTo(sf));
		}
	}
	
	public void align(Plane3D c1, Plane3D c2, float minDist, float maxDist, float maxForce, String interpolatorType){
		Vec3D to = c2.sub(this);
		float dist = to.magnitude();
		if(dist>minDist && dist<maxDist){
			float f = 1-((dist-minDist)/(maxDist-minDist)); //creates a range from 1 to 0
			float sf = getInterpolatedVal(interpolatorType,0,maxForce,f);
			interpolateToPlane3D(c2,maxDist,sf);
		}
	}
	
	public void repel(Vec3D c2,float minDist, float maxDist, float maxForce, String interpolatorType){
		Vec3D to = c2.sub(this);
		float dist = to.magnitude();
		if(dist>=minDist && dist<=maxDist){
			float f = 1-((dist-minDist)/(maxDist-minDist)); //creates a range from 1 to 0
			float sf = getInterpolatedVal(interpolatorType,0,maxForce,f);
			addForce(to.normalizeTo(-sf));
		}
	}
	
	// Dumping ground for older behaviours
	
	//TODO cleanup and borrow from code
	
	
	/*
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
*/

}