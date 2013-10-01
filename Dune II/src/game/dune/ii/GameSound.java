package game.dune.ii;

import java.io.IOException;
import java.util.HashMap;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;

import android.util.Log;

public class GameSound {

	// ===========================================================
	// Constants
	// ===========================================================

	public enum Sounds {Acknowledged, Affirmative, Affirmitive, Click, Cannot, Credit};
	public enum MusicTracks {Under_Construction};

	// ===========================================================
	// Fields
	// ===========================================================

	public static GameSound sound;
	
	private ActivityGame game;

	private HashMap<Sounds, Sound> soundTable = new HashMap<Sounds, Sound>();
	private HashMap<MusicTracks, Music> musicTable = new HashMap<MusicTracks, Music>();

	// ===========================================================
	// Constructors
	// ===========================================================

	/**
	 * Manages the sound and music for the game
	 * @param game		- The game activity
	 */
	public GameSound(ActivityGame game) {
		this.game = game;
		sound = this;
	}

	// ===========================================================
	// Getters & Setters
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Non-inherited Methods
	// ===========================================================

	/**
	 * Loads all the game sounds into memory
	 */
	public void LoadSounds()
	{
		SoundFactory.setAssetBasePath(Globals.SOUND_PATH);
		
		loadSound(Sounds.Acknowledged, 	"units/acknowledged.wav");
		loadSound(Sounds.Affirmative, 	"units/affirmative.wav");
		loadSound(Sounds.Affirmitive, 	"units/affirmitive.wav");
		loadSound(Sounds.Click, 		"units/click.wav");
		loadSound(Sounds.Cannot, 		"units/cannot.wav");
		loadSound(Sounds.Credit, 		"credit.wav");
		
		MusicFactory.setAssetBasePath(Globals.MUSIC_PATH);
		
		loadMusicTrack(MusicTracks.Under_Construction, "under_construction.mp3");
	};

	/**
	 * Loads a game sound into memory
	 * @param sound_id 	- The index of the sound in the manager
	 * @param file		- The file containing the sound
	 */
	private void loadSound(Sounds sound_id, String file){
		try {
			soundTable.put(sound_id, SoundFactory.createSoundFromAsset(game.getEngine().getSoundManager(), game, file));
		} catch (final IOException e) {
			Log.e("SOUND", "Sound not loaded: " + e.getMessage());
		}
	}
	
	/**
	 * Loads music track into memory
	 * @param music_id 	- The index of the track in the manager
	 * @param file		- The file containing the track
	 */
	private void loadMusicTrack(MusicTracks music_id, String file){
		try {
			musicTable.put(music_id, MusicFactory.createMusicFromAsset(game.getEngine().getMusicManager(), game, file));
		} catch (final IOException e) {
			Log.e("SOUND", "Music track not loaded: " + e.getMessage());
		}
	}
	
	/**
	 * Plays a sound
	 * @param sound_id 	- The index of the sound in the manager
	 */
	public void playSound(Sounds sound_id){
		try {
			soundTable.get(sound_id).play();
		} catch (final Exception e) {
			Log.e("SOUND", "Error playing sound " + sound_id.toString() );
		}
	};
	
	/**
	 * Plays a music track
	 * @param music_id 	- The index of the track in the manager
	 */
	public void playMusic(MusicTracks music_id){
		try {
			musicTable.get(music_id).setLooping(true);
			musicTable.get(music_id).play();
		} catch (final Exception e) {
			Log.e("SOUND", "Error playing track " + music_id.toString() );
		}
	};
	
	/**
	 * Fades the current music track from silence
	 */
	public void fadeInMusic(){};
	
	/**
	 * Fades the current music track to silence
	 */
	public void fadeOutMusic(){};
}
