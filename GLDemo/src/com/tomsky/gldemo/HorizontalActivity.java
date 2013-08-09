package com.tomsky.gldemo;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.tomsky.gldemo.view.HorizontalListView;

public class HorizontalActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.horizontal_layout);
		
		ArrayList<String> lists = new ArrayList<String>();
		lists.add("0");
		lists.add("1");
		lists.add("2");
		lists.add("3");
		lists.add("4");
		lists.add("5");
		lists.add("6");
		lists.add("7");
		lists.add("8");
		HorizontalListView hlv = (HorizontalListView) findViewById(R.id.horizontal_list);
		MyListAdapter adapter = new MyListAdapter(getLayoutInflater(), lists);
		hlv.setAdapter(adapter);
		final NotificationManager nm = ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));
		final RemoteViews RV = new RemoteViews(getPackageName(), R.layout.gl_notification);
		Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 
        		R.string.app_name, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        
		hlv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0) {
					Notification mNotification = new Notification(R.drawable.ic_launcher, "GL DEMO",
							System.currentTimeMillis());
					mNotification.contentIntent = contentIntent;
					mNotification.contentView = RV;
					mNotification.flags = mNotification.flags | Notification.FLAG_ONGOING_EVENT; 
					int mNotificationId = getPackageName().hashCode();
					nm.notify(mNotificationId, mNotification);
				}
				Toast.makeText(getApplicationContext(), "position:"+position, Toast.LENGTH_SHORT).show();
			}
		});
	}
}

class MyListAdapter extends BaseAdapter {

	private ArrayList<String> lists;
	private LayoutInflater inflater;
	
	public MyListAdapter(LayoutInflater inflater, ArrayList<String> lists) {
		this.inflater = inflater;
		this.lists = lists;
	}
	
	@Override
	public int getCount() {
		return lists.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh = null;
		if (convertView == null) {
			vh = new ViewHolder();
			convertView= inflater.inflate(R.layout.list_item, null);
			vh.iv = (ImageView) convertView;
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		ImageView iv = vh.iv;
		if (iv != null) {
			iv.setImageResource(R.drawable.aqi_ball);
		}
		return convertView;
	}
	
}

class ViewHolder {
	public ImageView iv;
}
