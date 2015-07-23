package dynamicTools;
import toxi.geom.Vec3D;

/*------------------------------------

Basic particle class

------------------------------------*/

public class Particle extends Vec3D {
	
	protected Vec3D accel = new Vec3D();
	protected Vec3D vel = new Vec3D();
	float spd = 1;
	float accLimit = 1;
	public int age =0;
	protected boolean f = false;
	
	Particle (float _x, float _y, float _z){
		super (_x,_y,_z);
	}
	
	Particle (Vec3D _o){
		super (_o);
	}
	
	//-------------------------------------------------------------------------------------

	  //Functions for motion

	  //-------------------------------------------------------------------------------------

	  public void addForce(Vec3D force){
	   if(force.magnitude()>0.001)accel.addSelf(force); 
	  }
	  
	  public void addForceAndUpdate(Vec3D force){
		  if(force.magnitude()>0.001){
			  accel.addSelf(force); 
			  update();
		  }
	  }

	  public void update(){
			  accel.limit(accLimit);
			  vel.addSelf(accel);
			  vel.limit(spd);
			  addSelf(vel);
			  accel=new Vec3D();
			  age++;
	  }
	  
	  public void update(float damping){
			 // accel.limit(10);
			  if(!f){
				  vel.addSelf(accel);
				  //vel.limit(spd);
				  vel.scaleSelf(damping);
				  addSelf(vel);
			  }
			  accel=new Vec3D();
		  }
	  
	  public void setSpeed(float s){
		  spd = s;
	  }
	  
	  public void setAccel(float a){
		  accLimit = a;
	  }
	
	  	public void lock(){
	  		f = true;
	  	}
	  	
	  public boolean locked(){
		  return f;
	  }
	
}
