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

