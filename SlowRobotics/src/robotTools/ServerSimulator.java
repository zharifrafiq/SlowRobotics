package robotTools;

import toxi.geom.Vec3D;

public class ServerSimulator {
	
	Robot robot;
	
	ServerSimulator(Robot _robot){
		robot = _robot;
	}
	
	public void step(){
		robot.updatePos(robot.add(getVel()));
	}
	
	public Vec3D getVel(){
		Vec3D ab = robot.targetPos.sub(robot);
		float mag = ab.magnitude();
		float sf = (mag>30)?1:mag/20;
		return ab.limit(3).scale(sf);
	}
}
