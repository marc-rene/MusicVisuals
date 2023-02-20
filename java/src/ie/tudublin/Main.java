package ie.tudublin;

import example.CubeVisual;
import example.MyVisual;
import example.RotatingAudioBands;

public class Main
{	

	public void startUI()
	{
		String[] a = {"MAIN"};

		Visual visualisor = new CubeVisual();
		//Visual visualisor = new MyVisual();
		//Visual visualisor = new RotatingAudioBands();
		
		visualisor.Set_Window_Size(800);
		visualisor.Set_Song_Path("Music/Death Grips - Spread Eagle Cross the Block.mp3");
        processing.core.PApplet.runSketch( a, visualisor);		
	}

	public static void main(String[] args)
	{
		Main main = new Main();
		main.startUI();			
	}
}