package voxelTools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ProGAL.geom3d.volumes.Volume;
import processing.core.PApplet;
import toxi.geom.Vec3D;
import toxi.geom.mesh.Face;
import toxi.geom.mesh.TriangleMesh;
import wblut.geom.WB_AABB;
import wblut.geom.WB_AABB3D;
import wblut.geom.WB_AABBTree;
import wblut.geom.WB_Point3d;
import wblut.geom.WB_Ray;
import wblut.geom.WB_Vector3d;
import wblut.hemesh.HEC_Creator;
import wblut.hemesh.HEC_FromFacelist;
import wblut.hemesh.HE_Intersection;
import wblut.hemesh.HE_Mesh;

public class MeshVoxeliser {

	protected VoxelGrid volume;
	protected int wallThickness = 0;
	protected TriangleMesh triMesh;
	protected HE_Mesh mesh;

	
	public MeshVoxeliser(int res, Vec3D s, TriangleMesh _triMesh, VoxelGrid _v) {
		this(res, res, res, s, _triMesh, _v);
	}

	public MeshVoxeliser(int resX, int resY, int resZ, Vec3D s, TriangleMesh _triMesh, VoxelGrid _v) {
		volume = _v;
		triMesh = _triMesh;
		mesh = convertToHe_Mesh(triMesh);
		
	}

	/**
	 * @return the volume
	 */
	 public VoxelGrid getVolume() {
		 return volume;
	 }
	 
	 public HE_Mesh getMesh(){
		 return mesh;
	 }
	 
	 private HE_Mesh convertToHe_Mesh(TriangleMesh m){
		  
		  //set the vertices
		  int numVerts = m.getVertices().size();
		  float[][] vertices=new float[numVerts][3];
		  System.out.println("Reading Mesh");
		  
		  for (int i=0;i<numVerts;i++) {
		    Vec3D v = m.getVertexForID(i);
		    vertices[i][0]=v.x;
		    vertices[i][1]=v.y;
		    vertices[i][2]=v.z;
		  }

		  //set the faces
		  int numFaces = m.getFaces().size();
		  int[][] faces = new int[numFaces][];
		  int c=0;
		  for (Face f : m.getFaces()) {
		    faces[c]=new int[3];
		    //add the vertices
		    faces[c][0] = f.a.id;
		    faces[c][1] = f.b.id;
		    faces[c][2] = f.c.id;
		    c++;
		  }
		  
		  System.out.println("Beginning conversion");
		  HEC_Creator creator=new HEC_FromFacelist().setVertices(vertices).setFaces(faces);
		  HE_Mesh tmp=  new HE_Mesh(creator);
		  System.out.println("Converted Mesh");
		  return tmp;
		  
		}

	 public boolean voxelizeMesh(HE_Mesh mesh, float iso, PApplet parent) {
		 //setup intersector
		 HE_Intersection intersect = new  HE_Intersection();
		 System.out.println("beginning voxelising");
		 //rescale to voxelspace
		 /*
		 WB_AABB3D box = mesh.getAABB();
		 WB_Point3d vScale = box.getMin();
		// vScale = vScale.sub(box.getMax());
		 //volume.setScale(new Vec3D((float)vScale.x, (float)vScale.y, (float)vScale.z));
		 //scale the mesh
		 WB_Point3d minP = new WB_Point3d(1, 1, 1);
		 WB_Point3d maxP = new WB_Point3d(volume.w-1, volume.h-1, volume.d-1);
		 mesh.fitInAABB(new WB_AABB3D(minP, maxP));
		 */
		 //ray direction
		 //NOTE  - RAY needs to enter and exit solid geom in order for fill to work
		 WB_Vector3d  v1 = new WB_Vector3d(volume.w*2, 0, 0);
		 WB_AABBTree mTree = new WB_AABBTree(mesh,100);
		 System.out.println("made AABB");

		 for (int z = 1; z<volume.d; z++) {
			 parent.println(((float)z/volume.d)*100);
			 for (int y = 1; y<volume.h; y++) {
				 WB_Point3d s = new WB_Point3d(-(volume.w*2), y-1, z-1);
				 WB_Ray ray =  new WB_Ray(s,v1);
				 List<WB_Point3d> pts = intersect.getIntersection(mTree, ray);

				 //convert to interger list
				 ArrayList<Integer> xPts = new ArrayList<Integer>();

				 for (WB_Point3d p:pts) {
					 xPts.add((int)p.x);
				 }

				 //sort the list into a decent order
				 Collections.sort(xPts);

				 if (xPts.size()>1) {
					 boolean f =true;
					 for (int i = 0; i<xPts.size()-1; i+=1) {
						 
						 int current = (int) parent.constrain(xPts.get(i),0,volume.w-1);
						 int next = (int) parent.constrain(xPts.get(i+1),0,volume.w-1);
						 //add a check to coincident points
						 /*
						 if(parent.abs(next-current)<2){
							 f=false;
							// continue;
						 }*/
						 
						 
						 //alternate filling
						 if (f) {
							 for (int x = current; x<=next; x++) {
								 volume.setValue(x, y, z, iso);
							 }
						 }
						 f=!f;
						
					 }
				 }
			 }
		 }
		// volume.normalizeScale();

		 return true;
	 }

}
