package rits.ubi.tpoapplication;

import java.util.Date;

import jp.ac.ritsumei.cs.ubi.logger.client.api.matching.CompareOperation;
import jp.ac.ritsumei.cs.ubi.logger.client.api.matching.EventDetectionRequest;
import jp.ac.ritsumei.cs.ubi.logger.client.api.utility.MatchingConstants;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;

public class TPOApplicationSampleActivity extends Activity {
	private TextView textViewA, textViewB, textViewC, textViewD, textViewE;
	
	/**
	 * イベントを受け取るレシーバー
	 */
	private EventReciever ev = new EventReciever();
	public class EventReciever extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if("A".equals(action)){
				textViewA.setText(new Date().toString() + " | " +
						intent.getStringExtra("text"));
			}
			if("B".equals(action)){
				textViewB.setText(new Date().toString() + " | " +
						intent.getStringExtra("text"));
			}
			if("C".equals(action)){
				textViewC.setText(new Date().toString() + " | " +
						intent.getStringExtra("text"));
			}
			if("D".equals(action)){
				textViewD.setText(new Date().toString() + " | " +
						intent.getStringExtra("text"));
			}
			if("E".equals(action)){
				textViewE.setText(new Date().toString() + " | " +
						intent.getStringExtra("text"));
			}
		}
	}
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		textViewA = (TextView) findViewById(R.id.a);
		textViewB = (TextView) findViewById(R.id.b);
		textViewC = (TextView) findViewById(R.id.c);
		textViewD = (TextView) findViewById(R.id.d);
		textViewE = (TextView) findViewById(R.id.e);
		
		synchronized (ev) {
			IntentFilter filter = new IntentFilter();
			filter.addAction("A");
			filter.addAction("B");
			filter.addAction("C");
			filter.addAction("D");
			filter.addAction("E");
			registerReceiver(ev, filter);			
		}
		
		//イベントの登録
		EventDetectionRequest query[] = createEventRequestDebug();
		Intent requestIntent = new Intent(MatchingConstants.QUERY);
		requestIntent.putExtra(
				MatchingConstants.getKey(MatchingConstants.NOTIFICATION), query);
		sendBroadcast(requestIntent);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		synchronized (ev) {
			unregisterReceiver(ev);
		}
	}

	/**
	 * イベント群の作成
	 * @return イベントの配列
	 */
	public EventDetectionRequest[] createEventRequestDebug(){
		EventDetectionRequest query[] = new EventDetectionRequest[2];
		
		//3軸合成加速度の1サンプル前との差分が5.0m/s^2より小さいというイベントを10回判定する
//		Intent a = new Intent("A");
//		a.putExtra("text", "accelerometer variation is smaller than 5.0");
//		CompareOperation predicateA = CompareOperation.create(
//				MatchingConstants.DT_DOUBLE, MatchingConstants.SN_ACCELEROMETER,
//				MatchingConstants.VN_VECTOR, MatchingConstants.AN_VARIATION, 
//				MatchingConstants.SMALLER_THAN, 2.0);
//		predicateA.setNumberOfDetection(MatchingConstants.KEEP);
//		query[0] = new EventDetectionRequest( a, predicateA );
		
//		//3軸合成加速度の1サンプル前との差分が5.0m/s^2より大きいというイベントを常に判定する
//		Intent b = new Intent("B");
//		b.putExtra("text", "accelerometer variation is larger than 5.0");
//		CompareOperation predicateB = CompareOperation.create(
//				MatchingConstants.DT_DOUBLE, MatchingConstants.SN_ACCELEROMETER,
//				MatchingConstants.VN_VECTOR, MatchingConstants.AN_VARIATION, 
//				MatchingConstants.LARGER_THAN, 2.0);
//		predicateB.setNumberOfDetection(MatchingConstants.KEEP);
//		query[1] = new EventDetectionRequest( b, predicateB );

		//緯度経度の矩形の中に入っているというイベントを判定する
		//setNumberOfDetectionを呼び出さない場合のデフォルトの判定回数は1回
		
		
		double latlng[] = {34.979498724235796,135.96398160837504,34.97958855576421,135.96404049162496};
		Intent c = new Intent("C");
		c.putExtra("text", "latlng is included rectangle");
		CompareOperation predicateC = CompareOperation.create(
				MatchingConstants.DT_DOUBLE, MatchingConstants.SN_LOCATION,
				MatchingConstants.VN_LAT_LNG, MatchingConstants.SMALLER_THAN, latlng);
		predicateC.setNumberOfDetection(MatchingConstants.KEEP);
		query[0] = new EventDetectionRequest( c, predicateC );
		
		double latlng2[] = {34.98184714901306,135.96286552753512,34.98207426837558,135.96304563168258};
		Intent d = new Intent("D");
		d.putExtra("text", "latlng is included rectangle");
		CompareOperation predicateD = CompareOperation.create(
				MatchingConstants.DT_DOUBLE, MatchingConstants.SN_LOCATION,
				MatchingConstants.VN_LAT_LNG, MatchingConstants.SMALLER_THAN, latlng2);
		predicateD.setNumberOfDetection(MatchingConstants.KEEP);
		query[1] = new EventDetectionRequest( d, predicateD );
		
		//NISHIO_SOTSUKEN_B&Gが聞こえているというイベント
//		Intent d = new Intent("D");
//		c.putExtra("text", "NISHIO_SOTSUKEN_B is heard");
//		CompareOperation predicateD = CompareOperation.create(
//				MatchingConstants.DT_STRING, MatchingConstants.SN_WiFi,
//				MatchingConstants.VN_ESSID, MatchingConstants.EQUAL, "aterm-298b2f-g");//"NISHIO_SOTSUKEN_B&G"
//
//		query[3] = new EventDetectionRequest( d, predicateD );
		
		//精度のいいLocationの速度が車の速度の範囲に収まっているイベント
//		Intent e = new Intent("E");
//		e.putExtra("text", "may be car");
//		CompareOperation larger = CompareOperation.create(
//				MatchingConstants.DT_DOUBLE, MatchingConstants.SN_LOCATION,
//				MatchingConstants.VN_SPEED, MatchingConstants.LARGER_THAN, 5.0);
//		CompareOperation smaller = CompareOperation.create(
//				MatchingConstants.DT_DOUBLE, MatchingConstants.SN_LOCATION,
//				MatchingConstants.VN_SPEED, MatchingConstants.SMALLER_THAN, 15.0);
//		LogicOperation speed = LogicOperation.and(larger, smaller);
//		
//		CompareOperation accuracy = CompareOperation.create(
//				MatchingConstants.DT_DOUBLE, MatchingConstants.SN_LOCATION,
//				MatchingConstants.VN_ACCURACY, MatchingConstants.SMALLER_THAN, 10.0);
//		LogicOperation speedOfCar = LogicOperation.and(speed, accuracy);
//		speedOfCar.setNumberOfDetection(10);
//		query[4] = new EventDetectionRequest( e, speedOfCar );
		
		return query;
	}
}