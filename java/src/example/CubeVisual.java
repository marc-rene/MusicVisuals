package example;

import ie.tudublin.Visual;
import processing.core.PShape;
import processing.opengl.PShader;



public class CubeVisual extends Visual
{
	boolean twocubes = true;
    PShape ducky;
	PShader test_shader;
	float total_time = 0;
	float deltatime = 0;

    public void settings()
    {
        size(Get_Window_Width(), Get_Window_Height(), P3D);
        println("CWD: " + System.getProperty("user.dir"));
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
            twocubes = ! twocubes;

        }
    }

    public void setup()
    {
        colorMode(RGB);
        
        setFrameSize(256);

        ducky = loadShape("Shapes/Rubber_Ducky.obj" );
		
		startMinim();
        loadAudio(Get_Song_Path());          // MP3 is OK
        //loadAudio("Music/TEST WAV - PETETE.wav");             // WAV is OK        
    }

    float smoothedBoxSize = 0;
    boolean increment = true; // Delete later

    public void draw()  // Called every Frame
    {
		deltatime = System.currentTimeMillis();
        calculateAverageAmplitude();
        background(20);
        noFill();
        lights();
        perspective(
            1.5f, // field-of-view angle (in radians)
            (Get_Window_Width() / Get_Window_Height()), // aspect - ratio of width to height... Need to figure out how to update this when going fullscreen
            1, // Near-Clip plane (Keep low)
            10000 // Far-Clip plane (Keep High)
            );
        camera
            ( 
                0,      // x-coordinate for the eye 
                0,      // y-coordinate for the eye
                0,      // z-coordinate for the eye
                0,      // x-coordinate for the center of the scene
                0,      // y-coordinate for the center of the scene
                1,     // z-coordinate for the center of the scene
                0,      // upX - usually 0.0, 1.0, or -1.0
                -1,      // upY - usually 0.0, 1.0, or -1.0
                0      // upZ - usually 0.0, 1.0, or -1.0
            );
        
        translate(0, 0, 250);
            
        float boxSize = 50 + (getAmplitude() * Get_Window_Width() / 5); //map(average, 0, 1, 100, 400); 
        smoothedBoxSize = lerp(smoothedBoxSize, boxSize, 0.2f);        
        if (twocubes)
        {
            background(smoothedBoxSize * 0.5f);
            // Box 1 X (going left and right)
            pushMatrix();
            translate((Tick_Tock * 100), -10, 0);
            rotateX(angle);
            fill(255,0,0);
            stroke(222,50,50); // Red
            strokeWeight(5); 
            box(smoothedBoxSize);
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
            translate(0,-2000, 5000);
            scale(smoothedBoxSize * 2);
            rotateZ((smoothedBoxSize - 50) * (Tick_Tock * 0.01f) );
            rotateY(angle); 
            shape(ducky);
            popMatrix();

        }
        else
        {
            rotateY(angle);
            rotateX(angle);
            //rotateZ(angle);
            //strokeWeight(1);
            //sphere(smoothedBoxSize/ 2);            
            strokeWeight( (Get_Window_Width() / 100) ); // If we have a small window, We want a small Stroke
            
            box(smoothedBoxSize);
        }
        
        
        if (Tick_Tock > 1)
        {   increment = false;  }
        else if (Tick_Tock < -1 ) 
        {   increment = true;   }

        if (increment)
        { Tick_Tock += 0.01f;}
        else
        { Tick_Tock -= 0.01f;}
        
        angle += 0.01f;
        deltatime = System.currentTimeMillis() - deltatime;
		total_time += deltatime;
    }

    float angle = 0;
    float Tick_Tock = 0;
} 