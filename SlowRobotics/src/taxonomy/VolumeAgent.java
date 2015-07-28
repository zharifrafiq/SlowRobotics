package taxonomy;
import core.Agent;
import core.Environment;
import core.MainApp;
import core.Plane3D;
import toxi.geom.Vec3D;
import toxi.math.CircularInterpolation;
import toxi.math.ExponentialInterpolation;
import toxi.math.LinearInterpolation;
import voxelTools.BooleanBrush;
import voxelTools.VoxelGrid;


public class VolumeAgent extends Agent{
	
	VoxelGrid volume;
	BooleanBrush brush;
	int sf;

	  VolumeAgent (Vec3D _o, boolean _f, VoxelGrid _v, BooleanBrush _br){
	   super (_o, _f);
	   f=_f;
	    volume = _v;
	    brush = _br;
	  }
	  
	
	//-------------------------------------------------------------------------------------
	  
	  //Voxel and volume brush functions
	  
	  //-------------------------------------------------------------------------------------
	
	  public int[] getGridPos(Vec3D l, VoxelGrid v){
		  int[] val = new int[3];
	    val[0] = (int) constrain(l.x/v.s.x,0,v.w-1);
	    val[1] = (int) constrain(l.y/v.s.y,0,v.h-1);
	    val[2] = (int) constrain(l.z/v.s.z,0,v.d-1); 
	    return val;
	  }
	  
	  void volBrushLocation(Vec3D tPt, int vdensity, float bSize, boolean fill){
	      int[] v = getGridPos(tPt, volume);
	      brush.setSize(bSize);
	      if(!fill){
	    	  brush.drawAtGridPos(x,y,z,vdensity);
	      }else{
	    	  brush.fillAtGridPos(x,y,z);
	      }
	  }
	  
	  void volBrushTrail(int vdensity, float bMin, float bMax){
	    LinearInterpolation interp = new LinearInterpolation();
	  // float sf = parent.round(parent.map(x/volume.w, 0,1,2,-2));
	     // float sf = 0;
	      for(int i = 0;i<trail.size();i++){
	        Vec3D tPt  = trail.get(i).a;
	        float f = map(i,0,trail.size()-1,0,1);
	        float bSize = interp.interpolate(bMin,bMax+sf,f);
	        volBrushLocation(tPt, vdensity, bSize, false);
	      }

	  }
	  
	  public void setScale(int s){
		  sf = s;
		  
	  }

}
