package dynamicTools;

import pointCloudTools.KinectHandler;
import processing.core.*;
import controlP5.*;
import toxi.geom.AABB;
import toxi.geom.Vec3D;
import toxi.geom.mesh.TriangleMesh;
import toxi.processing.*;
import voxelTools.BooleanBrush;
import voxelTools.VectorField;
import voxelTools.VoxelGrid;
import peasy.*; 
import robotTools.RobotWorkspace;


/*----------------------------------------------------------------------------------NOTES

TODO 
- 'environment/workspace' interface
- robotWorkspace then implements this (as does the agent environment)
- clean up task heirarchy (query too similar to moveTO etc)

/----------------------------------------------------------------------------------NOTES*/

@SuppressWarnings("serial")
public class MainApp extends PApplet {
	// ----------------------------------------------------------------------------------REQUIRED
	PeasyCam cam;
	public Environment environment;
	ControlP5 controlP5;
	public ToxiclibsSupport gfx;
	
	//sketch specific properties
	public int maxDist = 20; //max distance for planes to interact
	public float maxPlane = 0.02f; // this is the strength of an effect that finds an approximate plane for a point and its neighbours 
	public float maxZ = 0.07f; // the maximum amount that one plane tries to move in its z axis to minimise the distance to other planes
	public float maxXY = 0.03f; // ditto for x and y axis
	float maxAlign= 0.07f; // the maximum amount that one plane tries to match the orientation of a neighbour plane 
	public int bounds = 200; //this is the size of the octree-  once an DynamicGPlane leaves this bounds nothing works.
	float bendResist = 0.1f;
	float repelDist = 50f;
	float repelFactor = 0.00f;
	float strandStiffness = 0.05f;
	float alignmentStrength = 0.01f;
	float attractFactor = 0.00f;
	float segSearch = 0f;
	public float attractCutoff = 50f;
	Vec3D vscale= new Vec3D(1,1,1);

	//voxels
	public VoxelGrid voxels;
	VectorField vectors;
	public BooleanBrush vbrush;
	TriangleMesh mesh;
	public float bMin = 2f;
	public float bMax = 7f;
	public float bthickness = 2f;
	int dimX = 10;
	int dimY = 10;
	int dimZ = 10;
	int ctr = 0;
	boolean rec = false;
	boolean killSwitch =false;
	
	//kinect +robot variables
	RobotWorkspace robotWorkspace;
	public KinectHandler kinect;
	
	//calibration
	public float kinectTranslateX = 550;
	public float kinectTranslateY = -440;
	public float kinectTranslateZ = 1785;
	public float kinectScale = 1000;
	public float kinectRotateA= 0;
	public float kinectRotateAxisX= 0;
	public float kinectRotateAxisY= 0;
	public float kinectRotateAxisZ= 1;
	
	//steppers
	public int stepVal = -87;
	// ----------------------------------------------------------------------------------SETUP

	public void setup(){
		size(800,600,OPENGL);
		cam =new PeasyCam(this,500,0,500,500);
		gfx = new ToxiclibsSupport(this);
		
		setupP5();
		reset();
		//----------------------------------------------------------kinect setup
		kinect = new KinectHandler(this);
		try {
			 Thread.sleep(4000); //try to wait for kinect to initialise
			} catch (InterruptedException e) {
				
			}
		//----------------------------------------------------------robot setup
		robotWorkspace = new RobotWorkspace(this);
		//robotWorkspace.loadMesh(sketchPath("mesh.stl"));
	}

	public void draw(){
		background(0);
		kinect.renderColours();

		if(!killSwitch)robotWorkspace.run();
	//	robotWorkspace.renderMesh(gfx);
		gui();
		//saveFrame("/data2/img"+nf(frameCount, 4)+".png");
	}

	/*------------------------------------

	Global functions 

	------------------------------------*/

	public void keyPressed(){
		if(key =='r'){
			//rec = true;
			String rnd = ""+(stepVal);
			System.out.println(rnd);
			robotWorkspace.rc.sendArduino(rnd, 8888, "169.254.106.35");
		}

		if(key=='e'){
			environment.saveTrails();
			voxels.save("voxels_"+frameCount+"_"+dimX+"_"+dimY+"_"+dimZ+".raw");
		}
		 if(key == CODED){
		    if (keyCode == LEFT){
		    	//robot.modifyTargetVelocity(new Vec3D(-0.1f,0,0));
		    	robotWorkspace.robot.moveTarget(new Vec3D(-5,0,0));
		    }
		    if (keyCode == RIGHT){
		    	//robot.modifyTargetVelocity(new Vec3D(0.1f,0,0));
		    	robotWorkspace.robot.moveTarget(new Vec3D(5,0,0));
		    }
		    if (keyCode == UP){
		    	//robot.modifyTargetVelocity(new Vec3D(0,-0.1f,0));
		    	robotWorkspace.robot.moveTarget(new Vec3D(0,-5,0));
		    }
		    if (keyCode == DOWN){
		    	//robot.modifyTargetVelocity(new Vec3D(0,0.1f,0));
		    	robotWorkspace.robot.moveTarget(new Vec3D(0,5,0));
		    }
		 }
		 if(key =='<'){
			 robotWorkspace.robot.moveTarget(new Vec3D(0,0,-5));
		 }
		 if(key =='>'){
			 robotWorkspace.robot.moveTarget(new Vec3D(0,0,5));
		 }
		 if(key =='x'){
			 //kill switch
			 robotWorkspace.robot.setTarget(robotWorkspace.robot);
			 robotWorkspace.robot.setIO(1);
			 String pos = ""+80;
			 robotWorkspace.rc.sendArduino(pos, 8888, "169.254.106.35");
			 robotWorkspace.send();
			 killSwitch = true;
		 }
		 if(key == 'i'){
			 //toggle io
			 robotWorkspace.robot.toggleIO();
		 }
		 if(key=='l'){
			robotWorkspace.updatePcl();
		 }
		 if(key=='s'){
			 saveFrame("grab.png");
		 }
	}
	
	void reset(){
		environment = new Environment(this);
		voxels = new VoxelGrid(dimX,dimY,dimZ,vscale);
	}

	/*------------------------------------

	ControlP5

	------------------------------------*/

	public void setupP5(){
		controlP5 = new ControlP5(this);
		controlP5.setAutoDraw(false);
		/*controlP5.addSlider("kinectTranslateX",-1000,1000,40,40,100,10);
		controlP5.addSlider("kinectTranslateY",-1000,1000,40,60,100,10);
		controlP5.addSlider("kinectTranslateZ",-1000,2000,40,80,100,10);
		controlP5.addSlider("kinectScale",10,2000,40,100,100,10);
		controlP5.addSlider("kinectRotateA",-180,180,40,120,100,10);
		controlP5.addSlider("kinectRotateAxisX",-1.00f,1.00f,40,140,100,10);
		controlP5.addSlider("kinectRotateAxisY",-1.00f,1.00f,40,160,100,10);
		controlP5.addSlider("kinectRotateAxisZ",-1.00f,1.00f,40,180,100,10);*/
		controlP5.addSlider("stepVal",-200,200,40,40,100,10);
	}


	void gui() {
		hint(DISABLE_DEPTH_TEST);
		cam.beginHUD();
		controlP5.draw();
		//fill(255);
		//text("fps: "+frameRate, 50, 50);
		cam.endHUD();
		hint(ENABLE_DEPTH_TEST);
	}
}