package com.example.user.bidit.utils;

import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.EditText;

public class ValidateForm {

    public static boolean isValidEmailAddress(String email) {
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    public static boolean isPasswordsCoincide(String pass1, String pass2) {
        return pass1.equals(pass2);
    }

    public static boolean setErrorIfEmpty(EditText... editText) {
        boolean valid = true;
        for (EditText element : editText) {
            if (TextUtils.isEmpty(element.getText().toString())) {
                element.setError("Required");
                valid = false;
            } else
                element.setError(null);
        }
        return valid;
    }
}
