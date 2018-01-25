package org.paladyn.mediclog;

import android.os.Bundle;
import android.content.pm.PackageManager;
import android.content.pm.PackageInfo;
import android.app.Activity;
import android.view.View;
import android.widget.TextView;
import android.text.Html;
import android.text.Spanned;

public class About extends Activity {

 @Override
 protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	setContentView(R.layout.about_layout);


	String versionName = "";

	try {
          PackageInfo pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
	  versionName = pinfo.versionName;
	   } catch (PackageManager.NameNotFoundException e) {
		   e.printStackTrace();
	   }

	TextView aboutTextView = (TextView) findViewById(R.id.about_text_view);

    Spanned aboutText = Html.fromHtml("<h1>MedicLog, Version " + versionName + "</h1>"
	                + getString(R.string.about_text) + "<p>"
			);
// Following commented out until I can work out how to do it
//			+ getString(R.string.records_read) + Integer.toString(MedicLog.numRecsReadFromFile) + "<br>"
//			+ getString(R.string.records_appended) + String.valueOf(MedicLog.numRecsAppendedToFile) );

        aboutTextView.setText(aboutText);



    }



}

