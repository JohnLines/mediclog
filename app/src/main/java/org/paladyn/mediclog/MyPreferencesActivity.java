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
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.util.Log;

public class MyPreferencesActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();

        // This was intended to make 3 checkboxes work as a radio button, but crashed on running, for now just use two check boxes
/*
        Preference displayPrivacy = findPreference("displayPrivacy");
        displayPrivacy.setOnPreferenceClickListener(thispref -> {
            Log.d("mediclog", "displayPrivacy selected");
            return true;
        });

        Preference hidePrivacy = findPreference("hidePrivacy");
        hidePrivacy.setOnPreferenceClickListener(thispref -> {
            Log.d("mediclog", "hidePrivacy selected");
            return true;
        });
*/
    }

    public boolean onClickDisplayPrivacy(Preference thispref) {
        Log.d("mediclog", "displayPrivacy selected");
        return true;
    }

    public static class MyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }
}
