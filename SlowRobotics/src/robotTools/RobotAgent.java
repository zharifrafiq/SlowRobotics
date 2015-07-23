package robotTools;

import java.util.ArrayList;

import pointCloudTools.PointCloud;
import dynamicTools.MainApp;
import taxonomy.Agent;
import toxi.geom.AABB;
import toxi.geom.Vec3D;

public class RobotAgent extends Agent implements Task{


	PointCloud pcl;
	RobotWorkspace ws;
	Robot robot;
	boolean running;
	//give a reference to the task manager - this would handle states...
	
	public RobotAgent(PointCloud _pcl, RobotWorkspace _workspace, Robot _robot) {
		super(_robot.copy(), false, _workspace.parent);
		pcl = _pcl;
		robot = _robot;
		ws = _workspace;
	}
	
	public void run(){
		//addRandomVel();

		update();
		running = true;

		//TODO - add call to end() function
		//TODO - add stepper calls
		//checkBounds();
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
