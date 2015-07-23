package robotTools;

import toxi.geom.Vec3D;

public class RobotTask implements Task{
	
	boolean running;
	public Robot robot;

	public RobotTask(Robot _robot){
		running = false;
		robot = _robot;
	}
	
	public boolean finished(){
		return !running;
	}
	
	public int getIO(){
		return robot.io;
	}

	public void run() {
		// TODO Auto-generated method stub
		running = true;
	}

	public Vec3D getTarget() {
		// TODO Auto-generated method stub
		return robot.targetPos;
	}
	
	public void end(){
		running = false;
	}
}
