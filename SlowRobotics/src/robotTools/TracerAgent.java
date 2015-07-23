package robotTools;

import java.util.ArrayList;

import pointCloudTools.PathFinder;
import pointCloudTools.PointCloud;
import toxi.geom.AABB;
import toxi.geom.Vec3D;

public class TracerAgent extends RobotAgent{

	int height = 80;
	int runs = 0;
	//give a reference to the task manager - this would handle states...
	
	public TracerAgent(PointCloud _pcl, RobotWorkspace _ws, Robot _robot) {
		super(_pcl,_ws,_robot);
		setSpeed(1.8f);
		setAccel(1.8f);
		set(robot.x,robot.y,height);
		init();
		vel = Vec3D.randomVector();
		vel.set(vel.x,vel.y,0);
		running = false;
		// TODO Auto-generated constructor stub
	}
	
	public void run(){
		//addRandomVel();
		if(!running){
			
			//set(robot.x,robot.y,height);
			init();
		}
		trace();
		update();
		running = true;
		if(parent.frameCount%5==0)addToTrail(this);
		if(trail.size()>50){
			removeFromTrail(0);
			robot.setTarget(trail.get(0).a.add(new Vec3D(0,0,100))); //set target to end of trail
			String pos = ""+parent.stepVal;
			ws.rc.sendArduino(pos, 8888, "169.254.106.35");
			//could check distance from robot to agent and increase or decrease speed accordingly
		}
		if(age%50==0){
			ws.additiveScan(this,200); 
		//	height+=2;
			//set(x,y,height); //push up a bit every 50 frames
		}
		if(age>6000){
			String pos = ""+0;
			ws.rc.sendArduino(pos, 8888, "169.254.106.35");
			runs+=1;
			parent.stepVal-=5;
			if(parent.stepVal<-110){
				parent.stepVal = -85;
				runs =0;
			
			}
			end();
		}
		render();
		//TODO - add call to end() function
		//TODO - add stepper calls
		//checkBounds();
	}
	
	public void init(){
		age =0;
		resetTrail();
		ws.additiveScan(this, 300);
	}
	
	public void addRandomVel(){
		Vec3D f = Vec3D.randomVector().scale(0.2f);
		//f.set(f.x,f.y,0);
		//f.addSelf(new Vec3D(0,0,0.5f));
		addForce(f);
	}
	
	public void trace(){
		
		
		int r = 100;
		AABB box = AABB.fromMinMax(add(-r,-r,-r), add(r,r,r));
		ArrayList<Vec3D> pts = pcl.tree.getPointsWithinBox(box);
		
		if(pts!=null){
			Vec3D cpt = PathFinder.getClosestPointFOV(this, vel, 1f, pts);
			if(cpt!=null) {
				attract(cpt, r, 0.5f);
			}else{
			cpt = PathFinder.getClosestPointFOV(this, vel, 4f, pts);
			if(cpt!=null)attract(cpt, r, 0.8f);
			}
		}
		
			//Vec3D hPt = PathFinder.getLowestPointFOV(this, vel, 0.5f, 50f, pts);
			//if(hPt!=null) attract(hPt, 200, 0.1f);
			
			//TODO - behaviour to find edge of existing material
			//search for height
			//Vec3D planePt = PathFinder.getNearZ(this,vel,1.5f,this.z,pts);
			//if(planePt!=null)attract(planePt, 60, 1f);
			/*
			 * 
		int r = 100;
		AABB box = AABB.fromMinMax(add(-r,-r,r), add(r,r,r));
		ArrayList<Vec3D> pts = pcl.tree.getPointsWithinBox(box);
		
		if(pts!=null){
			Vec3D cpt = PathFinder.getLowestPointFOV(this, vel, 1f, pts);
				if(cpt!=null) {
					attract(cpt, r, 0.5f);
			}
		}*/
		
		
		
	}
	
	public void attract(Vec3D j, float max, float strength){
		Vec3D toPlane3D = j.sub(this);
		float ratio = 1-(toPlane3D.magnitude()/max);
		float f = interp.interpolate(0,strength,ratio);
		toPlane3D.scaleSelf(f);
		if(!toPlane3D.isZeroVector())addForce(toPlane3D);
	}
	
	public void checkBounds(){
		//if(x<0 || x>1000 ||y<-200 || y>800) vel.invert();
		if(z<10);
	}

	@Override
	public boolean finished() {
		// TODO Auto-generated method stub
		return !running;
	}

	@Override
	public void end() {
		// TODO Auto-generated method stub
		running = false;
	}
}
