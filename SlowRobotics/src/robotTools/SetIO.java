package robotTools;

public class SetIO extends RobotTask{
	int io;
	
	public SetIO(int _io, Robot _robot, int _priority) {
		super(_robot);
		io = _io;
		priority = _priority;
	}
	
	@Override
	public void run(){
		robot.setIO(io);
		end();
	}
}
