package taxonomy;
import java.util.ArrayList;

import core.Link;
import core.MainApp;
import processing.core.PApplet;
import toxi.geom.Vec3D;
import toxi.geom.mesh.TriangleMesh;
import voxelTools.BooleanBrush;
import voxelTools.VoxelGrid;


public class FlowAgent extends VolumeAgent{

	float B = 0.05f;
	int lastSub =0;
	int hangTime =0;
	int vdensity = 250;
	TriangleMesh m;
	PApplet parent;

	FlowAgent (Vec3D _o, boolean _f, VoxelGrid _v, BooleanBrush _br, TriangleMesh _m, int sf, PApplet _parent){
		super (_o, _f, _v, _br);
		m=_m;
		setScale(sf);
		parent = _parent;
		//reset();
	}

	public void run(){
		
		if(!locked()){

			update();
			flow(volume);
			addToTrail(this);
			if(trail.size()>25)removeFromTrail(0);
			if(lastSub==8){
				f=true;
				//TODO remove the agent
			}
			if(inBounds(450)){
				f=true;
				//TODO remove the agent
			}
			age++;
		}
		if(f)hangTrail();
		updateTrail();

	}

	void reset() {
		age = 0;
		hangTime =0;
		lastSub =0;
		f=false;
		set(2,2,2);
		vel = new Vec3D();
		accel = new Vec3D(parent.random(1),parent.random(1),parent.random(1));
		
		lastSub =0;
		resetTrail();
	}

	void flow(VoxelGrid volume) {

		//get the value of the current voxel
		float v = volume.getValue(this);

		//check neighbouring voxels for difference
		Vec3D from = volume.repelFromValue(this,0,1,0.1f).limit(0.1f);
		if(!from.isZeroVector()){
			lastSub+=1;
		}else{
			lastSub=0;
		}
		addForce(from);

	}

	public void hangTrail(){
		if(hangTime>100){
			volBrushTrail(250,2f,7f);
			// TODO remove the agent
		}
		stiffenTrail(0.04f);
		Link l = trail.get(0);
		l.a.addForce(new Vec3D(parent.noise(l.a.x*0.1f)-0.5f,parent.noise(l.a.y*0.1f)-0.5f,parent.noise(l.a.z*0.1f)-0.1f).scale(0.08f));
		addForceToTrail(new Vec3D(0,0,0.010f));
		hangTime+=1;
	}


}
