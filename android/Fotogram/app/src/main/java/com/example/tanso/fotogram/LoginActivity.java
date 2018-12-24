package com.example.tanso.fotogram;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.example.tanso.fotogram.Controller.ErrorCode;
import com.example.tanso.fotogram.Controller.FotogramAPI;
import com.example.tanso.fotogram.Controller.ResponseCode;
import com.example.tanso.fotogram.Model.LoggedUser;
import com.example.tanso.fotogram.Model.Model;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Has the user logged in already?
        SharedPreferences settings = getSharedPreferences(getString(R.string.shared_preferences_filename), 0);
        String sessionId = settings.getString("sessionId", "");
        String username = settings.getString("username", "");
        if(!sessionId.equals("") && !username.equals("")){
            startHome(sessionId, username);
        }
        else {

            setContentView(R.layout.activity_login);

            //Set login form
            final Button loginButton = findViewById(R.id.buttonLogin);
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    login();
                }
            });
            loginButton.setEnabled(false);
            final TextView error = findViewById(R.id.textViewError);
            final EditText usernameET = findViewById(R.id.editTextUsername);
            final EditText passwordET = findViewById(R.id.editTextPassword);
            usernameET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.toString().length() > 0 && passwordET.getText().length() > 0)
                        loginButton.setEnabled(true);
                    else
                        loginButton.setEnabled(false);
                    error.setText("");
                }
            });
            passwordET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.toString().length() > 0 && usernameET.getText().length() > 0)
                        loginButton.setEnabled(true);
                    else
                        loginButton.setEnabled(false);
                    error.setText("");
                }
            });

            //Set show password toggle
            passwordET.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.ic_eye, 0);
            passwordET.setCompoundDrawableTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorDisabled)));
            passwordET.setOnTouchListener(new View.OnTouchListener() {

                final int DRAWABLE_RIGHT = 2;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        if(event.getRawX() >= (passwordET.getRight() - passwordET.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                            passwordET.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            return true;
                        }
                    }
                    if(event.getAction() == MotionEvent.ACTION_UP) {
                        if(event.getRawX() >= (passwordET.getRight() - passwordET.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                            passwordET.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            return true;
                        }
                    }
                    return false;
                }
            });


        }
    }

    private void login(){
        EditText usernameET = findViewById(R.id.editTextUsername);
        EditText passwordET = findViewById(R.id.editTextPassword);
        final String usr,pswd;
        usr = usernameET.getText().toString();
        pswd = passwordET.getText().toString();

        HashMap<String,String> params = new HashMap<>();
        params.put("username",usr);
        params.put("password",pswd);
        FotogramAPI.makeAPICall(FotogramAPI.API.LOGIN,getApplicationContext(), params,
                new ResponseCode() {
                    @Override
                    public void run(String sid) {
                        SharedPreferences settings = getSharedPreferences(getString(R.string.shared_preferences_filename), 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("sessionId", sid);
                        editor.putString("username", usr);
                        editor.apply();
                        startHome(sid, usr);
                    }
                },
                new ErrorCode() {
                    @Override
                    public void run(VolleyError error) {
                        TextView errorTV = findViewById(R.id.textViewError);
                        errorTV.setText("invalid username or password");
                    }
        });
    }

    private void startHome(String sid, String usr){
        Log.d("ajeje", "sid: "+sid);
        Model model = Model.getInstance();
        model.setLoggedUser(new LoggedUser(usr,null, sid, null));
        Intent home = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(home);
        finish();
    }

    @Override
    public void onBackPressed() {
        this.finishAffinity();
    }
}
