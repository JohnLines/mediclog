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

import android.content.Context;
import android.graphics.Color;

import java.util.Arrays;

public class MedicLog {

    // At present getting error, cant find symbol for MODE_PRIVATE - as this is not an Activity it does not have
    // a context at the moment.
    //	SharedPreferences sharedPref = getSharedPreferences("org.paladyn.mediclog_preferences",MODE_PRIVATE);

    private static MedicLog instance;
    // we need to define the maximum length a line in the History Log can be
    private final int maxLogLineLength = 120;
    //public final int maxHistBuffLen = sharedPref.getInt("historyLength", 31);
    private final int maxHistBuffLen = 31;
    public int histBuffLength = 0;  // the maximum length of the historyBuffer we have used
    private int histBuffIndex = 0;  // the place in the historyBuffer we are using
    private int histBuffReadIndex = 0;  // the place in the historyBuffer we are reading from

    // want to make maxHistBuffLen dynamic, but for now allocate statically
    private int histBuffFirstReadIndex = 0;  // the first place in the history buffer which was read
    private int histBuffMaxReadIndex = 0;  // the maximum number of history buffer records to read
    // 31 = maxHistBuffLen, 120 = maxLogLineLength
    private char[][] historyBuffer = new char[maxHistBuffLen][maxLogLineLength];
    private int numRecsReadFromFile = 0;
    private int numRecsAppendedToFile = 0;
    // saveNeeded is initially false, change to true as soon as a value is changed.
    private boolean saveIsNeeded = false;
    // Need to keep a copy of the Save button original text colour so we can restore it
    private boolean saveTextOriginalColourKnown = false;
    private int saveTextOrignalColour;



    private MedicLog(Context context) {
    }

    static MedicLog getInstance(Context context) {
        if (instance == null) {
            instance = new MedicLog(context.getApplicationContext());
        }
        return instance;
    }

    void incNumRecsReadFromFile() {
        numRecsReadFromFile++;
    }

    void incNumRecsAppendedToFile() {
        numRecsAppendedToFile++;
    }

    void clearNumRecsReadFromFile() {
        numRecsReadFromFile = 0;
    }

    void clearNumRecsAppendedToFile() {
        numRecsAppendedToFile = 0;
    }

    int getNumRecsReadFromFile() {
        return numRecsReadFromFile;
    }

    int getNumRecsAppendedToFile() {
        return numRecsAppendedToFile;
    }

    void saveNeeded () {
        saveIsNeeded = true;
    }
    void saveUnneeded ()  {
        saveIsNeeded = false;
    }
    boolean isSaveTextOriginalColourKnown () { return saveTextOriginalColourKnown ; }
    void setSaveTextOriginalColourKnown()  { saveTextOriginalColourKnown = true; }
    void setSaveTextOrignalColour (int colour) { saveTextOrignalColour = colour ; }
    int getSaveTextOriginalColour () { return saveTextOrignalColour;}

    public int getHistBuffIndex() {
        return histBuffIndex;
    }

    private void incHistBuffIndex() {
        // wrap the history buffer index round in this routine.
        //Log.d("mediclog", "incHistBuffIndex -" + histBuffIndex);
        histBuffIndex++;
        if (histBuffIndex >= maxHistBuffLen) {
            histBuffIndex = 0;  /* Log.d("mediclog","Set histBuffIndex to zero"); */
        }
    }

    void clearHistBuffIndex() {
        histBuffIndex = 0;
    }

    void incHistBuffReadIndex() {
        // wrap the history buffer read index round in this routine. Note that we leave the Read Index at 0 until histBuffIndex has reached
        //maxHistBuffLen
        if (histBuffIndex < maxHistBuffLen) {
            //Log.d("mediclog", "Not incrementing histBuffReadIndex as histBuffIndex is " + histBuffIndex + " Max is " + maxHistBuffLen);
            return;
        }
        //Log.d("mediclog", "incHistBuffReadIndex -" + histBuffReadIndex);
        histBuffReadIndex++;
        if (histBuffReadIndex > maxHistBuffLen) {
            histBuffReadIndex = 0;  /* Log.d("mediclog","Set histBuffReadIndex to zero");  */
        }
    }

