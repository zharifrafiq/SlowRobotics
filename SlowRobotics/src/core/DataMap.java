package core;
import java.awt.Color;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import toxi.geom.Vec3D;
import toxi.geom.mesh.Face;
import toxi.geom.mesh.TriangleMesh;


public class DataMap {

	  PGraphics map; // image to store
	  PApplet parent;
	  int w, h;
	  String name;

	  DataMap(String toImg, PApplet _parent) {
	    //map=loadImage(toImg); //load the source image
		  parent = _parent;
	    w = map.width;
	    h = map.height;
	    name = toImg;
	  }
	  
	  DataMap(int _w, int _h, int _c, String _name, PApplet _parent){
		  parent = _parent;
	    map = parent.createGraphics(_w,_h);
	    fillMap(_c);
	    w = _w;
	    h = _h;
	    name = _name;
	  }

	  //-------------------------------------------------------------------------------------

	  //Functions for writing to the map

	  //-------------------------------------------------------------------------------------
	  
	  public void fillMap(int c){
	    map.beginDraw();
	    map.loadPixels();
	    for (int i = 0; i < map.pixels.length; i++) {
	      map.pixels[i] = c; 
	    }
	    map.updatePixels();
	    map.endDraw();
	  }
	  
	  public void fillNoise(float ns){
	    map.beginDraw();
	    map.loadPixels();
	    for (int i = 0; i < w;i++) {
	      for (int j = 0; j < h;j++) {
	        int nv = (int) (parent.noise(i*ns,j*ns)*255);
	        map.pixels[(j*w)+i] = parent.color(nv,nv,nv); 
	      }
	    }
	    map.updatePixels();
	    map.endDraw();
	  }
	  
	  public void drawMesh2D(TriangleMesh mesh, Vec3D o, float b){
	    map.loadPixels();
	    map.fill(b,20);
	    map.stroke(b,20);
	    map.strokeWeight(1);
	    map.strokeJoin(parent.ROUND);
	    map.beginDraw();

	    map.beginShape(parent.TRIANGLES);
	    for (Face f:mesh.faces){
	     map.vertex(f.a.x+o.x,f.a.y+o.y);
	     map.vertex(f.b.x+o.x,f.b.y+o.y);
	     map.vertex(f.c.x+o.x,f.c.y+o.y);
	    }
	    map.endShape();
	    map.endDraw();
	    map.updatePixels();
	  }
	  
	  public void addToCell(int x,int y, float v){
	    x = parent.constrain(x,0,w-1);
	    y = parent.constrain(y,0,h-1);
	    int c =map.pixels[(y*w)+x];
	    float bright = parent.brightness(c);
	    map.loadPixels();
	    map.pixels[(y*w)+x]=(int)parent.constrain(bright+v,0,255);
	    map.updatePixels();
	  }
	  
	  public void fade(float rate){
	    map.loadPixels();
	    for (int i = 0; i < map.pixels.length; i++) {
	      int c = map.pixels[i];
	      map.pixels[i] = parent.color(parent.red(c)*rate,parent.blue(c)*rate,parent.green(c)*rate);
	    }
	    map.updatePixels();
	  }

	  //-------------------------------------------------------------------------------------

	  //Functions for getting information from the map

	  //-------------------------------------------------------------------------------------
	  
	  public float getVal(int x,int y){
	    float b = 0;
	    if(x<w-1 && x>0 && y>0 && y<h-1){
	      b = parent.brightness(map.pixels[(y*w)+x]);
	    }
	    return b;
	  }
	  
	  //get a value from the image 
	  public Vec3D getVec(int x, int y) {
	    int nx = parent.constrain(x, 0, w-1);
	    int ny = parent.constrain(y, 0, h-1);
	    if(nx==x && ny==y){
	      int c =map.pixels[(ny*w)+nx];
	      float vx = parent.map(parent.red(c),0,255,-1,1);
	      float vy = parent.map(parent.green(c),0,255,1,-1);
	      Vec3D r = new Vec3D(vx, vy, 0);
	      r.normalizeTo(1);
	      r.invert();
	      return r;
	    }
	    else return null;
	  }
	  
	  public Vec3D getCellAboveThreshold(float t){
	    int c = 1000;
	    Vec3D r = new Vec3D();
	    while(c>0){
	      c--;
	      r = new Vec3D(parent.random(0, w), parent.random(0, h), 0);
	      float val = getVal(PApplet.parseInt(r.x),PApplet.parseInt(r.y));
	      if(val<t) return r;
	    }
	    return(r);
	  }
	  
	  public PImage getImage(){
	   return map; 
	  }
	  
	  public int getWidth(){
	    return w;
	  }
	  
	  public int getHeight(){
	    return h;
	  }
	  //-------------------------------------------------------------------------------------

	  //Rendering

	  //-------------------------------------------------------------------------------------
	  public void drawVectorImage(int r) {
		  parent.strokeWeight(2);
	    for (int i = 0;i<w;i+=r) {
	      for (int j = 0;j<h;j+=r) {
	        int c = map.pixels[(j*w)+i];
	          if(parent.brightness(c)>5){
	        	  parent.stroke(c);
	          float x = parent.map(parent.red(c),0,255,-1,1);
	          float y = parent.map(parent.green(c),0,255,1,-1);
	          Vec3D v = new Vec3D(x, y, 0);
	          v.normalizeTo(r);
	          //v.rotateZ(PI/2);
	          parent.line(i, j,-30, i+(v.x), j+(v.y),-30);
	        }
	      }
	    }
	  }
	  
	  public void drawImage(){
		  parent.noFill();
		  parent.rect(0,0,w,h);
		  parent.pushMatrix();
		  parent.translate(-w,-h,-100);
		  parent.image(map,w,h);
		  parent.popMatrix();
	  }
	  
	  public void saveImage(){
	   map.save("ground.png");
	  }
}
