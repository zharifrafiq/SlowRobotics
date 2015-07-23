package voxelTools;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javolution.io.Union;
import ProGAL.proteins.PDBFile.ParentRecord;

import com.vividsolutions.jts.awt.PointShapeFactory.X;

import controlP5.Println;
import processing.core.PApplet;
import processing.core.PImage;
import toxi.geom.Vec3D;

/*------------------------------------

 Class containing a 3d array of bytes and
 functions for searching, reading and writing 
 to this array

 ------------------------------------*/

public class VoxelGrid {
	Cell vals[];      // The array of bytes containing the pixels.
	public int w, h, d;
	public Vec3D s;
	
	public VoxelGrid(int _w, int _h, int _d, Vec3D _s) {
		w = _w;
		h = _h;
		d = _d;
		initGrid();
		s = _s;
	}
	
	public VoxelGrid(VoxelGrid v){
		w = v.w;
		h = v.h;
		d = v.d;
		s = v.s.copy();
		initGrid();
		booleanGrid(v, "union");
	}

	//-------------------------------------------------------------------------------------

	//Functions for initialisation

	//-------------------------------------------------------------------------------------

	void initGrid() {
		vals = new Cell[w*h*d];
		for (int i = 0; i<w; i++) {
			for (int j = 0; j<h; j++) {
				for (int k = 0; k<d; k++) {
					int index = i + w * (j + h * k);
					boolean edge = false;
					if(i==0 || i==w-1 || j==0 || j==h-1 || k==0 || k== d-1)edge = true;
					vals[index] = new Cell(0, edge);
				}
			}
		}
	}

	void createTerrain(PImage loadedImage) {
		loadedImage.resize(w, h);
		for (int i = 0; i<w; i++) {
			for (int j = 0; j<h; j++) {
				int v = (int) (((loadedImage.pixels[(j*w)+i]>> 16) & 0xFF)*d/255);
				for (int k=0; k<v; k++) {
					int index = i + w * (j + h * k);
					vals[index].set(((1-((float)k/v))*120+80));
				}
			}
		}
	}
	

	//-------------------------------------------------------------------------------------

	//Functions for writing to the voxel array

	//-------------------------------------------------------------------------------------
	
	public void booleanGrid (VoxelGrid vol, String type){
		for (int i = 0; i<w; i++) {
			for (int j = 0; j<h; j++) {
				for (int k = 0; k<d; k++) {
					int index = i + w * (j + h * k);
					Cell c = vol.get(i,j,k);
					float cv = c.get();
					if(cv>=0){
						float v = vals[index].get();
						if(type == "subtract"){
							vals[index].set(constrain(v-cv,0,255));	
						}else if(type == "union"){
							vals[index].set(constrain(v+cv,0,255));	
						}
					}
				}
			}
		}
	}

	public void createCube(int _x, int _y, int _z, int l, int v) {
		int xmin = (int) constrain(_x, 0, w);
		int ymin = (int) constrain(_y, 0, h);
		int zmin = (int) constrain(_z, 0, d);
		int xmax = (int) constrain(_x+l, 0, w);
		int ymax = (int) constrain(_y+l, 0, h);
		int zmax = (int) constrain(_z+l, 0, d);

		for (int i = xmin; i<xmax; i++) {
			for (int j = ymin; j<ymax; j++) {
				for (int k = zmin; k<zmax; k++) {
					int index = i + w * (j + h * k);
					vals[index].set(v);
				}
			}
		}
	}
	
	private float constrain(float v, float min, float max){
		if(v<min){
			return min;
		}else if(v>max){
			return max;
		}
		else return v;
	}
	
	public void createNoise(float ns, PApplet parent) {
		for (int i = 0; i<w; i++) {
			for (int j = 0; j<h; j++) {
				for (int k =0; k <d; k++){
					int index = i + w * (j + h * k);
					int val = (int) ((parent.noise(i*ns, j*ns, k*ns))*255);
					vals[index].set(val);
				}
			}
		}
	}

	public void setValue (int x, int y, int z, float val) {
		int index = x + w * (y + h * z);
		vals[index].set(val);
	}

	public void setVoxelAt(int index, int val){
		vals[index].set(val);
	}
	
	public void setScale(Vec3D scale){
		s = scale.copy();
	}
	
