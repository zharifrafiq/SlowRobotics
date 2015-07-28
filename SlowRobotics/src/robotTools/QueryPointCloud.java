package robotTools;

import pointCloudTools.PathFinder;
import pointCloudTools.PointCloud;
import processing.core.PImage;
import toxi.geom.Vec3D;

public class QueryPointCloud extends RobotTask{

	PointCloud pcl;
	String type;
	
	public QueryPointCloud(PointCloud _pcl, String _type, Robot _robot) {
		super(_robot);
		pcl = _pcl;
		type = _type;
		
	}
	
	@Override
	public void run(){
		if(!running){
			switch (type){
			case "high": getHighestPoint();
			break;
			case "close": getClosestPoint();
			break;
			case "low": getLowPoint();
			break;
			}
		}
		running = true;
		if(robot.getDistToTarget()<5)end();
	}
	
	 void getHighestPoint(){
		 Vec3D in = new Vec3D(0,0,-1000);
		Vec3D pt = PathFinder.getHighestPoint(in,pcl.getPoints());
		pt.addSelf(new Vec3D(0,0,30));
		if(in.distanceTo(pt)>1){
			robot.setTarget(pt);
		}else{
			end();
		}
	}
	 
	
	 void getClosestPoint(){
		Vec3D pt = PathFinder.getClosestPoint(robot, pcl.getPoints());
		pt.addSelf(new Vec3D(0,0,100));
		robot.setTarget(pt);
	}
	 
	 void getLowPoint(){
		 Vec3D in = new Vec3D(0,0,2000);
			Vec3D pt = PathFinder.getLowestPoint(in,pcl.getPoints());
			if(pt.z<5)pt.set(pt.x,pt.y,5); //limit
			pt.addSelf(new Vec3D(0,0,200));
			if(in.distanceTo(pt)>1){
				robot.setTarget(pt);
			}else{
				end();
			}
		}
	 
}
