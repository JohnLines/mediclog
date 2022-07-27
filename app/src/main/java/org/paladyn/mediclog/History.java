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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.Calendar;

public class History extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_layout);

        SharedPreferences sharedPref = getSharedPreferences("org.paladyn.mediclog_preferences", MODE_PRIVATE);
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        if (sharedPref.getBoolean("timeUTC", true)) {
            mdformat.setTimeZone(TimeZone.getTimeZone("GMT"));
        } else {
            mdformat.setTimeZone(TimeZone.getDefault());
        }

        TextView historyTextView = (TextView) findViewById(R.id.history_text_view);

        final SpannableStringBuilder sb = new SpannableStringBuilder();

        if (MedicLog.getInstance(getApplicationContext()).getNumRecsReadFromFile() +
                MedicLog.getInstance(getApplicationContext()).getNumRecsAppendedToFile() == 0) {
            sb.append(getString(R.string.no_history));
        } else {

            String line = MedicLog.getInstance(getApplicationContext()).getHistoryBufferFirstLine();
            // Log.d("mediclog", "History - first line *" + line + "*");
            while (line != null) {

                String[] values = line.split(",");
                if (values.length == 1) {
                    sb.append("Something has gone wrong - line is ").append(line);
                    historyTextView.setText(sb);
                    // Log.d("mediclog", "History - something wrong *" + line + "*");
                    return;
                }
                if (values.length > 0) {
                    String dateTime = values[1];
                    try {
                        java.util.Date rdate = mdformat.parse(dateTime);

                    java.util.Calendar rcal = java.util.Calendar.getInstance();
                    rcal.setTime(rdate);
                        int year = rcal.get(Calendar.YEAR);
                        int month = rcal.get(Calendar.MONTH);
                        int day = rcal.get(Calendar.DAY_OF_MONTH);
                        int hour = rcal.get(Calendar.HOUR_OF_DAY);
                        int minute = rcal.get(Calendar.MINUTE);
                    if (BuildConfig.DEBUG) {
                       Log.d("mediclog", "DateTime  " +
                            dateTime + " Y " + year + " m " + (month+1)+ " d "+ day + " h " + hour
                            + " m "+ minute);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
// for now just split
                    String[]  thisDateTime = dateTime.split(" ", 2);
                    sb.append(thisDateTime[0]);
                    if (sharedPref.getBoolean("historyShowTime", false) ) {
                        sb.append(' '); sb.append(thisDateTime[1]);
                    }

                    sb.append("-");
                }
                if (values.length > 4) {
                    sb.append("(").append(values[2]).append(",").append(values[3]).append(",").append(values[4]).append(") ");
                }
                if (values.length > 5) {
                    sb.append(values[5]).append(" ");
                }
                if (values.length > 6) {
                    sb.append(values[6]);
                }
                if (values.length > 7 && values[7].length() > 0) {
                    String comment = values[7].replace("\u0000", "");
                    // if (BuildConfig.DEBUG) {
                    //   Log.d("mediclog", "comment  " + comment.length()
                    //        + "*" + comment + "*");
                    // }
                    // hide emtpy comments of those which start with : unless historyShowEmptyComments is true
                    if ((comment.length() > 0 && comment.charAt(0) != ':' )  || sharedPref.getBoolean("historyShowEmptyComments", false) ) {
                        sb.append("\n").append(comment);
                    }
                } else {
                    if (BuildConfig.DEBUG) {
                        Log.d("mediclog", "comment - no comment ");
                    }
                }

                sb.append("\n");
                line = MedicLog.getInstance(getApplicationContext()).getHistoryBufferNextLine();
                // Log.d("mediclog", "History - line *" + line + "*");
            }
        }

        // historyTextView.setText(Html.fromHtml(sb));
        historyTextView.setText(sb);

        sb.clear();
        // Log.d("mediclog", "History - sb cleared");
        MedicLog.getInstance(getApplicationContext()).resetHistBuffReadIndex();
    }
}
