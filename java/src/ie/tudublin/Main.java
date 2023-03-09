// By Oisin Kiely, Cesar Hannin, Stephen Thompson, Liam

package ie.tudublin;

import java.io.File;
import java.util.Random;

import example.CubeVisual;
import Custom.Party_Manager;
import example.MyVisual;
import example.RotatingAudioBands;

public class Main
{	

	public void startUI()
	{
		String[] a = {"Custom"};

		/* Im going to try set up a thing where different visualisors can be loaded up and ready to switch to... so far this will have to do */
		//Visual visualisor = new CubeVisual();	// This is the 3 cubes and duck
		//Visual visualisor = new MyVisual();	// simple lines going up and down
		Visual visualisor = new RotatingAudioBands();	// the one where you can switch how the circle surrounding the duck looks
		
		visualisor.Set_Window_Size(1000,1000); // only works for RotatingAudioBands and CubeVisual
		visualisor.set_bands_count(20); // Anything more than 20 TANKS performance

		// Choose a song at Random
		File[] potential_songs = new File("data/Music/").listFiles(); // make a list of all songs in our music folder
		Random random = new Random(); // WHY DO WE INITIALISE RANDOM LIKE THIS??? Why Java? 
		String chosen_song = "Music/" + potential_songs[random.nextInt(potential_songs.length)].getName(); 
		System.out.println("Going to play " + chosen_song);
		
		visualisor.Set_Song_Path(chosen_song); // tell the visulaiser what our chosen song file's path is
        processing.core.PApplet.runSketch( a, visualisor); 
	}

	public static void main(String[] args)
	{
		Main main = new Main();
		/* Eh, not important rn */
		//Party_Manager party_Manager = new Party_Manager();
		//System.out.println(party_Manager.test());
		main.startUI();			
	}
}