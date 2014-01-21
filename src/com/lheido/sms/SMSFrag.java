package com.lheido.sms;

import java.util.ArrayList;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
//import android.content.pm.PackageManager;
//import android.database.Cursor;
import android.net.Uri;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.InputType;
import android.text.format.Time;
//import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

//import com.actionbarsherlock.app.SherlockFragment;
import com.lheido.sms.LheidoSMSFragBase;
import com.lheido.sms.LheidoUtils.LheidoDialog;

public class SMSFrag extends LheidoSMSFragBase {
    public static final int MESSAGE_DIALOG = 2;
	protected final String ARG_SMS_DELIVERED = "new_sms_delivered";
	protected final String ACTION_RECEIVE_SMS = "android.provider.Telephony.SMS_RECEIVED";
	protected final String ACTION_SENT_SMS = "com.lheido.sms.sent";
	protected final String ACTION_DELIVERED_SMS = "com.lheido.sms.delivered";
    public ArrayList<Message> Message_list = new ArrayList<Message>();
    public ListView ListeConversation = null;
    public ConversationAdapter conversationAdapter;
    public EditText sms_body;
    public String mem_body;
    public MainActivity act;
    public SMSFrag() {
        // Empty constructor required for fragment subclasses
    }
    
    public void setAct(MainActivity a){
		this.act = a;
	}
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	View rootView = inflater.inflate(R.layout.conversation, container, false);
    	super.__init__(rootView, R.id.list_conversation);
    	super.gen_conversation(Message_list);
    	
        conversationAdapter = new ConversationAdapter(context, R.layout.message, Message_list);
        liste.setAdapter(conversationAdapter);
        liste.setOnItemLongClickListener(new ConversationLongClick());
        //liste.setOnItemLongClickListener(new ConversationLongClick());
        if(sdk >= VERSION_CODES.HONEYCOMB){
        	liste.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        }
        inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        
        getActivity().setTitle(name);
        
