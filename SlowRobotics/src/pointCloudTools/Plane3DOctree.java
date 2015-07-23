package pointCloudTools;
import processing.core.PApplet;
import toxi.geom.PointOctree;
import toxi.geom.Vec3D;


public class Plane3DOctree extends PointOctree{
	  PApplet parent;
	  
	  public Plane3DOctree(Vec3D o, float d, PApplet _parent) {
	    super(o,d);
	    parent = _parent;
	  }

	  void draw() {
	    drawNode(this);
	  }

	  void drawNode(PointOctree n) {
	    if (n.getNumChildren() > 0) {
	    	parent.noFill();
	    	parent.stroke(n.getDepth(), 20);
	    	parent.pushMatrix(); 
	    	parent.translate(n.x, n.y, n.z);
	    	parent.box(n.getNodeSize());
	    	parent.popMatrix();
	      PointOctree[] childNodes=n.getChildren();
	      for (int i = 0; i < 8; i++) {
	        if(childNodes[i] != null) drawNode(childNodes[i]); 
	      }
	    }
	  }
}
