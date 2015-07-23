package robotTools;

public class SetIO extends RobotTask{
	int io;
	
	public SetIO(int _io, Robot _robot) {
		super(_robot);
		io = _io;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void run(){
		robot.setIO(io);
		end();
	}
}
