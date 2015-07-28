package robotTools;

import java.util.Comparator;

import toxi.geom.Vec3D;

public class RobotTask implements Task, Comparator<RobotTask>{
	
	boolean running;
	public Robot robot;
	int priority =1; //default priority

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
		running = true;
	}

	public Vec3D getTarget() {
		return robot.targetPos;
	}
	
	public void end(){
		running = false;
	}
	
	public int getPriority() {
		return priority;
	}
	
	public void setPriority(int _priority){
		priority =_priority;
	}

	@Override
	public int compare(RobotTask o1, RobotTask o2) {
		return( o1.getPriority()-o2.getPriority());
	}
}
