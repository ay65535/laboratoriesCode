/*
 * Copyright (C) 2008-2012 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */

package jp.ac.ritsumei.cs.ubi.logger.client.api.sensors;

/**
 * @author kome
 */
public class MyLeaveData {

     private long time;
     private int state;

     public MyLeaveData( long time, int state ){
          this.time = time;
          this.state = state;
     }

     public long getTime() {
          return time;
     }    

     public int getState() {
          return state;
     }

     public String toString(){
          return "{Leave(" + state + ")}";
     }

}