	public void normalizeScale(){
		s.normalize();
	}

	//-------------------------------------------------------------------------------------

	//Functions for modifying the voxel array

	//-------------------------------------------------------------------------------------

	public void blurall() {
		for (int z=0; z<d; z+=1) {
			for (int y=0; y<h; y+=1) {
				for (int x=0; x<w; x+=1) {
					blur2(x, y, z);
				}
			}
		}
	}

	public void blur2 (int x, int y, int z) {
		if ((x > 1) && (x < w-2) &&
				(y > 1) && (y < h-2) &&
				(z > 1) && (z < d-2)) {
			float sum = 0;
			for (int k=-1; k<=1; k++) {
				for (int j=-1; j<=1; j++) {
					for (int i=-1; i<=1; i++) {
						int index = (i+x) + w * ((j+y) + h * (k+z));
						float val=vals[index].get();
						int scalar = 1;
						if (k==0) {
							if (j*i==0) {
								scalar =2;
							}
							if (j==0 && i==0) {
								scalar = 4;
							}
						} else if (j==0 && i==0) {
							scalar=2;
						}

						sum+=(val*scalar);
					}
				}
			}
			int weightedAverage = (int) (sum/36);
			vals[x+w*(y+h*z)].set(weightedAverage);
		}
	}

	//-------------------------------------------------------------------------------------

	//Functions for reading the voxel array

	//-------------------------------------------------------------------------------------

	public Vec3D getVectorToBest(Vec3D p, int rad,int target){
		return getBest((int)(p.x/s.x), (int)(p.y/s.y), (int)(p.z/s.z),rad, 2*(float)Math.PI, new Vec3D(0,0,1),target);
	}

	public  Vec3D getVectorToBestInAngle(Vec3D p, int rad, float angle, Vec3D dir, float target){
		return getBest((int)(p.x/s.x), (int)(p.y/s.y), (int)(p.z/s.z),rad,angle, dir, target);

	}

	public Vec3D findValueInRadius(Vec3D p, int rad, float min, float max){
		return findValueInRadius((int)(p.x/s.x), (int)(p.y/s.y), (int)(p.z/s.z),rad, min,max);
	}

	public  Vec3D findValueInRadius(int _x, int _y, int _z, int rad, float min, float max){
		int x = (int) constrain(_x, 0, w-1);
		int y = (int) constrain(_y, 0, h-1);
		int z = (int) constrain(_z, 0, d-1);
		Vec3D free = new Vec3D();
		ArrayList<Integer>ii = getShuffled(-rad,rad);
		ArrayList<Integer>jj = getShuffled(-rad,rad);
		ArrayList<Integer>kk = getShuffled(-rad,rad);
		for (int i:ii) {
			for (int j:jj) {
				for (int k:kk) {
					int ix = i+x;
					int jy = j+y;
					int kz = k+z;
					if(ix>=0 && jy>=0 && kz>=0 && ix<w && jy<h && kz<d){
						int index = ix + w * (jy + h * kz);
						float v = vals[index].get();
						if(v>=min&&v<=max){
							return new Vec3D(i,j,k);
						}
					}
				}
			}
		}
		return free;
	}

	public  Vec3D getBest(int _x, int _y, int _z, int rad, float angle, Vec3D dir, float target){
		int x = (int) constrain(_x, 0, w-1);
		int y = (int) constrain(_y, 0, h-1);
		int z = (int) constrain(_z, 0, d-1);
		Vec3D toBest = new Vec3D();
		float best = 1000;
		ArrayList<Integer>ii = getShuffled(-rad,rad);
		ArrayList<Integer>jj = getShuffled(-rad,rad);
		ArrayList<Integer>kk = getShuffled(-rad,rad);
		for (int i:ii) {
			for (int j:jj) {
				for (int k:kk) {
					Vec3D toVoxel = new Vec3D(i,j,k);
					float a = toVoxel.angleBetween(dir,true);
					if(a<angle){
						int ix = i+x;
						int jy = j+y;
						int kz = k+z;
						if(ix>=0 && jy>=0 && kz>=0 && ix<w && jy<h && kz<d){
							int index = ix + w * (jy + h * kz);
							if(vals[index].io!=false){
								float val=vals[index].get();
								if(Math.abs(target-val)<best){
									best = Math.abs(target-val);
									vals[index].io=false;
									toBest = new Vec3D((int) constrain(i,-1,1),(int) constrain(j,-1,1),(int) constrain(k,-1,1));
									//toBest = toVoxel.copy().normalizeTo(s);
								}
							}
						}
					}
				}
			}
		}
		//toBest.normalizeTo(s);
		return toBest;
	}

