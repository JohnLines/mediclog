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

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class LocalFileProvider extends ContentProvider {

    public static final String AUTHORITY = "org.paladyn.mediclog.LocalFileProvider";
    private static final String CLASS_NAME = "LocalFileProvider";
    private UriMatcher uriMatcher;

    @Override
    public boolean onCreate() {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        // Add a URI to the matcher which will match against the form
        // 'content://it.my.app.LogFileProvider/*'
        // and return 1 in the case that the incoming Uri matches this pattern
        uriMatcher.addURI(AUTHORITY, "*", 1);

        return true;
    }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode)
            throws FileNotFoundException {

        String LOG_TAG = CLASS_NAME + " - openFile";

        //Log.v(LOG_TAG, "Called with uri: '" + uri + "'." + uri.getLastPathSegment());
        // URI always look like content://org.paladyn.mediclog.LocalFileProvider/mediclog.txt

        // Check incoming Uri against the matcher
        // If it returns 1 - then it matches the Uri defined in onCreate
        if (uriMatcher.match(uri) == 1) {// The desired file name is specified by the last segment of the
            // path
            // E.g.
            // 'content://it.my.app.LogFileProvider/Test.txt'
            // Take this and build the path to the file

            String fileLocation = getContext().getFilesDir() + File.separator
                    + uri.getLastPathSegment();
            // Protect against a possible Path Traversal vulnerability by checking that the Cannonical
            // path starts with the right string
            //Log.v(LOG_TAG, "fileLocation: '" + fileLocation + "'.");
            File f;
            try {
                f = new File(fileLocation);
                String oldvalid = getContext().getFilesDir() + File.separator;
                String newvalid = "/data/data/" + getContext().getPackageName() + "/files/";
                Boolean ob = f.getCanonicalPath().startsWith(oldvalid);
                Boolean nb = f.getCanonicalPath().startsWith(newvalid);
/*
                    Log.v(LOG_TAG, "oldvalid is " + oldvalid + "newvalid is " + newvalid);
                    if (ob) {
                        Log.v(LOG_TAG, "ob is true");
                    }
                    if (nb) {
                        Log.v(LOG_TAG, "nb is true");
                    }
*/
                if (!(ob || nb)) {
                    // The second case is a horrible kludge for API 28, where the path starts
                    // /data/data, as opposed to /data/user/0/

                    Log.v(LOG_TAG, "fileLocation: " + fileLocation + " is invalid");
                    Log.v(LOG_TAG, "f.getCanonicalPath is " + f.getCanonicalPath());
                    Log.v(LOG_TAG, "kludged path is /data/data/" + getContext().getPackageName() + "/files/");
                    throw new IllegalArgumentException();
                }
            } catch (IOException ex) {
                Log.v(LOG_TAG, "About the throw FileNotFoundException");
                throw new FileNotFoundException("Invalid path");
            }

            // Create & return a ParcelFileDescriptor pointing to the file
            // Note: I don't care what mode they ask for - they're only getting read only
            return ParcelFileDescriptor.open(f, ParcelFileDescriptor.MODE_READ_ONLY);

            // Otherwise unrecognised Uri
        }
        Log.v(LOG_TAG, "Unsupported uri: '" + uri + "'.");
        throw new FileNotFoundException("Unsupported uri: "
                + uri.toString());
    }

    @Override
    public int update(Uri uri, ContentValues contentvalues, String s, String[] as) {
        return 0;
    }

    @Override
    public int delete(Uri uri, String s, String[] as) {
        return 0;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentvalues) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String s, String[] as1, String s1) {
        return null;
    }
}
