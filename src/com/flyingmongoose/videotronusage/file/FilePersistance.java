package com.flyingmongoose.videotronusage.file;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.Context;
import android.widget.Toast;

public class FilePersistance
{
	String FILENAME = "user_key_persistance";
	Context con;
	
	public FilePersistance(Context _con) 
	{
		con = _con;
	}

	public void writeToFile(String user_key)
	{
		FileOutputStream fos;
		
		try
		{
			fos = con.openFileOutput(FILENAME, Context.MODE_PRIVATE);
			fos.write(user_key.getBytes());
			fos.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		Toast.makeText(con, "User-key saved!", Toast.LENGTH_SHORT).show();
	}
	
	public String readFromFile()
	{
		FileInputStream fis;
		
		try
		{
			fis = con.openFileInput(FILENAME);
			InputStreamReader inputStreamReader = new InputStreamReader(fis);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String line;
			
			line = bufferedReader.readLine();
			
			fis.close();
			return line;
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return "";
	}
}