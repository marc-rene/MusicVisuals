package example;

import ie.tudublin.Visual;
import ie.tudublin.VisualException;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PShape;


public class RotatingAudioBands extends Visual {

    PShape ducky;
    PFont font;
    PGraphics HUD;
    Boolean do_Spiral = false;
    Boolean Ortho_Camera = false;
    float adjust_back_and_forth = 1;
    float fov = 70;

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
        if (key == '3')
        {
            Ortho_Camera = false;
        }
        if (key == '4')
        {
            Ortho_Camera = true;
        }
        if (key == 'w')
        {
            adjust_back_and_forth -= 0.01f;
            println(adjust_back_and_forth);
        }
        if (key == 's')
        {
            adjust_back_and_forth += 0.01f;
            println(adjust_back_and_forth);
        }
        if (key == 'a')
        {
            fov -= 1f;
            println(fov);
        }
        if (key == 'd')
        {
            fov += 1f;
            println(fov);
        }
    }

    

    public void setup()
    {
        colorMode(HSB);
        
        setFrameSize(256);
    
        ducky = loadShape("Shapes/Rubber_Ducky.obj" );
        
        //HUD = createGraphics(Get_Window_Width(), Get_Window_Height(), P2D);
        font = createFont("Arial", 64);
        textFont(font);
        


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
    float hud_alpha = 1250;

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

        if(keyPressed == true)
        {
            keyPressed();
        }
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
       
       
        
        

        //translate(0, 0, -250);
        
        rot += getAmplitude() / 8.0f;
        
        rotateY(rot);
        float[] bands = getSmoothedBands();
        int rings = 75;
        float sensitivity = 0.02f;
        //Ducky
        pushMatrix();
        
        if (Ortho_Camera)
        {
            rings = 15;
            sensitivity = 1.4f;
            radius = 450;
            rotateZ(PI/2);
            translate(0,-60,0);
        }
        else
        {
            rings = 75;
            sensitivity = 0.02f;
            radius = 150;
            translate(0,0,0);
            rotateZ(PI);
        }

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

                for (int ring = 1; ring < rings; ring++) // 75 rings... overkill
                {
                    fill(map(i, 0, bands.length, 0, 255), 200, 200, (255 / ring) );
                    stroke(map(i, 0, bands.length, 0, 255), 255, 255);
                    int max_size = 2000;
                    pushMatrix();
                    Y_offset = clamp(-150, max_size, h * (h/(ring*sensitivity)) );   // sensitivity, lower number here means each ring will be more sensitive to sound
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
                h = clamp(5, 300, bands[i] / 5);
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
        

        
        float eyeY = lerp(-300, -800, abs(Tick_Tock)) * adjust_back_and_forth;
        float eyeZ = lerp(600, 800, abs(Tick_Tock)) * adjust_back_and_forth; 

        //camera stuff
        if(!Ortho_Camera)
        {   
            perspective( (fov*0.0174533f) ,1,0.01f, 90000f); //we dealing with radians here
            camera(0, eyeY, eyeZ, 0, 0, 0, 0, 1, 0); 
        }
        else
        {
            ortho(-1*1000*adjust_back_and_forth, 1000*adjust_back_and_forth, -1*1000*adjust_back_and_forth, 1000*adjust_back_and_forth, 0.01f, 90000);
            camera(0, 1000, 0, 0, 0, 0, 0, 0, 1); 
        }

        
        colorMode(RGB);
        hint(DISABLE_DEPTH_TEST);
        textAlign(CENTER);
        textMode(MODEL);
        translate(0,0,0);
        rotateX(0 + (eyeZ/2000));
        
        fill(255,255,255, clamp(0, 255, hud_alpha));
        text("W , S: control camera position", 0,0,255);
        fill(255,255,255, clamp(0, 255, hud_alpha*2));
        text("\nA , D: control camera FOV", 0,0,255);
        fill(255,255,255, clamp(0, 255, hud_alpha*4));
        text("\n\n1 , 2: Change Scenes", 0,0,255);
        fill(255,255,255, clamp(0, 255, hud_alpha*8));
        text("\n\n\n3 , 4: Change Perspective", 0,0,255);
        
        if (hud_alpha >= 0)
        { 
            hud_alpha  -= 1.5f;
            print("\rHud Alpha : "+ hud_alpha);
        }
        hint(ENABLE_DEPTH_TEST);
        colorMode(HSB);
        
    }
    

}