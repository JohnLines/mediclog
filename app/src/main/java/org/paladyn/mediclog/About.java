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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.widget.TextView;

import java.io.File;

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

        String fileName = getSharedPreferences("org.paladyn.mediclog_preferences", MODE_PRIVATE).getString("fileName", "mediclog.txt");
        File file = new File(getFilesDir(), fileName);
        long fileSizeInBytes = file.length();
        String fileSizeString = "";

        java.text.DecimalFormat df = new java.text.DecimalFormat("0.00");
        float sizeKb = 1024.0f;
        float sizeMb = sizeKb * sizeKb;
        float sizeGb = sizeMb * sizeKb;
        float sizeTerra = sizeGb * sizeKb;
        if(fileSizeInBytes < sizeMb)
            fileSizeString =  df.format(fileSizeInBytes / sizeKb)+ " Kb";
        else if(fileSizeInBytes < sizeGb)
            fileSizeString = df.format(fileSizeInBytes / sizeMb) + " Mb";
        else if(fileSizeInBytes < sizeTerra)
            fileSizeString =  df.format(fileSizeInBytes / sizeGb) + " Gb";




        Spanned aboutText = Html.fromHtml("<h1>MedicLog, Version " + versionName + "</h1>"
                + getString(R.string.about_text) + "<p>"
                + getString(R.string.records_read) + " " + MedicLog.getInstance(getApplicationContext()).getNumRecsReadFromFile() + "<br>"
                + getString(R.string.records_appended) + " " + MedicLog.getInstance(getApplicationContext()).getNumRecsAppendedToFile() + "<br>"
                + "File Name: " + fileName + "<br>"
                + "File Size (bytes): " + String.format("%,d bytes", fileSizeInBytes) + "(" + fileSizeString + ")" + "<br><br>"
                + "For documentation see https://johnlines.github.io/mediclog/"
        );

        aboutTextView.setText(aboutText);
    }


}
