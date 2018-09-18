package com.example.user.bidit.widgets;


import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.user.bidit.R;

public class CustomKeyboard extends LinearLayout implements View.OnClickListener {

    public CustomKeyboard(Context context) {
        this(context, null, 0);
    }

    public CustomKeyboard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomKeyboard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private static OnBidButtonListener mOnBidButtonListener;

    private Button mButton1, mButton2, mButton3, mButton4
            , mButton5, mButton6, mButton7, mButton8
            , mButton9, mButton0, mButtonEnter, mButtonDelete;
    private ConstraintLayout mLayoutDelete;

    SparseArray<String> keyboardValues = new SparseArray<>();

    InputConnection inputConnection;

    private void init(Context context, AttributeSet attrs) {

        LayoutInflater.from(context).inflate(R.layout.custum_keyboard, this, true);
        mButton1 = findViewById(R.id.button_1_custom_keyboard);
        mButton2 = findViewById(R.id.button_2_custom_keyboard);
        mButton3 = findViewById(R.id.button_3_custom_keyboard);
        mButton4 = findViewById(R.id.button_4_custom_keyboard);
        mButton5 = findViewById(R.id.button_5_custom_keyboard);
        mButton6 = findViewById(R.id.button_6_custom_keyboard);
        mButton7 = findViewById(R.id.button_7_custom_keyboard);
        mButton8 = findViewById(R.id.button_8_custom_keyboard);
        mButton9 = findViewById(R.id.button_9_custom_keyboard);
        mButton0 = findViewById(R.id.button_0_custom_keyboard);
        mButtonDelete = findViewById(R.id.button_delete_custom_keyboard);
        mButtonEnter = findViewById(R.id.button_enter_custom_keyboard);
        mLayoutDelete = findViewById(R.id.layout_delete_custom_keyboard);

        mButton1.setOnClickListener(this);
        mButton2.setOnClickListener(this);
        mButton3.setOnClickListener(this);
        mButton4.setOnClickListener(this);
        mButton5.setOnClickListener(this);
        mButton6.setOnClickListener(this);
        mButton7.setOnClickListener(this);
        mButton8.setOnClickListener(this);
        mButton9.setOnClickListener(this);
        mButton0.setOnClickListener(this);
        mButtonDelete.setOnClickListener(this);
        mButtonEnter.setOnClickListener(this);
        mLayoutDelete.setOnClickListener(this);

        keyboardValues.put(R.id.button_1_custom_keyboard, "1");
        keyboardValues.put(R.id.button_2_custom_keyboard, "2");
        keyboardValues.put(R.id.button_3_custom_keyboard, "3");
        keyboardValues.put(R.id.button_4_custom_keyboard, "4");
        keyboardValues.put(R.id.button_5_custom_keyboard, "5");
        keyboardValues.put(R.id.button_6_custom_keyboard, "6");
        keyboardValues.put(R.id.button_7_custom_keyboard, "7");
        keyboardValues.put(R.id.button_8_custom_keyboard, "8");
        keyboardValues.put(R.id.button_9_custom_keyboard, "9");
        keyboardValues.put(R.id.button_0_custom_keyboard, "0");
    }

    @Override
    public void onClick(View v) {

        if (inputConnection == null) return;

        if (v.getId() == R.id.button_delete_custom_keyboard || v.getId() == R.id.layout_delete_custom_keyboard) {
            CharSequence selectedText = inputConnection.getSelectedText(0);
            if (TextUtils.isEmpty(selectedText)) {
                inputConnection.deleteSurroundingText(1, 0);
            } else {
                inputConnection.commitText("", 1);
            }
        } else if (v.getId() == R.id.button_enter_custom_keyboard) {
            Log.d("asd", "onClick:  enter");
            mOnBidButtonListener.onBid();

        } else {
            String value = keyboardValues.get(v.getId());
            inputConnection.commitText(value, 1);
            Log.d("asd", "onClick: " + value);
        }
    }

    public void setInputConnection(InputConnection ic) {
        this.inputConnection = ic;
    }

    public static void setOnBidButtonListener(OnBidButtonListener onBidButtonListener) {
        mOnBidButtonListener = onBidButtonListener;
    }

    public interface OnBidButtonListener {
        void onBid();
    }
}
