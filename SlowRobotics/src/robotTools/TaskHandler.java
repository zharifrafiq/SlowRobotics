package robotTools;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;

import pointCloudTools.PointCloud;
import taxonomy.DabAgent;
import toxi.geom.Vec3D;


public class TaskHandler {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ArrayList<Task> taskList;
	Task currentTask;
	int index;
	boolean loop;
	boolean running;
	int priority;
	
	public TaskHandler(boolean _loop){
		taskList = new ArrayList<Task>();
		index = 0;
		loop = _loop;
		running = false;
	}
	
	public boolean finished(){
		return !running;
	}
	
	public void run(){
		if(hasTasks()){
			currentTask = taskList.get(index);
			currentTask.run();
			if(currentTask.finished()){
				System.out.println("task finished, starting task #"+(index+2));
				increment();
			}
		}
	}
	
	public boolean hasTasks(){
		if((!loop && index>=taskList.size()) || (taskList.size()==0) ){
			end();
			return false;
		}
		running = true;
		return true;
	}
	
	public void end(){
		running = false;
		index = 0; //reset to first task incase the handler is called again
	}
	
	public void addTask(Task task){
		taskList.add(task);
	}
	
	public void increment(){
		index+=1;
		if(loop && index==taskList.size())index = 0;
	}
	
	public Task get(){
		return taskList.get(index);
	}
	
	public boolean completedCurrent(){
		return currentTask.finished();
	}
	
	/*
	 * 
	 * TODO cleanup
	 * 
	public static TaskHandler followPath(Robot _robot){
		TaskHandler pathFollower = new TaskHandler(false);
		for (int i=0;i<5;i++){
			Vec3D pt = new Vec3D(i*100,0,0);
			pathFollower.addTask(new GoToPoint(pt,_robot));
		}
		return pathFollower;
	}
	
	public static TaskHandler followBeziers(Robot _robot){
		TaskHandler pathFollower = new TaskHandler(false);
		for (int i=0;i<4;i++){
			Vec3D pt = new Vec3D((float) Math.sin(Math.PI*2/i)*100,(float) Math.cos(Math.PI*2/i)*100,0);
			Vec3D nextPt = new Vec3D((float) Math.sin(Math.PI*2/(i+1))*100,(float) Math.cos(Math.PI*2/(i+1))*100,0);
			pathFollower.addTask(new BezierThroughPoint(pt,nextPt,0.5f,_robot));
		}
		return pathFollower;
	}
	
	public static TaskHandler coverSurface(Robot _robot, RobotWorkspace _ws, PointCloud _pcl){
		TaskHandler coverer = new TaskHandler(true);
		coverer.addTask(new GoToPoint(_robot.homePos, _robot));
		coverer.addTask(new MaskedScan(212, 214, 204, 10, _ws, _robot));
		coverer.addTask(new QueryPointCloud(_pcl, "low", _robot)); //dabber finishes task once surface is covered
		coverer.addTask(new OffsetCurrentPosition(new Vec3D(0,0,200), _robot));
		coverer.addTask(new DabAgent(_pcl, _ws, _robot));
		
		return coverer;
	}
	*/
	
}
