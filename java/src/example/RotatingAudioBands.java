package example;

import ie.tudublin.Visual;
import ie.tudublin.VisualException;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PShape;

import Custom.Mesh_Manager;



public class RotatingAudioBands extends Visual {

    PShape ducky;
    PFont font;
    PGraphics HUD; // failed HUD experiment
    Boolean do_Spiral = false;
    Boolean Ortho_Camera = false;
    float adjust_back_and_forth = 1;
    float[] bands; // frequency bands, for use later
    int max_size = 2000; // maximum height a ring part can be


    int rings; // for use in the spiral visual... how many rings do we want?
    float sensitivity = 0.02f; // how sensitive should each ring be to sound?
    
    // 3D Camera
    float fov = 70; // field of view
    float Camera_Right = 0; // Right-Left Movement
    float Camera_Forward = 1000; // forward-backwards movement
    float Camera_Up = -300; // up-down movement
    float Camera_Movement_Speed;
    
    // Where in the scene is the camera focusing (Co-ordinates)???
    float Camera_focus_at_X;
    float Camera_focus_at_Y;
    float Camera_focus_at_Z;
    
    /* All part of the failed free cam experiment */ 
    float Camera_Sensitivity = 10; // eh, fix later
    float Camera_R_X = 0; //Rotate Camera up and down (degrees)
    float Camera_R_Y = 0; //Rotate Camera Left and right (degrees)
    float Camera_x_direction; // which way is the camera facing?
    float Camera_y_direction;
    float Camera_z_direction;
    boolean free_cam = false;



    public void settings()
    {
        size(Get_Window_Width(), Get_Window_Height(), P3D);
        println("CWD: " + System.getProperty("user.dir"));
        //fullScreen(P3D, SPAN);
    }

    public void keyPressed() // Maybe a switch would be better? does java do switches???
    {
        if (key == ' ')
        {
            getAudioPlayer().cue(0); // go to 0 miliseconds into the song
            getAudioPlayer().play(); // initiate Death Grips
        }
        if (key == '1')
        {
            do_Spiral = true; // use the spiral visual
        }
        if (key == '2')
        {
            do_Spiral = false; // Use the "towers" visual instead
        }
        if (key == '3')
        {
            Ortho_Camera = false; // use 3D camera
            Camera_Up = -300; // zoom out
        }
        if (key == '4')
        {
            Ortho_Camera = true; // use 2D camera
            Camera_Up = 10; // zoom in
        }

        // Movement Control - I don't like this, because when we're moving, we dont move relative to the centre, we dont circle around it, so holding 'D' will make the camera just fly all the way away to right, this sucks
        if (key == 'w') // forward
        {
            Camera_Forward -= Camera_Movement_Speed;
        }
        if (key == 'a') // left
        {
            Camera_Right -= Camera_Movement_Speed;
        }
        if (key == 's') // backwards
        {
            Camera_Forward += Camera_Movement_Speed;
        }
        if (key == 'd') // right
        {
            Camera_Right += Camera_Movement_Speed;
        }
        if (key == 'e') // up
        {
            Camera_Up += Camera_Movement_Speed;
        }
        if (key == 'q') // down
        {
            Camera_Up -= Camera_Movement_Speed;
        }
        
        // free-cam, should we be able to fly around the scene?
        // this was a failed experiment, please burn, math is hard
        if (key == 'f')
        {            
            free_cam = false;
            println("\nDisabled Free Cam, focused on centre now\n");
        }
        if (key == 'g')   
        {
            free_cam = true;
            println("\nEnabled Free Cam!\n");
        }

        // Fov control
        if (key == 'z')
        {
            fov = clamp(1, 175, fov - 0.3f);
        }
        if (key == 'x')
        {
            fov = clamp(1, 175, fov + 0.3f);
        }


        // Camera Rotate Control
        
        // --- Keycodes ---
        // Down arrow   : 40
        // Right arrow  : 39
        // Up arrow     : 38
        // Left arrow   : 37
        if (keyCode == 37) // Left arrow, rotate camera left
        {
            Camera_R_Y = loop_value(0,360, (Camera_R_Y - 0.1f * Camera_Sensitivity) );
        }
        if (keyCode == 39) // Right arrow, rotate camera right
        {
            Camera_R_Y = loop_value(0,360, (Camera_R_Y + 0.1f * Camera_Sensitivity) );
        }
        if (keyCode == 38) // Up arrow, rotate camera up
        {
            Camera_R_X = loop_value(0,360, (Camera_R_X + 0.1f * Camera_Sensitivity) );
        }
        if (keyCode == 40 ) // Down arrow, rotate camera down
        {
            Camera_R_X = loop_value(0,360, (Camera_R_X - 0.1f * Camera_Sensitivity) );
        }

        // this is to test the file dialog function
        if (key == 'o')
        {
            Mesh_Manager test = new Mesh_Manager();
            println("\n" + test.Find_File() );
        }
    }
    
