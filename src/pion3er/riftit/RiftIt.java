/**
 * ##library.name##
 * ##library.sentence##
 * ##library.url##
 *
 * Copyright ##copyright## ##author##
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA  02111-1307  USA
 * 
 * @author      ##author##
 * @modified    ##date##
 * @version     ##library.prettyVersion## (##library.version##)
 */

package pion3er.riftit;


import java.util.logging.Level;
import java.util.logging.Logger;

import processing.core.*;
import processing.event.KeyEvent;
import processing.opengl.PShader;
import oculusvr.input.*;


/*
 * Knonw Issues
 * 
 * Hijack modus is not working properly (messed up camera positioning / "Renderer not of type color" error on color images )
 * View is generally some kind of distorted if objects move out of sight
 * Set Shader offset automatically
 * Oculus roll param to be integrated in camera
 * Set scene background properly
 * Set nearPlane...
 * 
 */


/**
 * This class provides the rendering stuff, sensor data and HDMIInfo for and from the Oculus Rift.
 * The sensor data is used for camera positioning
 * and parent's PGRaphics is replaced with a OculusRift view on the scene.
 * It uses the shader-contributions from michael.of.05 
 * (http://forum.processing.org/one/topic/oculus-rift-shader.html)
 *
 */

public class RiftIt implements PConstants  {
	
	protected PApplet parent;
	protected OculusRiftReader oculus;
	protected PShader barrel;
	protected PGraphics fb;
	public PGraphics scene;
	protected int eye_width, eye_height;
	public int offset =50;
	
	protected boolean hijackScene = true;
	protected boolean useHeadtrackingForCamera = true;
	protected boolean renderRiftView =true;
	protected boolean useKeyNavigation = true;
	protected PVector position;
	protected PVector lookAt;
	protected float   speed =  5.0f;
	
	public final static String VERSION = "##library.prettyVersion##";
	public final float ADJUSTSPHERE = 3*PI/2;
	public final PVector STRAFE = new PVector(0,1,0);
	
	
	/**
	 *
	 *Creates a new RiftIt instance which grabs the parent PApplet
	 *and hijacks its PGraphics scene... 
	 * 
	 * @param theParent the parent PApplet
	 */
	public RiftIt(PApplet theParent) {
			this(theParent,true);
	}
	
	/**
	 *
	 *Creates a new RiftIt instance which grabs the parent PApplet
	 *and evtl. hijacks its PGraphics scene 
	 * 
	 * @param theParent the parent PApplet
	 * @param hijackScene true if RiftIt should hijack parent's PGraphics
	 */
	public RiftIt(PApplet theParent, boolean hijackScene) {
		parent = theParent;	
		this.hijackScene = hijackScene;
		parent.registerMethod("stop", this);
		parent.registerMethod("draw", this);
		parent.registerMethod("pre", this);
		parent.registerMethod("keyEvent", this);
		parent.registerMethod("dispose", this);
		welcome();
		
		//Starting with default camera position
		position = new PVector();
		position.x=parent.width/2;
		position.y=parent.height/2;
		position.z=(float) (position.y / Math.tan(PI*30.0 / 180.0));
		
		//and default look at
		lookAt = new PVector();
		lookAt.x = 0;
		lookAt.y = 0;
		lookAt.z = -1;
		
		eye_width=parent.width/2;
		eye_height=parent.height;
		
		
		// Create framebuffer
		fb = parent.createGraphics(parent.width, parent.height, P3D);
		// Create PGraphics for actual scene
		scene = parent.createGraphics(eye_width, eye_height, P3D);
	
		
		// Load fragment shader for oculus rift barrel distortion
		barrel = parent.loadShader("barrel_frag.glsl");
		
		try{
			oculus = new OculusRiftReader();
		}catch(Exception e){
			System.err.println("Oculus Rift could not be initialized");
		}
		System.out.println("RiftIt initialized");
	} 
	
	
	private void welcome() {
		System.out.println("##library.name## ##library.prettyVersion## by ##author##");
	}
	
	
	
	/**
	 * return the version of the library.
	 * 
	 * @return String
	 */
	public static String version() {
		return VERSION;
	}
	
	
	public void pre(){
      this.calculateViewingDirection();
	}
	
	
	/**
	 * Calculate viewing direction
	 */
	private void calculateViewingDirection(){
		this.oculus.update();
		
		float[] pyr = this.oculus.getRotation(); 
		
		//up=-90 down =90  
		//adjust it to coordsystem
		float phi = - ADJUSTSPHERE - pyr[0] ;
        
        //left=90 right =-90 back =+-180
        //adjust it to coordsystem
		float theta = ADJUSTSPHERE - pyr[1];
        
        //leftup=90 rightup=-90 upsidedown=+-180
        float epsi = pyr[2];
    
        lookAt.x = (float) (Math.cos(theta) * Math.sin(phi));
        lookAt.y = (float)  Math.cos(phi);
        lookAt.z = (float) (Math.sin(theta) * Math.sin(phi));
	}
	
