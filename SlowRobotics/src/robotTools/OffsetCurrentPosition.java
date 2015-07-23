package robotTools;

import toxi.geom.Vec3D;

public class OffsetCurrentPosition extends RobotTask{
	Vec3D offset;
	
	public OffsetCurrentPosition(Vec3D _offset, Robot _robot) {
		super(_robot);
		offset = _offset;
	}
	
	@Override
	public void run(){
		if(!running)robot.setTarget(robot.add(offset));
		running = true;
		if(robot.getDistToTarget()<5)end();
	}

}
