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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.zip.DataFormatException;

public class MainActivity extends Activity {

    final int defaultSystolic = 130;
    final int defaultDiastolic = 80;
    final int defaultHeartrate = 60;
    final int defaultTemperature = 363;
    final int defaultWeight = 750;
    final int defaultO2 = 98;
    String fileFormatVersion = "1.1";
    String fileFormatOldVersion = "1.0";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mediclog_layout);

        SharedPreferences sharedPref = getSharedPreferences("org.paladyn.mediclog_preferences", MODE_PRIVATE);

        if (sharedPref.getBoolean("displayPrivacy", true) | sharedPref.getBoolean("displayPrivacyKeep", true)) {
            Intent i = new Intent(this, DisplayPrivacy.class);
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
     * * react to the user tapping/selecting an options menu item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_preferences:
                //Toast.makeText(this, "ADD!", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(this, MyPreferencesActivity.class);
                startActivity(i);
                return true;
            case R.id.menu_item_about:
                Intent j = new Intent(this, About.class);
                startActivity(j);
                return true;
            case R.id.menu_item_history:
                Intent k = new Intent(this, History.class);
                startActivity(k);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void makeSendDeleteVisible() {
        Button deleteBtn = (Button) findViewById(R.id.btnDelete);
        deleteBtn.setVisibility(View.VISIBLE);
        Button sendBtn = (Button) findViewById(R.id.btnSend);
        sendBtn.setVisibility(View.VISIBLE);
    }

    private void createLog(File file) {
        try {
            FileOutputStream os = new FileOutputStream(file);
            BufferedWriter fbw = new BufferedWriter(new OutputStreamWriter(os));
            fbw.write("MedicLog " + fileFormatVersion + ",Date time,Systolic,Diastolic,Heart rate,Temperature,Weight,Comment");
            fbw.newLine();
            fbw.flush();
            fbw.close();
            makeSendDeleteVisible();
 /*
            Button deleteBtn = (Button) findViewById(R.id.btnDelete);

            deleteBtn.setVisibility(View.VISIBLE);
            Button sendBtn = (Button) findViewById(R.id.btnSend);
            sendBtn.setVisibility(View.VISIBLE);

  */
            if (BuildConfig.DEBUG) {
                Log.d("mediclog", "Log file created " + file.getName());
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void readLog() throws DataFormatException {
        // Read in existing log entries until end of file
        FileInputStream is;
        BufferedReader reader;

        SharedPreferences sharedPref = getSharedPreferences("org.paladyn.mediclog_preferences", MODE_PRIVATE);

        File file = new File(getFilesDir(), sharedPref.getString("fileName", "mediclog.txt"));

        if (file.exists()) {
            try {
                Button deleteBtn = (Button) findViewById(R.id.btnDelete);
                deleteBtn.setVisibility(View.VISIBLE);
                Button sendBtn = (Button) findViewById(R.id.btnSend);
                sendBtn.setVisibility(View.VISIBLE);

                is = new FileInputStream(file);
                reader = new BufferedReader(new InputStreamReader(is));
                String recordFormat;
                String lastline = null;
                // First line should be the header
                String header = reader.readLine();
                String[] hvals = header.split(",");
                if (!(hvals[0].equals("MedicLog "+ fileFormatOldVersion) ||
                        hvals[0].equals("MedicLog " + fileFormatVersion) ) ) {
                    //throw new DataFormatException("MedicLog -Unknown format type *" + hvals[0] + "*");
                    Log.w("mediclog", "Unknown format type " + hvals[0]);
                }
                String line = reader.readLine();
                while (line != null) {
                    lastline = line;

                    MedicLog.getInstance(getApplicationContext()).incNumRecsReadFromFile();
                    if (BuildConfig.DEBUG) {
                        Log.d("mediclog", "Read line " + line);
                    }
                    // save the line in the history buffer
                    MedicLog.getInstance(getApplicationContext()).putHistoryBuffer(line);
                    // increment the read index
                    MedicLog.getInstance(getApplicationContext()).incHistBuffReadIndex();

                    line = reader.readLine();
                }

                if (lastline != null) {
                    String[] values = lastline.split(",");
                    recordFormat = values[0];
                    // recordFormat should always be 1 at the moment
                    if (!recordFormat.equals("1")) {
                        throw new DataFormatException("MedicLog Unknown record format *" + recordFormat + "*");
                        //Log.d("mediclog", "Unknown record format *" + recordFormat + "*");
                    }
                    EditText systolicText = (EditText) findViewById(R.id.systolicText);
                    if (values.length > 2 && values[2] != null) {
                        systolicText.setText(values[2]);
                    }
                    EditText diastolicText = (EditText) findViewById(R.id.diastolicText);
                    if (values.length > 3 && values[3] != null) {
                        diastolicText.setText(values[3]);
                    }
                    EditText heartrateText = (EditText) findViewById(R.id.heartrateText);
                    if (values.length > 4 && values[4] != null) {
                        heartrateText.setText(values[4]);
                    }
                    EditText tempText = (EditText) findViewById(R.id.tempText);
                    if (values.length > 5 && values[5] != null) {
                        tempText.setText(values[5]);
                    }
                    EditText weightText = (EditText) findViewById(R.id.weightText);
                    if (values.length > 6 && values[6] != null) {
                        weightText.setText(values[6]);
                    }
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        } else {
            Toast.makeText(getBaseContext(), R.string.file_doesnt_exist,
                    Toast.LENGTH_SHORT).show();
            if (BuildConfig.DEBUG) {
                Log.d("mediclog", "ReadLog -about to create file " + file.getName());
            }
            createLog(file);
        }
    }

    private void setSaveNeeded () {
        MedicLog.getInstance(getApplicationContext()).saveNeeded();
        Button saveBtn = (Button) findViewById(R.id.btnSave);
        if ( !MedicLog.getInstance(getApplicationContext()).isSaveTextOriginalColourKnown() ) {
            MedicLog.getInstance(getApplicationContext()).setSaveTextOrignalColour(saveBtn.getCurrentTextColor());
            MedicLog.getInstance(getApplicationContext()).setSaveTextOriginalColourKnown();
        }
        saveBtn.setTextColor(Color.RED);

    }

    private void unsetSaveNeeded () {
        MedicLog.getInstance(getApplicationContext()).saveUnneeded();
        Button saveBtn = (Button) findViewById(R.id.btnSave);
        // I want primary_text_light- deprecated in API level 28, but it is 0x01060003
       // saveBtn.setTextColor(Color.parseColor("#01060003"));
        if ( MedicLog.getInstance(getApplicationContext()).isSaveTextOriginalColourKnown() ) {
            saveBtn.setTextColor(MedicLog.getInstance(getApplicationContext()).getSaveTextOriginalColour()) ;
        } else {
            saveBtn.setTextColor(Color.LTGRAY);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        SharedPreferences sharedPref = getSharedPreferences("org.paladyn.mediclog_preferences", MODE_PRIVATE);

        if (MedicLog.getInstance(getApplicationContext()).getNumRecsReadFromFile() == 0) {
            if (BuildConfig.DEBUG) {
                Log.d("mediclog", "Log has not been read yet - reading it now");
            }
            try {
                readLog();
            } catch (DataFormatException dfe) {
                dfe.printStackTrace();
            }
        } else {
            makeSendDeleteVisible();
        }

        TextView textView = (TextView) findViewById(R.id.text_view);
        textView.setText(R.string.description);
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        if (sharedPref.getBoolean("timeUTC", true)) {
            mdformat.setTimeZone(TimeZone.getTimeZone("GMT"));
        } else {
            mdformat.setTimeZone(TimeZone.getDefault());
        }
        String strDate = mdformat.format(calendar.getTime());
        TextView dateView = (TextView) findViewById(R.id.date_view);
        dateView.setText(strDate);

        if (sharedPref.getBoolean("recordPulseOximeter", false)) {
            View o2Block = findViewById(R.id.oximiter_block) ;
            o2Block.setVisibility(View.VISIBLE);
        } else {
            View o2Block = findViewById(R.id.oximiter_block) ;
            o2Block.setVisibility(View.GONE);
        }
    }

    public void onClickBpClear(View view) {
        EditText systolicText = (EditText) findViewById(R.id.systolicText);
        systolicText.setText("");
        EditText diastolicText = (EditText) findViewById(R.id.diastolicText);
        diastolicText.setText("");
        EditText heartrateText = (EditText) findViewById(R.id.heartrateText);
        heartrateText.setText("");
        setSaveNeeded ();
    }

    public void onClickSystolicMinus(View view) {
        EditText systolicText = (EditText) findViewById(R.id.systolicText);
        String systolicStr = systolicText.getText().toString();

        int systolic = TextUtils.isEmpty(systolicStr) ? defaultSystolic : Integer.parseInt(systolicStr);
        systolic = systolic - 1;
        systolicStr = String.format("%d", systolic);
        systolicText.setText(systolicStr);
        setSaveNeeded ();
    }

    public void onClickSystolicPlus(View view) {
        EditText systolicText = (EditText) findViewById(R.id.systolicText);
        String systolicStr = systolicText.getText().toString();
        int systolic = TextUtils.isEmpty(systolicStr) ? defaultSystolic : Integer.parseInt(systolicStr);
        systolic = systolic + 1;
        systolicStr = String.format("%d", systolic);
        systolicText.setText(systolicStr);
        setSaveNeeded ();
    }

    public void onClickDiastolicMinus(View view) {
        EditText diastolicText = (EditText) findViewById(R.id.diastolicText);
        String diastolicStr = diastolicText.getText().toString();
        int diastolic = TextUtils.isEmpty(diastolicStr) ? defaultDiastolic : Integer.parseInt(diastolicStr);
        diastolic = diastolic - 1;
        diastolicStr = String.format("%d", diastolic);
        diastolicText.setText(diastolicStr);
        setSaveNeeded ();
    }

    public void onClickDiastolicPlus(View view) {
        EditText diastolicText = (EditText) findViewById(R.id.diastolicText);
        String diastolicStr = diastolicText.getText().toString();
        int diastolic = TextUtils.isEmpty(diastolicStr) ? defaultDiastolic : Integer.parseInt(diastolicStr);
        diastolic = diastolic + 1;
        diastolicStr = String.format("%d", diastolic);
        diastolicText.setText(diastolicStr);
        setSaveNeeded ();
    }

    public void onClickHrateMinus(View view) {
        EditText heartrateText = (EditText) findViewById(R.id.heartrateText);
        String heartrateStr = heartrateText.getText().toString();
        int heartrate = TextUtils.isEmpty(heartrateStr) ? defaultHeartrate : Integer.parseInt(heartrateStr);
        heartrate = heartrate - 1;
        heartrateStr = String.format("%d", heartrate);
        heartrateText.setText(heartrateStr);
        setSaveNeeded ();
    }

    public void onClickHratePlus(View view) {
        EditText heartrateText = (EditText) findViewById(R.id.heartrateText);
        String heartrateStr = heartrateText.getText().toString();
        int heartrate = TextUtils.isEmpty(heartrateStr) ? defaultHeartrate : Integer.parseInt(heartrateStr);
        heartrate = heartrate + 1;
        heartrateStr = String.format("%d", heartrate);
        heartrateText.setText(heartrateStr);
        setSaveNeeded ();
    }

    public void onClickTempClear(View view) {
        EditText tempText = (EditText) findViewById(R.id.tempText);
        tempText.setText("");
        setSaveNeeded ();
    }

    public void onClickTempMinus(View view) {
        EditText tempText = (EditText) findViewById(R.id.tempText);
        String tempStr = tempText.getText().toString();
        String tempStr2 = tempStr.replaceAll("\\.", "");
        int temp = TextUtils.isEmpty(tempStr2) ? defaultTemperature : Integer.parseInt(tempStr2);
        temp = temp - 1;
        tempStr = String.format("%d", temp);
        tempStr2 = new StringBuilder(tempStr).insert(tempStr.length() - 1, ".").toString();
        tempText.setText(tempStr2);
        setSaveNeeded ();
    }

    public void onClickTempPlus(View view) {
        EditText tempText = (EditText) findViewById(R.id.tempText);
        String tempStr = tempText.getText().toString();
        String tempStr2 = tempStr.replaceAll("\\.", "");
        int temp = TextUtils.isEmpty(tempStr2) ? defaultTemperature : Integer.parseInt(tempStr2);
        temp = temp + 1;
        tempStr = String.format("%d", temp);
        tempStr2 = new StringBuilder(tempStr).insert(tempStr.length() - 1, ".").toString();
        tempText.setText(tempStr2);
        setSaveNeeded ();
    }

    public void onClickWeightClear(View view) {
        EditText weightText = (EditText) findViewById(R.id.weightText);
        weightText.setText("");
        setSaveNeeded ();
    }

    public void onClickWeightMinus(View view) {
        EditText weightText = (EditText) findViewById(R.id.weightText);
        String weightStr = weightText.getText().toString();
        String weightStr2 = weightStr.replaceAll("\\.", "");
        int weight = TextUtils.isEmpty(weightStr2) ? defaultWeight : Integer.parseInt(weightStr2);
        weight = weight - 1;
        weightStr = String.format("%d", weight);
        weightStr2 = new StringBuilder(weightStr).insert(weightStr.length() - 1, ".").toString();
        weightText.setText(weightStr2);
        setSaveNeeded ();
    }

    public void onClickWeightPlus(View view) {
        EditText weightText = (EditText) findViewById(R.id.weightText);
        String weightStr = weightText.getText().toString();
        String weightStr2 = weightStr.replaceAll("\\.", "");
        int weight = TextUtils.isEmpty(weightStr2) ? defaultWeight : Integer.parseInt(weightStr2);
        weight = weight + 1;
        weightStr = String.format("%d", weight);
        weightStr2 = new StringBuilder(weightStr).insert(weightStr.length() - 1, ".").toString();
        weightText.setText(weightStr2);
        setSaveNeeded ();
    }

    public void onClickCommentClear(View view) {
        EditText commentText = (EditText) findViewById(R.id.commentText);
        commentText.setText("");
        setSaveNeeded ();


    }

    public void onClickOsClear(View view) {
        EditText pO2RText = (EditText) findViewById(R.id.pO2RText);
        pO2RText.setText("");
        EditText pO2AText = (EditText) findViewById(R.id.pO2AText);
        pO2AText.setText("");
        setSaveNeeded ();




    }

    public void onClickPo2RMinus(View view) {
        EditText pO2RText = (EditText) findViewById(R.id.pO2RText);
        String pO2RStr = pO2RText.getText().toString();
        int pO2R = TextUtils.isEmpty(pO2RStr) ? defaultO2 : Integer.parseInt(pO2RStr);
        pO2R = pO2R - 1;
        pO2RStr = String.format("%d", pO2R);
        pO2RText.setText(pO2RStr);
        setSaveNeeded ();
    }

    public void onClickPo2RPlus(View view) {
        EditText pO2RText = (EditText) findViewById(R.id.pO2RText);
        String pO2RStr = pO2RText.getText().toString();
        int pO2R = TextUtils.isEmpty(pO2RStr) ? defaultO2 : Integer.parseInt(pO2RStr);
        if ( pO2R < 100 ) { pO2R = pO2R + 1; }
        pO2RStr = String.format("%d", pO2R);
        pO2RText.setText(pO2RStr);
        setSaveNeeded ();
    }

    public void onClickPo2AMinus(View view) {
        EditText pO2AText = (EditText) findViewById(R.id.pO2AText);
        String pO2AStr = pO2AText.getText().toString();
        int pO2A = TextUtils.isEmpty(pO2AStr) ? defaultO2 : Integer.parseInt(pO2AStr);
        pO2A = pO2A - 1;
        pO2AStr = String.format("%d", pO2A);
        pO2AText.setText(pO2AStr);
        setSaveNeeded ();
    }

    public void onClickPo2APlus(View view) {
        EditText pO2AText = (EditText) findViewById(R.id.pO2AText);
        String pO2AStr = pO2AText.getText().toString();
        int pO2A = TextUtils.isEmpty(pO2AStr) ? defaultO2 : Integer.parseInt(pO2AStr);
        if ( pO2A < 100 ) { pO2A = pO2A + 1; }
        pO2AStr = String.format("%d", pO2A);
        pO2AText.setText(pO2AStr);
        setSaveNeeded ();
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
        TextView pO2RText = (EditText) findViewById(R.id.pO2RText);
        String pO2RStr = pO2RText.getText().toString();
        TextView pO2AText = (EditText) findViewById(R.id.pO2AText);
        String pO2AStr = pO2AText.getText().toString();


        try {
            SharedPreferences sharedPref = getSharedPreferences("org.paladyn.mediclog_preferences", MODE_PRIVATE);
            File file = new File(getFilesDir(), sharedPref.getString("fileName", "mediclog.txt"));
            if (!file.exists()) {
                if (BuildConfig.DEBUG) {
                    Log.d("mediclog", "Save -about to create file " + file.getName());
                }
                createLog(file);
            }
            FileOutputStream fOut = new FileOutputStream(file, true);
            OutputStreamWriter osw = new
                    OutputStreamWriter(fOut);
            BufferedWriter fbw = new BufferedWriter(osw);

            // In 1.1 Format the comment string can also contain Pulse Oximeter and wother data
            StringBuilder extraString = new StringBuilder();
            if (! commentStr.isEmpty()) {
                extraString.append(":C " + commentStr);
            }
            if (! pO2RStr.isEmpty()) {
                extraString.append(":OR " + pO2RStr);
            }
            if (! pO2AStr.isEmpty()) {
                extraString.append(":OA " + pO2AStr);
            }
            if (BuildConfig.DEBUG) {
                Log.d("mediclog", "Save - extraString is " + extraString);
            }

            String str = "1," + strDate + "," + systolicStr + "," + diastolicStr + "," + heartrateStr + "," + tempStr + "," + weightStr + "," + extraString;
            fbw.write(str);
            fbw.newLine();
            fbw.flush();
            fbw.close();
            // Also append it to the buffer
            MedicLog.getInstance(getApplicationContext()).putHistoryBuffer(str);

            MedicLog.getInstance(getApplicationContext()).incNumRecsAppendedToFile();
            MedicLog.getInstance(getApplicationContext()).incHistBuffReadIndex();

            Toast.makeText(getBaseContext(), R.string.save_successful,
                    Toast.LENGTH_SHORT).show();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        unsetSaveNeeded();
    }


    public void onClickSend(View view) {
        SharedPreferences sharedPref = getSharedPreferences("org.paladyn.mediclog_preferences", MODE_PRIVATE);

        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("text/csv");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, sharedPref.getString("sendTo", "").split(","));
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, sharedPref.getString("sendSubject", "MedicLog"));

        // Put something to pick up the attachment
        emailIntent.putExtra(
                android.content.Intent.EXTRA_STREAM, Uri.parse("content://" + LocalFileProvider.AUTHORITY + "/"
                        + sharedPref.getString("fileName", "mediclog.txt")));

        this.startActivity(Intent.createChooser(emailIntent, getString(R.string.send_email)));
    }

    public void onClickDelete(View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.delete_file);
        alert.setMessage(R.string.delete_message);
        alert.setPositiveButton(android.R.string.yes, (dialog, which) -> {
            SharedPreferences sharedPref = getSharedPreferences("org.paladyn.mediclog_preferences", MODE_PRIVATE);
            File dir = getFilesDir();
            File file = new File(dir, sharedPref.getString("fileName", "mediclog.txt"));
            Button deleteBtn = (Button) findViewById(R.id.btnDelete);
            deleteBtn.setVisibility(View.GONE);
            Button sendBtn = (Button) findViewById(R.id.btnSend);
            sendBtn.setVisibility(View.GONE);
            boolean deleted = file.delete();
            MedicLog.getInstance(getApplicationContext()).clearNumRecsReadFromFile();
            MedicLog.getInstance(getApplicationContext()).clearNumRecsAppendedToFile();
            MedicLog.getInstance(getApplicationContext()).clearHistBuffIndex();
            MedicLog.getInstance(getApplicationContext()).resetHistBuffReadIndex();

            if (BuildConfig.DEBUG) {
                Log.d("mediclog", "Log file deleted");
            }
            dialog.dismiss();
        });
        alert.setNeutralButton( R.string.Truncate, (dialog, which) -> {
                    SharedPreferences sharedPref = getSharedPreferences("org.paladyn.mediclog_preferences", MODE_PRIVATE);
                    File dir = getFilesDir();
                    File file = new File(dir, sharedPref.getString("fileName", "mediclog.txt"));
                    // delete existing file
                    boolean deleted = file.delete();
                    // Create file and Write a header,
                    createLog(file);
            if (MedicLog.getInstance(getApplicationContext()).getNumRecsReadFromFile() +
                    MedicLog.getInstance(getApplicationContext()).getNumRecsAppendedToFile() == 0) {
                // Do not do anything - there is nothing in history to read
            } else {
                try {
                    if (BuildConfig.DEBUG) {
                        Log.d("mediclog", "About to start truncate");
                    }
                    Integer numRead = 0;
                    String line = MedicLog.getInstance(getApplicationContext()).getHistoryBufferFirstLine();
                    // Log.d("mediclog", "History - first line *" + line + "*");
                    numRead = numRead + 1;
                    if (BuildConfig.DEBUG) {
                        Log.d("mediclog", "Truncate - r 1st line *"+ line + "*");
                    }
                    FileOutputStream fOut = new FileOutputStream(file, true);
                    OutputStreamWriter osw = new
                            OutputStreamWriter(fOut);
                    BufferedWriter fbw = new BufferedWriter(osw);
                    while (line != null) {
                        // remove null characters from line
                        line.replace("\u0000", "");
                        fbw.write(line);
                        fbw.newLine();
                        line = MedicLog.getInstance(getApplicationContext()).getHistoryBufferNextLine();
                        if (BuildConfig.DEBUG) {
                            Log.d("mediclog", "Truncate - r line *"+ line + "*");
                        }
                        if (line != null ) {numRead = numRead + 1; }
                    }
                    fbw.flush();
                    fbw.close();

                    MedicLog.getInstance(getApplicationContext()).resetHistBuffReadIndex();
                    MedicLog.getInstance(getApplicationContext()).clearNumRecsAppendedToFile();
                    MedicLog.getInstance(getApplicationContext()).setNumRecsReadFromFile( numRead );

                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
                    dialog.dismiss();
                });
        alert.setNegativeButton(android.R.string.no, (dialog, which) -> {
            // close dialog
            dialog.cancel();
        });
        alert.show();
    }
}
