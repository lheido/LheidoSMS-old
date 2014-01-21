package com.lheido.sms;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;

public class LheidoContact {
	private String name_ = null;
	private String lastsms_ = null;
	private long nb_sms_ = -1;
	private String phone_ = null;
	private String conversation_id_ = null;
	public LheidoContact(){
		// Empty constructor
	}
	public LheidoContact(String name, String phone, long nb_sms, String conversation_id){
		name_ = name;
		phone_ = phone;
		nb_sms_ = nb_sms;
		conversation_id_ = conversation_id;
	}
	public String getContactName(Context context, String address){
    	String res = "";
    	Cursor cur = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
		if(cur != null){
			String name = "";
			while(name.equals("") && cur.moveToNext()){
				String phone = cur.getString(cur.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)).toString();
				//Log.v("LHEIDO SMS LOG", phone + ", " + address);
				if(name.equals("") && PhoneNumberUtils.compare(phone, address)){
					name = cur.getString(cur.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)).toString();
					res += name;
				}
			}
			if(name.equals(""))
				res += address;
			cur.close();
		}
    	return res;
    }
	
	public String getName(){
		return name_;
	}
	public String getLastsms(){
		return lastsms_;
	}
	public long getNb_sms(){
		return nb_sms_;
	}
	public String getPhone(){
		return phone_;
	}
	public String getConversationId(){
		return conversation_id_;
	}
	public void setNb_sms(String count){
		this.nb_sms_ = Integer.parseInt(count);
	}
	public void setPhone(String address){
		this.phone_ = address;
	}
	public void setName(Context context, String name){
		this.name_ = getContactName(context, name);
	}
	public void setConversationId(String id){
		this.conversation_id_ = id;
	}
}
