package com.example.user.bidit.widgets;

import android.app.Dialog;
import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.widget.Button;
import android.widget.EditText;

import com.example.user.bidit.R;

public class AddMoneyDialog {
    private Dialog depositeDialog;
    private TextInputEditText mCardNumber, mCardCode, mMoneyAmount;
    private Button mAddMoney;

    public AddMoneyDialog(Context context) {
        depositeDialog = new Dialog(context);
        depositeDialog.setContentView(R.layout.add_money_dialog_view);
        depositeDialog.setCancelable(true);
        init();

    }

    private void init() {
        mAddMoney = depositeDialog.findViewById(R.id.btn_add_money_dialog_add_money);
        mCardCode = depositeDialog.findViewById(R.id.edit_text_card_code_add_money_dialog);
        mCardNumber = depositeDialog.findViewById(R.id.edit_text_card_number_add_money_dialog);
        mMoneyAmount = depositeDialog.findViewById(R.id.edit_text_money_amount_add_money_dialog);
    }
}
