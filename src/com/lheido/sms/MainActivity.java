package com.lheido.sms;

import java.util.ArrayList;

//import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
//import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
//import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
//import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
//import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
//import android.telephony.SmsMessage;
import android.text.InputType;
import android.text.format.Time;
import android.util.Log;
import android.view.ActionProvider;
import android.view.ContextMenu.ContextMenuInfo;
//import android.view.LayoutInflater;
import android.view.SubMenu;
import android.view.View;
//import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

//class perso
import com.lheido.sms.LheidoContact;
import com.lheido.sms.Message;
//import com.lheido.sms.ConversationAdapter;
import com.lheido.sms.ListeConversationsAdapter;
import com.lheido.sms.LheidoUtils.UserPref;

public class MainActivity extends SherlockFragmentActivity {
	protected final String ARG_SMS_DELIVERED = "new_sms_delivered";
	protected final String ACTION_RECEIVE_SMS = "android.provider.Telephony.SMS_RECEIVED";
	protected final String ACTION_SENT_SMS = "com.lheido.sms.sent";
	protected final String ACTION_DELIVERED_SMS = "com.lheido.sms.delivered";
    private DrawerLayout mDrawerLayout;
    private RelativeLayout mDrawerRelative;
    private RelativeLayout mNewConversationLayout;
    private ListView mDrawerList;
    private ViewPagerAdapter mViewPagerAdapter;
    private ViewPager mViewPager;
    protected static ListeConversationsAdapter lConversationsAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    public static Context context;
    public static ComponentName component;
    public static BroadcastReceiver mBroadCast;
    private int position_mem = 0;
    private EditText new_sms_body;
    private EditText new_sms_phone;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    public static String mem_body = null;
    public UserPref userPref;
    public ArrayList<SherlockFragment> pages;
    //private static ArrayList<String> mConversationListe = new ArrayList<String>();
    private static ArrayList<LheidoContact> lheidoConversationListe = new ArrayList<LheidoContact>();
    
    public void genListContact(){
    	final String[] projection = new String[] {"_id", "date", "message_count", "recipient_ids", "read", "type"};
    	Uri uri = Uri.parse("content://mms-sms/conversations?simple=true");
    	Cursor query = context.getContentResolver().query(uri, projection, null, null, "date DESC");
    	if (query.moveToFirst()) {
    		int i = 0;
    		do {
    			//mConversationListe.add(getConversationInfo(query));
    			lheidoConversationListe.add(getLConversationInfo(query));
    			i = i + 1;
    		} while (i < userPref.max_conversation && query.moveToNext());
    	} else {
    		//mConversationListe.add("Pas de conversations !");
    	}
    	if (query != null) {
    		query.close();
    	}
    }
    
    
    public void updateContactList(){
    	lheidoConversationListe.clear();
    	genListContact();
    	lConversationsAdapter.notifyDataSetChanged();
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	/* unregister SmsReceiver */
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        component = new ComponentName(context, SmsReceiver.class);
        context.getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_DISABLED , PackageManager.DONT_KILL_APP);
       
        mTitle = mDrawerTitle = getTitle();
        
        mem_body = null;
        
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerRelative = (RelativeLayout) findViewById(R.id.left_drawer);
        mDrawerList = (ListView) findViewById(R.id.left_drawer_list);
        mNewConversationLayout = (RelativeLayout) findViewById(R.id.right_drawer);
        
