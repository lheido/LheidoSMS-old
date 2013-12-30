package com.lheido.sms;

import java.util.ArrayList;

//import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.actionbarsherlock.app.SherlockFragment;
//import com.lheido.sms.SMSFrag;
//import com.lheido.sms.MMSFrag;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
	public String name;
	public String phone;
	public int thread_id;
	public int position;
	public ArrayList<SherlockFragment> pages;
	public ViewPagerAdapter(FragmentManager fm, ArrayList<SherlockFragment> p) {
		super(fm);
		pages = p;
	}
	
	@Override
	public Fragment getItem(int pos) {
		return pages.get(pos);
	}
	
	@Override
	public int getItemPosition(Object object){
		return POSITION_NONE;
	}
	
	@Override
	public int getCount() {
		return pages.size();
	}
}
