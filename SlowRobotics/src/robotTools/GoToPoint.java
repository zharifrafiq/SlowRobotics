package robotTools;

import toxi.geom.Vec3D;

public class GoToPoint extends RobotTask{
	Vec3D target;
	
	public GoToPoint(Vec3D _target, Robot _robot) {
		super(_robot);
		target = _target;
	}
	
	@Override
	public void run(){
		running = true;
		robot.setTarget(target);
		if(robot.getDistToTarget()<5)end();
	}

}
