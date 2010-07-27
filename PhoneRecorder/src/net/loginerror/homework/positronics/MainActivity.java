package net.loginerror.homework.positronics;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import java.lang.Math;


/**
 * Android softphone recording proof of concept
 * @author Arlen Phillips
 *
 * This proof of concept fills a buffer with fake data, 
 * in this case a sine wave, and then writes it to the root of
 * the SD card as a WAV file. Android can only write 3GP and MP4
 * formats nativly and cannot easily write them from a buffer.
 * Due to this and the complexity of MP3 
 * I chose to use WAV for this proof of concept.
 *
 */
public class MainActivity extends Activity implements OnClickListener{

	MediaRecorder recorder;
	boolean recording = false;
	TextView text;
	String fileName;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        Button recButton = (Button)this.findViewById(R.id.RecordButton);
        text = (TextView)this.findViewById(R.id.RecordText);
        recButton.setOnClickListener(this);
        recorder = new MediaRecorder();
    }
	
	
	/**
	 * Fills the buffer with a simple yet annoying sine wave.
	 * @param data buffer of bytes to be filled
	 */
	public void fillBuffer(byte[] data)
	{
		
		for (int i = 0; i < data.length; i++) {
			
			data[i] = (byte)(Math.sin(i) * 128);
		}
	}
	
	
	/** 
	* WAV writing code modified from part of the ringdroid open source project
	* found at http://code.google.com/p/ringdroid/
	*
	*    Licensed under the Apache License, Version 2.0 (the "License");
	*	 you may not use this file except in compliance with the License.
 	*    You may obtain a copy of the License at
 	*
 	*      http://www.apache.org/licenses/LICENSE-2.0
 	*      
 	*      Writes an 8 bit mono channel WAV file.
 	* 
 	* @param outputFile file to write data to
 	* @param input array of bytes containing 8 bit 11025 Hz sound data
	**/
	public void WriteWAVFile(File outputFile, byte[] input)
     throws java.io.IOException {

	 outputFile.createNewFile();
	 FileOutputStream out = new FileOutputStream(outputFile);
	 
	 int totalAudioLen = (int)(input.length);

	 long totalDataLen = totalAudioLen + 36;
	 long longSampleRate = 11025;
	 long byteRate = 11025 * 2 * 1; //mono, 1 channel
	
	 byte[] header = new byte[44];
	 header[0] = 'R';  // RIFF/WAVE header
	 header[1] = 'I';
	 header[2] = 'F';
	 header[3] = 'F';
	 header[4] = (byte) (totalDataLen & 0xff);
	 header[5] = (byte) ((totalDataLen >> 8) & 0xff);
	 header[6] = (byte) ((totalDataLen >> 16) & 0xff);
	 header[7] = (byte) ((totalDataLen >> 24) & 0xff);
	 header[8] = 'W';
	 header[9] = 'A';
	 header[10] = 'V';
	 header[11] = 'E';
	 header[12] = 'f';  // 'fmt ' chunk
	 header[13] = 'm';
	 header[14] = 't';
	 header[15] = ' ';
	 header[16] = 16;  // 4 bytes: size of 'fmt ' chunk
	 header[17] = 0;
	 header[18] = 0;
	 header[19] = 0;
	 header[20] = 1;  // format = 1
	 header[21] = 0;
	 header[22] = (byte) 1;
	 header[23] = 0;
	 header[24] = (byte) (longSampleRate & 0xff);
	 header[25] = (byte) ((longSampleRate >> 8) & 0xff);
	 header[26] = (byte) ((longSampleRate >> 16) & 0xff);
	 header[27] = (byte) ((longSampleRate >> 24) & 0xff);
	 header[28] = (byte) (byteRate & 0xff);
	 header[29] = (byte) ((byteRate >> 8) & 0xff);
	 header[30] = (byte) ((byteRate >> 16) & 0xff);
	 header[31] = (byte) ((byteRate >> 24) & 0xff);
	 header[32] = (byte) (2);  // block align
	 header[33] = 0;
	 header[34] = 8;  // bits per sample
	 header[35] = 0;
	 header[36] = 'd';
	 header[37] = 'a';
	 header[38] = 't';
	 header[39] = 'a';
	 header[40] = (byte) (totalAudioLen & 0xff);
	 header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
	 header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
	 header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
	 out.write(header, 0, 44);
	 
	 //quick and dirty buffer writing
	 for (int i = 0; i < input.length; i++) {
		
		 out.write(input,i,1);
	 }

	 out.close();
	}

    /**
     * On click listener for the test button.
     * Always saves a new file when clicked.
     */
	@Override
	public void onClick(View v) {
		
		Button button = (Button)v;
		
		button.setText("Saving");
				
		File output = new File( Environment.getExternalStorageDirectory(),"TestAudio.wav");
    	
    	byte[] data = new byte[10000]; //arbitrary buffer size
    	
    	fillBuffer(data);//fill the buffer with a sine wave
    	
    	try {
			WriteWAVFile(output, data);//try writing it to a WAV file
			text.setText("Wrote " + output.getAbsolutePath());
		} catch (IOException e) {
			text.setText("File IO Error! Make sure you have a SD card");
			e.printStackTrace();
		}

		button.setText("Push Me");

	}

	
	
}