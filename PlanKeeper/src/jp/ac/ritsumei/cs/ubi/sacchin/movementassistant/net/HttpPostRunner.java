/*
 * Copyright (C) 2008-2013 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.net;

import java.net.URISyntaxException;
import java.util.List;

import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.utils.HttpConnector;

import org.apache.http.NameValuePair;

import android.os.Handler;
import android.util.Log;

public class HttpPostRunner implements Runnable {
	private Handler downloadExecutor = new Handler();
	private List<NameValuePair> params;
	private String url;
	private Runnable successCallback;
	private Runnable failedCallback;
	
	public HttpPostRunner(String url, List<NameValuePair> params){
		this.params = params;
		this.url = url;
	}
	
	@Override
	public void run() {
		try {
			Log.v("HttpPostRunner", params.toString());
			boolean isSuccess = HttpConnector.postData(url, params);
			if(isSuccess && successCallback != null){
				downloadExecutor.post(successCallback);
			}
			if(!isSuccess && failedCallback != null){
				downloadExecutor.post(failedCallback);
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	public void setSuccessCallback(Runnable successCallback) {
		this.successCallback = successCallback;
	}

	public void setFailedCallback(Runnable failedCallback) {
		this.failedCallback = failedCallback;
	}
}
