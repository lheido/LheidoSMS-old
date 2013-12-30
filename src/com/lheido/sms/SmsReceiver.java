package com.lheido.sms;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.lheido.sms.LheidoContact;

public class SmsReceiver extends BroadcastReceiver {
	private static final String ARG_SMS_DELIVERED = "new_sms_delivered";
	private final String ACTION_RECEIVE_SMS = "android.provider.Telephony.SMS_RECEIVED";
	private final String ACTION_SENT_SMS = "com.lheido.sms.sent";
	private final String ACTION_DELIVERED_SMS = "com.lheido.sms.delivered";
	private Context mcontext;
	
	
	@Override
	public void onReceive(Context context, Intent intent) {
		mcontext = context;
		SharedPreferences userPref = PreferenceManager.getDefaultSharedPreferences(mcontext);
		boolean activ_notif = userPref.getBoolean("notif", true);
		boolean vibrate = userPref.getBoolean("vibration", true);
		Vibrator v = (Vibrator) mcontext.getSystemService(Context.VIBRATOR_SERVICE);
		String iAction = intent.getAction();
		if(iAction.equals(ACTION_RECEIVE_SMS)){
			LheidoContact contact = new LheidoContact();
			Bundle bundle = intent.getExtras();
			if(bundle != null){
				Object[] pdus = (Object[]) bundle.get("pdus");
				final SmsMessage[] messages = new SmsMessage[pdus.length];
				for(int i = 0; i<pdus.length; i++){
					messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
				}
				if(messages.length > -1){
					final String body = messages[0].getMessageBody();
					final String phone = messages[0].getDisplayOriginatingAddress();
					final String name = contact.getContactName(context, phone);
					Toast.makeText(context, "Sms reçu de " + name, Toast.LENGTH_LONG).show();
					if(activ_notif){
						Intent notificationIntent = new Intent(context, MainActivity.class);
						PendingIntent pIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
						// Create Notification using NotificationCompat.Builder
						NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
							// Set Icon
							.setSmallIcon(R.drawable.ic_launcher)
							// Set Ticker Message
							.setTicker(body)
							// Set Title
							.setContentTitle(""+name)
							// Set Text
							.setContentText(body)
							// Add an Action Button below Notification
							//.addAction(R.drawable.ic_launcher, "Ouvrir la conversation", pIntent)
							// Set PendingIntent into Notification
							.setContentIntent(pIntent)
							// Dismiss Notification
							.setAutoCancel(true);

						// Create Notification Manager
						NotificationManager notificationmanager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
						// Build Notification with Notification Manager
						notificationmanager.notify(0, builder.build());
						if(vibrate) v.vibrate(1000);
					}
				} 
			}
		}
		else if(iAction.equals(ACTION_DELIVERED_SMS)){
			switch(getResultCode()){
				case Activity.RESULT_OK:
					Toast.makeText(context, "Message remis" , Toast.LENGTH_SHORT).show();
					long _id = intent.getExtras().getLong(ARG_SMS_DELIVERED, -1);
					if(_id != -1){
						ContentValues values = new ContentValues();
				        values.put("read", true);
						try{
				        	context.getContentResolver().update(Uri.parse("content://sms"), values, "_id = "+_id, null);
				        }catch (Exception ex){
				        	Toast.makeText(context, ex.toString(), Toast.LENGTH_LONG).show();
				        }
					}
					//v.vibrate(1000);
					break;
				default:
					Toast.makeText(context, "Erreur, message non remis", Toast.LENGTH_SHORT).show();
					if(vibrate) v.vibrate(2000);
					break;
			}
		}
		else if(iAction.equals(ACTION_SENT_SMS)){
			switch(getResultCode()){
				case Activity.RESULT_OK:
					//Toast.makeText(context, "Le message a certainement dû être envoyé à quelqu'un..." , Toast.LENGTH_SHORT).show();
					//v.vibrate(1000);
					break;
				default:
					Toast.makeText(context, "Erreur, le message n'a pas était envoyé", Toast.LENGTH_SHORT).show();
					if(vibrate) v.vibrate(2000);
					break;
			}
		}
	}
	
}
