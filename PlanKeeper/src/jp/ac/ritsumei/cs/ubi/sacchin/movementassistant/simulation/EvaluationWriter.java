package jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.simulation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import android.os.Environment;
import android.util.Log;

public class EvaluationWriter {
	private File directory;
	private String folderName = "/SimulateEvaluation";
	
	private BufferedWriter bw = null;
	
	public EvaluationWriter(){
		directory = Environment.getExternalStorageDirectory();
		if (directory.exists() && directory.canWrite()){
			File file = new File(directory.getAbsolutePath() + folderName);
			Log.v("EvaluationWriter", "-" + file.mkdir());
		}
	}

	public void openFile(String sFileName) {
		if(bw != null){
			close();
		}
		
		String filepath = directory.getAbsolutePath() + folderName + "/" + sFileName;
		try {
		    bw = new BufferedWriter(new OutputStreamWriter(
		            new FileOutputStream(filepath, true), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
		    e.printStackTrace();
		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		}
	}
	
	public void close(){
		if(bw != null){
		    try {
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		bw = null;
	}
	
	public void println(String text){
		if(bw != null){
		    try {
				bw.write(text + "\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
