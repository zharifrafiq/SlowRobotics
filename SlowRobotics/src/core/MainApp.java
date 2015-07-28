package core;

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

This is an example setup for working with the classes in the library. 

/----------------------------------------------------------------------------------NOTES*/

@SuppressWarnings("serial")
public class MainApp extends PApplet {
	
	// ---------------------------------------------------------------------------EXTERNAL LIBS
	PeasyCam cam;
	ControlP5 controlP5;

	// ---------------------------------------------------------------------------SLOWROBOTICS
	public Environment environment; //store and update agents
	public VoxelGrid voxels; //voxel object
	public BooleanBrush vbrush; //for manipulating voxels
	public Canvas canvas;

	// -------------------------------- when running /simulating robots + vision
	//RobotWorkspace robotWorkspace;
	//public KinectHandler kinect;
	//steppers
	//public int stepVal = -87;
	// ----------------------------------------------------------------------------------SETUP

	public void setup(){
		size(800,600,OPENGL);
		cam =new PeasyCam(this,400);	
		setupP5();
		
		canvas = new Canvas(this.g); //drawing class 
		
		//create an environment for agents / voxels
		environment = new Environment(this, 2000);
		voxels = new VoxelGrid(10,10,10,new Vec3D(1,1,1));
		
		//add some agents
		for(int i=0;i<200;i++){
			Agent a = new Agent(Vec3D.randomVector().scale(200),false);
			environment.addAgent(a);
		}

		/*----------------------------------------------------------kinect setup
		//kinect = new KinectHandler(this);
		try {
			 Thread.sleep(4000); //try to wait for kinect to initialise
			} catch (InterruptedException e) {

			}
		//----------------------------------------------------------robot setup
		robotWorkspace = new RobotWorkspace(this);
		//robotWorkspace.loadMesh(sketchPath("mesh.stl"));
		 * */

	}

	public void draw(){
		background(0);
		environment.run(); //e.g. run agent pop
		canvas.drawPlanes(environment.pop, 10);
		//robotWorkspace.run(); e.g. run robotWorkspace
		gui(); //draws control p5 sliders as heads up display
	}

	/*------------------------------------

	Global functions 

	------------------------------------*/

	public void keyPressed(){
		if(key=='e'){
			environment.saveTrails();
			voxels.save("voxels_"+frameCount+"_"+voxels.w+"_"+voxels.h+"_"+voxels.d+".raw");
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
		/*
		TODO set this to the local variables of the kinectScanner class
		
		 * controlP5.addSlider("kinectTranslateX",-1000,1000,40,40,100,10);
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