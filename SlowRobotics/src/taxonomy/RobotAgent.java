package taxonomy;
import pointCloudTools.PointCloud;
import robotTools.Robot;
import robotTools.RobotWorkspace;
import robotTools.Task;


public class RobotAgent extends Agent implements Task{


	PointCloud pcl;
	RobotWorkspace ws;
	Robot robot;
	boolean running;
	int priority;
	//give a reference to the task manager - this would handle states...
	
	public RobotAgent(PointCloud _pcl, RobotWorkspace _workspace, Robot _robot) {
		super(_robot.copy(), false);
		pcl = _pcl;
		robot = _robot;
		ws = _workspace;
	}
	
	public void run(){
		//addRandomVel();

		update();
		running = true;
		//checkBounds();
	}
	

	public void checkBounds(){
		//if(x<0 || x>1000 ||y<-200 || y>800) vel.invert();
		if(z<10);
	}

	@Override
	public boolean finished() {
		return !running;
	}

	@Override
	public void end() {
		running = false;
	}

	@Override
	public int getPriority() {
		return priority;
	}
}
