package robotTools;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import processing.core.PApplet;
import processing.data.XML;
import toxi.geom.Vec3D;

public class RobotClient extends Thread {
	
	DatagramSocket ds;
	int port;

	byte[] receiveBuffer = new byte[1024]; 
	byte[] receiveData;
	PApplet parent;
	
	boolean running;
	boolean available;
	//-------------------------------------------------------------------------------------

	//Constructors

	//-------------------------------------------------------------------------------------
	
	public RobotClient(int _port, PApplet _parent) throws UnknownHostException{
		
		parent = _parent;
		port = _port;

		try {
		      ds = new DatagramSocket(port);
		    } catch (SocketException e) {
		      e.printStackTrace();
		    }
		running =false;

	}
	
	//-------------------------------------------------------------------------------------

	//threading

	//-------------------------------------------------------------------------------------
	
	@Override
	public void start(){
		running = true;
		super.start();
		
	}
	
	public void run () { 
		while (running) {
		      checkForMessage();
		      // New data is available!
		      available = true;
		    }
	} 
	
	public void quit() { 
	    System.out.println("Quitting."); 
	    running = false;
	    // In case the thread is waiting. . . 
	    interrupt(); 
	  } 
	
	//-------------------------------------------------------------------------------------

	//send/receive functions

	//-------------------------------------------------------------------------------------
	
	private void checkForMessage() {
	    DatagramPacket p = new DatagramPacket(receiveBuffer, receiveBuffer.length); 
	    try {
	      ds.receive(p);
	    } 
	    catch (IOException e) {
	      e.printStackTrace();
	    } 
	    receiveData = p.getData();
	  }
	
	public Vec3D getRobotPos(){
		
		Vec3D robotPos = new Vec3D();
		//message should be a string that is formatted XML
		
		//perform cast
		String message = new String( receiveData );
		int end = message.indexOf("</Robot>");
		String trimmedMessage = message.substring(0, end+8);
		//System.out.println(trimmedMessage);
		//get XML object
		
		XML xml = parent.parseXML(trimmedMessage);
		  if (xml == null) {
		    System.out.println("XML could not be parsed.");
		  } else {
		    XML posElement = xml.getChild("Position");
		    float x = posElement.getFloat("X");
		    float y = posElement.getFloat("Y");
		    float z = posElement.getFloat("Z");
		    robotPos = new Vec3D(x,y,z);
		  }
		available = false; //reset available after data returned
		return robotPos;
	}
	
	public void sendVector(Vec3D v, String element, String ip){
		
		String attributes = "X=\""+v.x+"\" Y=\""+v.y+"\" Z=\""+v.z+"\"";
		String data = "<Robot><" + element + " "+attributes + "/></Robot>";
		byte[] sendBytes = data.getBytes();
		try {
		    ds.send(new DatagramPacket(sendBytes,sendBytes.length, InetAddress.getByName(ip),port));
		  } 
		  catch (Exception e) {
		    e.printStackTrace();
		  }

	}
	
	public void sendIO(int val,String element, String ip){
		String data = "<Robot><" + element + ">" +val + "</" + element +"></Robot>";
		byte[] sendBytes = data.getBytes();
		try {
		    ds.send(new DatagramPacket(sendBytes,sendBytes.length, InetAddress.getByName(ip),port));
		  } 
		  catch (Exception e) {
		    e.printStackTrace();
		  }
	}
	
	public boolean sendArduino(String data, int arduinoPort, String ip){
		byte[] sendBytes = data.getBytes();
		try {
		    ds.send(new DatagramPacket(sendBytes,sendBytes.length, InetAddress.getByName(ip),arduinoPort));
		  } 
		  catch (Exception e) {
		    e.printStackTrace();
		    return false;
		  }
		return true;
	}
	
	public boolean available(){
		return available;
	}
	
}
