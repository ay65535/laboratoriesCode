/*
 * Copyright (C) 2008-2013 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.createplan.drivingdirection;

import java.util.ArrayList;
import java.util.Calendar;

public class TransferScraper {
	public static ArrayList<String> scrapingOfJoldan(String html){
		ArrayList<String> lines = new ArrayList<String>();
		String s[] = html.split("\n");
		for(int i = 0 ; i < s.length ; i++){
			if(s[i].contains("</b>発<b> → </b><b>")){
				lines.add(s[i]);
			}
		}
		return lines;
	}
	public static ArrayList<String> scrapingOfYahoo(String html){
		ArrayList<String> lines = new ArrayList<String>();
		String s[] = html.split("\n");
		for(int i = 0 ; i < s.length ; i++){
			if(s[i].contains("class=\"departure\"")){
				String temp[] = s[i].split("<span class=\"departure\">");
				System.out.println(temp[0].split("<h2>")[1] + " - " + temp[1].split("</span>")[0]);
			}else if(s[i].contains("class=\"route-departure\"")){
				System.out.println(s[i]);
				lines.add(s[i]);
			}
		}
		return lines;
	}
	
	public static Calendar scrapeArraivalTimeOfJoldan(String htmlLine, Calendar calendar){
		String temp[] = htmlLine.split("</b>発<b> → </b><b>");

		String arrival[] = temp[1].split("</b>着</div>")[0].split(":");
		int arrivalHour = Integer.parseInt(arrival[0]);
		int arrivalMinute = Integer.parseInt(arrival[1]);
		
		System.out.println("arrive at " + arrivalHour + ":" + arrivalMinute);
		
		calendar.set(Calendar.HOUR_OF_DAY, arrivalHour);
		calendar.set(Calendar.MINUTE, arrivalMinute);

		return calendar;
	}

	public static Calendar scrapeDepartureTimeOfJoldan(String htmlLine, Calendar calendar){
		String temp[] = htmlLine.split("</b>発<b> → </b><b>");

		String departure[] = temp[0].split("<div class=\"tm\"><b>")[1].split(":");
		int departureHour = Integer.parseInt(departure[0]);
		int departureMinute = Integer.parseInt(departure[1]);

		System.out.println("departure time " + departureHour + ":" + departureMinute);
		
		calendar.set(Calendar.HOUR_OF_DAY, departureHour);
		calendar.set(Calendar.MINUTE, departureMinute);

		return calendar;
	}

	public static Calendar scrapeArraivalTimeOfYahoo(String htmlLine, Calendar calendar){
		String temp[] = htmlLine.split("</span><span class=\"route-arrive-");

		String arrival[] = temp[1].split("</span>")[0].split(">")[1].split("到着")[0].split(":");
		int arrivalHour = Integer.parseInt(arrival[0]);
		int arrivalMinute = Integer.parseInt(arrival[1]);
		
		System.out.println("arrive at " + arrivalHour + ":" + arrivalMinute);
		
		calendar.set(Calendar.HOUR_OF_DAY, arrivalHour);
		calendar.set(Calendar.MINUTE, arrivalMinute);

		return calendar;
	}

	public static Calendar scrapeDepartureTimeOfYahoo(String htmlLine, Calendar calendar){
		String temp[] = htmlLine.split("</span><span class=\"route-arrive-");

		String departure[] = temp[0].split("<dt><span class=\"route-departure\">")[1].split("出発")[0].split(":");
		int departureHour = Integer.parseInt(departure[0]);
		int departureMinute = Integer.parseInt(departure[1]);

		System.out.println("departure time " + departureHour + ":" + departureMinute);
		
		calendar.set(Calendar.HOUR_OF_DAY, departureHour);
		calendar.set(Calendar.MINUTE, departureMinute);

		return calendar;
	}
}
