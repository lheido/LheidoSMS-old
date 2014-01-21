package com.lheido.sms;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Build.VERSION_CODES;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LheidoUtils {
	public static class UserPref{
    	public int max_conversation = 10;
    	public int max_sms = 21;
    	public boolean hide_keyboard = true;
    	public boolean first_upper = true;
    	public boolean vibrate = true;
    	public boolean vibrate_delivered = true;
    	public float text_size = 13.0F;
    	public boolean hold_message = true;
    	public int hold_message_num = 500;
    	UserPref(){}
    	public void setUserPref(SharedPreferences pref){
        	String pref_nb_conv = pref.getString("conversation_onload", "10");
        	String pref_nb_sms = pref.getString("sms_onload", "21");
        	String pref_text_size = pref.getString("text_size", "13");
        	hold_message = pref.getBoolean("hold_message", true);
        	hold_message_num = Integer.parseInt(pref.getString("hold_message_num", "500"));
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
        	this.vibrate_delivered = pref.getBoolean("delivered_vibration", true);
        }
	}
	
	public static class LheidoDialog extends Dialog implements android.view.View.OnClickListener {
		private static final int CONVERSATION_DIALOG = 1;
		private static final int MESSAGE_DIALOG = 2;
		private int id_dialog;
		private Context mContext;
		private MainActivity act;
		private int pos;
		private UserPref userPref;
		private LheidoContact lcontact;
		private Message sms;
		private SMSFrag parent;
		private long thread_id;
		public LheidoDialog(MainActivity activity, int type_dialog, int position, LheidoContact contact) {
			super(activity);
			this.act = activity;
			mContext = activity.getApplicationContext();
			id_dialog = type_dialog;
			pos = position;
			lcontact = contact;
			userPref = new UserPref();
	    	userPref.setUserPref(PreferenceManager.getDefaultSharedPreferences(mContext));
		}
		public LheidoDialog(MainActivity activity, int type_dialog, int position, long th_id, Message message, SMSFrag s) {
			super(activity);
			this.act = activity;
			mContext = activity.getApplicationContext();
			id_dialog = type_dialog;
			pos = position;
			sms = message;
			parent = s;
			thread_id = th_id;
			userPref = new UserPref();
	    	userPref.setUserPref(PreferenceManager.getDefaultSharedPreferences(mContext));
		}
		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			switch(id_dialog){
			case CONVERSATION_DIALOG:
				setContentView(R.layout.conversation_dialog);
				TextView supp_conv = (TextView) findViewById(R.id.supp_conversation_dialog);
				TextView clear_conv = (TextView) findViewById(R.id.clear_conversation_dialog);
				TextView clear_hold = (TextView) findViewById(R.id.clear_hold_conversation_dialog);
				TextView open_conv = (TextView) findViewById(R.id.open_conversation_dialog);
				supp_conv.setOnClickListener(this);
				clear_conv.setOnClickListener(this);
				clear_hold.setOnClickListener(this);
				open_conv.setOnClickListener(this);
				break;
			case MESSAGE_DIALOG:
				setContentView(R.layout.message_dialog);
				TextView supp_sms = (TextView) findViewById(R.id.supp_message_dialog);
				TextView transf_sms = (TextView) findViewById(R.id.transfert_message_dialog);
				TextView copy_sms = (TextView) findViewById(R.id.copy_message_dialog);
				TextView detail_sms = (TextView) findViewById(R.id.detail_message_dialog);
				supp_sms.setOnClickListener(this);
				transf_sms.setOnClickListener(this);
				copy_sms.setOnClickListener(this);
				detail_sms.setOnClickListener(this);
				break;
			default: break;
			}
		}
		
		@SuppressLint("NewApi")
		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
		    case R.id.supp_conversation_dialog:
		    	//Toast.makeText(mContext, "La conversation a était supprimée", Toast.LENGTH_LONG).show();
		    	Toast.makeText(mContext, "Action disable", Toast.LENGTH_LONG).show();
		    	break;
		    case R.id.clear_conversation_dialog:
		    	try{
		    		delete_sms(1, 1);
		    		Toast.makeText(mContext, "La conversation a était vidée", Toast.LENGTH_LONG).show();
		    	}catch(Exception ex){
		    		Toast.makeText(mContext, ex.toString(), Toast.LENGTH_LONG).show();
		    		Log.v("LHEIDO SMS LOG", ex.toString());
		    	}
		    	break;
		    case R.id.clear_hold_conversation_dialog:
		    	if(userPref.hold_message){
		    		if(userPref.hold_message_num < lcontact.getNb_sms()){
		    			delete_sms(userPref.hold_message_num, userPref.hold_message_num);
		    			Toast.makeText(mContext, "Anciens messages supprimés", Toast.LENGTH_LONG).show();
		    		}
		    	} else{
		    		Toast.makeText(mContext, "La suppression des anciens messages est désactivée", Toast.LENGTH_LONG).show();
		    	}
		    	break;
		    case R.id.open_conversation_dialog:
		    	this.act.selectItem(pos);
		    	break;
		    case R.id.supp_message_dialog:
		    	mContext.getContentResolver().delete(Uri.parse("content://sms/"+sms.getId()), "thread_id = "+thread_id, null);
		    	this.parent.remove_sms(pos);
		    	break;
		    case R.id.transfert_message_dialog: break;
		    case R.id.copy_message_dialog:
		    	int sdk = android.os.Build.VERSION.SDK_INT;
		    	if(sdk < VERSION_CODES.HONEYCOMB){
		    		android.text.ClipboardManager clipboard = (android.text.ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
		    		clipboard.setText(sms.getBody());
		    	} else{
		    		android.content.ClipboardManager clipboard = (android.content.ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
		    		ClipData clip = ClipData.newPlainText("simple text", sms.getBody());
		    		clipboard.setPrimaryClip(clip);
		    	}
		    	break;
		    case R.id.detail_message_dialog: break;
		    default:
		    	break;
		    }
			dismiss();
		}
		
		public void delete_sms(long count, long update){
			Uri uri = Uri.parse("content://sms");
			String[] projection = {"*"};
			String selection = "thread_id = ?";
			String[] selectionArgs = {""+lcontact.getConversationId()};
			Cursor cr = mContext.getContentResolver().query(uri, projection, selection, selectionArgs, "date DESC");
			if(cr != null){
				ArrayList<Long> list_id_delete = new ArrayList<Long>();
				long c = 0;
				while(cr.moveToNext()){
					if(c >= count)
						list_id_delete.add(cr.getLong(cr.getColumnIndexOrThrow("_id")));
					c ++;
				}
				cr.close();
				for(Long id : list_id_delete){
					mContext.getContentResolver().delete(Uri.parse("content://sms/"+id), selection, selectionArgs);
				}
			}
			this.act.updateContact(pos, ""+update);
		}
		
	}
	
}
