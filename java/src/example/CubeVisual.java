package example;

import ie.tudublin.Visual;

public class CubeVisual extends Visual
{
    boolean twocubes = false;

    public void settings()
    {
        size(800, 800, P3D);
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
        colorMode(HSB);
        
        setFrameSize(256);

        startMinim();
        loadAudio("Music/Death Grips - Beware.mp3");          // MP3 is OK
        //loadAudio("Music/TEST FLAC - Chop Suey.flac");        // FLACs cant be played
        //loadAudio("Music/TEST OGG - Death Grips Hacker.ogg"); // OGGs cant be played
        //loadAudio("Music/TEST WAV - PETETE.wav");             // WAV is OK
        
        
        //getAp().play();
        //startListening(); 
        
    }

    float smoothedBoxSize = 0;

    public void draw()
    {
        calculateAverageAmplitude();
        background(0);
        noFill();
        lights();
        stroke(map(getSmoothedAmplitude(), 0, 1, 0, 255), 255, 255);
        camera(0, 0, 0, 0, 0, -1, 0, 1, 0);
        translate(0, 0, -250);
               
        float boxSize = 50 + (getAmplitude() * 300);//map(average, 0, 1, 100, 400); 
        smoothedBoxSize = lerp(smoothedBoxSize, boxSize, 0.2f);        
        if (twocubes)
        {
            pushMatrix();
            translate(-100, 0, 0);
            rotateY(angle);
            rotateX(angle);
            box(smoothedBoxSize);
            //strokeWeight(1);
            //sphere(smoothedBoxSize);
            popMatrix();
            pushMatrix();
            translate(100, 0, 0);
            rotateY(angle);
            rotateX(angle);
            strokeWeight(5); 
            box(smoothedBoxSize);
            popMatrix();
        }
        else
        {
            rotateY(angle);
            rotateX(angle);
            //strokeWeight(1);
            //sphere(smoothedBoxSize/ 2);            
            strokeWeight(5);
            
            box(smoothedBoxSize);
        }
        angle += 0.01f;
    }
    float angle = 0;
} 