package com.flyingmongoose.videotronusage;

import com.flyingmongoose.videotronusage.file.FilePersistance;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

public class SettingsActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_first_run2);
		
		Button btn2 = (Button) findViewById(R.id.ok_btn);
		Context con = this.getApplicationContext();
		final FilePersistance fp = new FilePersistance(con);
		
		btn2.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				EditText et = (EditText) findViewById(R.id.first_run_user_key_txt);
				
				fp.writeToFile(et.getText().toString());
				
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
					
				loadActivity();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		return true;
	}
	
	private void loadActivity()
	{
		Intent i = new Intent(this, MainActivity.class);
		startActivity(i);
	}
}