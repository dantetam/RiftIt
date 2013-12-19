package pion3er.riftit;

import processing.core.PApplet;

public class TestIt  extends PApplet{
		
		
		 public void setup() {
			 // Set the canvas size
			 size(1000, 1000,P3D);

			 // Let's use anti aliasing!
			 smooth();

			 // Don't draw strokes on shapes
			 noStroke();
			 }
		
		public void draw(){
			
			
			 int step = 2*height/20;
			  for(int i=0;i<21;i++){
			      paintFullQuad(height-i*step);
			  }  
			  
			  
			  
			}
			 
			void paintFullQuad(int zpos){
			  pushMatrix();
			  translate(0,0,zpos);
			  
			  
			  line(0,0,0,width,0,0);
			  
			  stroke(color(0,0,255));
			  line(0,height,0,width,height,0);
			  stroke(color(255,0,0));
			  line(0,0,0,0,height,0);
			  stroke(color(0,255,0));
			  line(width,0,0,width,height,0);
			  popMatrix();
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
				//PApplet.main(new String[] { "--present", "TestIt"});
				new TestIt();
				 
			}
	}

