package robotTools;


import core.MainApp;
import core.Plane3D;
import processing.core.PImage;
import toxi.geom.Vec3D;

public class Robot extends Plane3D{
	public Vec3D targetPos, homePos;
	public int io =1;
	
	//-------------------------------------------------------------------------------------

	//Constructors

	//-------------------------------------------------------------------------------------
	
	public Robot(Vec3D _endEffector, Vec3D _velocity, RobotWorkspace _workspace){
		super(_endEffector);
		
		targetPos = copy();
		homePos = add(new Vec3D(0,200,-200));
	}
	
	public Robot (Plane3D _endEffector, RobotWorkspace _workspace){
		this(_endEffector, new Vec3D(), _workspace);
	}
	
	public void run(){
		

	}
	
	//-------------------------------------------------------------------------------------

	//get+set+initialisation

	//-------------------------------------------------------------------------------------
	
	public void setIO(int _io){
		io = _io;
	}
	
	public void toggleIO(){
		if(io==0){
			io=1;
		}else if(io==1){
			io=0;
		}
	}
	
	public void updatePos(Vec3D newPos){
		set(newPos);
	}
	
	public void setTarget(Vec3D target){
		targetPos = target;
	}
	
	public void moveTarget(Vec3D vector){
		targetPos.addSelf(vector);
	}
	
	public float getDistToTarget(){
		return distanceTo(targetPos);
	}
	
	//-------------------------------------------------------------------------------------

	//rendering

	//-------------------------------------------------------------------------------------
	
	public void render(MainApp parent){
	//	endEffector.render(20);
		if(io==1){
			parent.stroke(255,0,0);
		}else{
			parent.stroke(0,255,0);
		}
		parent.strokeWeight(3);
		parent.point(x, y,z);
	//	targetPos.render(5);
		parent.stroke(255,0,255);
		parent.line(x, y, z, targetPos.x,targetPos.y,targetPos.z);
	}
	
}
