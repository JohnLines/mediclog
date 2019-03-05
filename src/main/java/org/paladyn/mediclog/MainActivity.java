/* 
*   Copyright (C) 2018  John Lines <john.mediclog@paladyn.org>
*
*    This program is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    This program is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with this program.  If not, see <http://www.gnu.org/licenses/>
*/



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
import java.util.TimeZone;
import java.util.zip.DataFormatException;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Button;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.net.Uri;
import android.text.TextUtils;
import android.content.DialogInterface;



public class MainActivity extends Activity {


   final int defaultSystolic = 130;
   final int defaultDiastolic = 80;
   final int defaultHeartrate = 60;
   final int defaultTemperature = 363;
   final int defaultWeight = 750;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mediclog_layout);

	SharedPreferences sharedPref = getSharedPreferences("org.paladyn.mediclog_preferences",MODE_PRIVATE);


        if ( sharedPref.getBoolean("displayPrivacy",true) | sharedPref.getBoolean("displayPrivacyKeep",true) ) {
    		Intent i = new Intent(this,DisplayPrivacy.class);
		startActivity(i);
               }


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
		        case R.id.menu_item_about:
			       Intent j = new Intent(this,About.class);
			       startActivity(j);
			       return true;
                         case R.id.menu_item_history:
                                Intent k = new Intent(this,History.class);
                                startActivity(k);
                                return true;
	               default:
	                 return super.onOptionsItemSelected(item);
	               }
	     }

	private void createLog(File file) {
		try {
                FileOutputStream os = new FileOutputStream(file);
		BufferedWriter fbw = new BufferedWriter(new OutputStreamWriter(os));
		fbw.write("MedicLog 1.0,Date time,Systolic,Diastolic,Heart rate,Temperature,Weight,Comment");
		fbw.newLine();
		fbw.flush();
		fbw.close();
                Button deleteBtn=(Button)findViewById(R.id.btnDelete);
                deleteBtn.setVisibility(View.VISIBLE);
                Button sendBtn=(Button)findViewById(R.id.btnSend);
                sendBtn.setVisibility(View.VISIBLE);
                        if (BuildConfig.DEBUG) {
	                   Log.d("mediclog","Log file created "+file.getName());
	                   }
		} catch (IOException ioe) {
		       ioe.printStackTrace();
	        }	       
        }


	 private void readLog() throws DataFormatException {

//   Read in existing log entries until end of file
        FileInputStream is;
	BufferedReader reader;

	SharedPreferences sharedPref = getSharedPreferences("org.paladyn.mediclog_preferences",MODE_PRIVATE); 

	File file = new File(getFilesDir(), sharedPref.getString("fileName","mediclog.txt") );

	if (file.exists()) {
		try {
                Button deleteBtn=(Button)findViewById(R.id.btnDelete);
                deleteBtn.setVisibility(View.VISIBLE);
                Button sendBtn=(Button)findViewById(R.id.btnSend);
                sendBtn.setVisibility(View.VISIBLE);

		is = new FileInputStream(file);
		reader = new BufferedReader(new InputStreamReader(is));
		String recordFormat;
		String lastline = null;
		// First line should be the header
		String header = reader.readLine();
		String[] hvals = header.split(",");
                if ( ! hvals[0].equals("MedicLog 1.0")) {
//			throw new DataFormatException("MedicLog -Unknown format type *"+hvals[0]+"*");
			Log.w("mediclog","Unknown format type "+hvals[0]);
		}
		String line = reader.readLine();
		while (line != null) {
			lastline = line;

                        MedicLog.getInstance(getApplicationContext()).incNumRecsReadFromFile();
                        if (BuildConfig.DEBUG) {
	                   Log.d("mediclog","Read line "+line);
	                   }
                           /* save the line in the history buffer */
                           MedicLog.getInstance(getApplicationContext()).putHistoryBuffer(line);
                           /* increment the read index */
                           MedicLog.getInstance(getApplicationContext()).incHistBuffReadIndex();
                         
			line = reader.readLine();
	        }
		if (lastline != null) {
		String[] values = lastline.split(",");
		recordFormat = values[0];
		// recordFormat should always be 1 at the moment
		if ( ! recordFormat.equals("1")) {

			throw new DataFormatException("MedicLog Unknown record format *"+recordFormat+"*");
//			Log.d("mediclog","Unknown record format *"+recordFormat+"*");
		}
	        EditText systolicText = (EditText) findViewById(R.id.systolicText);
		if (values.length >2 && values[2] != null) {systolicText.setText(values[2]);}
	        EditText diastolicText = (EditText) findViewById(R.id.diastolicText);
		if (values.length >3 && values[3] != null) {diastolicText.setText(values[3]);}
	 	EditText heartrateText = (EditText) findViewById(R.id.heartrateText);
		if (values.length > 4 && values[4] != null) {heartrateText.setText(values[4]);}
	 	EditText tempText = (EditText) findViewById(R.id.tempText);
		if (values.length > 5 && values[5] != null ) {tempText.setText(values[5]);}
	 	EditText weightText = (EditText) findViewById(R.id.weightText);
		if (values.length > 6 && values[6] != null) {weightText.setText(values[6]);}
		}
	   } catch(IOException ioe) {
		   ioe.printStackTrace();
	       }
	   } else {
		Toast.makeText(getBaseContext(), "File does not exist",
				Toast.LENGTH_SHORT).show();
                        if (BuildConfig.DEBUG) {
	                   Log.d("mediclog","ReadLog -about to create file "+file.getName());
	                   }
		createLog(file);
	        }
	       
            } 


    @Override
    public void onStart() {
        super.onStart();

	SharedPreferences sharedPref = getSharedPreferences("org.paladyn.mediclog_preferences",MODE_PRIVATE);

       if ( MedicLog.getInstance(getApplicationContext()).getNumRecsReadFromFile()==0 ) {
               if (BuildConfig.DEBUG) { Log.d("mediclog","Log has not been read yet - reading it now"); }
	try {
	  readLog();
	}  catch (DataFormatException dfe) {
		dfe.printStackTrace();
	}
        }
	
        TextView textView = (TextView) findViewById(R.id.text_view);
        textView.setText("Medic Log - logs medical information");
        Calendar calendar = Calendar.getInstance();
        
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	   if ( sharedPref.getBoolean("timeUTC",true)) {
		   mdformat.setTimeZone(TimeZone.getTimeZone("GMT"));
	   } else {
                   mdformat.setTimeZone(TimeZone.getDefault());
           }
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

    public void onClickBpClear(View view) {
	 EditText systolicText = (EditText) findViewById(R.id.systolicText);
	 systolicText.setText("");
	 EditText diastolicText = (EditText) findViewById(R.id.diastolicText);
	 diastolicText.setText("");
	 EditText heartrateText = (EditText) findViewById(R.id.heartrateText);
	 heartrateText.setText("");

    }

    public void onClickSystolicMinus(View view) {
	 EditText systolicText = (EditText) findViewById(R.id.systolicText);
	 String systolicStr = systolicText.getText().toString();

         int systolic = TextUtils.isEmpty(systolicStr) ? defaultSystolic : Integer.parseInt(systolicStr);
	 systolic = systolic - 1;
	 systolicStr = String.format("%d",systolic);
	 systolicText.setText(systolicStr);

    }

    public void onClickSystolicPlus(View view) {
	 EditText systolicText = (EditText) findViewById(R.id.systolicText);
	 String systolicStr = systolicText.getText().toString();
         int systolic = TextUtils.isEmpty(systolicStr) ? defaultSystolic : Integer.parseInt(systolicStr);
	 systolic = systolic + 1;
	 systolicStr = String.format("%d",systolic);
	 systolicText.setText(systolicStr);
      }

    public void onClickDiastolicMinus(View view) {
	 EditText diastolicText = (EditText) findViewById(R.id.diastolicText);
	 String diastolicStr = diastolicText.getText().toString();
         int diastolic = TextUtils.isEmpty(diastolicStr) ? defaultDiastolic :  Integer.parseInt(diastolicStr);
	 diastolic = diastolic - 1;
	 diastolicStr = String.format("%d",diastolic);
	 diastolicText.setText(diastolicStr);

    }

    public void onClickDiastolicPlus(View view) {
	 EditText diastolicText = (EditText) findViewById(R.id.diastolicText);
	 String diastolicStr = diastolicText.getText().toString();
         int diastolic = TextUtils.isEmpty(diastolicStr) ? defaultDiastolic : Integer.parseInt(diastolicStr);
	 diastolic = diastolic + 1;
	 diastolicStr = String.format("%d",diastolic);
	 diastolicText.setText(diastolicStr);
      }


    public void onClickHrateMinus(View view) {
	 EditText heartrateText = (EditText) findViewById(R.id.heartrateText);
	 String heartrateStr = heartrateText.getText().toString();
         int heartrate = TextUtils.isEmpty(heartrateStr) ? defaultHeartrate : Integer.parseInt(heartrateStr);
	 heartrate = heartrate - 1;
	 heartrateStr = String.format("%d",heartrate);
	 heartrateText.setText(heartrateStr);

    }

    public void onClickHratePlus(View view) {
	 EditText heartrateText = (EditText) findViewById(R.id.heartrateText);
	 String heartrateStr = heartrateText.getText().toString();
         int heartrate = TextUtils.isEmpty(heartrateStr) ? defaultHeartrate : Integer.parseInt(heartrateStr);
	 heartrate = heartrate + 1;
	 heartrateStr = String.format("%d",heartrate);
	 heartrateText.setText(heartrateStr);
      }
	 

    public void onClickTempClear(View view) {
	 EditText tempText = (EditText) findViewById(R.id.tempText);
	 tempText.setText("");
    }


    public void onClickTempMinus(View view) {
	 EditText tempText = (EditText) findViewById(R.id.tempText);
	 String tempStr = tempText.getText().toString();
	 String tempStr2 = tempStr.replaceAll("\\.","");
         int temp = TextUtils.isEmpty(tempStr2) ? defaultTemperature : Integer.parseInt(tempStr2);
	 temp = temp - 1;
	 tempStr = String.format("%d",temp);
	 tempStr2 = new StringBuilder(tempStr).insert(tempStr.length()-1,".").toString();
	 tempText.setText(tempStr2);


    }

    public void onClickTempPlus(View view) {
	 EditText tempText = (EditText) findViewById(R.id.tempText);
	 String tempStr = tempText.getText().toString();
	 String tempStr2 = tempStr.replaceAll("\\.","");
         int temp = TextUtils.isEmpty(tempStr2) ? defaultTemperature : Integer.parseInt(tempStr2);
	 temp = temp + 1;
	 tempStr = String.format("%d",temp);
	 tempStr2 = new StringBuilder(tempStr).insert(tempStr.length()-1,".").toString();
	 tempText.setText(tempStr2);
    }

    public void onClickWeightClear(View view) {
	 EditText weightText = (EditText) findViewById(R.id.weightText);
	 weightText.setText("");
    }

    public void onClickWeightMinus(View view) {
	 EditText weightText = (EditText) findViewById(R.id.weightText);
	 String weightStr = weightText.getText().toString();
	 String weightStr2 = weightStr.replaceAll("\\.","");
         int weight = TextUtils.isEmpty(weightStr2) ? defaultWeight : Integer.parseInt(weightStr2);
	 weight = weight - 1;
	 weightStr = String.format("%d",weight);
	 weightStr2 = new StringBuilder(weightStr).insert(weightStr.length()-1,".").toString();
	 weightText.setText(weightStr2);

    }

    public void onClickWeightPlus(View view) {
	 EditText weightText = (EditText) findViewById(R.id.weightText);
	 String weightStr = weightText.getText().toString();
	 String weightStr2 = weightStr.replaceAll("\\.","");
         int weight = TextUtils.isEmpty(weightStr2) ? defaultWeight : Integer.parseInt(weightStr2);
	 weight = weight + 1;
	 weightStr = String.format("%d",weight);
	 weightStr2 = new StringBuilder(weightStr).insert(weightStr.length()-1,".").toString();
	 weightText.setText(weightStr2);


    }

    public void onClickCommentClear(View view) {
	 EditText commentText = (EditText) findViewById(R.id.commentText);
	 commentText.setText("");
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
	SharedPreferences sharedPref = getSharedPreferences("org.paladyn.mediclog_preferences",MODE_PRIVATE); 
		 File file = new File(getFilesDir(),sharedPref.getString("fileName","mediclog.txt"));
                if ( ! file.exists()) {
                        if (BuildConfig.DEBUG) {
	                   Log.d("mediclog","Save -about to create file "+file.getName());
	                   }
	            	createLog(file);
		}
		 FileOutputStream fOut = new FileOutputStream(file,true);
		OutputStreamWriter osw = new
			OutputStreamWriter(fOut);
		BufferedWriter fbw = new BufferedWriter(osw);

		fbw.write("1,"+strDate+","+systolicStr+","+diastolicStr+","+heartrateStr+","+tempStr+","+weightStr+","+commentStr);
		fbw.newLine();
		fbw.flush();
		fbw.close();
// Also append it to the buffer
                MedicLog.getInstance(getApplicationContext()).putHistoryBuffer("1,"+strDate+","+systolicStr+","+diastolicStr+","+heartrateStr+","+tempStr+","+weightStr+","+commentStr);

                MedicLog.getInstance(getApplicationContext()).incNumRecsAppendedToFile();
                MedicLog.getInstance(getApplicationContext()).incHistBuffReadIndex();

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
	   emailIntent.setType("text/csv");
	   emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, sharedPref.getString("sendTo","").split(","));
	   emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, sharedPref.getString("sendSubject","MedicLog"));

