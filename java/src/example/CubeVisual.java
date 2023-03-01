package example;

import ie.tudublin.Visual;
import processing.core.PShape;
import processing.opengl.PShader;



public class CubeVisual extends Visual
{
	boolean twocubes = true; // do we want the visual with multiple cubes, or just the one?
    PShape ducky; // our duck obj "shape"
	PShader test_shader; // this went nowhere 
	
    float angle = 0;
    float Tick_Tock = 0;
    float total_time = 0; // how long has the sketch been running for
	float deltatime = 0; // how long did the last frame take to render?

    public void settings()
    {
        size(Get_Window_Width(), Get_Window_Height(), P3D); // set the windows size, and use 3D graphics (P3D) 
        //println("CWD: " + System.getProperty("user.dir")); 
        //fullScreen(P3D, SPAN);
    }

    public void keyPressed()
    {
        if (key == ' ')
        {
            getAudioPlayer().cue(0); // Play from 0 miliseconds into the song.. from the start basically
            getAudioPlayer().play();
            
        }
        if (key == '1')
        {
            twocubes = !twocubes; // toggle between the 2 visuals
        }
    }

    public void setup()
    {
        colorMode(RGB); 
        
        setFrameSize(256); // the "resolution" of the audio input for the calculations... I think?

        ducky = loadShape("Shapes/Rubber_Ducky.obj" ); // set the obj file for our ducky
		
		startMinim();
        loadAudio( Get_Song_Path() ); // .MP3 and .WAV is OK        
    }

    float smoothedBoxSize = 0;
    boolean increment = true; // for use in the tick-tock (like a metronome) variable later

    public void draw()  // Called every Frame
    {
		deltatime = System.currentTimeMillis(); // start the timer
        calculateAverageAmplitude();
        background(20); // set red, green, and blue to 20
        noFill(); 
        lights();
        
        /* theres perspective cameras (Like in 3D games, you can get a sense of depth), and then orthographic cameras (Like in 2D games, no depth) */
        perspective(
            1.5f, // field-of-view angle (in radians)
            (Get_Window_Width() / Get_Window_Height()), // aspect - ratio of width to height... Need to figure out how to update this when going fullscreen
            0.1f, // Near-Clip plane (Keep low), the point at which items will clip into the camera
            10000 // Far-Clip plane (Keep High), the point at which items will stop rendering in the distance
            );
        camera
            ( 
                0,      // x-coordinate for the eye 
                0,      // y-coordinate for the eye
                0,      // z-coordinate for the eye
                0,      // x-coordinate for the center of the scene
                0,      // y-coordinate for the center of the scene
                1,      // z-coordinate for the center of the scene
                0,      // upX - 
                -1,     // upY - we want Y-axis to be up, idk why its minus
                0       // upZ - 
            );
        
        translate(0, 0, 250); // the boxes that we'll render later should go 250 units in front of the camera
            
        float boxSize = 50 + (getAmplitude() * Get_Window_Width() / 5); // the box size will increase depending on the music loudness
        smoothedBoxSize = lerp(smoothedBoxSize, boxSize, 0.2f); // I dont fully understand this        
        
        if (twocubes) // the 3 cubes and duck in the background
        {
            background(smoothedBoxSize * 0.5f); // Make the background lighter or darker with the music
            
            // Box 1 X (going left and right)
            pushMatrix();
            translate((Tick_Tock * 100), -10, 0);
            rotateX(angle);
            fill(255,0,0); // what colour our box be? (red)
            stroke(222,50,50); // What will the outline colour be? (Red)
            strokeWeight(5); 
            box(smoothedBoxSize); // make a box with X size
            popMatrix();
            
            // Box 2 Y (Going up and down)
            pushMatrix();
            translate(0, ((Tick_Tock/2) * 100), 0);
            rotateY(angle/3);
            fill(0,255,0);
            stroke(50,222,50); // Green
            strokeWeight(5); 
            box(smoothedBoxSize);
            popMatrix();
            
            // Box 3 Z (going back and forth)
            pushMatrix();
            translate(100, -50, (Tick_Tock * -100));
            rotateZ(angle);
            fill(50,50,222);
            stroke(0,0,255); // Blue
            strokeWeight(5); 
            box(smoothedBoxSize);
            popMatrix();
            
            // Custom Shape (Duck)
            pushMatrix();
            translate(0,-2000, 5000); // way off in the distance now
            scale(smoothedBoxSize * 2); // make this thing huge, and change size with the msuic
            rotateZ((smoothedBoxSize - 50) * (Tick_Tock * 0.01f) );
            rotateY(angle); 
            shape(ducky); //render a custom shape... our duck
            popMatrix();

        }//end if

        else // do the 2 cubes spinning inside eachother
        {
			pushMatrix();
            rotateY(angle * (smoothedBoxSize / 1000) ); // rotate some more depending what the music like
            rotateX(angle);
            strokeWeight( (Get_Window_Width() / 100) ); // If we have a small window, We want a small Stroke
            stroke(50,100,150);
            box(50 + smoothedBoxSize * Tick_Tock); // make a box that gets bigger and smaller
			
            stroke(50,150, (100 + (50 * (Tick_Tock)) ) );
			rotateY(-1 * (angle * (smoothedBoxSize / 100) ));
			box(smoothedBoxSize * 3 * (Tick_Tock / 2) ); // this box also gets bigger and smaller, but not as much
			popMatrix();
        }
        
        // Tick tock is supposed to be like a metronome, bouncing between -1 and 1
        if (Tick_Tock > 1)
        {   increment = false;  }
        else if (Tick_Tock < -1 ) 
        {   increment = true;   }

        if (increment)
        { Tick_Tock += 0.01f;}
        else
        { Tick_Tock -= 0.01f;}
        
        angle += 0.01f; // we're using rads, not degrees... crap
        deltatime = System.currentTimeMillis() - deltatime; // end timer! How long did all that rendering take to do?
		total_time += deltatime; 
    }//end draw

    
} 