    public void setup()
    {
        colorMode(HSB); // this was here already...
        
        setFrameSize(1024); 
    
        ducky = loadShape("C:/TUD/OneDrive - Technological University Dublin/Documents/College/Year 2/OOP/Project/MusicVisuals/java/data/Shapes/Rubber_Ducky.obj" );
        
        //HUD = createGraphics(Get_Window_Width(), Get_Window_Height(), P2D);, tried to make a HUD, it failed
        font = createFont("Arial", 64); // use the system's version of the arial font
        textFont(font); // set the font to be used
        
        startMinim();
        loadAudio(Get_Song_Path());     
    }
    
    float radius = 150;
    
    float smoothedBoxSize = 0;
    
    // black magic math
    float theta; 
    float h;
    float x;
    float z;
    
    float Y_offset = 0; // how far off the ground something should be
    float rot = 0;

    float Tick_Tock = 0; // metronome
    boolean increment = false;
    float hud_alpha = 1750; // The starting transparency of the HUD... will decrement later, making it invisable

    private float clamp(float min, float max, float value) // keep a value in range of something... does java have this already???
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


    private float loop_value(float min, float max, float value) // Sorry This sucks... I apologise - Cesar
    {
        if (value < min)
        {
            float difference = min - value;
            return max - difference;
        }
        else if (value >= max)
        {
            float difference = value - max;
            return min + difference;
        }
        return value;
    }

    // idk if Java has this to-degree and to-radian stuff already?
    private float toDegrees(float rad)
    {
        return (rad * 57.2958f);
    }

    private float toRad(float degree)
    {
        return 0.01745f * degree;
    }


