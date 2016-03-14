package uiuc.mbr;

import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import uiuc.mbr.serv.AlarmService;

public class OnAlarmActivity extends AppCompatActivity
{
	private TextView currentAlarmName;
	private View alarmsEmpty, noCurrent, yesCurrent;

	private List<Alarm> alarms;
	private final Adapter adapter = new Adapter();
	private MediaPlayer soundPlayer = null;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_on_alarm);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

		currentAlarmName = (TextView)findViewById(R.id.a_onalarm_current_name);
		alarmsEmpty = findViewById(R.id.a_onalarm_empty);
		noCurrent = findViewById(R.id.a_onalarm_nocurrent);
		yesCurrent = findViewById(R.id.a_onalarm_yescurrent);
		ListView listView = (ListView)findViewById(R.id.a_onalarm_list);
		listView.setAdapter(adapter);
	}


	private void refresh()
	{
		alarms = AlarmService.getUntriggeredAlarms();
		Collections.sort(alarms);
		adapter.notifyDataSetChanged();

		String text;
		Alarm triggered = AlarmService.getTriggeredAlarm();
		if(triggered == null)
			text = null;
		else
		{
			Calendar now = Calendar.getInstance();
			if(triggered.when.compareTo(now) <= 0)
				text = triggered.name;
			else
				text = null;
		}

		if(text == null)
		{
			yesCurrent.setVisibility(View.GONE);
			noCurrent.setVisibility(View.VISIBLE);
		}
		else
		{
			yesCurrent.setVisibility(View.VISIBLE);
			noCurrent.setVisibility(View.GONE);
			currentAlarmName.setText(text);

			Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
			if(uri == null)
				uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			soundPlayer = MediaPlayer.create(getApplicationContext(), uri);
			soundPlayer.setLooping(true);
			soundPlayer.start();
		}
	}


	@Override
	protected void onStart()
	{
		super.onStart();
		refresh();
	}


	@Override
	protected void onStop()
	{
		stopSoundIfPlaying();
		super.onStop();
	}


	private void stopSoundIfPlaying()
	{
		if(soundPlayer != null)
		{
			soundPlayer.stop();
			soundPlayer.release();
			soundPlayer = null;
		}
	}


	public void clickAlarmOffBtn(View v)
	{
		stopSoundIfPlaying();
		AlarmService.clearTriggeredAlarm(getApplicationContext());
		refresh();
	}



	private class Adapter extends BaseAdapter
	{
		@Override
		public int getCount(){return alarms == null ? 0 : alarms.size();}

		@Override
		public Alarm getItem(int i){return alarms.get(i);}

		@Override
		public long getItemId(int i){return getItem(i).when.hashCode();}

		@Override
		public View getView(int i, View view, ViewGroup parent)
		{
			View v = view != null ? view : LayoutInflater.from(getApplicationContext()).inflate(R.layout.sub_onalarm_alarm, parent, false);
			TextView name = (TextView)v.findViewById(R.id.sub_onalarm_name);
			TextView time = (TextView)v.findViewById(R.id.sub_onalarm_when);
			Alarm alarm = getItem(i);

			name.setText(alarm.name);
			time.setText(alarm.when.getTime().toString());
			return v;
		}


		@Override
		public void notifyDataSetChanged()
		{
			updateEmpty();
			super.notifyDataSetChanged();
		}


		/**Updates the display of whether the list of alarms is empty.*/
		private void updateEmpty(){alarmsEmpty.setVisibility(alarms.isEmpty() ? View.VISIBLE : View.INVISIBLE);}
	}
}
