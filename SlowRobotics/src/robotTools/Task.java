package robotTools;

public interface Task{
	
	public boolean finished();
	
	public void run();
	
	public void end();
	
	public int getPriority();
}
