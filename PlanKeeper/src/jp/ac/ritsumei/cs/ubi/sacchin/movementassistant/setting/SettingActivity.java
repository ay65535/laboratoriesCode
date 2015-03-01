/*
 * Copyright (C) 2008-2013 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.setting;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.R;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.net.HttpPostRunner;
import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.utils.URLConstants;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

public class SettingActivity extends Activity{
	private Button apply;
	private EditText devidEditText, intervalEditText;
	private DatePicker datePicker;
	private SharedPreferences settingPreferences;
	private Context c = this;

	private OnClickListener applyButtonClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			tapApplyButton();
			saveSettings();
		}
	};

	private OnClickListener devidTapListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			enableApplyButton();
		}
	};

	private OnClickListener intervalTapListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			enableApplyButton();
		}
	};
	
	private OnClickListener datePickerTapListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			enableApplyButton();
		}
	};

	public void saveSettings() {
		synchronized (settingPreferences) {
			String devidStr = String.valueOf(devidEditText.getText());
			String intervalStr = String.valueOf(intervalEditText.getText());

			int devid = -1, interval = -1;
			try {
				devid = Integer.parseInt(devidStr);
			} catch (NumberFormatException e) {
				Toast.makeText(this, "Please enter a number.", Toast.LENGTH_SHORT).show();
				return;
			}

			try {
				interval = Integer.parseInt(intervalStr);
				if(60 < interval){
					interval = 60;
				}
			} catch (NumberFormatException e) {
				Toast.makeText(this, "Please enter a number.", Toast.LENGTH_SHORT).show();
				return;
			}
			Editor editor = settingPreferences.edit();
			editor.putInt("interval", interval);
			editor.putInt("devid", devid);
			
			int y = datePicker.getYear();
			int m = datePicker.getMonth();
			int d = datePicker.getDayOfMonth();
			editor.putInt("y", y);
			editor.putInt("m", m);
			editor.putInt("d", d);
			editor.commit();
			System.out.println(y + "" + m + "" + d);

			postSetting(devid, interval, y, m, d);
		}
	}

	private void postSetting(int devid, int interval, int y, int m, int d) {
		List <NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(
				"pattern", "setting"));
		params.add(new BasicNameValuePair(
				"devid", String.valueOf(devid)));
		params.add(new BasicNameValuePair(
				"interval", String.valueOf(interval)));
		params.add(new BasicNameValuePair(
				"year", String.valueOf(y)));
		params.add(new BasicNameValuePair(
				"month", String.valueOf(m)));
		params.add(new BasicNameValuePair(
				"day", String.valueOf(d)));

		HttpPostRunner hpr = new HttpPostRunner(
				URLConstants.SERVER_URL, params);
		hpr.setSuccessCallback(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(c, "サーバにも設定を反映しました。", 
						Toast.LENGTH_SHORT).show();
			}
		});
		hpr.setFailedCallback(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(c, "サーバでエラーが発生しました。", 
						Toast.LENGTH_SHORT).show();
			}
		});
		new Thread(hpr).start();
	}

	protected void initView() {
		setContentView(R.layout.setting);

		devidEditText = (EditText) findViewById(R.id.devid);
		devidEditText.setOnClickListener(devidTapListener);
		devidEditText.setOnKeyListener(enableApplyButtonListener);
		intervalEditText = (EditText) findViewById(R.id.interval);
		intervalEditText.setOnClickListener(intervalTapListener);
		intervalEditText.setOnKeyListener(enableApplyButtonListener);
		datePicker = (DatePicker) findViewById(R.id.datePicker);
		datePicker.setOnClickListener(datePickerTapListener);
		datePicker.setOnKeyListener(enableApplyButtonListener);
		
		apply = (Button) findViewById(R.id.apply);
		apply.setOnClickListener(applyButtonClickListener);
	}

	protected void reloadSettings() {
		int devid = settingPreferences.getInt("devid", -1);
		if(devid != -1){
			devidEditText.setText(String.valueOf(devid));
		}
		int interval = settingPreferences.getInt("interval", -1);
		if(interval != -1){
			intervalEditText.setText(String.valueOf(interval));
		}
		int y = settingPreferences.getInt("y", 2013);
		int m = settingPreferences.getInt("m", 0);
		int d = settingPreferences.getInt("d", 1);
		datePicker.updateDate(y, m, d);
	}

	protected EnableApplyButtonListener enableApplyButtonListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		settingPreferences = getSharedPreferences("setting", MODE_PRIVATE);
		enableApplyButtonListener = new EnableApplyButtonListener();
		initView();
		reloadSettings();
	}

	private class EnableApplyButtonListener implements OnClickListener, OnKeyListener{
		@Override
		public void onClick(View arg0) {
			enableApplyButton();
		}

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			return false;
		}
	}

	public void tapApplyButton() {
		if(apply != null){
			apply.setEnabled(false);
		}
	}

	public void enableApplyButton() {
		if(apply != null){
			apply.setEnabled(true);
		}
	}
}