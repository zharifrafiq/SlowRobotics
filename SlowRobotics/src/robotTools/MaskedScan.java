package robotTools;

import toxi.geom.Vec3D;

public class MaskedScan extends RobotTask{
	
	RobotWorkspace ws;
	int r,g,b,fuzz;
	
	public MaskedScan(int _r, int _g, int _b, int _fuzz, RobotWorkspace _ws, Robot _robot){
		super(_robot);
		ws = _ws;
		r = _r;
		g = _g;
		b= _b;
		fuzz = _fuzz;
	}
	
	@Override
	public void run(){
		running = true;
		ws.pcl = ws.additiveScan(robot, 400);
		ws.pcl.buildTree();
		end();
	}
	
}
