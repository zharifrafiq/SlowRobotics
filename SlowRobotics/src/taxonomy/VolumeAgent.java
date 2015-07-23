package taxonomy;
import dynamicTools.MainApp;
import dynamicTools.Plane3D;
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
	
	VolumeAgent (Vec3D _o, Vec3D _x, Vec3D _y, boolean _f, MainApp _p, VoxelGrid _v, BooleanBrush _br) {
	    super(_o, _x, _y, _f,_p);
	    f=_f;  
	    volume = _v;
	    brush = _br;
	  }
	  
	  VolumeAgent (Vec3D _o, boolean _f, MainApp _p, VoxelGrid _v, BooleanBrush _br){
	   super (_o, _f,_p);
	   f=_f;
	    volume = _v;
	    brush = _br;
	  }
	  
	  VolumeAgent (Vec3D _o, Vec3D _x, boolean _f, MainApp _p, VoxelGrid _v, BooleanBrush _br){
	    super (_o,_x, _f, _p);
	    f = _f;
	    volume = _v;
	    brush = _br;
	  }

	  VolumeAgent (Plane3D _b, boolean _f, MainApp _p, VoxelGrid _v, BooleanBrush _br) {
	    super(_b, _f,_p);
	    f=_f;
	    volume = _v;
	    brush = _br;
	  }
	
	@Override
	public void run(){
		
	
		
	}
	
	//-------------------------------------------------------------------------------------
	  
	  //Voxel and volume brush functions
	  
	  //-------------------------------------------------------------------------------------
	  public int[] getGridPos(Vec3D l, VoxelGrid v){
		  int[] val = new int[3];
	    val[0] = (int) parent.constrain(l.x/v.s.x,0,v.w-1);
	    val[1] = (int) parent.constrain(l.y/v.s.y,0,v.h-1);
	    val[2] = (int) parent.constrain(l.z/v.s.z,0,v.d-1); 
	    return val;
	  }
	  
	  void volBrushLocation(int vdensity){
	      int[] v = getGridPos(this, parent.voxels);
	      parent.vbrush.drawAtGridPos(x,y,z,vdensity);
	      parent.vbrush.fillAtGridPos(x,y,z);
	  }
	  
	  void volBrushTrail(int vdensity){
	    LinearInterpolation interp = new LinearInterpolation();
	  // float sf = parent.round(parent.map(x/volume.w, 0,1,2,-2));
	     // float sf = 0;
	      for(int i = 0;i<trail.size();i++){
	        Vec3D tPt  = trail.get(i).a;
	        float f = parent.map(i,0,trail.size()-1,0,1);
	        float bSize = interp.interpolate(parent.bMin,parent.bMax+sf,f);
	        brush.setSize(bSize);
	        int[] ix = getGridPos(tPt,volume);
	        brush.drawAtGridPos(ix[0],ix[1],ix[2],vdensity);
	      }
	      
	      //subtract
	      for(int i = 0;i<trail.size();i++){
	        Vec3D tPt  = trail.get(i).a;
	        float f = parent.map(i,0,trail.size()-1,0,1);
	        float bSize = interp.interpolate(parent.bMin,parent.bMax+sf,f);
	        bSize-=(parent.bthickness);
	        if(sf<0)bSize-=1;
	        brush.setSize(bSize);
	        int[] ix = getGridPos(tPt, volume);
	        brush.drawAtGridPos(ix[0],ix[1],ix[2],0);
	      }
	      //fill
	      for(int i = 0;i<trail.size();i++){
	        Vec3D tPt  = trail.get(i).a;
	        float f = parent.map(i,0,trail.size()-1,0,1);
	        float bSize = interp.interpolate(parent.bMin,parent.bMax+sf,f);
	        brush.setSize(bSize);
	        int[] ix = getGridPos(tPt,volume);
	        brush.fillAtGridPos(ix[0],ix[1],ix[2]);
	      }
	      
	  }
	  
	  public void setScale(int s){
		  sf = s;
		  
	  }

}