//  put something to pick up the attachment
//
           emailIntent.putExtra(
               android.content.Intent.EXTRA_STREAM,Uri.parse("content://"+LocalFileProvider.AUTHORITY+"/"
		       + sharedPref.getString("fileName","mediclog.txt")));


	   this.startActivity(Intent.createChooser(emailIntent,"Sending email..."));


	}

     public void onClickDelete(View view) {

            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Delete file");
            alert.setMessage("Are you sure you want to delete?");
            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences sharedPref = getSharedPreferences("org.paladyn.mediclog_preferences",MODE_PRIVATE);
                    File dir = getFilesDir();
                    File file = new File(dir, sharedPref.getString("fileName","mediclog.txt"));
                    Button deleteBtn=(Button)findViewById(R.id.btnDelete);
                    deleteBtn.setVisibility(View.GONE);
                    Button sendBtn=(Button)findViewById(R.id.btnSend);
                    sendBtn.setVisibility(View.GONE);
                    boolean deleted = file.delete();
                    MedicLog.getInstance(getApplicationContext()).clearNumRecsReadFromFile();
                    MedicLog.getInstance(getApplicationContext()).clearNumRecsAppendedToFile();
                    MedicLog.getInstance(getApplicationContext()).clearHistBuffIndex();
                    MedicLog.getInstance(getApplicationContext()).resetHistBuffReadIndex();

                    if (BuildConfig.DEBUG) {
                        Log.d("mediclog","Log file deleted");
                    }
                    dialog.dismiss();
                }
            });
         alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
             public void onClick(DialogInterface dialog, int which) {
                 // close dialog
                 dialog.cancel();
             }
         });
         alert.show();

     }

	   }



