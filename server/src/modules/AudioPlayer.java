package modules;

import java.io.File;
import java.io.FileInputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import org.apache.log4j.Logger;

public class AudioPlayer {

	private static final Logger	LOG	= Logger.getLogger(AudioPlayer.class);
	private static AudioPlayer	instance;

	private AudioPlayer() {
	}

	public static AudioPlayer getInstance() {
		if (instance == null) {
			instance = new AudioPlayer();
		}
		return instance;
	}

	public synchronized void playFile(File file, int volume) {
		if (volume <= 0 || volume > 100) {
			LOG.warn("Invalid volume, will not play");
			return;
		}
		try {
			Clip clip = AudioSystem.getClip();
			AudioInputStream inputStream = AudioSystem.getAudioInputStream(new FileInputStream(file));
			clip.open(inputStream);
			clip.start();
		} catch (Exception e) {
			LOG.error("Unable to play sound", e);
		}
	}
}
