package robotTools;

import java.net.UnknownHostException;




import java.util.Scanner;

import pointCloudTools.PointCloud;
import dynamicTools.MainApp;
import dynamicTools.Plane3D;
import toxi.geom.AABB;
import toxi.geom.Vec3D;
import toxi.geom.mesh.STLReader;
import toxi.geom.mesh.WETriangleMesh;
import toxi.processing.ToxiclibsSupport;

public class RobotWorkspace {
	
	public MainApp parent;
	WETriangleMesh mesh;
	public PointCloud pcl;
	public Robot robot;
	public RobotClient rc;
	public TaskHandler tasks; 
	//---------------------------TESTING
	ServerSimulator sim;
	TracerAgent test;
	DabAgent db;
	
	public RobotWorkspace(MainApp _parent){
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
		tasks = new TaskHandler(true); //looping tasks
		test = new TracerAgent(pcl, this, robot);
		//db = new DabAgent(pcl, this, robot);
		initTasks();
		
		sim = new ServerSimulator(robot);
	}
	
	public void initTasks(){
		//add tasks
		tasks.addTask(new GoToPoint(robot.homePos, robot)); //start at home
		tasks.addTask(new MaskedScan(250, 250, 250, 10, this, robot));
		//tasks.addTask(new OffsetPoint(test,new Vec3D(0,0,150), robot)); //follow agent
		//tasks.addTask(test); // dab
		tasks.addTask(test);
		//tasks.addTask(new DabAgent(pcl, this, robot));
		//tasks.addTask(TaskHandler.coverSurface(robot, this, pcl)); //run agent task
		//then add task for making the legs	
	}
	
	public void run(){
	    listen(); //update robot pos from rsi
		//listenSim();
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
	
	public PointCloud maskScan(int r, int g, int b, int fuzz, Vec3D loc, float rad){
		PointCloud tmp = new PointCloud(parent);
		//AABB box = AABB.fromMinMax(robot.add(-rad,-rad,-1000), robot.add(rad,rad,-50));
		AABB box = AABB.fromMinMax(loc.add(new Vec3D(-rad,-rad,-rad)), loc.add(new Vec3D(rad,rad,0)));
		parent.noFill();
		parent.stroke(255,0,0);
		parent.gfx.box(box);
		tmp.load(parent.kinect.copyAABB(box));
		tmp.extractColourRange(r, g, b, fuzz);
		//pcl.loadSinWavePts();
		return tmp;
	}
	
	public PointCloud additiveScan(Vec3D loc, float rad){
		pcl.appendHeightField(maskScan(253, 253, 253, 10, loc,rad));
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
