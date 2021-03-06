package jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.plan;

import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.utils.MyGeoPoint;

/**
 * 乗り換えポイントを表現するクラス．
 * @author sacchin
 *
 */
public class TransferPoint extends MyGeoPoint{
	/**
	 * この乗り換えポイントが含まれる単一の移動手段が存在する，移動手段の組み合わせ内での位置．
	 */
	private int order;
	
	/**
	 * 出発地点となる立ち寄りポイントID．
	 */
	private long fromid;
	
	/**
	 * 到着地点となる立ち寄りポイントID．
	 */
	private long toid;
	
	/**
	 * この乗り換えポイントが含まれる単一の移動手段が存在する，移動手段の組み合わせの名前．
	 */
	private String transportationNames;

	/**
	 * コンストラクタ．
	 * @param mostLowLat
	 * @param mostLowLng
	 * @param mostHighLat
	 * @param mostHighLng
	 * @param order　この乗り換えポイントが含まれる単一の移動手段が存在する，移動手段の組み合わせ内での位置．
	 * @param fromid 出発地点となる立ち寄りポイントID．
	 * @param toid　到着地点となる立ち寄りポイントID．
	 * @param transportationNames この乗り換えポイントが含まれる単一の移動手段が存在する，移動手段の組み合わせの名前．
	 */
	public TransferPoint(double mostLowLat, double mostLowLng,
			double mostHighLat, double mostHighLng, int order, 
			long fromid, long toid, String transportationNames) {
		super(mostLowLat, mostLowLng, mostHighLat, mostHighLng);
		this.order = order;
		this.fromid = fromid;
		this.toid = toid;
		this.transportationNames = transportationNames;
	}

	public String getTransportationNames() {
		return transportationNames;
	}

	public int getOrder() {
		return order;
	}

	public long getFromid() {
		return fromid;
	}

	public long getToid() {
		return toid;
	}
}
