package jp.ac.ritsumei.cs.ubi.zukky.BRM.kubiwa;

import java.sql.Timestamp;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 * Kubiwa DBを操作するHelperクラス
 * どう動いているかはkubiwa.cfg.xml, KubiwaUser.hbm.xmlあたりを見れば分かるはず
 * @see http://www.techscore.com/tech/Java/Others/Hibernate/index/
 * @author SoichiroHorimi
 */

public class KubiwaHelper{ 
    private Session getSession() {
        Configuration f = new Configuration();
        Configuration c = f.configure("jp/ac/ritsumei/cs/ubi/zukky/BRM/util/kubiwa.cfg.xml");
        @SuppressWarnings("deprecation")
		SessionFactory sessionFactory = c.buildSessionFactory();
        return sessionFactory.openSession();
    }
    
    /**
     * 指定した期間のGPSの値をDBから取得する関数
     * @param devId
     * @param sTime 取得したいGPSの期間の開始時刻
     * @param eTime　取得したいGPSの期間の終了時刻
     * @return 取得したいGPSのリスト
     */
    
    //おそらくListをキャストしてないことによる警告が出てる
    @SuppressWarnings("unchecked")
    public List<Gps> getGps(int devId, Timestamp sTime, Timestamp eTime){
    	Criteria c=getSession().createCriteria(Gps.class);
        //検索条件を設定．MySQLで"WHERE userid=..."と指定するみたいなイメージ
    	c.add(Restrictions.eq("devId", devId));
    	c.add(Restrictions.between("time", sTime, eTime));
    	c.addOrder(Order.asc("time"));
    	
        //useridに紐づいたデバイスID一覧を返す
        return c.list();
    }
    
    @SuppressWarnings("unchecked")
	public List<Gps> getGpsPlace(int devId, Timestamp sTime, Timestamp eTime, 
			double latLow, double latHigh, double lngLow, double lngHigh){
    	Criteria c=getSession().createCriteria(Gps.class);
        //検索条件を設定．MySQLで"WHERE userid=..."と指定するみたいなイメージ
    	c.add(Restrictions.eq("devId", devId));
    	c.add(Restrictions.between("time", sTime, eTime));
    	c.add(Restrictions.between("lat", latLow, latHigh));
    	c.add(Restrictions.between("lng", lngLow, lngHigh));
    	c.addOrder(Order.asc("time"));
    	
    	return c.list();
    }
    
    /**
     * 
     * @param id
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Device> getDevicesTest(){
    	Criteria c=getSession().createCriteria(Device.class);
        //検索条件を設定．MySQLで"WHERE userid=..."と指定するみたいなイメージ
    	c.add(Restrictions.eq("devName", "vino"));
    	c.add(Restrictions.between("devId", 0, 10));
    	
        //useridに紐づいたデバイスID一覧を返す
		return c.list();
    }
    
//    /**
//     * 指定したKubiwaIDに紐づいたデバイス一覧を取得する
//     * @param id
//     * @return
//     */
//    @SuppressWarnings("unchecked")
//	public List<Device> getDevicesByUserId(int id){
//    	Criteria c=getSession().createCriteria(Device.class);
//        //検索条件を設定．MySQLで"WHERE userid=..."と指定するみたいなイメージ
//        c.add(Restrictions.eq("userid", id));
//        
//        //useridに紐づいたデバイスID一覧を返す
//        return c.list();
//    }
//
////    /**
//     * 指定した名前のユーザを返す
//     * @param name
//     * @return
//     */
//    public KubiwaUser getUserByName(String name) {
//    	KubiwaUser result=null;
//    	Session s=getSession();
//    	
//        Criteria c=s.createCriteria(KubiwaUser.class);
//        c.add(Restrictions.eq("uname", name));
//        
//        try{
//        	//条件にマッチした結果を返す．nameに一致する結果は一つだけ
//        	result=(KubiwaUser) c.uniqueResult();
//        	
//        }catch(HibernateException e){
//        	e.printStackTrace();
//        }finally{
//			s.close();
//		}
//        return result;
//    }
    
    /**
     * クラスの使い方サンプル
     * @param args
     */
    public static void main(String[] args){
    //	String name="vino";
    	KubiwaHelper helper=new KubiwaHelper();
    	
    	List<Device> devices = helper.getDevicesTest();
    	for(Device d: devices){
    		System.out.println(d);
    	}
    	
//    	KubiwaUser u=helper.getUserByName(name);
//    	if(u==null){
//    		System.out.println("kubiwauser "+name+" not found.");
//    	}
//    	else{
//    		List<Device> devices=helper.getDevicesByUserId(u.getId());
//    		for(Device d : devices){
//    			System.out.println("devid="+d.getDevId());
//    		}
//    	}
    }
}