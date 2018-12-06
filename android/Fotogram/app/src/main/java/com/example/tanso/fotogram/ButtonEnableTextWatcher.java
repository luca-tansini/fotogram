package com.example.tanso.fotogram;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ButtonEnableTextWatcher implements TextWatcher {

    private ArrayList<Button> buttons;
    private ArrayList<EditText> others;

    public ButtonEnableTextWatcher(Button button) {
        this.buttons = new ArrayList<>();
        this.buttons.add(button);
    }

    public ButtonEnableTextWatcher(Button button, EditText other) {
        this.buttons = new ArrayList<>();
        this.buttons.add(button);
        this.others = new ArrayList<>();
        this.others.add(other);
    }

    public ButtonEnableTextWatcher(ArrayList<Button> buttons, ArrayList<EditText> others) {
        this.buttons = buttons;
        this.others = others;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (count == 0)
            for(Button b: this.buttons)
                b.setEnabled(false);
        else{
            boolean flag = true;
            for(TextView tv: this.others) {
                if (tv.getText().toString().equals("")) {
                    flag = false;
                    break;
                }
            }
            if(flag)
                for(Button b: this.buttons)
                    b.setEnabled(true);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
