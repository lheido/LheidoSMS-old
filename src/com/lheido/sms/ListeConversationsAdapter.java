package com.lheido.sms;

import java.util.ArrayList;

//import com.actionbarsherlock.app.ActionBar.LayoutParams;

import android.content.Context;
//import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
//import android.widget.RelativeLayout;
import android.widget.TextView;

public class ListeConversationsAdapter extends BaseAdapter {
	/**
	 * Récupérer un item de la liste en fonction de sa position
	 * @param position - Position de l'item à récupérer
	 * @return l'item récupéré
	 */
	//private LayoutInflater mInflater;
	private ArrayList<LheidoContact> mListConv;
	private Context mContext;
	
	public ListeConversationsAdapter(Context context, int ressource, ArrayList<LheidoContact> list_conversation){
		mContext = context;
		mListConv = list_conversation;
		//mInflater = LayoutInflater.from(mContext);
	}
	
	public LheidoContact getItem(int position) {
		return mListConv.get(position);
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
		LheidoContact contact = (LheidoContact) this.getItem(r);
		 
		ListeConversationViewHolder holder; 
		if(convertView == null)
		{
			holder = new ListeConversationViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.conversations_list, parent, false);
			holder.mName = (TextView) convertView.findViewById(R.id.list_conversation_contact_name);
			holder.mCount = (TextView) convertView.findViewById(R.id.list_conversation_count);
			//holder.mLayout = (RelativeLayout) convertView.findViewById(R.id.message_relativeLayout);
			convertView.setTag(holder);
		}
		else
			holder = (ListeConversationViewHolder) convertView.getTag();
	 
		holder.mName.setText(contact.getName());
		holder.mCount.setText(contact.getNb_sms());
	 
		//RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.mLayout.getLayoutParams();
		//Check whether message is mine to show green background and align to right
		/*if(contact.isRight())
		{
			
			//holder.mLayout.setGravity(Gravity.RIGHT);
			//holder.mLayout.setPadding(42, 0, 0, 0);
			holder.mBody.setBackgroundColor(0xFF202020);
		}
		//If not mine then it is from sender to show orange background and align to left
		else
		{
			//holder.mLayout.setGravity(Gravity.LEFT);
			//holder.mLayout.setPadding(0, 0, 42, 0);
			holder.mBody.setBackgroundColor(0xFF2A2A2A);
		}*/
		return convertView;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mListConv.size();
	}
	static class ListeConversationViewHolder {
		//public RelativeLayout mLayout;
		public TextView mName;
		public TextView mCount;
		//public ImageView mContactPicture;
	}
}

