package com.lheido.sms;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;

public class LheidoSMSFragBase extends SherlockFragment {
	public static final String ARG_CONVERSATION_ID = "conversation_id";
	public static final String ARG_CONTACT_PHONE = "contact_phone";
	public static final String ARG_CONVERSATION_NUMBER = "conversation_number";
    public static final String ARG_CONTACT_NAME = "contact_name";
	public Context context;
	public MainActivity.UserPref userPref;
	public String name;
	public String phoneContact;
	public int conversationId;
	public ListView liste;
	public int list_conversationId;
	public BroadcastReceiver mBroadCast;
	
	public LheidoSMSFragBase(){
		// Empty constructor required for fragment subclasses
	}
	
	public void add_sms(long _id, String body, String type, int read,Time t, int position, ArrayList<Message> liste){
    	Message sms = new Message();
    	if(_id != -1)
    		sms.setId(_id);
		sms.setBody(body);
		sms.setDate(t);
		if(type.equals("2")){
			sms.setRight(true);
			if(read == 1)
				sms.setRead(true);
			else sms.setRead(false);
		}
		if(position != 0){
			liste.add(sms);
		} else{
			liste.add(0, sms);
		}
    }
	
	public void add_mms(long _id, String body, Bitmap pict, String type, int read, Time t, int position, ArrayList<Message> liste){
		Message sms = new Message();
    	if(_id != -1)
    		sms.setId(_id);
    	sms.setBody(body);
		sms.setPicture(pict);
		sms.setDate(t);
		if(type.equals("2")){
			sms.setRight(true);
			if(read == 1)
				sms.setRead(true);
			else sms.setRead(false);
		}
		if(position != 0){
			liste.add(sms);
		} else{
			liste.add(0, sms);
		}
	}
	
	public void gen_conversation(ArrayList<Message> liste, Uri uri){
		liste.clear();
        Cursor query = context.getContentResolver().query(uri, new String[]{"*"}, "thread_id = "+conversationId, null, "date DESC");
        if(query != null){
        	int count = 0;
        	while(count < userPref.max_sms && query.moveToNext()){
        		String string = "";
        		long _id = query.getLong(query.getColumnIndexOrThrow("_id"));
        		string = query.getString(query.getColumnIndexOrThrow("body")).toString();
        		String type = query.getString(query.getColumnIndexOrThrow("type")).toString();
        		int read = query.getInt(query.getColumnIndexOrThrow("read"));
        		long date = query.getLong(query.getColumnIndexOrThrow("date"));
        		Time t = new Time();
        		t.set(date);
        		Log.v("LHEIDO SMS LOG", "_id = "+_id+",\n body = "+string+",\n read = "+read);
        		add_sms(_id, string, type, read, t, 1, liste);
        		count += 1;
        	}
        	query.close();
        }
	}
	
	public void gen_MMSconversation(ArrayList<Message> liste, Uri uri){
		liste.clear();
		try{
			Cursor query = context.getContentResolver().query(uri, new String[]{"*"}, "thread_id = "+conversationId, null, "date DESC");
			if(query != null){
				int count = 0;
				while(count < userPref.max_sms && query.moveToNext()){
					String string = "";
					long mmsId = query.getLong(query.getColumnIndexOrThrow("_id"));
					int read = query.getInt(query.getColumnIndexOrThrow("read"));
					long date = query.getLong(query.getColumnIndexOrThrow("date"));
					Time t = new Time();
					t.set(date);
					Log.v("LHEIDO SMS LOG", "_id = "+mmsId+",\n body = "+string+",\n read = "+read);
					getMMSData(mmsId, read, t, liste);
					//add_sms(_id, string, type, read, t, 1, liste);
					count += 1;
				}
				query.close();
			}
			if(liste.isEmpty()){
				Time now = new Time();
				now.setToNow();
				add_sms(-1L, "Pas de mms", "1", 0, now, 0, liste);
			}
		}catch(Exception ex){}
	}
	
	public void getMMSData(long mmsId, int read, Time t, ArrayList<Message> liste){
		String selectionPart = "mid=" + mmsId;
		Uri uri = Uri.parse("content://mms/part");
		try{
			Cursor cPart = context.getContentResolver().query(uri, null, selectionPart, null, null);
			if (cPart.moveToFirst()) {
				do {
					String partId = cPart.getString(cPart.getColumnIndex("_id"));
					String type = cPart.getString(cPart.getColumnIndex("ct"));
					if ("image/jpeg".equals(type) || "image/bmp".equals(type) ||
							"image/gif".equals(type) || "image/jpg".equals(type) ||
							"image/png".equals(type)) {
						Bitmap pict = getMmsImage(partId);
						add_mms( mmsId, "", pict, type, read, t, 1, liste);
					}
				} while (cPart.moveToNext());
			}
		}catch(Exception ex){}
	}
	
	private Bitmap getMmsImage(String _id) {
	    Uri partURI = Uri.parse("content://mms/part/" + _id);
	    InputStream is = null;
	    Bitmap bitmap = null;
	    try {
	        is = context.getContentResolver().openInputStream(partURI);
	        bitmap = BitmapFactory.decodeStream(is);
	    } catch (IOException e) {}
	    finally {
	        if (is != null) {
	            try {
	                is.close();
	            } catch (IOException e) {}
	        }
	    }
	    return bitmap;
	}
	
	public void __init__(View rootView, int id_liste){
		context = getActivity();
    	userPref = MainActivity.setUserPref(PreferenceManager.getDefaultSharedPreferences(context));
        name = getArguments().getString(ARG_CONTACT_NAME);
        phoneContact = getArguments().getString(ARG_CONTACT_PHONE);
        conversationId = getArguments().getInt(ARG_CONVERSATION_ID);
        liste = (ListView) rootView.findViewById(id_liste);
        list_conversationId = getArguments().getInt(ARG_CONVERSATION_NUMBER);
	}
}
