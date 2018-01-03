package org.paladyn.mediclog;

import java.lang.StringBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.net.Uri;


public class MedicLog extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mediclog_layout);
    }

    @Override
        public boolean onCreateOptionsMenu(Menu menu) {
		        // Inflate the menu; this adds items to the action bar if it is present.
			         getMenuInflater().inflate(R.menu.menu_main, menu);
			                 return true;
			                     }


     /**
      *      * react to the user tapping/selecting an options menu item
      *           */
        @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	            switch (item.getItemId()) {
		                case R.id.menu_item_preferences:
		                //Toast.makeText(this, "ADD!", Toast.LENGTH_SHORT).show();
	                Intent i = new Intent(this, MyPreferencesActivity.class);
	                startActivity(i);
	               return true;
	               default:
	                 return super.onOptionsItemSelected(item);
	               }
	     }



    @Override
    public void onStart() {
        super.onStart();

//   Read in existing log entries until end of file
        FileInputStream is;
	BufferedReader reader;
//	final File file = new File("mediclog.txt",MODE_PRIVATE);
	final File file = new File(getFilesDir(), "mediclog.txt");

	if (file.exists()) {
		try {
		is = new FileInputStream(file);
		reader = new BufferedReader(new InputStreamReader(is));
		String line = reader.readLine();
		String lastline = null;
		while (line != null) {
			lastline = line;
			line = reader.readLine();
	        }
		if (lastline != null) {
		String[] values = lastline.split(",");
	        EditText systolicText = (EditText) findViewById(R.id.systolicText);
		systolicText.setText(values[1]);
	        EditText diastolicText = (EditText) findViewById(R.id.diastolicText);
		diastolicText.setText(values[2]);
	 	EditText heartrateText = (EditText) findViewById(R.id.heartrateText);
		heartrateText.setText(values[3]);
	 	EditText tempText = (EditText) findViewById(R.id.tempText);
		tempText.setText(values[4]);
	 	EditText weightText = (EditText) findViewById(R.id.weightText);
		weightText.setText(values[5]);
		}
	   } catch(IOException ioe) {
		   ioe.printStackTrace();
	       }
	   } else {
		Toast.makeText(getBaseContext(), "File does not exist",
				Toast.LENGTH_SHORT).show();
		try {
                FileOutputStream os = new FileOutputStream(file);
//                OutputStreamWriter osw = new openFileOutput("mediclog.txt",MODE_PRIVATE);
		BufferedWriter fbw = new BufferedWriter(new OutputStreamWriter(os));
		fbw.close();
		} catch (IOException ioe) {
		       ioe.printStackTrace();
	        }	       
            }
	
        TextView textView = (TextView) findViewById(R.id.text_view);
        textView.setText("Medic Log - logs medical information");
        Calendar calendar = Calendar.getInstance();
// Note, time is in UTC at the moment. There should be a preferences item to say
// if it should be in local time.	
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String strDate = mdformat.format(calendar.getTime());
	TextView dateView = (TextView) findViewById(R.id.date_view);
	dateView.setText(strDate);
        TextView bpView = (TextView) findViewById(R.id.bp_view);
        bpView.setText("Blood Pressure");
        TextView tempView = (TextView) findViewById(R.id.temp_view);
        tempView.setText("Temperature");
        TextView weightView = (TextView) findViewById(R.id.weight_view);
        weightView.setText("Weight");


    }

    public void onClickSystolicMinus(View view) {
	 EditText systolicText = (EditText) findViewById(R.id.systolicText);
	 String systolicStr = systolicText.getText().toString();
         int systolic = Integer.parseInt(systolicStr);
	 systolic = systolic - 1;
	 systolicStr = String.format("%d",systolic);
	 systolicText.setText(systolicStr);

    }

    public void onClickSystolicPlus(View view) {
	 EditText systolicText = (EditText) findViewById(R.id.systolicText);
	 String systolicStr = systolicText.getText().toString();
         int systolic = Integer.parseInt(systolicStr);
	 systolic = systolic + 1;
	 systolicStr = String.format("%d",systolic);
	 systolicText.setText(systolicStr);
      }

    public void onClickDiastolicMinus(View view) {
	 EditText diastolicText = (EditText) findViewById(R.id.diastolicText);
	 String diastolicStr = diastolicText.getText().toString();
         int diastolic = Integer.parseInt(diastolicStr);
	 diastolic = diastolic - 1;
	 diastolicStr = String.format("%d",diastolic);
	 diastolicText.setText(diastolicStr);

    }

    public void onClickDiastolicPlus(View view) {
	 EditText diastolicText = (EditText) findViewById(R.id.diastolicText);
	 String diastolicStr = diastolicText.getText().toString();
         int diastolic = Integer.parseInt(diastolicStr);
	 diastolic = diastolic + 1;
	 diastolicStr = String.format("%d",diastolic);
	 diastolicText.setText(diastolicStr);
      }


    public void onClickHrateMinus(View view) {
	 EditText heartrateText = (EditText) findViewById(R.id.heartrateText);
	 String heartrateStr = heartrateText.getText().toString();
         int heartrate = Integer.parseInt(heartrateStr);
	 heartrate = heartrate - 1;
	 heartrateStr = String.format("%d",heartrate);
	 heartrateText.setText(heartrateStr);

    }

    public void onClickHratePlus(View view) {
	 EditText heartrateText = (EditText) findViewById(R.id.heartrateText);
	 String heartrateStr = heartrateText.getText().toString();
         int heartrate = Integer.parseInt(heartrateStr);
	 heartrate = heartrate + 1;
	 heartrateStr = String.format("%d",heartrate);
	 heartrateText.setText(heartrateStr);
      }
	 



    public void onClickTempMinus(View view) {
	 EditText tempText = (EditText) findViewById(R.id.tempText);
	 String tempStr = tempText.getText().toString();
	 String tempStr2 = tempStr.replaceAll("\\.","");
         int temp = Integer.parseInt(tempStr2);
	 temp = temp - 1;
	 tempStr = String.format("%d",temp);
	 tempStr2 = new StringBuilder(tempStr).insert(tempStr.length()-1,".").toString();
	 tempText.setText(tempStr2);


    }

    public void onClickTempPlus(View view) {
	 EditText tempText = (EditText) findViewById(R.id.tempText);
	 String tempStr = tempText.getText().toString();
	 String tempStr2 = tempStr.replaceAll("\\.","");
         int temp = Integer.parseInt(tempStr2);
	 temp = temp + 1;
	 tempStr = String.format("%d",temp);
	 tempStr2 = new StringBuilder(tempStr).insert(tempStr.length()-1,".").toString();
	 tempText.setText(tempStr2);


    }

    public void onClickWeightMinus(View view) {
	 EditText weightText = (EditText) findViewById(R.id.weightText);
	 String weightStr = weightText.getText().toString();
	 String weightStr2 = weightStr.replaceAll("\\.","");
         int weight = Integer.parseInt(weightStr2);
	 weight = weight - 1;
	 weightStr = String.format("%d",weight);
	 weightStr2 = new StringBuilder(weightStr).insert(weightStr.length()-1,".").toString();
	 weightText.setText(weightStr2);

    }

    public void onClickWeightPlus(View view) {
	 EditText weightText = (EditText) findViewById(R.id.weightText);
	 String weightStr = weightText.getText().toString();
	 String weightStr2 = weightStr.replaceAll("\\.","");
         int weight = Integer.parseInt(weightStr2);
	 weight = weight + 1;
	 weightStr = String.format("%d",weight);
	 weightStr2 = new StringBuilder(weightStr).insert(weightStr.length()-1,".").toString();
	 weightText.setText(weightStr2);


    }

    public void onClickSave(View view) {
	 TextView dateText = (TextView) findViewById(R.id.date_view);
	 String strDate = dateText.getText().toString();
	 EditText systolicText = (EditText) findViewById(R.id.systolicText);
	 String systolicStr = systolicText.getText().toString();
	 EditText diastolicText = (EditText) findViewById(R.id.diastolicText);
	 String diastolicStr = diastolicText.getText().toString();
	 EditText heartrateText = (EditText) findViewById(R.id.heartrateText);
	 String heartrateStr = heartrateText.getText().toString();
	 EditText tempText = (EditText) findViewById(R.id.tempText);
	 String tempStr = tempText.getText().toString();
	 EditText weightText = (EditText) findViewById(R.id.weightText);
	 String weightStr = weightText.getText().toString();
	 EditText commentText = (EditText) findViewById(R.id.commentText);
	 String commentStr = commentText.getText().toString();

	 try {
		 FileOutputStream fOut =
			 openFileOutput("mediclog.txt",MODE_PRIVATE|MODE_APPEND);
//			 openFileOutput("mediclog.txt",MODE_PRIVATE);
		OutputStreamWriter osw = new
			OutputStreamWriter(fOut);
//                OutputStreamWriter osw = utStreamWriter(
//			  openFileOutput("mediclog.txt",MODE_PRIVATE|MODE_APPEND));
		BufferedWriter fbw = new BufferedWriter(osw);

		fbw.write(strDate+","+systolicStr+","+diastolicStr+","+heartrateStr+","+tempStr+","+weightStr+","+commentStr);
		fbw.newLine();
		fbw.flush();
		fbw.close();

		Toast.makeText(getBaseContext(), "File saved successfully!",
				Toast.LENGTH_SHORT).show();
		}	
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
	}


     public void onClickSend(View view) {
	   SharedPreferences sharedPref = getSharedPreferences("org.paladyn.mediclog_preferences",MODE_PRIVATE); 

           final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
	   emailIntent.setType("plain/text");
//	   emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, "john@paladyn.org");
	   emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, sharedPref.getString("sendTo",""));
//	   emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "MedicLog");
	   emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, sharedPref.getString("sendSubject","MedicLog"));

//  put something to pick up the attachment
//
           emailIntent.putExtra(
               android.content.Intent.EXTRA_STREAM,Uri.parse("content://"+LocalFileProvider.AUTHORITY+"/"
		       + "mediclog.txt"));


	   this.startActivity(Intent.createChooser(emailIntent,"Sending email..."));


	}

}




