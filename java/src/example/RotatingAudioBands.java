package example;

import ie.tudublin.Visual;
import ie.tudublin.VisualException;
import processing.core.PShape;

public class RotatingAudioBands extends Visual {

    PShape ducky;
    Boolean do_Spiral = false;

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
            getAudioPlayer().cue(0);
            getAudioPlayer().play();
        }
        if (key == '1')
        {
            do_Spiral = true;
        }
        if (key == '2')
        {
            do_Spiral = false;
        }
 
    }

    public void setup()
    {
        colorMode(HSB);
        
        setFrameSize(256);

        ducky = loadShape("Shapes/Rubber_Ducky.obj" );

        startMinim();
        loadAudio(Get_Song_Path());
        //getAudioPlayer().play();
        //startListening(); 
        
    }
    
    float radius = 150;
    
    float smoothedBoxSize = 0;
    float theta;
    float h;
    float x;
    float z;
    
    float Y_offset = 0;
    float rot = 0;

    float Tick_Tock = 0;
    boolean increment = false;

    public float clamp(float min, float max, float value)
    {
        if (value > max)
        {
            return max;
        }
        else if (value < min)
        {
            return min;
        }
        else
        {
            return value;
        }
    }

    public void draw()
    {
        
        calculateAverageAmplitude();
        try
        {
            calculateFFT();
        }
        catch(VisualException e)
        {
            e.printStackTrace();
        }
        calculateFrequencyBands();
        background(0);
        stroke(255);
        lights();
        stroke(map(getSmoothedAmplitude(), 0, 1, 0, 255), 255, 255);
       
        float eyeY = lerp(-300, -800, abs(Tick_Tock));
        float eyeZ = lerp(600, 800, abs(Tick_Tock));
        
        camera(0, eyeY, eyeZ, 0, 100, 0, 0, 1, 0);
        //translate(0, 0, -250);
        
        rot += getAmplitude() / 8.0f;
        
        rotateY(rot);
        float[] bands = getSmoothedBands();
        
        //Ducky
        pushMatrix();
        translate(0,0,0);
        
        rotateZ(PI);
        rotateY(rot/1.2f );
        scale(4);
        shape(ducky);
        popMatrix();
        
        //SPIRAL
        if (do_Spiral) {
            
            for(int i = 0 ; i < bands.length ; i ++)
            {
                theta = map(i, 0, bands.length, 0, TWO_PI);
                h = clamp(1, 300, bands[i] / 5);
                x = sin(theta) * (radius/1.3f) ;
                z = cos(theta) * (radius/1.3f) ;

                for (int ring = 1; ring < 75; ring++) // 75 rings... overkill
                {
                    fill(map(i, 0, bands.length, 0, 255), 200, 200, (255 / ring) );
                    stroke(map(i, 0, bands.length, 0, 255), 255, 255);
                    int max_size = 2000;
                    pushMatrix();
                    Y_offset = clamp(-150, max_size, h * (h/(ring*0.02f)) );   // The 0.1f here is the sensitivity, lower number here means each ring will be more sensitive to sound
                    translate( x * (ring*0.4f), max_size + (-1 * Y_offset / 2) , z * (ring*0.4f) );
                    rotateY(theta);
                    box( (250 * ring) / getBands().length, Y_offset, 25);
                    popMatrix();  
                }
            }
        }//end if


        //Not a Spiral
        else {
            for(int i = 0 ; i < bands.length ; i ++)
            {
                noFill();
                theta = map(i, 0, bands.length, 0, TWO_PI);
                h = clamp(1, 300, bands[i] / 5);
                x = (sin(theta) * radius) * clamp(1, 1.55f, (h/100) );
                z = (cos(theta) * radius) * clamp(1, 1.55f, (h/100) );
                stroke(map(i, 0, bands.length, 0, 255), 255, 255);
                
                // middle
                pushMatrix();
                translate(x, -40 , z );
                rotateY(theta);
                box(20, 50, h/1.5f);
                popMatrix();
                
                // Upper Ring
                pushMatrix();
                x = sin(theta) * radius;
                z = cos(theta) * radius;
                //h = bands[i];
                fill(map(i, 0, bands.length, 0, 255), 255, 255, 100);
                translate(x, (- h / 2) - 75  , z);
                box(50, h, 50);
                
                //Lower Ring
                translate(x*2, 800  , z*2);
                fill(map(i, 0, bands.length, 0, 255), 200, 200, 50);
                box(clamp(50, 150, h), h*4, clamp(50, 150, h));
                popMatrix();

            }
        }// end else

        if (Tick_Tock > 1)
        {   increment = false;  }
        else if (Tick_Tock < -1 ) 
        {   increment = true;   }

        if (increment)
        { Tick_Tock += 0.01f;}
        else
        { Tick_Tock -= 0.01f;}

        
    }
    float angle = 0;

}