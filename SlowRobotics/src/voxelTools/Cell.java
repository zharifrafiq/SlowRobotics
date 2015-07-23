package voxelTools;
import java.util.Set;

import org.omg.CORBA.PUBLIC_MEMBER;


public class Cell {
	
	float val;
	boolean io;
	public boolean edge;
	
	Cell(float v, boolean _e){
		val =v;
		io=true;
		edge = _e;
	}
	
	Cell(int v, boolean _e){
		val = (float)v;
		io=true;
		edge = _e;
	}
	
	public void set(float v){val=v;}
	public float get(){return val;}
	public boolean on(){return io;}
	public void turnOff(){io=false;}

}
