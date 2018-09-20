
package com.example.user.bidit.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.user.bidit.R;
import com.example.user.bidit.firebase.FireBaseAuthenticationManager;

public class Balance extends AppCompatActivity {
    private TextView mBalance;
    private Button mAddMoney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);
        init();
        initListeners();

    }

    private void init() {
        mBalance = findViewById(R.id.text_view_activity_balance_current_balance);
        mBalance.setText(String.valueOf(FireBaseAuthenticationManager.getInstance().getCurrentUser().getBallance()));
        mAddMoney = findViewById(R.id.btn_activity_balance_add_money);
    }

    private void initListeners() {
        mAddMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
