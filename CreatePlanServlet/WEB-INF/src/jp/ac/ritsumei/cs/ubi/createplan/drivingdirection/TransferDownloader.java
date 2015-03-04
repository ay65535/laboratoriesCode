/*
 * Copyright (C) 2008-2013 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.createplan.drivingdirection;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import jp.ac.ritsumei.cs.ubi.createplan.utils.HttpConnector;

public class TransferDownloader{
	private static String TAHOO_TRANSFER_DOWNLOD_URL = "http://transit.loco.yahoo.co.jp/search/result?flatlon=&";
	private static String JOLDAN_TRANSFER_DOWNLOD_URL = "http://www.jorudan.co.jp/norikae/cgi/nori.cgi?";
	
	public static List<Long> getMoveTimes(String from, String to, Calendar calendar, PrintWriter pw) {
		List<Long> times = new ArrayList<Long>();
		try{
//			String urlOforigin = buildURLForYahoo(from, to, calendar);
			String urlOforigin = buildURLForJoldan(from, to, calendar);
					
			String html = HttpConnector.downloadDataForALine(urlOforigin);
//			ArrayList<String> lines = TransferScraper.scrapingOfYahoo(html);
			ArrayList<String> lines = TransferScraper.scrapingOfJoldan(html);
			
			for(String line : lines){
//				long arrival = TransferScraper.scrapeArraivalTimeOfYahoo(line, calendar).getTimeInMillis();
//				long departure = TransferScraper.scrapeDepartureTimeOfYahoo(line, calendar).getTimeInMillis();
				
				long arrival = TransferScraper.scrapeArraivalTimeOfJoldan(line, calendar).getTimeInMillis();
				long departure = TransferScraper.scrapeDepartureTimeOfJoldan(line, calendar).getTimeInMillis();
				
				long diff = arrival - departure;
				times.add(diff);
			}
			
			return times;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String buildURLForYahoo(String from, String to, Calendar calendar)
			throws UnsupportedEncodingException {
		String yearday = calendar.get(Calendar.YEAR) + "" + 
				(calendar.get(Calendar.MONTH) + 1);
		int m1 = calendar.get(Calendar.MINUTE) / 10;
		int m2 = calendar.get(Calendar.MINUTE) - (m1 * 10);
		
		String[] encs = {"sjis", "utf-8", "euc-jp"};

		String urlOforigin = TAHOO_TRANSFER_DOWNLOD_URL + 
				"from=" + URLEncoder.encode(from, encs[1]) +
				"&tlatlon=" +
				"&to=" + URLEncoder.encode(to, encs[1]) +
				"&via=&shin=1&ex=1&al=1&hb=1&lb=1&sr=1&expkind=1" +
				"&ym=" + yearday +
				"&d=" +	calendar.get(Calendar.DAY_OF_MONTH) + 
				"&datepicker=" +
				"&hh=" + calendar.get(Calendar.HOUR_OF_DAY) + 
				"&m1=" + m1 +
				"&m2=" + m2 +
				"&type=1" +
				"&ws=2" +
				"&s=0" +
				"&x=100" +
				"&y=15" +
				"&kw=";
		return urlOforigin;
	}
	
	private static String buildURLForJoldan(String from, String to, Calendar calendar)
			throws UnsupportedEncodingException {
		String yearday = calendar.get(Calendar.YEAR) + "" + 
				(calendar.get(Calendar.MONTH) + 1);
		int m1 = calendar.get(Calendar.MINUTE) / 10;
		int m2 = calendar.get(Calendar.MINUTE) - (m1 * 10);
		
		String[] encs = {"sjis", "utf-8", "euc-jp"};
		
		String urlOforigin = JOLDAN_TRANSFER_DOWNLOD_URL + 
		"eki1=" + URLEncoder.encode(from, encs[1]) +
		"&eki2=" + URLEncoder.encode(to, encs[1]) +
		"&eki3=&via_on=1" +
		"&Dym=" + yearday +
		"&Ddd=" + calendar.get(Calendar.DAY_OF_MONTH) +
		"&Dhh=" + calendar.get(Calendar.HOUR_OF_DAY) +
		"&Dmn1=" + m1 +
		"&Dmn2=" + m2 +
		"&Cway=0" + 
		"&C7=1" + 
		"&C2=0" +
		"&C3=0" +
		"&C1=0" + 
		"&C4=0" + 
		"&C6=2" + 
		"&S.x=85" + 
		"&S.y=15" + 
		"&S=%E6%A4%9C%E7%B4%A2" + 
		"&Cmap1=" + 
		"&rf=nr" + 
		"&pg=0" + 
		"&eok1=" + 
		"&eok2=" + 
		"&eok3=" +
		"&Csg=1";

		return urlOforigin;
	}
}
