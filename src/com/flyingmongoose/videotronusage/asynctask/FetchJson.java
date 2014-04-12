package com.flyingmongoose.videotronusage.asynctask;

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

import com.flyingmongoose.videotronusage.MainActivity;
import com.flyingmongoose.videotronusage.R;
import com.flyingmongoose.videotronusage.file.FilePersistance;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;
import android.widget.TextView;

public class FetchJson extends AsyncTask<Void, String, Void> 
{
	Activity root;
	
	public FetchJson(Activity parent)
	{
		root = parent;
	}
	
	@Override
	protected void onProgressUpdate(String... values)
	{
		TextView tv = (TextView) root.findViewById(R.id.textView1);
		tv.setText(values[0]);
    }
	
    @Override
    protected Void doInBackground(Void... arg0)
    {
    	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		Context con = root.getApplicationContext();
		FilePersistance fp = new FilePersistance(con);
		
		String key = fp.readFromFile();
		//String key = "FFFF696F2A2AEBC3";
		String caller = "flyingmongoose.com";
		String url = String.format("https://www.videotron.com/api/1.0/internet/usage/wired/%s.json?lang=en&caller=%s", key, caller);
		
		StringBuilder builder = new StringBuilder();
	    HttpClient client = new DefaultHttpClient();
	    HttpGet httpGet = new HttpGet(url);
	    
	    publishProgress("Fetching Videotron data for user-key: " + key);
	    
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
	    	msg += "\n";
	    	msg += String.format("Combined: %.2f GB (%s%%)", Float.parseFloat(m.get("uploaded")) + Float.parseFloat(m.get("downloaded")), m.get("percent")) + "\n";
	    }
	    catch (Exception e)
	    {
	    	Log.v("Exception", e.getMessage());
	    }
	    
	    publishProgress(msg);
	    return null;
    }
}