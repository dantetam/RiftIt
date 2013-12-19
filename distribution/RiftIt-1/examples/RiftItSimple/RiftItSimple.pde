/**  THIS IS NOT WORKING PROPERLY YET :(
*
* Basic Example which transforms the drawn scene to a Oculus Rift output 
* binding the camera to the rift. Hereby the drawn scene is some kind of hijacked
* and converted to a oculus view. 
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
    rift =new RiftIt(this);
    
    //depending on your resolution; See RiftItCodeExamples
    //rift.offset=90;
}

void draw(){
    
    background(0);
    pushMatrix();
    translate(500, height*0.35, -200);
    noFill();
    stroke(255);
    sphere(280);
    popMatrix();

    int f=height;
    stroke(color(255,0,0));
    line(-f,0,0,f,0,0);
    stroke(color(0,255,0));
    line(0,-f,0,0,f,0);
    stroke(color(0,0,255));
    line(0,0,-f,0,0,f);
   
}