/*
 * Copyright (C) 2008-2013 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.view;

import java.util.Calendar;

import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.EventNotificationActivity;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;

/**
 * シミュレーションの起点と期間を指定するためのPickerを含んだダイアログを生成するクラス．
 * @author sacchin
 *
 */
public class DateTimePicker {

	/**
	 * Pickerを表示するActivity．
	 */
	private EventNotificationActivity activity;
	
	/**
	 * 年月日を指定するPicker．
	 */
	private DatePicker datePicker;
	
	/**
	 * 時分を指定するPicker．
	 */
	private TimePicker timePicker;
	
	/**
	 * ダイアログのタイトル．
	 */
	private TextView title;
	
	/**
	 * 期間(分)を入力するEditText．
	 */
	private EditText interval;

	/**
	 * コンストラクタ
	 * @param activity
	 */
	public DateTimePicker(EventNotificationActivity activity){
		this.activity = activity;
		this.datePicker = new DatePicker(activity);
		this.timePicker = new TimePicker(activity);
		this.interval = new EditText(activity);
		this.title = new TextView(activity);
	}

	/**
	 * 年月日時分を含んだPickerを生成するメソッド．
	 * @return
	 */
	public LinearLayout createDateTimePicker(){
		LinearLayout layout = new LinearLayout(activity);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.addView(datePicker);
		layout.addView(timePicker);
		return layout;
	}
	
	/**
	 * シミュレーションの起点と期間を指定するためのPickerを含んだレイアウトを生成するメソッド．
	 * @return シミュレーションの起点と期間を指定するためのPickerを含んだScrollView．
	 */
	public ScrollView createIntervalPicker(){
		LinearLayout layout = new LinearLayout(activity);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.addView(datePicker);
		layout.addView(timePicker);
		title.setText("シミュレートする期間(分)");
		layout.addView(title);
		layout.addView(interval);
		ScrollView s = new ScrollView(activity);
		s.addView(layout);
		return s;
	}

	/**
	 * 年月日のPickerを取得するメソッド．
	 * @return
	 */
	public DatePicker getDatePicker() {
		return datePicker;
	}

	/**
	 * 時分のPickerを取得するメソッド．
	 * @return
	 */
	public TimePicker getTimePicker() {
		return timePicker;
	}

	/**
	 * 入力された年を取得するメソッド．
	 * @return 年(1900-)
	 */
	public int getYear() {
		return datePicker.getYear();
	}

	/**
	 * 入力された月を取得するメソッド．
	 * @return 月(0-11)
	 */
	public int getMonth() {
		return datePicker.getMonth();
	}

	/**
	 * 入力された日にちを取得するメソッド．
	 * @return 日(0-31)
	 */
	public int getDayOfMonth() {
		return datePicker.getDayOfMonth();
	}

	/**
	 * 入力された時間を取得するメソッド．
	 * @return 時(0-23)
	 */
	public int getHour() {
		return timePicker.getCurrentHour();
	}

	/**
	 * 入力された分を取得するメソッド．
	 * @return 分(0-59)
	 */
	public int getMinute() {
		return timePicker.getCurrentMinute();
	}

	/**
	 * 入力されたシミュレーション期間を取得するメソッド．
	 * @return 分(0-)
	 */
	public int getInterval() {
		String intervalStr = String.valueOf(interval.getText());
		try {
			return Integer.parseInt(intervalStr);
		} catch (NumberFormatException e) {
			return -1;
		}
	}
	
	/**
	 * 入力された年月日時分をもとにCalendarクラスを生成する．
	 * @return
	 */
	public Calendar getCalendar(){
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, datePicker.getYear());
		c.set(Calendar.MONTH, datePicker.getMonth());
		c.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
		c.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
		c.set(Calendar.MINUTE, timePicker.getCurrentMinute());
		return c;
	}
}