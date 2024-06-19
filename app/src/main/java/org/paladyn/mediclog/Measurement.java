package org.paladyn.mediclog;

import android.text.TextUtils;
import android.widget.EditText;
import android.view.View;


public class Measurement {
    int value;
    int default_value;
    int resourceId;


    public void setDefault_value(int value) {
        this.default_value = value;
    }

    public void onClickPlusMinus(EditText mText, boolean plus) {
//        EditText mText = (EditText) android.view.View.findViewById(resid);
        String mStr = mText.getText().toString();
        int measurement = TextUtils.isEmpty(mStr) ? default_value : Integer.parseInt(mStr);
        if (plus) {
            measurement = measurement + 1;
        } else measurement = measurement - 1;
        mStr = String.format("%d", measurement);
        mText.setText(mStr);
//        MainActivity.setSaveNeeded ();
    }

    public void onClickPlusMinusFractional(EditText mText, boolean plus, int places) {
//        EditText mText = (EditText) android.view.View.findViewById(resid);
        String mStr = mText.getText().toString();
        String mStr2 = mStr.replaceAll("\\.", "");
        int measurement = TextUtils.isEmpty(mStr) ? default_value : Integer.parseInt(mStr2);
        if (plus) {
            measurement = measurement + 1;
        } else measurement = measurement - 1;
        mStr = String.format("%d", measurement);
        mStr2 = new StringBuilder(mStr).insert(mStr.length() - places, ".").toString();
        mText.setText(mStr2);
//        MainActivity.setSaveNeeded ();
    }

    public void onClickPlusMinusMax(EditText mText, boolean plus, int max) {
//        EditText mText = (EditText) android.view.View.findViewById(resid);
        String mStr = mText.getText().toString();
        int measurement = TextUtils.isEmpty(mStr) ? default_value : Integer.parseInt(mStr);
        if (plus) {
            if (measurement < max) {
                measurement = measurement + 1;
            }
        } else measurement = measurement - 1;
        mStr = String.format("%d", measurement);
        mText.setText(mStr);
//        MainActivity.setSaveNeeded ();
    }

}
