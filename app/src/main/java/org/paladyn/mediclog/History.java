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
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.Calendar;
import java.time.Month;

public class History extends Activity {

    boolean showTime = false;
    boolean showEmptyComments = false;
    SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private void displayHistoryView(boolean showTime, boolean showEmptyComments) {
        // The history view code will go here
        boolean headerWritten = false;
        boolean newYear = false;
        boolean newMonth = false;
        int year = 0;
        int month = 0;
        String monthName;

        setContentView(R.layout.history_layout);
        TextView historyTextView = (TextView) findViewById(R.id.history_text_view);

        final SpannableStringBuilder sb = new SpannableStringBuilder();

        Log.d("mediclog", "History displayHistoryView showTime " + showTime + " showEmptyComments " + showEmptyComments);

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
                        int thisYear = rcal.get(Calendar.YEAR);
                        if (thisYear != year) {
                            newYear = true;
                            year = thisYear;
                        }
                        int thisMonth = rcal.get(Calendar.MONTH);
                        if (thisMonth != month) {
                            newMonth = true;
                            month = thisMonth;
                        }
                        int day = rcal.get(Calendar.DAY_OF_MONTH);
                        int hour = rcal.get(Calendar.HOUR_OF_DAY);
                        int minute = rcal.get(Calendar.MINUTE);
                        if (BuildConfig.DEBUG) {
                            Log.d("mediclog", "DateTime  " +
                                    dateTime + " Y " + year + " m " + (month + 1) + " d " + day + " h " + hour
                                    + " m " + minute);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
// for now just split
                    // Before Writing any data, but after knowing the date, write a header if needed
                    if (! headerWritten || newYear || newMonth ) {
                        int boldStart = sb.length();
                        sb.append(String.format("%02d", month + 1));
                        //      new Month jMonth = Month.of(month);
                        //      sb.append(jMonth.getDisplayName(java.time.format.TextStyle.SHORT_STANDALONE))
                        sb.append(" - ");
                        sb.append(String.valueOf(year));
                        sb.append(" BP   \u2103     kg");
                        int boldEnd = sb.length();
                        // Make the header BOL
                        sb.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
                            boldStart, boldEnd,android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        sb.append("\n");
                        newYear = false;
                        newMonth = false;
                        headerWritten = true;
                    }


                    String[] thisDateTime = dateTime.split(" ", 2);
                    //split yyyy-mm-dd
                    String[] thisDate  = thisDateTime[0].split("-",3);

                    // sb.append(thisDateTime[0]);
                    sb.append(thisDate[2]);
                    if (showTime) {
                        sb.append(' ');
                        sb.append(thisDateTime[1]);
                    }

                    sb.append("-");
                }
                if (values.length > 4) {
                    sb.append("(").append(values[2]).append("/").append(values[3]).append(" ").append(values[4]).append(") ");
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
                    // hide empty comments or those which start with : unless historyShowEmptyComments is true
                    if ((comment.length() > 0 && comment.charAt(0) != ':') ||
                            (comment.length() >1  && comment.charAt(0) == ':' && comment.charAt(1) == 'C' ) ||
                            showEmptyComments) {
                        int cmtStartIndex = 0;   // where does the part of the comment do be displayed start
                        int cmtEndIndex = comment.length();
                        if (comment.length() >1  && comment.charAt(0) == ':' && comment.charAt(1) == 'C' ) {
                            cmtStartIndex = 2;
                            // truncate displayed string at first : if there is one
                            if (comment.indexOf(":", 1) > 0 ) {
                                cmtEndIndex = comment.indexOf(":", 1);
                            }
                        }
                        int cmtStart = sb.length();
                        sb.append("\n").append(comment.substring(cmtStartIndex, cmtEndIndex));
                        int cmtEnd = sb.length();
                        sb.setSpan(new android.text.style.StyleSpan(Typeface.ITALIC),
                                cmtStart, cmtEnd,android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_layout);

        SharedPreferences sharedPref = getSharedPreferences("org.paladyn.mediclog_preferences", MODE_PRIVATE);

        if (sharedPref.getBoolean("timeUTC", true)) {
            mdformat.setTimeZone(TimeZone.getTimeZone("GMT"));
        } else {
            mdformat.setTimeZone(TimeZone.getDefault());
        }
        if (sharedPref.getBoolean("historyShowTime", false) ) {
            Log.d("mediclog","History - setting showTime to true");
            showTime = true;
        }
        if (sharedPref.getBoolean("historyShowEmptyComments", false) ) {
            showEmptyComments = true;
        }
        Log.d("mediclog","History onCreate about to call displayHistoryView");
        displayHistoryView( showTime, showEmptyComments);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_history, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)  {
        SharedPreferences sharedPref = getSharedPreferences("org.paladyn.mediclog_preferences", MODE_PRIVATE);
        switch (item.getItemId()) {
            case R.id.showTime:
                if (! item.isChecked()) {
                    Log.d("mediclog","onOptionsItemSelected - setting showTime to true");
                    item.setChecked(true);
                   showTime = true;
                } else {
                    Log.d("mediclog","onOptionsItemSelected - showTime was not checked");
                    item.setChecked(false);
                    showTime = false;
                }
            case R.id.showEmptyComments:
                if (! item.isChecked()) {
                    Log.d("mediclog","onOptionsItemSelected - setting showEmptyComments to true");
                    item.setChecked(true);
                    showEmptyComments = true;
                } else {
                    Log.d("mediclog","onOptionsItemSelected - showEmptyComments was not checked");
                    item.setChecked(false);
                    showEmptyComments = false;
                }

        }
        Log.d("mediclog","History onOptionsItemSelected about to call displayHistoryView");
    displayHistoryView( showTime, showEmptyComments);
    return true;
    }

}


