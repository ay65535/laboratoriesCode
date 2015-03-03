/*
 * Copyright (C) 2008-2012 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */

package jp.ac.ritsumei.cs.ubi.logger.client.api.sensors;

import java.util.ArrayList;
import java.util.HashMap;

import jp.ac.ritsumei.cs.ubi.logger.client.api.utility.Bytes;
import jp.ac.ritsumei.cs.ubi.logger.client.api.utility.MatchingConstants;

public class SensorData {

     protected HashMap< String, Long > time;
     protected HashMap< String, byte[] > locationSensorData;
     protected HashMap< String, byte[] > wifiSensorData;
     protected HashMap< String, byte[] > accSensorData;
     protected HashMap< String, byte[] > leaveSensorData;

     public SensorData(){
          this.time= new HashMap<String, Long>();
          this.locationSensorData = new HashMap<String, byte[]>();
          this.wifiSensorData = new HashMap<String, byte[]>();
          this.accSensorData = new HashMap<String, byte[]>();
          this.leaveSensorData = new HashMap<String, byte[]>();
     }

     public void putTime( String key, long value ){
          time.put(key, value);
     }

     public void putSensorData( String key, byte value[] ){
          if( key.contains(MatchingConstants.PARAM_STRING[MatchingConstants.SN_ACCELEROMETER]) ){
               locationSensorData.put(key, value);
          }else if( key.contains(MatchingConstants.PARAM_STRING[MatchingConstants.SN_WiFi]) ){
//             wifiSensorData.clear();
               wifiSensorData.put(key, value);
          }else if( key.contains(MatchingConstants.PARAM_STRING[MatchingConstants.SN_LOCATION]) ){
               accSensorData.put(key, value);
          }else if( key.contains(MatchingConstants.PARAM_STRING[MatchingConstants.SN_LEAVE]) ){
               leaveSensorData.put(key, value);
          }
     }

     public long getTime( String key ){
          if( time!=null && key!=null ){
               Long t = time.get(key);
               if( t!=null ){
                    return t.longValue();
               }
          }
          return 0;
     }

     public ArrayList<String> getSSIDs(){
          ArrayList<String> returnMap = new ArrayList<String>();
          HashMap<String, byte[]> sensorData = this.wifiSensorData;

          for(String temp : sensorData.keySet()){
               byte byteSSID[] = sensorData.get(temp);
               if(byteSSID != null){
                    String ssid = String.valueOf(Bytes.toStringBinary(byteSSID));
                    returnMap.add(ssid);
               }
          }
          return returnMap;
     }

    

     public byte[] getSensorDate( String key ){
          if( key!=null ){
               byte temp[] = null;
               if( key.contains(MatchingConstants.PARAM_STRING[MatchingConstants.SN_ACCELEROMETER]) ){
                    temp = locationSensorData.get(key);
               }else if( key.contains(MatchingConstants.PARAM_STRING[MatchingConstants.SN_WiFi]) ){
                    temp = wifiSensorData.get(key);
               }else if( key.contains(MatchingConstants.PARAM_STRING[MatchingConstants.SN_LOCATION]) ){
                    temp = accSensorData.get(key);
               }else if( key.contains(MatchingConstants.PARAM_STRING[MatchingConstants.SN_LEAVE]) ){
                    temp = leaveSensorData.get(key);
               }

               if( temp!=null ){
                    return temp;
               }
          }
          return null;
     }

    

     public void clear(){
          time.clear();
          locationSensorData.clear();
          accSensorData.clear();
          wifiSensorData.clear();
          leaveSensorData.clear();
     }

    

     public void clear( int sensorName ){
          if( sensorName == MatchingConstants.SN_ACCELEROMETER ){
               accSensorData.clear();
          }else if( sensorName == MatchingConstants.SN_WiFi ){
               wifiSensorData.clear();
          }else if( sensorName == MatchingConstants.SN_LOCATION ){
               locationSensorData.clear();
          }else if( sensorName == MatchingConstants.SN_LEAVE ){
               leaveSensorData.clear();
          }
     }

    

     public HashMap<String, Long> getTimeMap(){
          return time;
     }



     public HashMap<String, byte[]> getSensorDataMap( int sensorName ){
          if( sensorName == MatchingConstants.SN_ACCELEROMETER ){
               return accSensorData;
          }else if( sensorName == MatchingConstants.SN_WiFi ){
               return wifiSensorData;
          }else if( sensorName == MatchingConstants.SN_LOCATION ){
               return locationSensorData;
          }else if( sensorName == MatchingConstants.SN_LEAVE ){
               return leaveSensorData;
          }
          return null;
     }

    

     public String toString(){
          return "{" + time.toString() + "," +
          locationSensorData.toString() + "," +
          wifiSensorData.toString() + "," +
          accSensorData.toString() + "," +
          leaveSensorData.toString() + "}";
     }

    

     public String toLog(){
          int sum = locationSensorData.size() + accSensorData.size() + wifiSensorData.size() + leaveSensorData.size();
          return "Time=" + time.size() + ", SensorData=" + sum ;
     }

}

