package game.dune.ii;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;

public class MenuSound {

	private Activity activity;

	private SoundPool sound_pool;
	private MediaPlayer media_player;

	private int good_click;
	private int bad_click;
	private int alert;
	
	private int music_id;


	public MenuSound(Activity activity) {
		this.activity = activity;
		this.sound_pool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);

		//Enable the volume buttons
        activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        
		good_click = sound_pool.load(activity, R.raw.good_click, 1);
		bad_click = sound_pool.load(activity, R.raw.bad_click, 1);
		alert = sound_pool.load(activity, R.raw.alert, 1);
	}

	public void playGoodClick() {
		playSound(good_click);
	}
	
	public void playBadClick() {
		playSound(bad_click);
	}

	public void playAlert() {
		playSound(alert);
	}

	private void playSound(int soundID) {
		//If the sound system has not yet been setup, then bail
		if(sound_pool == null)
		{
			Log.e("SOUND", "Sound " + soundID + " attempted to play prior to sound system initialization.");
			return;
		}
		
//        float volume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) / audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

		float volume = 1;//....// whatever in the range = 0.0 to 1.0

		// play sound with same right and left volume, with a priority of 1,
		// zero repeats (i.e play once), and a playback rate of 1f
		sound_pool.play(soundID, volume, volume, 1, 0, 1f);
	}
	
	public void playMusic(int sound)
	{
		music_id = sound;
		media_player = MediaPlayer.create(activity, sound);
		media_player.setAudioStreamType(AudioManager.STREAM_MUSIC);
		media_player.setLooping(true);
		media_player.start();	
	}
	
	public void pause()
	{
		fadeOutMusic();
	}
	
	public void resume()
	{
       if(music_id != -1) 
    	   fadeInMusic(music_id);
	}

	public void fadeInMusic(int sound_id) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
			   for(float i=0;i<1;i=i+0.1f)
			   {
				   try {
					   Thread.sleep(100);
					   media_player.setVolume(i, i);
				   } catch (Exception e) {
					   Log.v("Error", e.toString());
				   }
			   }
			}
		});
		
		playMusic(sound_id);
		thread.start();
	}
	
	public void fadeOutMusic() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
			   for(float i=1;i>0;i=i-0.1f)
			   {
				   try {
					   Thread.sleep(100);
					   media_player.setVolume(i, i);
				   } catch (Exception e) {
					   Log.v("Error", e.toString());
				   }
			   }
				media_player.stop();
				media_player.release();
			}
		});
		thread.start();
	}
}
