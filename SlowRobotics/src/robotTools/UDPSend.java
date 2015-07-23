package robotTools;

import toxi.geom.Vec3D;

public class UDPSend extends RobotTask{
	
	int port;
	String ip, data;
	RobotClient rc;
	
	public UDPSend(RobotClient _rc, int _port, String _ip, String _data, Robot _robot) {
		super(_robot);
		rc = _rc;
		port = _port;
		ip = _ip;
		data = _data;
	}
	
	@Override
	public void run(){
		running = true;
		if(rc.sendArduino(data, port, ip))end();
	}
}