        userPref = new UserPref();
        userPref.setUserPref(PreferenceManager.getDefaultSharedPreferences(this));
        
        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        lheidoConversationListe.clear();
        if(lheidoConversationListe.isEmpty()){
        	genListContact();
        }/* else { //sinon maj des infos
        	for(int i=0; i<lheidoConversationListe.size();i++){
        		Cursor c = context.getContentResolver().query(Uri.parse("content://mms-sms/conversations?simple=true"), new String[]{"*"}, "_id = "+lheidoConversationListe.get(i).getConversationId(), null, null);
        		if(c.moveToFirst()){
        			String count = c.getString(c.getColumnIndex("message_count")).toString(); 
        			if(!count.equals(lheidoConversationListe.get(i).getNb_sms())){
        				lheidoConversationListe.get(i).setNb_sms(count);
        			}
        			c.close();
        		}
        	}
        }*/
        lConversationsAdapter = new ListeConversationsAdapter(this, R.layout.conversations_list, lheidoConversationListe);
        mDrawerList.setAdapter(lConversationsAdapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View drawerView) {
            	getSupportActionBar().setTitle(mTitle);
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                mDrawerToggle.syncState();
            }

            public void onDrawerOpened(View drawerView) {
            	if(drawerView.equals(mNewConversationLayout)){
            		if(mDrawerLayout.isDrawerOpen(mDrawerRelative)){
            			mDrawerLayout.closeDrawer(mDrawerRelative);
            		}
            	} else if(drawerView.equals(mDrawerRelative)){
            		if(mDrawerLayout.isDrawerOpen(mNewConversationLayout)){
            			mDrawerLayout.closeDrawer(mNewConversationLayout);
            		}
            	}
                getSupportActionBar().setTitle(mDrawerTitle);
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                mDrawerToggle.syncState();
            }
            /*
             * not animate ic_drawer on mNewConversationDrawer is opened
             */
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
               if (drawerView.equals(mDrawerRelative)) {
                  super.onDrawerSlide(drawerView, slideOffset);
               }
               else {
                  // do nothing on all other views
               }
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        /*** gestion nouveau message ***/
        final ImageButton new_send_button = (ImageButton) findViewById(R.id.new_send_button);
        new_sms_body = (EditText) findViewById(R.id.send_new_body);
        new_sms_phone = (EditText) findViewById(R.id.send_new_phone);
        new_send_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(userPref.hide_keyboard){
					InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE); 
					inputManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				}
				String body = new_sms_body.getText().toString();
				String new_phone = new_sms_phone.getText().toString();
				if(body.length() > 0){
					Message new_sms = new Message();
					new_sms.setBody(body);
					new_sms.setRight(true);
					new_sms.setRead(true);
					Time now = new Time();
					now.setToNow();
					new_sms.setDate(now);
					//add_sms("", body, "2", "1", now, 0);
					//conversationAdapter.notifyDataSetChanged();
					//lheidoConversationListe.get(i).setNb_sms(""+(Integer.parseInt(lheidoConversationListe.get(i).getNb_sms())+1));
					//lConversationsAdapter.notifyDataSetChanged();
					new_sms_body.setText(R.string.empty_sms);
					new_sms_phone.setText(R.string.empty_sms);
					int new_threadID = Integer.parseInt(getLastThreadID()) + 1;
					long new_id = store_sms(new_sms, new_threadID);
					SmsManager manager = SmsManager.getDefault();
					ArrayList<String> bodyPart = manager.divideMessage(body);
					if(bodyPart.size() > 1){
						ArrayList<PendingIntent> piSent = new ArrayList<PendingIntent>();
						ArrayList<PendingIntent> piDelivered = new ArrayList<PendingIntent>();
						for(int i = 0; i < bodyPart.size(); i++){
							Intent ideli = new Intent(ACTION_DELIVERED_SMS);
							ideli.putExtra(ARG_SMS_DELIVERED, new_id);
							piSent.add(PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SENT_SMS) , PendingIntent.FLAG_UPDATE_CURRENT));
							piDelivered.add(PendingIntent.getBroadcast(context, 0, ideli , PendingIntent.FLAG_UPDATE_CURRENT));
						}
						manager.sendMultipartTextMessage(new_phone, null, bodyPart , piSent, piDelivered);
					}
					else {
						Intent ideli = new Intent(ACTION_DELIVERED_SMS);
						ideli.putExtra(ARG_SMS_DELIVERED, new_id);
		                PendingIntent piSent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SENT_SMS) , 0);
		                PendingIntent piDelivered = PendingIntent.getBroadcast(context, 0, ideli, PendingIntent.FLAG_UPDATE_CURRENT);
						manager.sendTextMessage(new_phone, null, body, piSent, piDelivered);
					}
					Toast.makeText(context, new_phone + " - " + body, Toast.LENGTH_LONG).show();
				} else{
					Toast.makeText(context, R.string.empty_message, Toast.LENGTH_LONG).show();
				}
			}
		});
        
        
        pages = new ArrayList<SherlockFragment>();
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), pages);
    	mViewPager = (ViewPager) findViewById(R.id.content_frame);
    	mViewPager.setAdapter(mViewPagerAdapter);
    	
    	/*if (savedInstanceState == null) {
            selectItem(position_mem); // 0 par default
        }*/
        
    }
    
    public String getLastThreadID(){
    	String last = null;
    	final String[] projection = new String[] {"_id", "date", "message_count", "recipient_ids", "read", "type"};
    	Uri uri = Uri.parse("content://mms-sms/conversations?simple=true");
    	Cursor query = context.getContentResolver().query(uri, projection, null, null, "date DESC");
    	if(query != null){
    		while(query.moveToNext())
    			last = query.getString(query.getColumnIndexOrThrow("_id")).toString();
    		Log.v("LHEIDO SMS LOG", last);
    		Log.v("LHEIDO SMS LOG", "nb rows = " + query.getCount());
    		if (query != null) {
    			query.close();
    		}
    	}
    	return last;
    }
    
    @Override
    protected void onDestroy(){
    	context.getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_ENABLED , PackageManager.DONT_KILL_APP);
    	super.onDestroy();
    }
    /*
    @Override
    public void onBackPressed(){
    	boolean newConversationIsOpened = mDrawerLayout.isDrawerOpen(mNewConversationLayout);
    	boolean listConversationIsOpened = mDrawerLayout.isDrawerOpen(mDrawerRelative);
    	if(newConversationIsOpened){
    		mDrawerLayout.closeDrawer(mNewConversationLayout);
    	} else if(listConversationIsOpened){
    		mDrawerLayout.closeDrawer(mDrawerRelative);
    	} else{
    		super.onBackPressed();
    	}
    }
    */
    @Override
    protected void onResume(){
    	super.onResume();
    	try{
    		userPref.setUserPref(PreferenceManager.getDefaultSharedPreferences(this));
    	}catch(Exception ex){}
    	//updateContactList();
    	try{
    		selectItem(position_mem);
    	}catch(Exception ex){
    		selectItem(0);
    	}
    	if(userPref.first_upper)
    		new_sms_body.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
    	new_sms_body.setSingleLine(false);
    }
    
    @Override
    protected void onPause(){
    	super.onPause();
    }
    
    public LheidoContact getLConversationInfo(Cursor query){
    	LheidoContact contact = new LheidoContact();
    	contact.setConversationId(query.getString(query.getColumnIndex("_id")).toString());
    	contact.setNb_sms(query.getString(query.getColumnIndex("message_count")).toString());
        String recipientId = query.getString(query.getColumnIndex("recipient_ids")).toString();
        String[] recipientIds = recipientId.split(" ");
        for(int k=0; k < recipientIds.length; k++){
        	Uri ur = Uri.parse("content://mms-sms/canonical-addresses" );
        	if(recipientIds[k] != ""){
        		Cursor cr = context.getContentResolver().query(ur, new String[]{"*"}, "_id = " + recipientIds[k], null, null);
        		if(cr != null){
        			while(cr.moveToNext()){
        				//String id = cr.getString(0).toString();
        				String address = cr.getString(1).toString();
        				contact.setPhone(address);
        				contact.setName(context, address);
        			}
        			cr.close();
        		}
        	}
        }
    	return contact;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mNewConversationLayout);
        menu.findItem(R.id.action_new_sms).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         // The action bar home/up action should open or close the drawer.
         // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(getMenuItem(item))) {
            return true;
        }
        // Handle action buttons
        //boolean drawerOpen;
        switch(item.getItemId()) {
        case R.id.action_settings:
            Intent intent = new Intent(context, LheidoSmsPreferenceActivity.class);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
            return true;
        case R.id.action_new_sms:
        	/*drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerRelative);
        	if(drawerOpen){
        		mDrawerLayout.closeDrawer(mDrawerRelative);
        	}*/
        	mDrawerLayout.openDrawer(mNewConversationLayout);
        	return true;
        case android.R.id.home:
        	/*Toast.makeText(context, "Home open", Toast.LENGTH_LONG).show();
        	drawerOpen = mDrawerLayout.isDrawerOpen(mNewConversationLayout);
        	if(drawerOpen){
        		mDrawerLayout.closeDrawer(mNewConversationLayout);
        	}*/
        	mDrawerLayout.openDrawer(mDrawerRelative);
        	return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    private android.view.MenuItem getMenuItem(final MenuItem item) {
        return new android.view.MenuItem() {
           @Override
           public int getItemId() {
              return item.getItemId();
           }

           public boolean isEnabled() {
              return true;
           }

           @Override
           public boolean collapseActionView() {
              // TODO Auto-generated method stub
              return false;
           }

           @Override
           public boolean expandActionView() {
              // TODO Auto-generated method stub
              return false;
           }

           @Override
           public ActionProvider getActionProvider() {
              // TODO Auto-generated method stub
              return null;
           }

           @Override
           public View getActionView() {
              // TODO Auto-generated method stub
              return null;
           }

           @Override
           public char getAlphabeticShortcut() {
              // TODO Auto-generated method stub
              return 0;
           }

           @Override
           public int getGroupId() {
              // TODO Auto-generated method stub
              return 0;
           }

           @Override
           public Drawable getIcon() {
              // TODO Auto-generated method stub
              return null;
           }

           @Override
           public Intent getIntent() {
              // TODO Auto-generated method stub
              return null;
           }

           @Override
           public ContextMenuInfo getMenuInfo() {
              // TODO Auto-generated method stub
              return null;
           }

           @Override
           public char getNumericShortcut() {
              // TODO Auto-generated method stub
              return 0;
           }

           @Override
           public int getOrder() {
              // TODO Auto-generated method stub
              return 0;
           }

           @Override
           public SubMenu getSubMenu() {
              // TODO Auto-generated method stub
              return null;
           }

           @Override
           public CharSequence getTitle() {
              // TODO Auto-generated method stub
              return null;
           }

           @Override
           public CharSequence getTitleCondensed() {
              // TODO Auto-generated method stub
              return null;
           }

           @Override
           public boolean hasSubMenu() {
              // TODO Auto-generated method stub
              return false;
           }

           @Override
           public boolean isActionViewExpanded() {
              // TODO Auto-generated method stub
              return false;
           }

           @Override
           public boolean isCheckable() {
              // TODO Auto-generated method stub
              return false;
           }

           @Override
           public boolean isChecked() {
              // TODO Auto-generated method stub
              return false;
           }

           @Override
           public boolean isVisible() {
              // TODO Auto-generated method stub
              return false;
           }

           @Override
           public android.view.MenuItem setActionProvider(ActionProvider actionProvider) {
              // TODO Auto-generated method stub
              return null;
           }

           @Override
           public android.view.MenuItem setActionView(View view) {
              // TODO Auto-generated method stub
              return null;
           }

           @Override
           public android.view.MenuItem setActionView(int resId) {
              // TODO Auto-generated method stub
              return null;
           }

           @Override
           public android.view.MenuItem setAlphabeticShortcut(char alphaChar) {
              // TODO Auto-generated method stub
              return null;
           }

           @Override
           public android.view.MenuItem setCheckable(boolean checkable) {
              // TODO Auto-generated method stub
              return null;
           }

           @Override
           public android.view.MenuItem setChecked(boolean checked) {
              // TODO Auto-generated method stub
              return null;
           }

           @Override
           public android.view.MenuItem setEnabled(boolean enabled) {
              // TODO Auto-generated method stub
              return null;
           }

           @Override
           public android.view.MenuItem setIcon(Drawable icon) {
              // TODO Auto-generated method stub
              return null;
           }

           @Override
           public android.view.MenuItem setIcon(int iconRes) {
              // TODO Auto-generated method stub
              return null;
           }

           @Override
           public android.view.MenuItem setIntent(Intent intent) {
              // TODO Auto-generated method stub
              return null;
           }

           @Override
           public android.view.MenuItem setNumericShortcut(char numericChar) {
              // TODO Auto-generated method stub
              return null;
           }

           @Override
           public android.view.MenuItem setOnActionExpandListener(OnActionExpandListener listener) {
              // TODO Auto-generated method stub
              return null;
           }

           @Override
           public android.view.MenuItem setOnMenuItemClickListener(OnMenuItemClickListener menuItemClickListener) {
              // TODO Auto-generated method stub
              return null;
           }

           @Override
           public android.view.MenuItem setShortcut(char numericChar, char alphaChar) {
              // TODO Auto-generated method stub
              return null;
           }

           @Override
           public void setShowAsAction(int actionEnum) {
              // TODO Auto-generated method stub

           }

           @Override
           public android.view.MenuItem setShowAsActionFlags(int actionEnum) {
              // TODO Auto-generated method stub
              return null;
           }

           @Override
           public android.view.MenuItem setTitle(CharSequence title) {
              // TODO Auto-generated method stub
              return null;
           }

           @Override
           public android.view.MenuItem setTitle(int title) {
              // TODO Auto-generated method stub
              return null;
           }

           @Override
           public android.view.MenuItem setTitleCondensed(CharSequence title) {
              // TODO Auto-generated method stub
              return null;
           }

           @Override
           public android.view.MenuItem setVisible(boolean visible) {
              // TODO Auto-generated method stub
              return null;
           }
        };
     }
    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        	position_mem = position;
            selectItem(position);
        }
    }

    private void selectItem(int position) {
    	SherlockFragment SMSFragConversation = new SMSFrag();
    	//SherlockFragment MMSFragConversation = new MMSFrag();
        Bundle args = new Bundle();
        args.putInt(SMSFrag.ARG_CONVERSATION_NUMBER, position);
        args.putString(SMSFrag.ARG_CONTACT_NAME, lheidoConversationListe.get(position).getName());
        args.putString(SMSFrag.ARG_CONTACT_PHONE, lheidoConversationListe.get(position).getPhone());
        args.putInt(SMSFrag.ARG_CONVERSATION_ID, Integer.parseInt(lheidoConversationListe.get(position).getConversationId()));
        SMSFragConversation.setArguments(args);
        //MMSFragConversation.setArguments(args);
        pages.clear();
        pages.add(SMSFragConversation);
        //pages.add(MMSFragConversation);
        mViewPagerAdapter.notifyDataSetChanged();
        
        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(lheidoConversationListe.get(position).getName());
        mDrawerLayout.closeDrawer(mDrawerRelative);
        //registerReceiver(mBroadCast, new IntentFilter("Receive.sms.inAPP"));
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    
    public static long store_sms(Message sms, int thread_id){
	    try {
	        ContentValues values = new ContentValues();
	        values.put("address", sms.getPhone());
	        values.put("body", sms.getBody());
	        values.put("read", 0);
	        if(thread_id != -1)
	        	values.put("thread_id", thread_id);
	        values.put("date", sms.getDateNormalize());
	        Uri uri_id = context.getContentResolver().insert(Uri.parse("content://sms/sent"), values);
	        long new_id = Long.parseLong(uri_id.toString().substring(14));
	        //Log.v("LHEIDO SMS LOG", "new_id = "+new_id);
	        return new_id;
	    } catch (Exception ex) {
	    	Toast.makeText(context, ex.toString(), Toast.LENGTH_LONG).show();
	    }
	    return -1;
	}
}
