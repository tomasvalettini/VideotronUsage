package com.flyingmongoose.videotronusage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import com.flyingmongoose.videotronusage.file.FilePersistance;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity
{	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		
		fetchOrNoInternet();
	}

	private void fetchOrNoInternet()
	{
		if (!isNetworkAvailable())
		{
			noConnectivity();
		}
		else
		{
			fetchVideotronData();
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
				fetchOrNoInternet();
			}
		});
	}

	private void fetchVideotronData()
	{
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		Context con = this.getApplicationContext();
		FilePersistance fp = new FilePersistance(con);
		
		String key = fp.readFromFile();
		//String key = "FFFF696F2A2AEBC3";
		String caller = "flyingmongoose.com";
		String url = String.format("https://www.videotron.com/api/1.0/internet/usage/wired/%s.json?lang=en&caller=%s", key, caller);
		
		TextView tv = (TextView) findViewById(R.id.textView1);
		
		tv.setText(key);
		
		StringBuilder builder = new StringBuilder();
	    HttpClient client = new DefaultHttpClient();
	    HttpGet httpGet = new HttpGet(url);
	    
	    try
	    {
	    	HttpResponse response = client.execute(httpGet);
	    	StatusLine statusLine = response.getStatusLine();
	    	
	    	int statusCode = statusLine.getStatusCode();
	    	if (statusCode == 200)
	    	{
	    		HttpEntity entity = response.getEntity();
	    		InputStream content = entity.getContent();
	    		BufferedReader reader = new BufferedReader(new InputStreamReader(content));
	    		String line;
	    		
	    		while ((line = reader.readLine()) != null)
	    		{
	    			builder.append(line);
	    		}
	    	}
	    	else
	    	{
	    		Log.e(MainActivity.class.toString(), "Failed to download file");
	    	}
	    }
	    catch (ClientProtocolException e)
	    {
	    	Log.v("ClientProtocolException", e.getMessage());
	    }
	    catch (IOException e)
	    {
	    	Log.v("IOException", e.getMessage());
	    }
		
	    String s = builder.toString();
	    String msg = "";
	    
	    try
	    {
	    	JSONObject j = new JSONObject(s);
	    	Map<String, String> m = new HashMap<String, String>();
	    	
	    	m.put("package", j.getJSONArray("internetAccounts").getJSONObject(0).getString("packageName"));
	    	m.put("started", j.getString("daysFromStart"));
	    	m.put("reset", j.getString("daysToEnd"));
	    	m.put("downloaded", Float.toString(Float.parseFloat(j.getJSONArray("internetAccounts").getJSONObject(0).getString("downloadedBytes"))/1024/1024/1024));
	    	m.put("uploaded", Float.toString(Float.parseFloat(j.getJSONArray("internetAccounts").getJSONObject(0).getString("uploadedBytes"))/1024/1024/1024));
	    	m.put("percent", Float.toString(Float.parseFloat(j.getJSONArray("internetAccounts").getJSONObject(0).getString("combinedPercent"))));
	    	m.put("cap", Float.toString(Float.parseFloat(j.getJSONArray("internetAccounts").getJSONObject(0).getString("maxCombinedBytes"))/1024/1024/1024));
	        
	    	msg += String.format("Package name: %s", m.get("package")) + "\n";
	    	msg += String.format("Days until reset: %s", m.get("reset")) + "\n";
	    	msg += String.format("Days elapsed: %s", m.get("started")) + "\n";
	    	msg += String.format("Bandwidth cap: %.0f GB", Float.parseFloat(m.get("cap"))) + "\n";
	    	msg += String.format("Downloaded: %.2f GB", Float.parseFloat(m.get("downloaded"))) + "\n";
	    	msg += String.format("Uploaded: %.2f GB", Float.parseFloat(m.get("uploaded"))) + "\n";
	    	msg += "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -" + "\n";
	    	msg += String.format("Combined: %.2f GB (%s%%)", Float.parseFloat(m.get("uploaded")) + Float.parseFloat(m.get("downloaded")), m.get("percent")) + "\n";
	    }
	    catch (Exception e)
	    {
	    	Log.v("Exception", e.getMessage());
	    }
	    
		tv.setText(msg);
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