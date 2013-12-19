/**
* Basic Example which transforms the drawn scene to a oculus Rift output 
* binding the camera to the rift. This example writes directly to the render 
* scene of RiftIt. No hijacking of the main draw scene.
* You can fly around with wasd towards your viewing direction.
* To navigate along x, y and z you can use
* UP, DOWN, LEFT, RIGHT,+ ,- keys.
*
* Be aware that you only have width/2 to
* draw instead of width as the rift needs two pics side by side.
*
* Standard resolution is 1280 x 800. If you have sth. different adjust
* it with rift.offset=xxx; See RiftItCodeExamples.pde for further info.
*
*Author Christoph Hanke
*/


import pion3er.riftit.*;

RiftIt rift;

void setup(){
    size(displayWidth,displayHeight,P3D);
    
    //write direct to the render scene of RiftIt. No hijacking of the main draw scene
    rift =new RiftIt(this,false);
    
    //depending on your resolution; See RiftItCodeExamples
    //rift.offset=90;
}

void draw(){
    int f=height;  
  
    background(0);

    rift.scene.beginDraw();
    
    rift.scene.background(0);
    rift.scene.pushMatrix();
    rift.scene.translate(width/2, height/2, 0);
    rift.scene.noFill();
    rift.scene.stroke(255);
    rift.scene.sphere(280);
    rift.scene.popMatrix();
    
     //Coord System
    rift.scene.stroke(color(255,0,0));
    rift.scene.line(-f,0,0,f,0,0);
    rift.scene.stroke(color(0,255,0));
    rift.scene.line(0,-f,0,0,f,0);
    rift.scene.stroke(color(0,0,255));
    rift.scene.line(0,0,-f,0,0,f);
    
    rift.scene.endDraw();
    
}