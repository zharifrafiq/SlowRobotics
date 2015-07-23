package robotTools;

import toxi.geom.Vec3D;

public class BezierThroughPoint extends RobotTask{
	
	Vec3D targetA, targetB;
	float f,raInit;
	
	public BezierThroughPoint(Vec3D _tA, Vec3D _tB, float _f, Robot _robot) {
		super(_robot);
		targetA = _tA;
		targetB = _tB;
		f = _f;
		raInit = robot.distanceTo(targetA);
	}
	
	@Override
	public void run(){
		running = true;
		robot.setTarget(getBezier());
		if(robot.getDistToTarget()<5)end();
	}
	
	public Vec3D getBezier(){
		Vec3D ra = new Vec3D(targetA.sub(robot));
		float raCurrent = ra.magnitude();
		if(raCurrent/raInit<f){
			Vec3D ab = targetB.sub(targetA).scale(f-(raCurrent/raInit)); //draw bezier
			return targetA.add(ab);
		}
		return targetA;
	}
}
