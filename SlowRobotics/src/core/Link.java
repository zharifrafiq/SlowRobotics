package core;
import toxi.geom.Vec3D;

/*------------------------------------

Class that handles connections between planes
to form springs and trails

// TODO decide how to incorporate pins on links

------------------------------------*/

public class Link{
	public Plane3D a;
	public Plane3D b;
	public boolean spr;
	public float l;
	public float stiffness;
	public float linkAngle;
	
	
	//-------------------------------------------------------------------------------------

	//Constructors

	//-------------------------------------------------------------------------------------
	
	public Link(Plane3D _a, Plane3D _b, boolean _spr){
		this(_a, _b, _spr,  _a.distanceTo(_b));
	}
	
	public Link(Plane3D _a, Plane3D _b, boolean _spr, float _l){
		a = _a;
		b = _b;
		spr = _spr;
		l = _l;
		stiffness = 0.2f;
		linkAngle = (float)Math.PI;
	}
	
	public void setAngle(float angle){
		linkAngle = angle;
	}
	
	public void alignEnds(){
		a.interpolateToZZ(b.sub(a).normalize(), 0.1f);
		b.interpolateToZZ(b.sub(a).normalize(), 0.1f);
		a.interpolateToXX(b.xx, 0.1f);
	}
	
	public void alignWithLink(Link l, float sf){
		a.interpolateToXX(l.a.xx, sf);
		b.interpolateToXX(l.b.xx, sf);
		//a.interpolateToXX(l.a.zz, 0.1f);
	}

	public void spring(){
		float d = (l-(a.distanceTo(b)));
		Vec3D ab = b.sub(a).getNormalized();
		a.addForce(ab.scale(-d*stiffness));
		b.addForce(ab.scale(d*stiffness));
	}
	
	public float angleBetween(Link other, boolean flip){
		Vec3D ab = a.sub(b);
		Vec3D abo = other.a.sub(other.b);
		if(flip)abo.invert();
		return ab.angleBetween(abo,true);
	}

	public Vec3D closestPt(Vec3D p) {
		Vec3D dir = b.sub(a);
		float t = p.sub(a).dot(dir) / dir.magSquared();
		return a.add(dir.scaleSelf(t));
	}
	
	public void addForce(Vec3D force){
		a.addForce(force);
		b.addForce(force);
	}
	
	public void update(){
		a.update(0.05f);
		b.update(0.05f);
		
	}
	public void setA(Plane3D _a){
		a = _a;
	}

	public void setB(Plane3D _b){
		b = _b;
	}
	
	public void updateLength(){
		l = a.distanceTo(b);
	}

	void render(){
		// TODO need to rethink rendering
		//gfx.line(this); 
	}
}