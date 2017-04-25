/**
 * (c) Copyright 2008, Dr. Walter W. Schilling, Jr.
 * ALL RIGHTS RESERVED 
 * Permission to use, copy, modify, and distribute this software for 
 * any purpose and without fee is hereby granted, provided that the above
 * copyright notice appear in all copies and that both the copyright notice
 * and this permission notice appear in supporting documentation, and that 
 * the name of Walter W. Schilling, Jr. not be used in advertising
 * or publicity pertaining to distribution of the software without specific,
 * written prior permission. 
 *
 * THE MATERIAL EMBODIED ON THIS SOFTWARE IS PROVIDED TO YOU "AS-IS"
 * AND WITHOUT WARRANTY OF ANY KIND, EXPRESS, IMPLIED OR OTHERWISE,
 * INCLUDING WITHOUT LIMITATION, ANY WARRANTY OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE.  IN NO EVENT SHALL MSOE
 * BE LIABLE TO YOU OR ANYONE ELSE FOR ANY DIRECT,
 * SPECIAL, INCIDENTAL, INDIRECT OR CONSEQUENTIAL DAMAGES OF ANY
 * KIND, OR ANY DAMAGES WHATSOEVER, INCLUDING WITHOUT LIMITATION,
 * LOSS OF PROFIT, LOSS OF USE, SAVINGS OR REVENUE, OR THE CLAIMS OF
 * THIRD PARTIES, WHETHER OR NOT WALTER SCHILLING HAS BEEN
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS, HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, ARISING OUT OF OR IN CONNECTION WITH THE
 * POSSESSION, USE OR PERFORMANCE OF THIS SOFTWARE. 
 * 
 * @version $Rev:: 3                       $:  Revision of last commit
 * @author  $Author:: schilling            $:  Author of last commit
 * $Date:: 2008-10-20 19:51:02 -0#$:  Date of last commit
 * $Log$:
 * 
 * This class will play one of three audio wave file, an error music song, a happy music song, and a sad music song.
 * 
 */


import java.io.File;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioErrorPlayer implements StockTickerAudioInterface {
	enum Position {
		LEFT, RIGHT, NORMAL
	};

	/**
	 * @author schilling This is a private class that will handle the playing of
	 *         a wave file. It is a Runnable class, so it requires its own
	 *         thread be spawned.
	 */
	private static class WavePlayer implements Runnable {
		private static final Lock mutex = new ReentrantLock();

		private String filename;

		private Position curPosition;

		private final int EXTERNAL_BUFFER_SIZE = 524288; // 128Kb

		/**
		 * @param wavfile
		 *            The name of the audio file that is to be played.
		 */
		public WavePlayer(String wavfile) {
			this(wavfile, Position.NORMAL);
			filename = wavfile;
			curPosition = Position.NORMAL;
		}

		/**
		 * 
		 * @param wavfile
		 *            The name of the audio file to play.
		 * @param p
		 */
		public WavePlayer(String wavfile, Position p) {
			filename = wavfile;
			curPosition = p;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			File soundFile = new File(filename);
			if (!soundFile.exists()) {
				System.err.println("Wave file not found: " + filename);
				return;
			}

			mutex.lock();

			AudioInputStream audioInputStream = null;
			try {
				audioInputStream = AudioSystem.getAudioInputStream(soundFile);
			} catch (UnsupportedAudioFileException e1) {
				e1.printStackTrace();
				return;
			} catch (IOException e1) {
				e1.printStackTrace();
				return;
			}

			AudioFormat format = audioInputStream.getFormat();
			SourceDataLine auline = null;
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

			try {
				auline = (SourceDataLine) AudioSystem.getLine(info);
				auline.open(format);
			} catch (LineUnavailableException e) {
				e.printStackTrace();
				return;
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}

			if (auline.isControlSupported(FloatControl.Type.PAN)) {
				FloatControl pan = (FloatControl) auline.getControl(FloatControl.Type.PAN);
				if (curPosition == Position.RIGHT)
					pan.setValue(1.0f);
				else if (curPosition == Position.LEFT)
					pan.setValue(-1.0f);
			}

			auline.start();
			int nBytesRead = 0;
			byte[] abData = new byte[EXTERNAL_BUFFER_SIZE];

			try {
				while (nBytesRead != -1) {
					nBytesRead = audioInputStream.read(abData, 0, abData.length);
					if (nBytesRead >= 0)
						auline.write(abData, 0, nBytesRead);
				}
			} catch (IOException e) {
				e.printStackTrace();
				return;
			} finally {
				auline.drain();
				auline.close();
			}
			mutex.unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.msoe.se2831.lab6.StockTickerAudioInterface#playErrorMusic()
	 */
	public void playErrorMusic() {
		WavePlayer wp = new WavePlayer("apollo-failureisnotanoption.wav");
		new Thread(wp).start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.msoe.se2831.lab6.StockTickerAudioInterface#playHappyMusic()
	 */
	public void playHappyMusic() {
		WavePlayer wp = new WavePlayer("money.wav");
		new Thread(wp).start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.msoe.se2831.lab6.StockTickerAudioInterface#playSadMusic()
	 */
	public void playSadMusic() {
		WavePlayer wp = new WavePlayer("GRR.WAV");
		new Thread(wp).start();

	}
}
