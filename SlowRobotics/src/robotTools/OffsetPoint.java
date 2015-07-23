package robotTools;

import toxi.geom.Vec3D;

public class OffsetPoint extends RobotTask{
	Vec3D target;
	Vec3D offset;
	
	public OffsetPoint(Vec3D _target, Vec3D _offset, Robot _robot) {
		super(_robot);
		target = _target;
		offset = _offset;
	}
	
	@Override
	public void run(){
		running = true;
		robot.setTarget(target.add(offset));
		if(robot.getDistToTarget()<5)end();
	}

}
