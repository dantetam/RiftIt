/*Basic Example which shows the viewing direction of the rift. 
*
*See RiftItCodeExamples.pde for further info.
*
*Author Christoph Hanke
*/


import pion3er.riftit.*;

RiftIt rift;
PVector camPosition;

void setup(){
     size(displayWidth,displayHeight,P3D);
     
     rift= new RiftIt(this);
     
     //turn rift scene rendering off, so we only get the data
     rift.renderRiftView(false);
     
}

void draw(){
    background(0);
    float x=mouseX-width/2;
    float y=mouseY-height/2;
    float z= 400;
    camera(x,y,z,0,0,0,0,1,0);
    
    //Coord System
    int f=height/4;
    stroke(color(255,0,0));
    line(-f,0,0,f,0,0);
    stroke(color(0,255,0));
    line(0,-f,0,0,f,0);
    stroke(color(0,0,255));
    line(0,0,-f,0,0,f);
    
   
    //adjust it to the coordsystem
    float adjust = 3*PI/2;
    
    //up=-90 down =90  
    float phi =- adjust -  rift.pitch();
    
    //left=90 right =-90 back =+-180
    float theta = adjust -rift.yaw();;

    float cx =  (float) (Math.cos(theta) * Math.sin(phi));
    float cy =  (float)  Math.cos(phi);
    float cz =  (float) (Math.sin(theta) * Math.sin(phi));

    stroke(255);
    line(0,0,0,f*cx,f*cy,f*cz);
    line(f*cx+10,f*cy+10,f*cz+10,f*cx,f*cy,f*cz);
    line(f*cx-10,f*cy-10,f*cz-10,f*cx,f*cy,f*cz);

}