package org.paladyn.mediclog;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.TextView;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.content.SharedPreferences;


public class DisplayPrivacy extends Activity {

 @Override
 protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	setContentView(R.layout.privacy_layout);

       if (BuildConfig.DEBUG) {
	       Log.d("mediclog","DisplayPrivacy");
       }


	TextView privacyTextView = (TextView) findViewById(R.id.privacy_text_view);

    Spanned privacyText = Html.fromHtml(
	                 getString(R.string.privacy_policy) + "<p>"
			);

        privacyTextView.setText(privacyText);



    }

 public void onClickAccept(View view) {

       if (BuildConfig.DEBUG) {
	       Log.d("mediclog","Accept Privacy - before update");
       }

 	SharedPreferences sharedPref = getSharedPreferences("org.paladyn.mediclog_preferences",MODE_PRIVATE); 
		   sharedPref.edit().putBoolean("displayPrivacy", false).commit();

       if (BuildConfig.DEBUG) {
	       Log.d("mediclog","Accept Privacy - after update");
       }
       finish();
   }



 }