	/**
	 * Positions the camera in the provided PGrapics object
	 * according to actual position and oculus' viewing direction
	 * 
	 * @param g PGrapics object to be camera positioned
	 */
	private void positionCamera(PGraphics g){
		
		float cx = position.x + lookAt.x;
        float cy = position.y + lookAt.y;
        float cz = position.z + lookAt.z;
       
        g.camera(position.x, position.y, position.z, 
			           cx,cy,cz,
			           0, 1, 0); 

	}
	
	
	
	public void draw(){
	
		if(useHeadtrackingForCamera ){
			if(this.hijackScene){
				//this is not right.....
				//BAD
				//after shading, the camera
				// of  this.parent it should be brought
				//back to normal view to stop this slipping view
				//which occurs right now....
				// Last line in this draw method trys to reset this, but obviously wrong
				
				this.positionCamera(this.parent.g);		
			}
			else{
				this.positionCamera(this.scene);	
			}
		}
		
		if(renderRiftView){
			
			//hijack the scene
			if(hijackScene){
				//What role/impact does this have on the pre/post camerapositioning?
				parent.loadPixels();
				scene.loadPixels();
				copyHalfSide(parent.g, scene);
				scene.updatePixels(); 
				
				//to be set properly according to parent.background()
				parent.background(0);
			}
			
			
			//Draw actual scene
			//parent.background ggf löschen
			//parent.background(0);
			//parent.blendMode(ADD);
		  
			  //System.out.println(" Render left eye");
			  // Render left eye
			  set_shader("left");
			  parent.shader(barrel);
			  fb.beginDraw();
			  fb.background(0);
			  fb.image(scene, offset, 0, eye_width, eye_height);
			  fb.endDraw();
			  parent.image(fb, 0, 0);
			  
			  parent.resetShader();
			  
			  // Render right eye
			  //System.out.println(" Render right eye");
			  set_shader("right");
			  parent.shader(barrel);
			  fb.beginDraw();
			  fb.background(0);
			  fb.image(scene, eye_width-offset, 0, eye_width, eye_height);
			  fb.endDraw();
			  parent.image(fb, 0, 0);
			  
		}
		
		  //Let's try to restore normal view in
		  //parent. 
		  //It's better than not doing this, but still wrong....
		  if(useHeadtrackingForCamera ){
				if(this.hijackScene){
					parent.camera();
				}
		  }
				
	}
	
	
	/**
	 * 
	 * Eventually another try for hijacking the scene instead of the not working approach
	 * in draw()......
	 * Could be called at the beginning 
	 * of parent.draw() and sets the camera
	 * somehow....
	 * 
	 */
	public void beginHijack(){
		//???parent.beginCamera();
		if(useHeadtrackingForCamera){
			positionCamera(parent.g);
		}   
	}
	
	/**
	 * ...and reset the changes
	 * at the end of parent.draw()
	 */ 
	public void endHijack(){
		//???parent.endCamera();
        parent.camera(); 
	}
	
	

	public void keyEvent(KeyEvent e){
		
		if(useKeyNavigation){
			//freefly wasd
			PVector move = new PVector();
			if(e.getKey()=='w' || e.getKey()=='W'){
				PVector.mult(lookAt, speed, move);
				position = PVector.add(position, move);
			}
			if(e.getKey()=='s' || e.getKey()=='S'){
				PVector.mult(lookAt, -speed, move);
				position = PVector.add(position, move);
			}
			if(e.getKey()=='a' || e.getKey()=='A'){
				PVector.mult(lookAt, speed, move);
				PVector.cross(STRAFE, move, move);
				position = PVector.add(position, move);
			}
			if(e.getKey()=='d' || e.getKey()=='D'){
				PVector.mult(lookAt, -speed, move);
				PVector.cross(STRAFE, move, move);
				position = PVector.add(position, move);
			}
			
			
			//coord navigaton
			if(e.getKey()=='+'){
				this.position.y = this.position.y - this.speed;
			}
			if(e.getKey()=='-'){
				this.position.y = this.position.y + this.speed;
			}
			if(e.getKeyCode() == LEFT){
				this.position.x = this.position.x - this.speed;
			}
			if(e.getKeyCode() == RIGHT){
				this.position.x = this.position.x + this.speed;
			}
			if(e.getKeyCode() == DOWN){
				this.position.z = this.position.z + this.speed;
			}
			if(e.getKeyCode() == UP){
				this.position.z = this.position.z - this.speed;
			}
			
		}
	}
	
	
	
	
	public void stop(){
		this.oculus.destroy();
	}
	
	public void dispose(){
		this.oculus.destroy();
	}
	
	
	
	/**
	 * returns the pitch param
	 * @return pitch in radians
	 */
	public float pitch(){
		return this.oculus.getPitch();
	}
	
	/**
	 * returns the yaw param
	 * @return yaw in radians
	 */
	public float yaw(){
			return this.oculus.getYaw();
		}

