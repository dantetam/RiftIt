/**
* Basic code example which transforms the drawn scene to a oculus Rift output 
* binding the camera to the rift. 
* You can fly around with wasd towards your viewing direction.
* To navigate along x, y and z you can use
* UP, DOWN, LEFT, RIGHT,+ ,- keys.
*
* Be aware that you only have width/2 to
* draw instead of width as the rift needs two pics side by side.
* Standard resolution is 1280 x 800. If you have sth. different adjust
* it with rift.offset=xxx; (see below).
*
*Author Christoph Hanke
*/


import pion3er.riftit.*;

RiftIt rift;

void setup(){
    size(displayWidth,displayHeight,P3D);
    //write direct to the render scene of Riftit.
    rift =new RiftIt(this,false);
    
    /*you probably have to set this value depending on your screen resolution
     * It's the leftside-offset of the pictures for the left and right eye
     * this will be done automatically in future
     * for 1280 x 800 offset = 50  ( default)
     * for 1920x1080 offset=90 is a good value*/
     //rift.offset=90;
    
    /* Just add this line below to any existing 3D Sketch 
     * and you're transferred inside with your rift. 
     * Same as rift = new RiftIt(this,true);
     * Unfortunatly it's not working properly yet :( */
    //rift = new RiftIt(this);
    
    /*turns camera positioning off*/
    //rift.useHeadTrackingForCamera(false);
    
    //turns Key navigation off
    //rift.useKeyNavigation(false);
    
    //turns rift rendering off, so you can just use the rifts position data
    //rift.renderRiftView(false);
    
    //sets moving speed (default =5.0)
    rift.speed(10.0);
    
    //gets HMDInfo
    println("->"+rift.HMDInfo());
    
    
}

void draw(){
    int f=height;  
  
    background(0);
    
    //write direct to the render scene of Riftit.
    rift.scene.beginDraw();
    rift.scene.background(0);
    rift.scene.pushMatrix();
    rift.scene.translate(width/2, height/2, 0);
    rift.scene.noFill();
    rift.scene.stroke(255);
    rift.scene.sphere(280);
    rift.scene.popMatrix();
    
    rift.scene.pushMatrix();
    rift.scene.translate(0, height/2, 0);
    rift.scene.noFill();
    rift.scene.stroke(255);
    rift.scene.sphere(100);
    rift.scene.popMatrix();
    
     //Coord System
    rift.scene.stroke(color(255,0,0));
    rift.scene.line(-f,0,0,f,0,0);
    rift.scene.stroke(color(0,255,0));
    rift.scene.line(0,-f,0,0,f,0);
    rift.scene.stroke(color(0,0,255));
    rift.scene.line(0,0,-f,0,0,f);
    
    rift.scene.endDraw();
    
    println("Pitch "+rift.pitch()+" ("+rift.pitchDegree()+"°)"); 
    println("Yaw "+rift.yaw()+" ("+rift.yawDegree()+"°)");
    println("Roll "+rift.roll()+" ("+rift.rollDegree()+"°)"); 
    println("X Acceleration "+rift.xAcc());
    println("Y Acceleration "+rift.yAcc());
    println("Z Acceleration "+rift.zAcc());
}