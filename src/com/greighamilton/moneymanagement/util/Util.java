package com.greighamilton.moneymanagement.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.Days;

import android.text.format.Time;


public class Util {
	
	public static boolean ASCENDING = true;
	public static boolean DESCENDING = false;
	
	public static String makeDayString(int day){
		return ((day < 10) ? "0" + day : "" + day);
	}
	
	public static String makeDayString(String day){
		return ((day.length() < 2) ? "0" + day : "" + day);
	}
	
	public static String makeMonthString(int month){
		return ((month < 10) ? "0" + month : "" + month);
	}
	
	public static String makeMonthString(String month){
		return ((month.length() < 2) ? "0" + month : "" + month);
	}
	
	public static String makeYearString(int year){
		return "" + year;
	}

	public static String makeDateString(int day, int month, int year) {
		
		return makeYearString(year) + "-" +
		makeMonthString(month) + "-" +
		makeDayString(day);
		
	}
	
	public static String getTodaysDate() {	
		
		return makeDateString(getTodaysDay(), getTodaysMonth(), getTodaysYear());	
		
	}
	
	public static int getTodaysDay() {	
		
		Time time = new Time();
		time.setToNow();
		return time.monthDay;	
		
	}
	
	public static int getTodaysMonth() {	
		
		Time time = new Time();
		time.setToNow();
		return time.month + 1;	
		
	}
	
	public static int getTodaysYear() {	
		
		Time time = new Time();
		time.setToNow();
		return time.year;	
		
	}
	
	public static int getDayFromString(String date) {
		
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
			DateTime dt = new DateTime((Date) sdf.parse(date));
			return dt.getDayOfMonth();
		} catch (Exception e) {
			return -1;
		}	
		
	}
	
	public static int getMonthFromString(String date) {
		
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
			DateTime dt = new DateTime((Date) sdf.parse(date));
			return dt.getMonthOfYear();
		} catch (Exception e) {
			return -1;
		}	
		
	}
	
	public static int getYearFromString(String date) {
		
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
			DateTime dt = new DateTime((Date) sdf.parse(date));
			return dt.getYear();
		} catch (Exception e) {
			return -1;
		}	
		
	}
	
	public static List<String> getListOfMonthsShort(){
		
		List<String> months = new ArrayList<String>();
		months.add("Jan"); months.add("Feb"); months.add("Mar"); months.add("Apr");
		months.add("May"); months.add("Jun"); months.add("Jul"); months.add("Aug");
		months.add("Sep"); months.add("Oct"); months.add("Nov"); months.add("Dec");
		return months;
		
	}
	
	public static List<String> getListOfMonthsLong(){
		
		List<String> months = new ArrayList<String>();
		months.add("January"); months.add("February"); months.add("March"); months.add("April");
		months.add("May"); months.add("June"); months.add("July"); months.add("August");
		months.add("September"); months.add("October"); months.add("November"); months.add("December");
		return months;
		
	}
	
	public static List<String> getListOfYears(){
		
		List<String> years = new ArrayList<String>();
		years.add("2013"); years.add("2014"); years.add("2015"); years.add("2016");
		years.add("2017"); years.add("2018"); years.add("2019"); years.add("2020");
		return years;
		
	}
	
	public static int daysUntil(String dateString) throws ParseException {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);			
		Date itemDate = (Date) sdf.parse(dateString);
		Date nowDate = (Date) sdf.parse(getTodaysDate());
		return Days.daysBetween(new DateTime(nowDate), new DateTime(itemDate)).getDays();
		
	}

}
