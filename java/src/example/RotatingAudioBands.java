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
    PGraphics HUD;
    Boolean do_Spiral = false;
    Boolean Ortho_Camera = false;
    float adjust_back_and_forth = 1;
    
    // 3D Camera
    float fov = 70; // field of view
    float Camera_Right = 0; // Right-Left Movement
    float Camera_Forward = 1000; 
    float Camera_Up = -300;
    float Camera_Movement_Speed;
    float Camera_Sensitivity = 10;
    float Camera_R_X = 0; //Rotate Camera up and down (degrees)
    float Camera_R_Y = 0; //Rotate Camera Left and right (degrees)
    float Camera_x_direction; // which way is the camera facing? (Vector)
    float Camera_y_direction;
    float Camera_z_direction;
    float Camera_focus_at_X;
    float Camera_focus_at_Y;
    float Camera_focus_at_Z;
    boolean free_cam = false;
    
    


    public void settings()
    {
        size(Get_Window_Width(), Get_Window_Height(), P3D);
        println("CWD: " + System.getProperty("user.dir"));
        //fullScreen(P3D, SPAN);
    }

    public void keyPressed()
    {
        // --- Keycodes ---
        // Down arrow   : 40
        // Right arrow  : 39
        // Up arrow     : 38
        // Left arrow   : 37
        
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
            Camera_Up = -300;
        }
        if (key == '4')
        {
            Ortho_Camera = true;
            Camera_Up = 10;
        }

        // Movement Control
        if (key == 'w')
        {
            Camera_Forward -= Camera_Movement_Speed;
        }
        if (key == 'a')
        {
            Camera_Right -= Camera_Movement_Speed;
        }
        if (key == 's')
        {
            Camera_Forward += Camera_Movement_Speed;
        }
        if (key == 'd')
        {
            Camera_Right += Camera_Movement_Speed;
        }
        if (key == 'e')
        {
            Camera_Up += Camera_Movement_Speed;
        }
        if (key == 'q')
        {
            Camera_Up -= Camera_Movement_Speed;
        }
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

        if (key == 'o')
        {
            Mesh_Manager test = new Mesh_Manager();
            println("\n" + test.Find_File() );
        }
    }

    

    public void setup()
    {
        colorMode(HSB);
        
        setFrameSize(1024); 
    
        ducky = loadShape("C:/TUD/OneDrive - Technological University Dublin/Documents/College/Year 2/OOP/Project/MusicVisuals/java/data/Shapes/Rubber_Ducky.obj" );
        
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
    float hud_alpha = 1750;

    private float clamp(float min, float max, float value)
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


    private float loop_value(float min, float max, float value) // Sorry This sucks... please make a better one if you guys can? - Cesar
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

    
    
    private float toDegrees(float rad)
    {
        return (rad * 57.2958f);
    }

    private float toRad(float degree)
    {
        return 0.01745f * degree;
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
        
        // Are we in a 3D or 2D camera?
        if (Ortho_Camera)
        {
            rings = 15;
            sensitivity = 1.4f;
            Camera_Movement_Speed = 0.05f;
            
            radius = 450;
            rotateZ(PI/2);
            translate(0,-60,0);
        }
        else
        {
            rings = 75;
            sensitivity = 0.05f;
            Camera_Movement_Speed = 10f;
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
        

        
        
        
        //camera stuff
        if(!Ortho_Camera)
        {   
            perspective( (fov*0.0174533f) ,1,0.01f, 90000f); //we dealing with radians here
            
            if (free_cam == true)
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
            camera( Camera_Right, Camera_Up, Camera_Forward, 
                    Camera_focus_at_X, Camera_focus_at_Y, Camera_focus_at_Z,
                    0, 1, 0 // Axis
                    ); 
        }
        else
        {
            ortho(-1*100*Camera_Up, 100*Camera_Up, -1*100*Camera_Up, 100*Camera_Up, 0.01f, 90000);
            camera(0, 1000, 0, 0, 0, 0, 0, 0, 1); 
        }

        
        colorMode(RGB);
        hint(DISABLE_DEPTH_TEST);
        textMode(MODEL);
        translate(0,0,0);
        textAlign(CENTER);
        //rotateX(PI);
        
        fill(255,255,255, clamp(0, 255, hud_alpha));
        text("E  W  Q ", -350,0,255);
        text("\nA S D : control camera position", 0,0,255);

        fill(255,255,255, clamp(0, 255, hud_alpha*2));
        text("\n\nZ, X: control camera FOV", 0,0,255);
        
        fill(255,255,255, clamp(0, 255, hud_alpha*4));
        text("\n\n\n1, 2: Change Scenes", 0,0,255);
        
        fill(255,255,255, clamp(0, 255, hud_alpha*8));
        text("\n\n\n\n3, 4: Change Perspective", 0,0,255);
        
        if (hud_alpha >= 0)
        { 
            hud_alpha  -= 1.5f;
        }
        print(String.format("\rHud Alpha : %.2f \tFOV : %.2f\t X : %.2f \t Y : %.2f \t Z : %.2f \t Left/Right Rotation : %.2f \t Up/Down Rotation : %.2f \t Camera X: %.2f \tY: %.2f \tZ: %.2f" , 
            hud_alpha, fov, Camera_Right, Camera_Up, Camera_Forward, Camera_R_Y, Camera_R_X, Camera_x_direction, Camera_y_direction, Camera_z_direction));
        hint(ENABLE_DEPTH_TEST);
        colorMode(HSB);
        
    }
    

}