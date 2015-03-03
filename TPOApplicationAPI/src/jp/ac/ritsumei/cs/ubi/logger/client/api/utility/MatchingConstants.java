/*
 * Copyright (C) 2008-2012 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */

package jp.ac.ritsumei.cs.ubi.logger.client.api.utility;

/**
 * This is contsnts class
 * @author sacchin
 * @author kome
 */

public class MatchingConstants {

     public static final String START_SERVICE = "start_service";
     public static final String STOP_SERVICE = "sop_service";

     public static final String PACKAGENAME = "packageName";

     public static final String QUERY = "QUERY";
     public static final String QUERY_REMOVE = "REMOVE";
     public static final String QUERY_ALL_REMOVE = "ALLREMOVE";
     public static final String QUERY_REPLY_INTENT = "replyIntent";

     public static final String MATCHING_RESULT = "matching_result";
     public static final String LOCATION_RAWCACHE_INTENT = "matching_location_raw";
     public static final String WIFI_RAWCACHE_INTENT = "matching_wifi_raw";
     public static final String ACC_RAWCACHE_INTENT = "matching_acc_raw";

     public static final String QUERY_MATCHING_TYPE = "matchingType";
     public static final String QUERY_SENSOR_TYPE = "sensorType";
     public static final String QUERY_ATTRIBUTE = "attribute";
     public static final String QUERY_CALCURATER = "calcurater";
     public static final String QUERY_TABLE_NAME = "tableName";
     public static final String QUERY_CONSTANTS = "constants";
     public static final String DATA_TYPE_INT = "int";
     public static final String OPERATION_TYPE_LOGIC = "logic";
     public static final String OPERATION_TYPE_COMPARE = "compare";
     public static final String MATCHING_TYPE_LARGE = "large";
     public static final String MATCHING_TYPE_SMALL = "small";
     public static final String SENSOR_NAME_LOCATION = "Location";
     public static final String SENSOR_NAME_LOCATION_LATLNG = "latlng";
     public static final String SENSOR_NAME_LOCATION_ACCURACY = "accuracy";
     public static final String SENSOR_NAME_LOCATION_SATELLITE = "satellite";
     public static final String SENSOR_NAME_LOCATION_SPEED = "speed";
     public static final String SENSOR_NAME_WIFI = "WiFi";
     public static final String SENSOR_NAME_WIFI_BSSID = "bssid";
     public static final String SENSOR_NAME_WIFI_ESSID = "essid";
     public static final String SENSOR_NAME_WIFI_RSSI = "rssi";
     public static final String SENSOR_NAME_ACC = "Accelerometer";
     public static final String SENSOR_NAME_ACC_VECTOR = "vector";
     public static final String SENSOR_NAME_ACC_ISCHANGE = "isChangeGreatry";
     public static final String CALCULATER_EQUAL = "equal";
     public static final String CALCULATER_LARGER = "larger";
     public static final String CALCULATER_SMALLER = "smaller";
     public static final String CALCULATER_AND = "and";
     public static final String CALCULATER_OR = "or";
    
//     public static enum Operation{
//          COMPARE, LOGIC;
//     }
//    
//     public static enum Calculater{
//          EQUAL, LARGER_THAN, SMALLER_THAN, AND, OR;
//     }
//    
//     public static enum Sensor{
//          LOCATION, WiFi, ACCELEROMETER;
//     }
//    
//     public static enum Attribute{
//          LAT, LNG, ACCURACY, SATELLITE, SPEED,
//          BSSID, ESSID, RSSI,
//          LAT_LNG,
//          VECTOR;
//     }
//    
//     public static enum DataType{
//          INT, FLOAT, DOUBLE, STRING;
//     }
    
     public static String getKey(int id){
          if(id < PARAM_STRING.length){
               return PARAM_STRING[id];
          }
          return null;
     }

     public static final String PARAM_STRING[] = {"Compare","Logic",
          "=",">","<","&&","||","Location","WiFi","Accelerometer","Lat","Lng","Accuracy","Satellite",
          "Speed","Bssid","Essid","Rssi","LatLng","X-axis","Y-axis","Z-axis","Vector","int","float",
          "double","String","Notification","OverlapRatio","NoParam","AccelerometerVariation","StepDetecter","Database",
          "Leave","State"};

     public static final int KEEP = -5;

     public static final int COMPARE = 0;
     public static final int LOGIC = 1;

     public static final int EQUAL = 2;
     public static final int LARGER_THAN = 3;
     public static final int SMALLER_THAN = 4;
     public static final int AND = 5;
     public static final int OR = 6;

     /**
      * SN means "Sensor Name".
      */
     public static final int SN_LOCATION = 7;

     /**
      * SN means "Sensor Name".
      */
     public static final int SN_WiFi = 8;

     /**
      * SN means "Sensor Name".
      */
     public static final int SN_ACCELEROMETER = 9;

     /**
      * VN means "Value Name".
      */
     public static final int VN_LAT = 10;

     /**
      * VN means "Value Name".
      */
     public static final int VN_LNG = 11;

     /**
      * VN means "Value Name".
      */
     public static final int VN_ACCURACY = 12;

     /**
      * VN means "Value Name".
      */
     public static final int VN_SATELLITE = 13;

     /**
      * VN means "Value Name".
      */
     public static final int VN_SPEED = 14;

     /**
      * VN means "Value Name".
      */
     public static final int VN_BSSID = 15;

     /**
      * VN means "Value Name".
      */
     public static final int VN_ESSID = 16;

     /**
      * VN means "Value Name".
      */
     public static final int VN_RSSI = 17;

     /**
      * VN means "Value Name".
      */
     public static final int VN_LAT_LNG = 18;

     /**
      * VN means "Value Name".
      */
     public static final int VN_X_AXIS = 19;

     /**
      * VN means "Value Name".
      */
     public static final int VN_Y_AXIS = 20;

     /**
      * VN means "Value Name".
      */
     public static final int VN_Z_AXIS = 21;

     /**
      * VN means "Value Name".
      */
     public static final int VN_VECTOR = 22;

     /**
      * DT means "Data Type".
      */
     public static final int DT_INT = 23;

     /**
      * DT means "Data Type".
      */
     public static final int DT_FLOAT = 24;

     /**
      * DT means "Data Type".
      */
     public static final int DT_DOUBLE = 25;

     /**
      * DT means "Data Type".
      */
     public static final int DT_STRING = 26;

     public static final int NOTIFICATION = 27;

     /**
      * AN means "Attribute Name".
      */
     public static final int AN_OVERLAP_RATIO = 28;

     public static final int NO_PARAM = 29;

     /**
      * AN means "Attribute Name".
      */
     public static final int AN_VARIATION = 30;

     /**
      * AN means "Attribute Name".
      */
     public static final int AN_STEP_DETECTER = 31;

     public static final int DATABASE = 32;

     /**
      * SN means "Sensor Name".
      */
     public static final int SN_LEAVE = 33;

     /**
      * VN means "Value Name".
      */
     public static final int VN_STATE = 34;

}

