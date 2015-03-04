package jp.ac.ritsumei.cs.ubi.zukky.BRM.util;

public class Util{	
	
	public enum VelocityType {
		DOPPLER, HUBENY
	}
	
	private final static double SMOOTHING_RATE = 0.05;
	private final static  double SMOOTHING_RATE_CHECKPOINT = 0.12;
	
	//pribate final String filePath = 

	public static double getSmoothingRate() {
		return SMOOTHING_RATE;
	}

	public static double getSmoothingRateCheckPoint() {
		return SMOOTHING_RATE_CHECKPOINT;
	}
}
