package robotTools;

import pointCloudTools.Scanner;

public class MaskedScan extends RobotTask{
	
	RobotWorkspace ws;
	Scanner scanner;
	int r,g,b,fuzz;
	
	public MaskedScan(int _r, int _g, int _b, int _fuzz, RobotWorkspace _ws, Robot _robot, Scanner _scanner){
		super(_robot);
		ws = _ws;
		r = _r;
		g = _g;
		b= _b;
		fuzz = _fuzz;
		scanner = _scanner;
	}
	
	@Override
	public void run(){
		running = true;
		ws.pcl = ws.additiveScan(robot, 400, scanner);
		ws.pcl.buildTree();
		end();
	}
	
}
