package com.lheido.sms;

import java.util.ArrayList;

//import com.actionbarsherlock.app.ActionBar.LayoutParams;

import android.content.Context;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lheido.sms.LheidoUtils.UserPref;

public class ConversationMMSAdapter extends BaseAdapter {
	/**
	 * Récupérer un item de la liste en fonction de sa position
	 * @param position - Position de l'item à récupérer
	 * @return l'item récupéré
	 */
	public UserPref userPref;
	//private LayoutInflater mInflater;
	private ArrayList<Message> mListSms;
	private Context mContext;
	
	public ConversationMMSAdapter(Context context, int ressource, ArrayList<Message> conversation){
		mContext = context;
		mListSms = conversation;
		userPref = new UserPref();
		userPref.setUserPref(PreferenceManager.getDefaultSharedPreferences(mContext));
		//mInflater = LayoutInflater.from(mContext);
	}
	
	public Message getItem(int position) {
		return mListSms.get(getCount() -1 - position);
	}

	/**
	 * Récupérer l'identifiant d'un item de la liste en fonction de sa position (plutôt utilisé dans le cas d'une
	 * base de données, mais on va l'utiliser aussi)
	 * @param position - Position de l'item à récupérer
	 * @return l'identifiant de l'item
	 */
	public long getItemId(int position) {
		return position;
	}
	
	public View getView(int r, View convertView, ViewGroup parent) {
		Message message = (Message) this.getItem(r);
		ConversationViewHolderMMS holder; 
		if(convertView == null)
		{
			holder = new ConversationViewHolderMMS();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.message_mms, parent, false);
			holder.mBody = (TextView) convertView.findViewById(R.id.message_mms);
			holder.mImg = (ImageView) convertView.findViewById(R.id.image_message_mms);
			holder.mdate = (TextView) convertView.findViewById(R.id.date_message_mms);
			holder.mLayout = (RelativeLayout) convertView.findViewById(R.id.message_relativeLayout_mms);
			holder.mIsRead = (View) convertView.findViewById(R.id.is_read_mms);
			convertView.setTag(holder);
		}
		else
			holder = (ConversationViewHolderMMS) convertView.getTag();
	 
		holder.mBody.setText(message.getBody());
		holder.mBody.setTextSize(userPref.text_size);
		try{
			holder.mImg.setImageBitmap(message.getPicture());
		}catch(Exception ex){}
		holder.mdate.setText(message.getDate());
	 
		//RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.mLayout.getLayoutParams();
		if(message.isRight()) {
			
			holder.mLayout.setGravity(Gravity.RIGHT);
			holder.mLayout.setPadding(42, 0, 0, 0);
			holder.mBody.setBackgroundColor(0xFF2A2A2A);
			holder.mImg.setBackgroundColor(0xFF2A2A2A);
			if(message.isRead())
				holder.mIsRead.setBackgroundColor(0xFF2A5A2A);
			else{
				holder.mIsRead.setBackgroundColor(0x00000000);
			}
		}
		else {
			holder.mLayout.setGravity(Gravity.LEFT);
			holder.mLayout.setPadding(0, 0, 42, 0);
			holder.mBody.setBackgroundColor(0xFF1A1A1A);
			holder.mImg.setBackgroundColor(0xFF1A1A1A);
			holder.mIsRead.setBackgroundColor(0x00000000);
		}
		return convertView;
	}
	@Override
	public int getCount() {
		return mListSms.size();
	}
	class ConversationViewHolderMMS {
		public RelativeLayout mLayout;
		public RelativeLayout mRelative_body;
		public ImageView mImg;
		public TextView mBody;
		public TextView mdate;
		public View mIsRead;
	}
}

