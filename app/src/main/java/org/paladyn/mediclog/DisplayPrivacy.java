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
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


public class DisplayPrivacy extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.privacy_layout);

        if (BuildConfig.DEBUG) {
            Log.d("mediclog", "DisplayPrivacy");
        }


        TextView privacyTextView = (TextView) findViewById(R.id.privacy_text_view);

        Spanned privacyText = Html.fromHtml(
                getString(R.string.privacy_policy) + "<p>"
        );

        privacyTextView.setText(privacyText);


    }

    public void onClickAccept(View view) {

        if (BuildConfig.DEBUG) {
            Log.d("mediclog", "Accept Privacy - before update");
        }

        SharedPreferences sharedPref = getSharedPreferences("org.paladyn.mediclog_preferences", MODE_PRIVATE);

        sharedPref.edit().putBoolean("displayPrivacy", false).commit();
        //                  sharedPref.edit().putBoolean("displayPrivacyKeep", false).commit();

        if (BuildConfig.DEBUG) {
            Log.d("mediclog", "Accept Privacy - after update");
        }
        finish();
    }

    public void onClickAcceptKeep(View view) {

        if (BuildConfig.DEBUG) {
            Log.d("mediclog", "AcceptKeep Privacy - before update");
        }
// Don't adjust privacy settings
        SharedPreferences sharedPref = getSharedPreferences("org.paladyn.mediclog_preferences", MODE_PRIVATE);

        sharedPref.edit().putBoolean("displayPrivacy", false).commit();
        sharedPref.edit().putBoolean("displayPrivacyKeep", true).commit();


        finish();
    }


}

