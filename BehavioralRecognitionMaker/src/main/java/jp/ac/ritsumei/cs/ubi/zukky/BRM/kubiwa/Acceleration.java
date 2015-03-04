package jp.ac.ritsumei.cs.ubi.zukky.BRM.kubiwa;

import jp.ac.ritsumei.cs.ubi.library.accelerometer.AccelerometerObject;


/**
 * 加速度に関する情報を保持するクラス
 * @author zukky
 *
 */
public class Acceleration extends AccelerometerObject{
	
	private boolean step;
	private long time;
	
	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public boolean isStep() {
		return step;
	}

	public void setStep(boolean step) {
		this.step = step;
	}

	public Acceleration(long t,  float x, float y, float z) {
		super(t, x, y, z);
	}

	public Acceleration(Acceleration acc){
		super(acc.getT(), acc.getX(), acc.getY(), acc.getZ());
		this.time = acc.getTime();
	}
	
	public Acceleration(){
		super(0, 0, 0, 0);
	}
}
