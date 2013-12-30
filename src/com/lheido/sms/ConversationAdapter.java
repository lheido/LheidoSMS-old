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
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ConversationAdapter extends BaseAdapter {
	/**
	 * Récupérer un item de la liste en fonction de sa position
	 * @param position - Position de l'item à récupérer
	 * @return l'item récupéré
	 */
	public MainActivity.UserPref userPref;
	//private LayoutInflater mInflater;
	private ArrayList<Message> mListSms;
	private Context mContext;
	
	public ConversationAdapter(Context context, int ressource, ArrayList<Message> conversation){
		mContext = context;
		mListSms = conversation;
		userPref = MainActivity.setUserPref(PreferenceManager.getDefaultSharedPreferences(mContext));
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
		ConversationViewHolder holder; 
		if(convertView == null)
		{
			holder = new ConversationViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.message, parent, false);
			holder.mBody = (TextView) convertView.findViewById(R.id.message);
			holder.mdate = (TextView) convertView.findViewById(R.id.date_message);
			holder.mLayout = (RelativeLayout) convertView.findViewById(R.id.message_relativeLayout);
			holder.mIsRead = (View) convertView.findViewById(R.id.is_read);
			convertView.setTag(holder);
		}
		else
			holder = (ConversationViewHolder) convertView.getTag();
	 
		holder.mBody.setText(message.getBody());
		holder.mBody.setTextSize(userPref.text_size);
		holder.mdate.setText(message.getDate());
	 
		//RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.mLayout.getLayoutParams();
		//Check whether message is mine to show green background and align to right
		if(message.isRight())
		{
			
			holder.mLayout.setGravity(Gravity.RIGHT);
			holder.mLayout.setPadding(42, 0, 0, 0);
			holder.mBody.setBackgroundColor(0xFF2A2A2A);
			if(message.isRead())
				holder.mIsRead.setBackgroundColor(0xFF2A5A2A);
			else{
				holder.mIsRead.setBackgroundColor(0x00000000);
			}
		}
		//If not mine then it is from sender to show orange background and align to left
		else
		{
			holder.mLayout.setGravity(Gravity.LEFT);
			holder.mLayout.setPadding(0, 0, 42, 0);
			holder.mBody.setBackgroundColor(0xFF1A1A1A);
			holder.mIsRead.setBackgroundColor(0x00000000);
		}
		return convertView;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mListSms.size();
	}
	class ConversationViewHolder {
		public RelativeLayout mLayout;
		public TextView mBody;
		public TextView mdate;
		public View mIsRead;
		//public ImageView mContactPicture;
	}
}

