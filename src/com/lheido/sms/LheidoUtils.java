package com.lheido.sms;

import android.content.SharedPreferences;

public class LheidoUtils {
	public static class UserPref{
    	public int max_conversation = 10;
    	public int max_sms = 21;
    	public boolean hide_keyboard = true;
    	public boolean first_upper = true;
    	public boolean vibrate = true;
    	public float text_size = 13.0F;
    	UserPref(){}
    	public void setUserPref(SharedPreferences pref){
        	String pref_nb_conv = pref.getString("conversation_onload", "10");
        	String pref_nb_sms = pref.getString("sms_onload", "21");
        	String pref_text_size = pref.getString("text_size", "13");
        	try{
        		this.max_conversation = Integer.parseInt(pref_nb_conv);
        	}catch(Exception ex){
        		this.max_conversation = 10000;
        	}
        	try{
        		this.max_sms = Integer.parseInt(pref_nb_sms);
        	}catch(Exception ex){
        		this.max_sms = 100000;
        	}
        	try{
        		this.text_size = Float.parseFloat(pref_text_size);
        	}catch(Exception ex){
        		this.text_size = 13.0F;
        	}
        	this.hide_keyboard = pref.getBoolean("hide_keyboard", true);
        	this.first_upper = pref.getBoolean("first_uppercase", true);
        	this.vibrate = pref.getBoolean("vibration", true);
        }
	}
}
