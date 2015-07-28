package dynamicTools;

import pointCloudTools.KinectScanner;
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


	//voxels
	public VoxelGrid voxels;
	public BooleanBrush vbrush;
	TriangleMesh mesh;
	
	//robots
	RobotWorkspace robotWorkspace;
	//public KinectHandler kinect;
	

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
		//kinect = new KinectHandler(this);
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
		//kinect.renderColours();

		robotWorkspace.run();
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
			voxels.save("voxels_"+frameCount+"_"+voxels.w+"_"+voxels.h+"_"+voxels.d+".raw");
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
		environment = new Environment(this, 2000);
		//set size of voxels here
		voxels = new VoxelGrid(10,10,10,new Vec3D(1,1,1));
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