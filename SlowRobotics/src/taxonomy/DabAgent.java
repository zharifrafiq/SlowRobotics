package taxonomy;

import java.util.ArrayList;

import pointCloudTools.PathFinder;
import pointCloudTools.PointCloud;
import robotTools.Robot;
import robotTools.RobotWorkspace;
import controlP5.Println;
import toxi.geom.AABB;
import toxi.geom.Vec3D;

public class DabAgent extends RobotAgent{

	int[] stepTargets = new int[]{-85,-65,-60,0,0}; 
	public int steps =50;
	int maxSteps = 200;
	public int numTargets = 10;
	
	public DabAgent(PointCloud _pcl, RobotWorkspace _workspace, Robot _robot) {
		super(_pcl,_workspace,_robot);
		setSpeed(1.5f);
		setAccel(1.8f);
	}
	
	public void run() {
		if(!running){
			reset();
		}
		running = true;
		robot.setTarget(this);
			//move away from point cloud
			//for now just move up

			if(steps<120)addForce(new Vec3D(0,0,0.03f)); //acceleration of the agent
			update();
			//interpolation for stepVal
			if(steps>1)steps-=1; //how much to release the trigger each frame
			String pos = ""+getStepperVal();
			ws.rc.sendArduino(pos, 8888, "169.254.106.35");
			if(steps<=1){
					robot.homePos = add(new Vec3D(0,100,100));
					end();
			}

	}
	
	public void attract(Vec3D j, float max, float strength){
		Vec3D toPlane3D = j.sub(this);
		float ratio = 1-(toPlane3D.magnitude()/max);
		float f = interp.interpolate(0,strength,ratio);
		toPlane3D.scaleSelf(f);
		if(!toPlane3D.isZeroVector())addForce(toPlane3D);
	}
	
	
	public void reset(){
		
		set(PathFinder.getHighestPoint(new Vec3D(0,0,-1000), pcl.getPoints()).add(new Vec3D(0,0,30)));
		vel = new Vec3D();
		steps = maxSteps;
		numTargets = 10;

	}

	public void getNextPoint(){
		
		int r = 100;
		 AABB box = AABB.fromMinMax(add(-r,-r,-200), add(r,r,-0));
		 ArrayList<Vec3D> pts = pcl.tree.getPointsWithinBox(box);
		 Vec3D pt = PathFinder.getLowestPoint(copy(),pts);
		 pt.addSelf(new Vec3D(0,0,30));
		 if(pt.z<5)pt.set(pt.x,pt.y,5); //limit
		 set(pt);
		
		
		//set(pcl.randomPoint().copy());
		//resetTrail();
		vel = new Vec3D();;
		 steps = maxSteps;
		 robot.setTarget(this);
		
		 
	}
	
	public int getStepperVal(){
		float f = map(steps, maxSteps, 0, 1, stepTargets.length);
		int i = (int)f;
		float lerpFactor = f-i;
		int stepVal = (int)lerp(stepTargets[i-1], stepTargets[i],lerpFactor);
		return stepVal;
	}

}
