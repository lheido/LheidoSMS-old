package com.lheido.sms;

import java.io.IOException;
import java.io.InputStream;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build.VERSION_CODES;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.telephony.PhoneNumberUtils;
import android.widget.Toast;

public class LheidoContact {
	private String name_ = null;
	private String lastsms_ = null;
	private long nb_sms_ = -1;
	private String phone_ = null;
	private String conversation_id_ = null;
	private Bitmap pic;
	private long id;
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
					//long id = cur.getLong(cur.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
					//this.setId(id);
					//this.setPic(context);
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
	
	public void setPic(Context context){
		InputStream input = openPhoto(context);
		if(input != null)
			this.pic = BitmapFactory.decodeStream(input);
		else 
			this.pic = null;
	}
	
	public Bitmap getPic(){
		return this.pic;
	}
	
	@SuppressLint("NewApi")
	public InputStream openPhoto(Context context){
		InputStream input = null;
		Uri uri = ContentUris.withAppendedId(Contacts.CONTENT_URI, this.id);
		//input = Contacts.openContactPhotoInputStream(context.getContentResolver(), uri);
		int sdk = android.os.Build.VERSION.SDK_INT;
    	if(sdk < VERSION_CODES.HONEYCOMB)
    		input = Contacts.openContactPhotoInputStream(context.getContentResolver(), uri);
    	else{
    		try{
    			input = Contacts.openContactPhotoInputStream(context.getContentResolver(), uri, true);
    		}catch(Exception ex){
    			Toast.makeText(context, "Error input hd photo\n"+ex.toString(), Toast.LENGTH_LONG).show();
    		}
    	}
    	return input;
	}
	
	/*public InputStream openDisplayPhoto(Context context, long contactId) {
	     Uri contactUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, contactId);
	     Uri displayPhotoUri = Uri.withAppendedPath(contactUri, Contacts.Photo.DISPLAY_PHOTO);
	     try {
	         AssetFileDescriptor fd =
	             context.getContentResolver().openAssetFileDescriptor(displayPhotoUri, "r");
	         return fd.createInputStream();
	     } catch (IOException e) {
	         return null;
	     }
	 }*/
	
	public long getId() {
		return this.id;
	}
	public void setId(long id_){
		this.id = id_;
	}
	public void setName(String string) {
		this.name_ = string;
	}
}
