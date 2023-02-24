package ie.tudublin;

import java.io.File;
import java.util.Random;

//import example.CubeVisual;
import Custom.Party_Manager;
//import example.MyVisual;
import example.RotatingAudioBands;

public class Main
{	

	public void startUI()
	{
		String[] a = {"Custom"};

		//Visual visualisor = new CubeVisual();
		//Visual visualisor = new MyVisual();
		Visual visualisor = new RotatingAudioBands();
		
		visualisor.Set_Window_Size(800);
		
		// Choose a song at Random
		File[] potential_songs = new File("data/Music/").listFiles();
		Random random = new Random();
		String chosen_song = "Music/" + potential_songs[random.nextInt(potential_songs.length)].getName();
		System.out.println("Going to play " + chosen_song);
		
		visualisor.Set_Song_Path(chosen_song);
        processing.core.PApplet.runSketch( a, visualisor);		
	}

	public static void main(String[] args)
	{
		Main main = new Main();
		Party_Manager party_Manager = new Party_Manager();
		System.out.println(party_Manager.test());
		main.startUI();			
	}
}