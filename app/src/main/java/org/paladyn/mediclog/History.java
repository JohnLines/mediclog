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

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.TextView;
import android.text.Html;
import android.text.Spanned;
import android.text.Spannable;
import android.text.SpannableStringBuilder;

import android.util.Log;

public class History extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_layout);


        TextView historyTextView = (TextView) findViewById(R.id.history_text_view);

        final SpannableStringBuilder sb = new SpannableStringBuilder();


        if (MedicLog.getInstance(getApplicationContext()).getNumRecsReadFromFile() +
                MedicLog.getInstance(getApplicationContext()).getNumRecsAppendedToFile() == 0) {
            sb.append(getString(R.string.no_history));
        } else {

            String line = MedicLog.getInstance(getApplicationContext()).getHistoryBufferFirstLine();
//          Log.d("mediclog","History - first line *"+line+"*");
            while (line != null) {

                String[] values = line.split(",");
                if (values.length == 1) {
                    sb.append("Something has gone wrong - line is " + line);
                    historyTextView.setText(sb);
//                    Log.d("mediclog","History - something wrong *"+line+"*");
                    return;
                }
                if (values.length > 0) {
                    sb.append(values[1] + "-");
                }
                if (values.length > 4) {
                    sb.append("(" + values[2] + "," + values[3] + "," + values[4] + ") ");
                }
                if (values.length > 5) {
                    sb.append(values[5] + " ");
                }
                if (values.length > 6) {
                    sb.append(values[6]);
                }

                if (values.length > 7 && values[7].length() > 0) {
                    sb.append("\n" + values[7]);
                }

                sb.append("\n");
                line = MedicLog.getInstance(getApplicationContext()).getHistoryBufferNextLine();
//          Log.d("mediclog","History - line *"+line+"*");
            }
        }

//      historyTextView.setText(Html.fromHtml(sb));
        historyTextView.setText(sb);


        sb.clear();
        /*       Log.d("mediclog","History - sb cleared");  */
        MedicLog.getInstance(getApplicationContext()).resetHistBuffReadIndex();


    }


}

