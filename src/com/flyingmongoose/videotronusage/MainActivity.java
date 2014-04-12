package com.flyingmongoose.videotronusage;

import com.flyingmongoose.videotronusage.asynctask.FetchJson;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity
{
	MainActivity instance;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		instance = this;
		
		mainOrNoInternet();
	}

	private void mainOrNoInternet()
	{
		if (!isNetworkAvailable())
		{
			noConnectivity();
		}
		else
		{
			setContentView(R.layout.activity_main);
			
			final Button btn = (Button)this.findViewById(R.id.check_usage);
			
			btn.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View arg0)
				{
					if (!isNetworkAvailable())
					{
						noConnectivity();
					}
					else
					{
						FetchJson fj = new FetchJson(instance);
						fj.execute();
						btn.setVisibility(View.GONE);
					}
				}
			});
		}
	}

	private void noConnectivity()
	{
		setContentView(R.layout.activity_no_connectivity);
		
		//TextView tv = (TextView)this.findViewById(R.id.output_message);
		Button btn = (Button)this.findViewById(R.id.try_again_btn);
		
		btn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				mainOrNoInternet();
			}
		});
	}
	
	private boolean isNetworkAvailable()
	{
	    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    
	    return (activeNetworkInfo != null && connectivityManager.getActiveNetworkInfo().isAvailable() && connectivityManager.getActiveNetworkInfo().isConnected());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
			case R.id.action_settings:
				Intent i = new Intent(this, SettingsActivity.class);
				startActivity(i);
				break;
		}
		
		return true;
	}
}