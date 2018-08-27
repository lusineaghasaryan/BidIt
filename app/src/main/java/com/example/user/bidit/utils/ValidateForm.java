package com.example.user.bidit.utils;

import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.EditText;

public class ValidateForm {

    public static boolean isValidEmailAddress(String email) {
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    public static boolean isPasswordsCoincide(String pPass1, String pPass2) {
        return pPass1.equals(pPass2);
    }

    public static boolean setErrorIfEmpty(EditText... pEditText) {
        boolean valid = true;
        for (EditText element : pEditText) {
            if (TextUtils.isEmpty(element.getText().toString())) {
                element.setError("Required");
                valid = false;
            } else
                element.setError(null);
        }
        return valid;
    }

    public static boolean isPassportSeriesValid(String pPassportSeries) {
        boolean valid = true;
        if (pPassportSeries.length() != 9) {
            valid = false;
        }
        return valid;
    }
}
