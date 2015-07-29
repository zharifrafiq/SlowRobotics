package core;

import java.util.ArrayList;
import java.util.HashMap;

import pointCloudTools.Plane3DOctree;
import processing.core.PApplet;
import toxi.geom.AABB;
import toxi.geom.Vec3D;
import toxi.geom.mesh.TriangleMesh;

/*------------------------------------

Class containing agents, data structures and maps.
Contains functions for adding to, initialising and
saving the environment

------------------------------------*/

public class Environment {


	HashMap<String, DataMap> maps = new HashMap<String, DataMap>();
	// TODO sort out including pins in a map
	//MultiValueMap<Plane3D, Link> links = new MultiValueMap<Plane3D, Link>();
	public Plane3DOctree pts;
	public ArrayList<Agent> pop;
	public ArrayList<Agent> removeAgents;
	ArrayList<Agent> addAgents;
	PApplet parent;
	float bounds;

	public Environment(PApplet _parent, float _bounds) {
		parent = _parent;
		bounds = _bounds;
		pop = new ArrayList<Agent>();
		removeAgents = new ArrayList<Agent>();
		addAgents = new ArrayList<Agent>();
		pts = new Plane3DOctree(new Vec3D(-bounds,-bounds,-bounds), bounds*2,parent);
	}

	//-------------------------------------------------------------------------------------

	//Functions for updating the environment

	//-------------------------------------------------------------------------------------

	public void run() {
		for (Agent a: pop)a.run(this);
		updateEnvironment();
	}

	public void updateEnvironment(){
		//delete any dead Agents
		for (Agent a:addAgents){
			pop.add(a);
		}
		for (Agent a: removeAgents){
			pop.remove(a); 
		}
		addAgents = new ArrayList<Agent>();
		removeAgents = new ArrayList<Agent>();
		pts = new Plane3DOctree(new Vec3D(-bounds,-bounds,-bounds), bounds*2,parent);
		for(Agent a:pop){
			pts.addPoint(a);
			/*
			for (Link l:a.trail){
				pts.add(l.a);
				links.put(l.a, l);
				//links.put(l.b, l);
			}*/
		}
	}
	
	public void addMesh(TriangleMesh m){
		//mesh.addMesh(m); 
	}

	public void insert(Plane3D j){
		pts.addPoint(j);
	}

	//-------------------------------------------------------------------------------------

	//Functions for reading the environment

	//-------------------------------------------------------------------------------------

	public DataMap getMap (String mapName) {
		return maps.get(mapName);
	}

	public boolean containsPts (Vec3D p, float e){
		Vec3D boxPos = p.sub(new Vec3D(e,e,e).scaleSelf((float) 0.5));
		AABB b = new AABB(boxPos,e);
		ArrayList inBox = pts.getPointsWithinBox(b);
		if (inBox ==null) return false;
		return true;
	}
	
	public ArrayList getWithinSphere(Vec3D p, float rad){
		return pts.getPointsWithinSphere(p,rad);
	}

	//-------------------------------------------------------------------------------------

	//Functions for creating DataMaps

	//-------------------------------------------------------------------------------------

	public void addDataMap(String name, String loc) {
		maps.put(name, new DataMap(loc,parent));
	}

	public void addNewDataMap(String name, int w, int h) {
		maps.put(name, new DataMap(w, h, parent.color(255, 255,255),name,parent));
	}

	//-------------------------------------------------------------------------------------

	//Functions for creating and removing Agents

	//-------------------------------------------------------------------------------------
	
	public void addAgent(Agent a){
		addAgents.add(a);
	}
	
	public void addAgent(Plane3D loc){
		Agent a = new Agent(loc,false);
		addAgent(a);
	}
	
	public void remove(Agent a){
		removeAgents.add(a);
	}
	
	public void removeAll(){
		removeAgents.addAll(pop);
	}
	//-------------------------------------------------------------------------------------

	//Functions for importing and exporting

	//-------------------------------------------------------------------------------------
	
	public void importPlanes(String name, boolean fixed){
		String[] txtLines = parent.loadStrings(name);
		for (int i = 0; i < txtLines.length; i++) {

			String[] pts = parent.split(txtLines[i], '/');     
			String[] sPt = parent.split(pts[0], ',');
			Vec3D o = new Vec3D(Float.valueOf(sPt[0]), Float.valueOf(sPt[1]), Float.valueOf(sPt[2]));
			String[] ePt = parent.split(pts[1], ',');
			Vec3D b = new Vec3D(Float.valueOf(ePt[0]), Float.valueOf(ePt[1]), Float.valueOf(ePt[2])).normalize(); 
			String[] nPt = parent.split(pts[2], ',');
			Vec3D n = new Vec3D(Float.valueOf(nPt[0]), Float.valueOf(nPt[1]), Float.valueOf(nPt[2])).normalize();
			Agent a = new Agent(new Plane3D(o,b,n),fixed);
			pop.add(a);
		}
	}
	
