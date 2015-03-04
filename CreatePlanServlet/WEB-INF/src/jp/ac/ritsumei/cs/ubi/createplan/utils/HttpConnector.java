/*
 * Copyright (C) 2008-2013 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.createplan.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class HttpConnector {
	public static HttpURLConnection getConnection(String urlStr) 
			throws IOException{
		System.out.println(urlStr);
		URL url = new URL(urlStr);
		HttpURLConnection urlconn = (HttpURLConnection)url.openConnection();
		urlconn.setRequestMethod("GET");
		urlconn.setInstanceFollowRedirects(false);
		urlconn.setRequestProperty("Accept-Language", "ja;q=0.7,en;q=0.3");
		return urlconn;
	}
	
	public static ArrayList<String> downloadDataForSim(String urlStr) 
			throws IOException {
		HttpURLConnection con = getConnection(urlStr);
		con.connect();

		BufferedReader reader = new BufferedReader(
				new InputStreamReader(con.getInputStream()));
		ArrayList<String> re = new ArrayList<String>();
		while (true){
			String line = reader.readLine();
			if (line == null){
				break;
			}
			re.add(line);
		}
		reader.close();
		con.disconnect();
		return re;
	}
	
	public static String downloadDataForALine(String urlStr) {
		HttpGet request = new HttpGet(urlStr);
		DefaultHttpClient httpClient = new DefaultHttpClient();
		System.out.println(urlStr);
		try {
		    String result = httpClient.execute(request, new ResponseHandler<String>() {
				public String handleResponse(HttpResponse response)
						throws ClientProtocolException, IOException {
		            
		            switch (response.getStatusLine().getStatusCode()) {
		            case HttpStatus.SC_OK:
		                return EntityUtils.toString(response.getEntity(), "UTF-8");
		            
		            case HttpStatus.SC_NOT_FOUND:
		                throw new RuntimeException("No Data!");
		            default:
		                throw new RuntimeException("Some Error");
		            }
		        }
		    });
		    return result;
		} catch (ClientProtocolException e) {
		    throw new RuntimeException(e);
		} catch (IOException e) {
		    throw new RuntimeException(e);
		} finally {
		    httpClient.getConnectionManager().shutdown();
		}
	}
	
}
