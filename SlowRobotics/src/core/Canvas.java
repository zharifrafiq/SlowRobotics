package core;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.media.opengl.GL2;

import processing.core.PGraphics;
import processing.opengl.PJOGL;
import toxi.geom.Vec3D;

//TODO multithreading

public class Canvas{
	PGraphics graphics;
	
	public Canvas(PGraphics _graphics){
		graphics = _graphics;
	}
	
	public void drawPts(ArrayList pts, float rad){
		graphics.strokeWeight(rad);
		graphics.stroke(255);
		for(Vec3D p:(ArrayList<Vec3D>) pts){
			graphics.point(p.x,p.y,p.z);
		}
	}
	
	public void drawPlanes(ArrayList planes, float s){
		graphics.strokeWeight(2);
		graphics.stroke(255);
		for(Plane3D p:(ArrayList<Plane3D>)planes){
			graphics.stroke(255, 100, 100);
			graphics.line(p.x, p.y, p.z, p.x+(p.xx.x*s), p.y+(p.xx.y*s), p.z+(p.xx.z*s)); 
			graphics.stroke(100,100,255);
			graphics.line(p.x, p.y, p.z, p.x+(p.yy.x*s), p.y+(p.yy.y*s), p.z+(p.yy.z*s)); 
		}
	}
	
	public void drawPtBuffer(FloatBuffer pts){
		int numPts = pts.capacity();
		PJOGL pgl = (PJOGL)graphics.beginPGL();
		GL2 gl2 = pgl.gl.getGL2();

		gl2.glEnable( GL2.GL_BLEND );
		gl2.glEnable(GL2.GL_POINT_SMOOTH);      

		gl2.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		gl2.glVertexPointer(3, GL2.GL_FLOAT, 0, pts);

		gl2.glDrawArrays(GL2.GL_POINTS, 0, (numPts)-1);
		gl2.glDisableClientState(GL2.GL_VERTEX_ARRAY);
		gl2.glDisable(GL2.GL_BLEND);
		graphics.endPGL();
	}
}
