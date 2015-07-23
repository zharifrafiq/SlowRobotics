package taxonomy;
import java.util.ArrayList;

import dynamicTools.Link;
import dynamicTools.MainApp;
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

	FlowAgent (Vec3D _o, boolean _f, MainApp _p, VoxelGrid _v, BooleanBrush _br, TriangleMesh _m, int sf){
		super (_o, _f,_p, _v, _br);
		m=_m;
		setScale(sf);
		//reset();
	}

	@Override
	public void run(){
		if(!locked()){
			update();
			flow(parent.voxels);
			addToTrail(this);
			if(trail.size()>25)removeFromTrail(0);
			if(lastSub==8){
				if(age<30)remove();
				f=true;
			}
			checkBounds(450);
			age++;
		}
		if(f)hangTrail();
		updateTrail();
		render(); 
		if(age>400)remove();
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
	
	void remove(){
		parent.environment.remove(this);
		//parent.addAgentOnMesh(m, 1, sf);
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
			volBrushTrail(250);
			remove();
		}
		stiffenTrail(0.04f);
		Link l = trail.get(0);
		l.a.addForce(new Vec3D(parent.noise(l.a.x*0.1f)-0.5f,parent.noise(l.a.y*0.1f)-0.5f,parent.noise(l.a.z*0.1f)-0.1f).scale(0.08f));
		addForceToTrail(new Vec3D(0,0,0.010f));
		hangTime+=1;
	}


	@Override
	public void checkBounds(int bounds) {
		if (x<0 || x>bounds*2 || y<0 || y>bounds*2 || z<0 || z>bounds)remove();
	}


}