    // Every Frame
    public void draw()
    {
        // Are any keys being pressed?
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
        
        background(0); // black
    
        stroke(255);
        lights();
        stroke(map(getSmoothedAmplitude(), 0, 1, 0, 255), 255, 255); // Idont know, this was here when i got here... i dont understand - César
       
        rot += getAmplitude() / 8.0f; // increase rotation based on song
        
        rotateY(rot);

        bands = getSmoothedBands(); // Frequency bands
    
        //Ducky
        pushMatrix();

        // Are we in a 3D or 2D camera?
        if (Ortho_Camera) // 2D camera
        {
            rings = 15;
            sensitivity = 1.4f; // every ring should be less sensitive because we have less rings
            Camera_Movement_Speed = 0.05f;
            
            radius = 450;
            rotateZ(PI/2); // rotate the duck upwards to the camera, 
            translate(0,-60,0); // centre it... idk why 0,0,0 isn't centre? I'm dumb
        }
        else // 3D Camera
        {
            rings = 75; // More rings... Laptop cant render 99999 rings sadly
            sensitivity = 0.05f; // every ring should be more sensitive, because we have more rings... looks cooler i think? Tweak and see what yee think
            Camera_Movement_Speed = 10f;
            radius = 150;
            translate(0,0,0);
            rotateZ(PI); // flip the duck over
        }

        rotateY(rot/1.2f );
        scale(4); // scale up ducky
        shape(ducky); // render geometry for ducky
        popMatrix();
        
    
        //SPIRAL
        if (do_Spiral) {
            
            for(int i = 0 ; i < bands.length ; i ++)
            {
                // Mathmatical Black Magic
                theta = map(i, 0, bands.length, 0, TWO_PI);
                h = clamp(1, 300, bands[i] / 5);
                x = sin(theta) * (radius/1.3f) ;
                z = cos(theta) * (radius/1.3f) ;
                
                for (int r = 1; r < rings; r++) // 75 rings... overkill
                {
                    fill(map(i, 0, bands.length, 0, 255), 200, 200, (255 / r) );
                    stroke(map(i, 0, bands.length, 0, 255), 255, 255); // Change colour depending on which band we're on
                    
                    pushMatrix();
                    Y_offset = clamp(-150, max_size, h * (h/(r * sensitivity)) );   // sensitivity, lower number here means each ring will be more sensitive to sound
                    // the "h * (h/(r * sensitivity))" makes the box flush to the ground, only the top part will go up...
                    // Clamp the height between -150 and whatever the max height can be
                    translate( x * (r * 0.4f), max_size + (-1 * Y_offset / 2) , z * (r * 0.4f) );
                    rotateY(theta);
                    box( (250 * r) / getBands().length, Y_offset, 25);  // the 250 size here is a sweetspot, any bigger and rings will overlap with eachother
                    popMatrix();  
                }//end for-loop for ring parts for this particular band
            }
        }//end if
        
        
        //Not a Spiral
        else {
            for(int i = 0 ; i < bands.length ; i ++)
            {
                noFill();

                // Mathmatical Black Magic
                theta = map(i, 0, bands.length, 0, TWO_PI);
                h = clamp(5, 300, bands[i] / 5);
                x = (sin(theta) * radius) * clamp(1, 1.55f, (h/100) );
                z = (cos(theta) * radius) * clamp(1, 1.55f, (h/100) );
                stroke(map(i, 0, bands.length, 0, 255), 255, 255);
                
                // middle (bits that pop outwards)
                pushMatrix();
                translate(x, -40 , z );
                rotateY(theta);
                box(20, 50, h/1.5f);
                popMatrix();
                
                // Upper Ring
                pushMatrix();
                x = sin(theta) * radius;
                z = cos(theta) * radius;
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
        

        
        //camera stuff
        if(!Ortho_Camera) // 3D camera
        {   
            perspective( (fov*0.0174533f) , 1, 0.01f, 90000f); // I cant remember what the 0.017 is here for... probably degree to radians tomfoolery?
            
            if (free_cam == true) // meh
            {
                Camera_x_direction = cos(toRad(Camera_R_Y) );
                Camera_y_direction = sin(toRad(Camera_R_Y) );
                Camera_z_direction = cos(toRad(Camera_R_X) );
                Camera_focus_at_X = Camera_Right + Camera_x_direction;
                Camera_focus_at_Y = Camera_Up + Camera_y_direction;
                Camera_focus_at_Z = Camera_Forward + Camera_z_direction;
            }
            else // look at the centre of the scene
            {
                Camera_focus_at_X = 0;
                Camera_focus_at_Y = 0;
                Camera_focus_at_Z = 0;
            }
            camera( Camera_Right, Camera_Up, Camera_Forward,    // where is the camera? 
                    Camera_focus_at_X, Camera_focus_at_Y, Camera_focus_at_Z,    // where is the camera facing
                    0, 1, 0 // Axis... Y is up
                    ); 
        }

        else // 2D Camera
        {
            ortho(-1*100*Camera_Up, 100*Camera_Up, -1*100*Camera_Up, 100*Camera_Up, 0.01f, 90000); // zoom in or out depending on our Camera up-ness
            camera(0, 1000, 0, 0, 0, 0, 0, 0, 1); // camera is 1000 units up in the air
        }

        
        colorMode(RGB);
        hint(DISABLE_DEPTH_TEST); // make our following text render ontop of everything
        textMode(MODEL); // I forgot what this does
        translate(0,0,0);
        textAlign(CENTER);
        
        fill(255,255,255, clamp(0, 255, hud_alpha)); // the clamp here is for the alpha... transparency, which decreases overtime
        text("E  W  Q ", -350,0,255);
        text("\nA S D : control camera position", 0,0,255);

        fill(255,255,255, clamp(0, 255, hud_alpha*2)); // each bit of text will disappear at different rates
        text("\n\nZ, X: control camera FOV", 0,0,255);
        
        fill(255,255,255, clamp(0, 255, hud_alpha*4));
        text("\n\n\n1, 2: Change Scenes", 0,0,255);
        
        fill(255,255,255, clamp(0, 255, hud_alpha*8));
        text("\n\n\n\n3, 4: Change Perspective", 0,0,255);
        
        if (hud_alpha >= 0) // do we need to decrease the transparency?
        { 
            hud_alpha  -= 1.5f;
        }

        // For debugging... life saver
        print(String.format("\rHud Alpha : %.2f \tFOV : %.2f\t X : %.2f \t Y : %.2f \t Z : %.2f \t Left/Right Rotation : %.2f \t Up/Down Rotation : %.2f \t Camera X: %.2f \tY: %.2f \tZ: %.2f" , 
            hud_alpha, fov, Camera_Right, Camera_Up, Camera_Forward, Camera_R_Y, Camera_R_X, Camera_x_direction, Camera_y_direction, Camera_z_direction));
            
        hint(ENABLE_DEPTH_TEST); // STOP rendering on top of everything
        colorMode(HSB);
    }//end draw
}