	ArrayList<Integer> getShuffled(int min, int max){
		List<Integer> l = new ArrayList<Integer>();
		for(int i = min;i<=max;i++){
			l.add(i);
		}
		Collections.shuffle(l);
		return (ArrayList<Integer>) l;
	}

	public  Vec3D getNormal(int _x, int _y, int _z, float v, int rad, float jitter) {
		int x = (int) constrain(_x, 0, w-1);
		int y = (int) constrain(_y, 0, h-1);
		int z = (int) constrain(_z, 0, d-1);
		Vec3D from = new Vec3D();
		for (int i=-rad; i<=rad; i++) {
			for (int j=-rad; j<=rad; j++) {
				for (int k=-rad; k<=rad; k++) {
					int ix = i+x;
					int jy = j+y;
					int kz = k+z;
					if(ix>=0 && jy>=0 && kz>=0 && ix<w && jy<h && kz<d){
						int index = ix + w * (jy + h * kz);
						float val=vals[index].get();
						float diff = v-val;
						if(diff!=0)from.addSelf(new Vec3D(i,j,k).add(Vec3D.randomVector().scale(jitter)).scale(diff));
					}
				}
			}
		}
		return from;
	}
	
	public Vec3D repelFromValue(Vec3D p, float v, int rad,float jitter){
		return getNormal((int)(p.x/s.x), (int)(p.y/s.y), (int)(p.z/s.z),v,rad,jitter);
	}

	public int getIndexFor(int _x, int _y, int _z) {
		int x = (int) constrain(_x, 0, w-1);
		int y = (int) constrain(_y, 0, h-1);
		int z = (int) constrain(_z, 0, d-1);
		int index = x + w * (y + h * z);
		return index;
	}

	public Cell get(int x, int y, int z) throws IndexOutOfBoundsException{
		try {
			if(x>w || x<0 || y<0 || y>h || z<0 || z>d){
				throw new IndexOutOfBoundsException();
			}
			int index = x + w * (y + h * z);
			Cell val = vals[index];
			return val;
		} catch (Exception e) {
			return new Cell(-1,true);
		}


	}

	public float getValue(int x, int y, int z){
		return get(x,y,z).val;
	}
	
	public float getValue(Vec3D p){
		return getValue((int)(p.x/s.x), (int)(p.y/s.y), (int)(p.z/s.z));
	}

	public  Cell get(Vec3D p){
		return get((int)(p.x/s.x), (int)(p.y/s.y), (int)(p.z/s.z));
	}

	public  int getW(){
		return w;
	}

	public int getH(){
		return h;
	}

	public int getD(){
		return d;
	}

	//-------------------------------------------------------------------------------------

	//Functions for drawing

	//-------------------------------------------------------------------------------------

	public void render(int res, int threshold, float sf, PApplet parent) {
		int index = 0;
		parent.strokeWeight(1);
		for (int z=0; z<d; z+=res) {
			for (int y=0; y<h; y+=res) {
				for (int x=0; x<w; x+=res) {
					index = x + w * (y + h * z);
					float val = vals[index].get()*sf;
					if (val>threshold) {
						parent.stroke(val);
						parent.point(x*s.x,y*s.y,z*s.z);
						//Vec3D normal = getNormal(x, y, z, 2);
						//parent.line(x*s,y*s,z*s,x*s+normal.x*5,y*s+normal.y*5,z*s+normal.z*5);
					}
				}
			}
		}
	}
	
	public void save(String fn) {
		  int c=0;
		  int tot = vals.length;
		  try {
		    BufferedOutputStream ds = new BufferedOutputStream(new FileOutputStream(fn));
		    // ds.writeInt(volumeData.length);
		    for (Cell e:vals) {
		    	if(!e.edge){
		    		ds.write((int) e.get());
		    	}else{
		    		ds.write((int) 0);
		    	}
		    }
		    ds.flush();
		    ds.close();
		  } 
		  catch (IOException e) {
		    e.printStackTrace();
		  }
		}

}
