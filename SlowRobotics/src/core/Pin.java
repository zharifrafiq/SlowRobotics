package core;
import toxi.geom.Vec3D;

/*------------------------------------

// TODO incorporate this class into the
 * rest of the dynamics package and finish up

------------------------------------*/

public class Pin{
	
	Link l;
	float param;
	float seg;
	Pin joinedTo;
	boolean pinned;
	float offset=1;
	
	Pin(Link _l, float _p, float _seg){
		param = _p;
		l = _l;
		seg = _seg;
	}
	
	public void pinTo(Pin pin){
		joinedTo=pin;
		pinned = true;
		//offset = getPoint().distanceTo(pin.getPoint());
		//if(offset<1)offset=1;
	}
	
	public void update(){
		if(pinned){
			//orient parent link to the orientation of the pin
			//maintain a set distance to the target pin
			fixDist();
		}
	}
	
	public Vec3D getPoint(){
		return (l.a.add(l.b.sub(l.a).scaleSelf(param)));
	}
	
	public void addForce(Vec3D force){
		l.addForce(force);
	}
	public void fixDist(){
		Vec3D ab = joinedTo.getPoint().sub(getPoint());
		ab.normalizeTo(ab.magnitude()-offset);
		//ab.limit(0.5f);
		l.addForce(ab.scale(0.4f));
		//joinedTo.addForce(ab.scale(-0.05f));
	}
}