        final ImageButton send_button = (ImageButton) rootView.findViewById(R.id.send_button);
        sms_body = (EditText) rootView.findViewById(R.id.send_body);
        sms_body.setOnFocusChangeListener(new View.OnFocusChangeListener() {
        	@Override
        	public void onFocusChange(View v, boolean hasFocus) {
        		if(hasFocus){
        			if(sdk < VERSION_CODES.HONEYCOMB)
        				liste.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        		}else {
        			if(sdk < VERSION_CODES.HONEYCOMB)
        				liste.setTranscriptMode(ListView.TRANSCRIPT_MODE_DISABLED);
        			inputManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        		}
        	}
        });
        liste.setOnItemClickListener(new ListView.OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				sms_body.clearFocus();
			}
        	
        });

        if(mem_body != null) sms_body.setText(mem_body);
        if(userPref.first_upper)
        	sms_body.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        sms_body.setSingleLine(false);
        send_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(userPref.hide_keyboard){
					inputManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				}
				String body = sms_body.getText().toString();
				if(body.length() > 0){
					Message new_sms = new Message();
					new_sms.setBody(body);
					new_sms.setRight(true);
					new_sms.setRead(false);
					Time now = new Time();
					now.setToNow();
					new_sms.setDate(now);
					long new_id = MainActivity.store_sms(new_sms, conversationId);
					add_sms(new_id, body, "2", 32, now, 0, Message_list);
					conversationAdapter.notifyDataSetChanged();
					conversation_nb_sms += 1;
					act.updateContact(list_conversationId, ""+conversation_nb_sms);
					sms_body.setText(R.string.empty_sms);
					SmsManager manager = SmsManager.getDefault();
					ArrayList<String> bodyPart = manager.divideMessage(body);
					if(bodyPart.size() > 1){
						ArrayList<PendingIntent> piSent = new ArrayList<PendingIntent>();
						ArrayList<PendingIntent> piDelivered = new ArrayList<PendingIntent>();
						for(int i = 0; i < bodyPart.size(); i++){
							Intent ideli = new Intent(ACTION_DELIVERED_SMS);
							ideli.putExtra(ARG_SMS_DELIVERED, new_id);
							piSent.add(PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SENT_SMS) , 0));
							piDelivered.add(PendingIntent.getBroadcast(context, 0, ideli , PendingIntent.FLAG_UPDATE_CURRENT));
						}
						manager.sendMultipartTextMessage(phoneContact, null, bodyPart , piSent, piDelivered);
					}
					else {
						Intent ideli = new Intent(ACTION_DELIVERED_SMS);
						ideli.putExtra(ARG_SMS_DELIVERED, new_id);
		                PendingIntent piSent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SENT_SMS) , 0);
		                PendingIntent piDelivered = PendingIntent.getBroadcast(context, 0, ideli, PendingIntent.FLAG_UPDATE_CURRENT);
						manager.sendTextMessage(phoneContact, null, body, piSent, piDelivered);
					}
					liste.smoothScrollToPosition(liste.getBottom());
					sms_body.clearFocus();
				} else{
					Toast.makeText(context, R.string.empty_message, Toast.LENGTH_LONG).show();
				}
			}
		});
        
        send_button.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				Toast.makeText(context, ""+conversationId+"\n"+phoneContact+"\n"+name, Toast.LENGTH_LONG).show();
				return true;
			}
		});
        
        mBroadCast = new BroadcastReceiver(){
			@Override
			public void onReceive(Context context, Intent intent) {
				SharedPreferences userPref = PreferenceManager.getDefaultSharedPreferences(context);
				boolean vibrate = userPref.getBoolean("vibration", true);
				Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
				String iAction = intent.getAction();
				//Toast.makeText(context, "===> ACTION " + iAction, Toast.LENGTH_LONG).show();
				if(iAction.equals(ACTION_RECEIVE_SMS)){
					Bundle bundle = intent.getExtras();
					if(bundle != null){
						Object[] pdus = (Object[]) bundle.get("pdus");
						final SmsMessage[] messages = new SmsMessage[pdus.length];
						for(int j = 0; j<pdus.length; j++){
							messages[j] = SmsMessage.createFromPdu((byte[]) pdus[j]);
						}
						if(messages.length > -1){
							LheidoContact new_contact = new LheidoContact();
							String body = "";
							for(SmsMessage x : messages){
								body += x.getMessageBody();
							}
							long date = messages[0].getTimestampMillis();
							Time t = new Time();
		            		t.set(date);
							String phone = messages[0].getDisplayOriginatingAddress();
							String new_name = new_contact.getContactName(context, phone); 
							if(PhoneNumberUtils.compare(phoneContact, phone)){
								//on est dans la bonne conversation !
								add_sms(-1L, body, "", 0, t, 0, Message_list);
								conversationAdapter.notifyDataSetChanged();
								conversation_nb_sms += 1;
								act.updateContact(list_conversationId, ""+conversation_nb_sms);
								Toast.makeText(context, "Nouveau message de " + new_name, Toast.LENGTH_LONG).show();
								liste.smoothScrollToPosition(liste.getBottom());
							} else{
								Toast.makeText(context, "Nouveau message de " + new_name, Toast.LENGTH_LONG).show();
							}
							if(vibrate) v.vibrate(1000);
						} 
					}
				}
				else if(iAction.equals(ACTION_DELIVERED_SMS)){
					switch(getResultCode()){
						case Activity.RESULT_OK:
							long _id = intent.getExtras().getLong(ARG_SMS_DELIVERED, -1);
							if(_id != -1){
								ContentValues values = new ContentValues();
						        values.put("status", 0);
						        try{
						        	context.getContentResolver().update(Uri.parse("content://sms/"+_id), values, null, null);
						        	int k = 0;
						        	boolean find = false;
						        	while(!find && k < Message_list.size()){
						        		if(_id == Message_list.get(k).getId()){
						        			//Toast.makeText(context, "Trouver mouahahah" , Toast.LENGTH_SHORT).show();
						        			find = true;
						        			Message_list.get(k).setRead(true);
						        			conversationAdapter.notifyDataSetChanged();
						        		}
						        		k++;
						        	}
						        	if(!find)
						        		Toast.makeText(context, "Pas trouver l'id :(, c'est pas normal ><" , Toast.LENGTH_SHORT).show();
						        }catch (Exception ex){
						        	Toast.makeText(context, ex.toString(), Toast.LENGTH_LONG).show();
						        }
							}
							boolean vibrate_delivered = userPref.getBoolean("delivered_vibration", true);
							if(vibrate_delivered){
								long[] pattern = {
										0,  // Start immediately
										100,100,100,100,100,100,100
								};
								v.vibrate(pattern, -1);
							}
							Toast.makeText(context, "Message remis" , Toast.LENGTH_SHORT).show();
							break;
						default:
							Toast.makeText(context, "Erreur, message non remis", Toast.LENGTH_SHORT).show();
							if(vibrate)
								v.vibrate(2000);
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
							if(vibrate)
								v.vibrate(2000);
							break;
					}
				}
			}
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_RECEIVE_SMS);
        filter.addAction(ACTION_SENT_SMS);
        filter.addAction(ACTION_DELIVERED_SMS);
        filter.setPriority(2000);
        context.registerReceiver(mBroadCast, filter);
        return rootView;
    }
    
    @Override
    public void onDestroyView(){
    	context.unregisterReceiver(mBroadCast);
    	//context.getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_ENABLED , PackageManager.DONT_KILL_APP);
    	String text = "";
    	try{
    		text = sms_body.getText().toString();
    	}catch(Exception ex){
    		text = "";
    	}
    	if(!text.equals("")) mem_body = text;
    	super.onDestroyView();
    }
    
    public void remove_sms(int position){
    	Message_list.remove(position);
    	conversationAdapter.notifyDataSetChanged();
    }
    
    public class ConversationLongClick implements ListView.OnItemLongClickListener{
		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
			Message sms = Message_list.get(Message_list.size() -1 - position);
			LheidoDialog dialog = new LheidoDialog(act, MESSAGE_DIALOG, position, conversationId, sms, SMSFrag.this);
			dialog.show();
			return false;
		}
    }
    
    
}