    void resetHistBuffReadIndex() {
        // set the histBuffReadIndex back to zero, if the History Buffer is not full - i.e. numRecsReadFromFile +
        //if (histBuffIndex < maxHistBuffLen) {
        if ((numRecsReadFromFile + numRecsAppendedToFile - 1) < maxHistBuffLen) {
            //Log.d("mediclog", "Reset histBuffReadIndex to zero as histBuffIndex is " + histBuffIndex + " Max is " + maxHistBuffLen);
            histBuffReadIndex = 0;
        } else {
            //Log.d("mediclog", "Reset histBuffReadIndex to histBuffIndex " + histBuffIndex + " n.b. Max is " + maxHistBuffLen);
            histBuffReadIndex = histBuffIndex - 1;   // histBuffIndex will be pointing at the next place to write.
            if (histBuffReadIndex > 0) {
                histBuffReadIndex = maxHistBuffLen;
                //Log.d("mediclog", "Reset histBuffReadIndex to maxHistBuffLen " + maxHistBuffLen);
            }
        }
    }

    void putHistoryBuffer(String line) {
        // Put a string into the History Buffer at the current index, and move the index on
        //Log.d("mediclog", "putHistoryBuffer *" + line + "*");
        // Fill the historyBuffer line with zeros.

        Arrays.fill(historyBuffer[histBuffIndex], '\u0000');
        line.getChars(0, line.length(), historyBuffer[histBuffIndex], 0);

        incHistBuffIndex();
    }

    String getHistoryBufferFirstLine() {
        // return the first line from the HistoryBuffer,

        histBuffMaxReadIndex = numRecsReadFromFile + numRecsAppendedToFile - 1;

        //Log.d("mediclog", "getHistoryBufferFirstLine index is now " + histBuffReadIndex + " MaxReadIndex is " + histBuffMaxReadIndex + " Index is " + histBuffIndex);
        // wrap round if needed
        // If the history Buffer is full then set the histBuffReadIndex to the HistBuffIndex, as HistBuffIndex has already wrapped

        if (histBuffMaxReadIndex > maxHistBuffLen) {
            histBuffReadIndex = histBuffIndex;
            //Log.d("mediclog", "Reset histBuffReadIndex to HistBuffIndex " + histBuffReadIndex);
        }
        histBuffFirstReadIndex = histBuffReadIndex;

        char[] histBuffLine = historyBuffer[histBuffReadIndex];

        String line = String.valueOf(histBuffLine);

        // if we have hit the number of records in the buffer, return null
        if (histBuffReadIndex == histBuffMaxReadIndex) {
            return null;
        }

        histBuffReadIndex++;

        // wrap round if needed
        if (histBuffReadIndex > maxHistBuffLen) {
            histBuffReadIndex = 0;
        }

        return line;
    }

    String getHistoryBufferNextLine() {
        // return the next line from the HistoryBuffer,
        char[] histBuffLine = historyBuffer[histBuffReadIndex];

        String line = String.valueOf(histBuffLine);

        //Log.d("mediclog", "getHistoryBufferNextLine index is now " + histBuffReadIndex + " firstReadIndex is " + histBuffFirstReadIndex + " maxReadIndex is " + histBuffMaxReadIndex);

        // if we are now back to the start, return null
        if (histBuffReadIndex == histBuffFirstReadIndex) {
            //Log.d("mediclog", "getHistoryBufferNextLine - back to start");
            return null;
        }

        // if we have hit the number of records in the buffer, return null
        if (histBuffReadIndex > histBuffMaxReadIndex) {
            //Log.d("mediclog", "getHistoryBufferNextLines - " + histBuffReadIndex + " reached max " + histBuffMaxReadIndex);
            return null;
        }

        histBuffReadIndex++;

        // otherwise wrap round, if needed to the start of the buffer, and return the line
        if (histBuffReadIndex >= maxHistBuffLen) {
            histBuffReadIndex = 0;
        }

        return line;
    }
}
