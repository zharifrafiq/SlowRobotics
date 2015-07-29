package robotTools;

import java.net.UnknownHostException;

import core.MainApp;
import core.Plane3D;
import pointCloudTools.Scanner;
import pointCloudTools.PointCloud;
import processing.core.PApplet;
import taxonomy.DabAgent;
import taxonomy.TracerAgent;
import toxi.geom.AABB;
import toxi.geom.Vec3D;
import toxi.geom.mesh.STLReader;
import toxi.geom.mesh.WETriangleMesh;
import toxi.processing.ToxiclibsSupport;

public class RobotWorkspace {
	
	public PApplet parent;
	WETriangleMesh mesh;
	public PointCloud pcl;
	public Robot robot;
	public RobotClient rc;
	public TaskHandler tasks; 
	ServerSimulator sim;


	
	public RobotWorkspace(PApplet _parent){
		parent = _parent;
		pcl = new PointCloud(parent);
		Plane3D initialPlane = new Plane3D(new Vec3D(540.5f,-18.1f,600f));
	
		try {
			rc = new RobotClient(5008, parent);
			rc.start();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		robot = new Robot(initialPlane,this);
		//db = new DabAgent(pcl, this, robot);
		initTasks();
		
		sim = new ServerSimulator(robot);
	}
	
	public void initTasks(){
	}
	
	public void run(){
		
		// TODO kill calls to listen + send, do this in the robot client thread
		
	    listen(); //update robot pos from rsi
		//listenSim();
	    
	    // TODO task handler should also be a seperate thread. 
	    
		tasks.run(); 
		send(); //update rsi
		//render();	
	}
	
	
	public void listen(){
		//----------------------------------------------------------robot comms
		if (rc.available()) {
		    robot.updatePos(rc.getRobotPos());
		}
	}
	
	public void listenSim(){
		sim.step();
	}
	
	public void send(){
		rc.sendVector(robot.targetPos, "Position", "10.220.244.122");
		rc.sendIO(robot.io, "Gripper", "10.220.244.122");
	}
	
	public PointCloud updatePcl(){
		//float r = 800;
		//AABB box = AABB.fromMinMax(robot.add(-r,-r,-1000), robot.add(r,r,-50));
		//pcl.load(parent.kinect.copyAABB(box));
		//pcl.extractColourRange(253, 253, 253, 10);
		pcl.loadSinWavePts();
		return pcl;
		//mesh = pcl.createDelaunayMesh();
	}
	
	public PointCloud maskScan(int r, int g, int b, int fuzz, Vec3D loc, float rad, Scanner scanner){
		PointCloud tmp = new PointCloud(parent);
		//AABB box = AABB.fromMinMax(robot.add(-rad,-rad,-1000), robot.add(rad,rad,-50));
		AABB box = AABB.fromMinMax(loc.add(new Vec3D(-rad,-rad,-rad)), loc.add(new Vec3D(rad,rad,0)));
		parent.noFill();
		parent.stroke(255,0,0);
		tmp.load(scanner.copyAABB(box));
		tmp.extractColourRange(r, g, b, fuzz);
		//pcl.loadSinWavePts();
		return tmp;
	}
	
	public PointCloud additiveScan(Vec3D loc, float rad, Scanner scanner){
		pcl.appendHeightField(maskScan(253, 253, 253, 10, loc,rad, scanner));
		return pcl;
	}

	public void loadMesh(String path){
		mesh=(WETriangleMesh)new STLReader().loadBinary(parent.sketchPath(path),STLReader.TRIANGLEMESH);
	}
	
	public void renderMesh(ToxiclibsSupport gfx){
		parent.strokeWeight(1);
		if(mesh!=null)gfx.mesh(mesh);
	}
	
	public void render(){
		robot.render(parent);
		if(pcl.ready())pcl.renderColours();
	}
}