	public void importLinkedPlanes(String name){
		String[] txtLines = parent.loadStrings(name);
		
		for (int i = 0; i <txtLines.length; i+=1) {

			
			String[] txtPlanes = parent.split(txtLines[i], '|');  //get list of planes 
			String[] fixedFirst = parent.split(txtPlanes[0], '&');  //get whether first is fixed
			
			boolean fixed = (Float.valueOf(fixedFirst[1])>0)?false:true;
			
			String[] sVecs = parent.split(fixedFirst[0], '/'); //get vectors of first plane
			
			String[] sPt = parent.split(sVecs[0], ','); //get first vector
			Vec3D o = new Vec3D(Float.valueOf(sPt[0]), Float.valueOf(sPt[1]), Float.valueOf(sPt[2]));
			String[] sXX = parent.split(sVecs[1], ',');
			Vec3D x = new Vec3D(Float.valueOf(sXX[0]), Float.valueOf(sXX[1]), Float.valueOf(sXX[2])).normalize(); 
			String[] sYY = parent.split(sVecs[2], ',');
			Vec3D y = new Vec3D(Float.valueOf(sYY[0]), Float.valueOf(sYY[1]), Float.valueOf(sYY[2])).normalize();
			
			Agent newPt = new Agent(o,x,y,fixed);
			
			for (int j = 1; j<txtPlanes.length;j++){ 
				String[] fixedTmp = parent.split(txtPlanes[j], '&');  //get whether current
				boolean f = (Float.valueOf(fixedTmp[1])>0)?false:true;
				System.out.println(f);
				String[] tVecs = parent.split(fixedTmp[0], '/'); //get vectors of first plane
				
				
				String[] tPt = parent.split(tVecs[0], ','); //get first vector
				Vec3D newOrigin = new Vec3D(Float.valueOf(tPt[0]), Float.valueOf(tPt[1]), Float.valueOf(tPt[2]));
				String[] tXX = parent.split(tVecs[1], ',');
				Vec3D newXX = new Vec3D(Float.valueOf(tXX[0]), Float.valueOf(tXX[1]), Float.valueOf(tXX[2])).normalize(); 
				String[] tYY = parent.split(tVecs[2], ',');
				Vec3D newYY = new Vec3D(Float.valueOf(tYY[0]), Float.valueOf(tYY[1]), Float.valueOf(tYY[2])).normalize();
				Plane3D plane = new Plane3D(newOrigin,newXX,newYY);
				if(f)plane.lock();
				newPt.addToTrail(plane);
			}
			
			//set link angles
		//	newPt.initLinkAngles();
			addAgent(newPt);

		}

	}
	
	void saveComponents(){
		ArrayList<String>lineList = new ArrayList<String>();
		for (Agent l: pop) {
			lineList.add(l.x +"," + l.y + "," + l.z +"/" + (l.xx.x) +"," + (l.xx.y) + "," + (l.xx.z)+"+"+l.x +"," + l.y + "," + l.z +"/" + (l.yy.x) +"," + (l.yy.y) + "," + (l.yy.z));
		}
		String[] skin = new String[lineList.size()];
		for (int i =0;i<lineList.size()-1;i++) {
			skin[i]=lineList.get(i);
		}
		parent.saveStrings("comps.txt", skin);
	}

	void saveTrails(){
		ArrayList<String>lineList = new ArrayList<String>();
		for (Agent a: pop) {
			if(a.trail.size()>4){
				String c = "";
				for(int i = 0; i<a.trail.size();i++){
					Link l = a.trail.get(i);
					c = c+l.a.x +"," + l.a.y + "," + l.a.z +"/";
					if(i == a.trail.size()-1){
						c = c+l.b.x +"," + l.b.y + "," + l.b.z +"/";
					}
				}
				lineList.add(c);
			}
		}
		String[] skin = new String[lineList.size()];
		for (int i =0;i<lineList.size()-1;i++) {
			skin[i]=lineList.get(i);
		}
		parent.saveStrings("trails.txt", skin);
	}
	
	void saveMeshes(){
		//mesh.saveAsSTL(sketchPath("allMeshes.stl"));
	}
	
	public void saveTrailPlanes(){
		ArrayList<String>lineList = new ArrayList<String>();
		for (Agent a: pop) {
			if(a.trail.size()>4){
				String c = "";
				for(Link l:a.trail){
					c = c+l.a.x +"," + l.a.y + "," + l.a.z +"+" 
				        + l.a.xx.x +"," + (l.a.xx.y) + "," + (l.a.xx.z)+"+"+
							(l.a.yy.x) +"," + (l.a.yy.y) + "," + (l.a.yy.z)+"/";
				}
				lineList.add(c);
			}
		}
		String[] skin = new String[lineList.size()];
		for (int i =0;i<lineList.size()-1;i++) {
			skin[i]=lineList.get(i);
		}
		parent.saveStrings("trails.txt", skin);
	}

	void saveMaps(){
		DataMap m = maps.get("ground");
		m.saveImage();
	}

	//-------------------------------------------------------------------------------------

	//Functions for rendering and saving the environment

	//-------------------------------------------------------------------------------------
	
	public void render(){

	}

	
}