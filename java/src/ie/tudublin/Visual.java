package ie.tudublin;

import processing.core.PApplet;
import ddf.minim.*;
import ddf.minim.analysis.FFT;

public abstract class Visual extends PApplet
{
	private int frameSize = 512;
	private int sampleRate = 44100;

	private String song_path = "Music/Death Grips - Klink.mp3"; // If no song is ever set... play Klink by default
	// Default Window sizes
	private int Window_Width = 400;
	private int Window_Height = 400;

	private float[] bands;
	private float[] smoothedBands;

	private Minim minim;
	private AudioInput ai;
	private AudioPlayer ap;
	private AudioBuffer ab;
	private FFT fft;
	//private int bands_count;

	private String song_path = "";

	private float amplitude  = 0;
	private float smothedAmplitude = 0;

	
	
	public void startMinim() 
	{
		minim = new Minim(this);

		fft = new FFT(frameSize, sampleRate);

		bands = new float[15]; // The more than 15 means less bands, less than 15 means more bands... less bands, more performance
		println("YOU GOT "+ bands.length + " BANDS");
		
  		smoothedBands = new float[bands.length];

	}

	float log2(float f) {
		return log(f) / log(2.0f);
	}

	protected void calculateFFT() throws VisualException
	{
		fft.window(FFT.HAMMING);
		if (ab != null)
		{
			fft.forward(ab);
		}
		else
		{
			throw new VisualException("You must call startListening or loadAudio before calling fft");
		}
	}

	public int get_bands_count() // dont need anymore... scared to delete
	{
		return bands.length;
	}

	public void set_bands_count(int total)
	{
		//bands_count = total;
		bands = new float[total];
		smoothedBands = new float[bands.length];
	}

	public void calculateAverageAmplitude()
	{
		float total = 0;
		
		for(int i = 0 ; i < ab.size() ; i ++)
        {
			total += abs(ab.get(i));
		}
		amplitude = total / ab.size();
		smothedAmplitude = PApplet.lerp(smothedAmplitude, amplitude, 0.1f);
	}


	// Window Control Stuff
	// Width
	public int Get_Window_Width()
	{
		return Window_Width;
	}
	
	// Height
	public int Get_Window_Height()
	{
		return Window_Height;
	}
	
	public void Set_Window_Size(int w, int h)
	{
		Window_Height = h;
		Window_Width = w;
	}
	
	// Song path
	public String Get_Song_Path()
	{
		return song_path;
	}
	public void Set_Song_Path(String song_to_find)
	{
		song_path = song_to_find;
	}


	protected void calculateFrequencyBands() {
		for (int i = 0; i < bands.length; i++) {
			int start = (int) pow(2, i) - 1;
			int w = (int) pow(2, i);
			int end = start + w;
			float average = 0;
			for (int j = start; j < end; j++) {
				average += fft.getBand(j) * (j + 1);
			}
			average /= (float) w;
			bands[i] = average * 5.0f;
			smoothedBands[i] = lerp(smoothedBands[i], bands[i], 0.05f);
		}
	}

	public void startListening()
	{
		ai = minim.getLineIn(Minim.MONO, frameSize, 44100, 16);
		ab = ai.left;
	}

	public void loadAudio(String filename)
	{
		ap = minim.loadFile(filename, frameSize);
		ab = ap.mix;
	}

	public int getFrameSize() {
		return frameSize;
	}

	public void setFrameSize(int frameSize) {
		this.frameSize = frameSize;
	}

	public int getSampleRate() {
		return sampleRate;
	}

	public void setSampleRate(int sampleRate) {
		this.sampleRate = sampleRate;
	}

	public float[] getBands() {
		return bands;
	}

	public float[] getSmoothedBands() {
		return smoothedBands;
	}

	public Minim getMinim() {
		return minim;
	}

	public AudioInput getAudioInput() {
		return ai;
	}


	public AudioBuffer getAudioBuffer() {
		return ab;
	}

	public float getAmplitude() {
		return amplitude;
	}

	public float getSmoothedAmplitude() {
		return smothedAmplitude;
	}

	public AudioPlayer getAudioPlayer() {
		return ap;
	}

	public FFT getFFT() {
		return fft;
	}
}