	/**
	 * returns the roll param
	 * @return roll in radians
	 */
	public float roll(){
		return (float) Math.toDegrees(this.oculus.getRoll());
	}
	
	/**
	 * returns the pitch param
	 * @return pitch in degrees
	 */
	public float pitchDegree(){
		return (float) Math.toDegrees(this.oculus.getPitch());
	}
	
	/**
	 * returns the yaw
	 * @return yaw in degrees
	 */
	public float yawDegree(){
			return (float) Math.toDegrees(this.oculus.getYaw());
		}

	/**
	 * returns the roll
	 * @return roll in degrees
	 */
	public float rollDegree(){
		return this.oculus.getRoll();
	}
	
	/**
	 * Returns the acceleration
	 * @return acceleration in G
	 */
	public float xAcc(){
		return this.oculus.getX();
	}
	
	/**
	 * Returns the acceleration
	 * @return acceleration in G
	 */
	public float yAcc(){
		return this.oculus.getY();
	}
	
	/**
	 * Returns the acceleration
	 * @return acceleration in G
	 */
	public float zAcc(){
		return this.oculus.getZ();
	}
	
	/**
	 * Determines if yaw, pitch and roll
	 * shall be used for the camera perspective.
	 * 
	 * @param b whether the Rift is uased as scene cam
	 */
	public void useHeadTrackingForCamera(boolean b){
		 this.useHeadtrackingForCamera=b;
	}
	

	/**
	 * Tells if yaw, pitch and roll
	 * is used for the camera perspective.
	 * 
	 */
	public boolean useHeadTrackingForCamera(){
		return this.useHeadtrackingForCamera;
	}
	
	/**
	 * Determines if RiftIt renders the scene.
	 * 
	 * @param b whether RiftIt renders sth.
	 */
	public void renderRiftView(boolean b){
		 this.renderRiftView=b;
	}
	

	/**
	 * Tells if RiftIt renders the scene.
	 * 
	 */
	public boolean renderRiftView(){
		return this.renderRiftView;
	}
	
	/**
	 * Sets moving speed (default =1.0)
	 * for wasd and coord navigation
	 * @param speed
	 */
	public void speed(float speed){
		this.speed=speed;
	}
	
	/**
	 * Returns moving speed
	 * for wasd and coord navigation
	 * @return moving speed
	 */
	public float speed(){
		return this.speed;
	}
	
	/**
	 * Returns the HMD info as sent by the rift device
	 * 
	 * @return all HMD info as String
	 */
	public String HMDInfo(){
		this.oculus.updateHMDInfo();
		return this.oculus.getHMDInfo().toString();
	}
	
	private void set_shader(String eye)
	{
	  float x = 0.0f;
	  float y = 0.0f;
	  float w = 0.5f;
	  float h = 1.0f;
	  float DistortionXCenterOffset = 0.25f;
	  float as = w/h;

	  float K0 = 1.0f;
	  float K1 = 0.22f;
	  float K2 = 0.24f;
	  float K3 = 0.0f;

	  float scaleFactor = 0.7f;

	  if (eye == "left")
	  {
	    x = 0.0f;
	    y = 0.0f;
	    w = 0.5f;
	    h = 1.0f;
	    DistortionXCenterOffset = 0.25f;
	  }
	  else if (eye == "right")
	  {
	    x = 0.5f;
	    y = 0.0f;
	    w = 0.5f;
	    h = 1.0f;
	    DistortionXCenterOffset = -0.25f;
	  }

	  barrel.set("LensCenter", x + (w + DistortionXCenterOffset * 0.5f)*0.5f, y + h*0.5f);
	  barrel.set("ScreenCenter", x + w*0.5f, y + h*0.5f);
	  barrel.set("Scale", (w/2.0f) * scaleFactor, (h/2.0f) * scaleFactor * as);
	  barrel.set("ScaleIn", (2.0f/w), (2.0f/h) / as);
	  barrel.set("HmdWarpParam", K0, K1, K2, K3);
	}
	
	public static void copyHalfSide(PGraphics source, PGraphics destination){
		 for ( int j = 0; j < destination.height; j++) {
			    for ( int i = 0; i < destination.width; i++) {
			      int loc = i + j*source.width;  
			      destination.pixels[i+j*destination.width] = source.pixels[loc];  
			    }
			  }
	}
	
	
	public static void main(String[] args){
        /*try {
            OculusRiftReader orr = new OculusRiftReader();
            orr.update();
            for(int i=0; i<100;i++){
            	orr.update();
            	for(float j:orr.getRotation()){ 
	            	System.out.print(Math.toDegrees(j)+"   ");
            	}
            	System.out.println(" "+orr.getX()+"    "+orr.getY()+"    "+orr.getZ());
            	
            	Thread.sleep(1000);
            }
            orr.destroy();
        } catch (Exception ex) {
            Logger.getLogger(OculusRiftReader.class.getName()).log(Level.SEVERE, null, ex);
        }*/
		
		//RiftIt rit = new RiftIt(new TestIt());
		 
	}
	
}





