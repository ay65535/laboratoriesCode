/*
 * Copyright (C) 2008-2013 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.utils;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class HttpConnector {
	private final static int TIMEOUT_INTERVAL = 300000;

	public static boolean postData(String urlStr, List <NameValuePair> params) throws URISyntaxException{
		if(urlStr == null){
			return false;
		}

		URI url = new URI(urlStr.replace(" ", "_"));
		Log.v("postData connecting to", String.valueOf(url));
		HttpPost method = new HttpPost(urlStr);
		HttpClient httpClient = getHttpClient();

		try {
			method.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

			HttpResponse httpResponse = httpClient.execute(method);
			if(httpResponse == null || httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK ){
				return false;
			}
			Log.v("httpResponse", String.valueOf(httpResponse.getStatusLine().getStatusCode()) );
			return true;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (org.apache.http.ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if(httpClient != null){
				httpClient.getConnectionManager().shutdown();
			}
		}
		return false;
	}

	public static String downloadDataForALine(String urlStr) throws URISyntaxException {
		if(urlStr == null){
			return null;
		}

		URI url = new URI(urlStr.replace(" ", "_"));
		Log.v("downloadDataForALine connecting to", String.valueOf(url));
		HttpGet request = new HttpGet(urlStr);
		DefaultHttpClient httpClient = getHttpClient();

		try {
			String result = httpClient.execute(request, new ResponseHandler<String>() {
				@Override
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
			if(httpClient != null){
				httpClient.getConnectionManager().shutdown();
			}
		}
	}

	public static ArrayList<String> downloadDataForEachLine(String urlStr) 
			throws URISyntaxException, ClientProtocolException, IOException {
		if(urlStr == null){
			return null;
		}

		URI url = new URI(urlStr.replace(" ", "_"));
		Log.v("downloadDataForEachLine connecting to", String.valueOf(url));
		HttpGet req = new HttpGet(urlStr);
		HttpClient httpClient = getHttpClient();
		HttpResponse httpResponse = httpClient.execute(req);

		if(httpResponse == null || httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK ){
			return null;
		}
		Log.v("httpResponse", String.valueOf(httpResponse.getStatusLine().getStatusCode()) );

		HttpEntity httpEntity = httpResponse.getEntity();

		try {
			return parseToList(httpEntity);
		} catch (Exception e) {
			return null;
		} finally{
			if(httpClient != null){
				httpClient.getConnectionManager().shutdown();
			}
			if(httpEntity != null){
				httpEntity.consumeContent();
			}
		}
	}

	public static ArrayList<String> parseToList(HttpEntity httpEntity) 
			throws IllegalStateException, IOException{
		DataInputStream in = null;
		try{
			in = new DataInputStream(httpEntity.getContent());
			ArrayList<String> responses = new ArrayList<String>();
			while(true){
				@SuppressWarnings("deprecation")
				String lineStr = in.readLine();
				if(lineStr != null){
					responses.add(lineStr);
				}else{
					break;
				}
			}
			return responses;
		} catch(EOFException e) {
			throw new EOFException(e.getMessage());
		} finally {
			if(in != null){
				in.close();
			}
			if(httpEntity != null){
				httpEntity.consumeContent();
			}
		}
	}

	public static DefaultHttpClient getHttpClient(){
		DefaultHttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, TIMEOUT_INTERVAL);
		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, TIMEOUT_INTERVAL);
		return httpClient;
	}
}
