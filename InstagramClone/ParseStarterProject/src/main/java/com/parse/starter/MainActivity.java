/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

    EditText mUsername;
    EditText mPassword;
    TextView changeSignUpMode;
    Button mButton;
    ImageView logo;
    RelativeLayout mRelativeLayout;

    Boolean signUpModeActive;

    //Hide keyboard on UI when user clicks the logo or anywhere else
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.logo || v.getId() == R.id.relativeLayout){
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromInputMethod(getCurrentFocus().getWindowToken(), 0);
        }
    }
    //Attempt to login when the user presses Enter key
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){
            signUpOrLogIn(v);
        }
        return false;
    }

    public void signUpOrLogIn(View view){
        //If the user wants to sign up
        if(signUpModeActive){
            ParseUser parseUser = new ParseUser();
            parseUser.setUsername(String.valueOf(mUsername.getText()));
            parseUser.setPassword(String.valueOf(mPassword.getText()));

            parseUser.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Log.i("AppInfo", "SignUp successful");
                        showUserList();
                    } else {
                        Log.i("AppInfo", "SignUp failed");
                        //e.printStackTrace();
                        //Display error message received from Parse
                        Toast.makeText(getApplicationContext(),e.getMessage().substring(e.getMessage().indexOf(" ")), Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {  //If the user wants to login
            ParseUser.logInInBackground(String.valueOf(mUsername.getText()),String.valueOf(mPassword.getText()), new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if(user != null){
                        Log.i("AppInfo", "Login successful");
                        showUserList();
                    } else {
                        Log.i("AppInfo", "Login failed");
                        Toast.makeText(getApplicationContext(),e.getMessage().substring(e.getMessage().indexOf(" ")), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    public void showUserList(){
        Intent intent = new Intent(MainActivity.this, UserList.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(ParseUser.getCurrentUser() != null){
            //showUserList();
        }
        signUpModeActive = true;

        mButton = (Button) findViewById(R.id.signUpButton);
        mUsername = (EditText) findViewById(R.id.username);
        mPassword = (EditText) findViewById(R.id.password);
        logo = (ImageView) findViewById(R.id.logo);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);


        //Hide keyboard on UI when user clicks logo or anywhere else
        logo.setOnClickListener(this);
        mRelativeLayout.setOnClickListener(this);

        //Attempt to login when the user presses Enter key
        mUsername.setOnKeyListener(this);
        mPassword.setOnKeyListener(this);


        changeSignUpMode = (TextView) findViewById(R.id.changeSignUpMode);
        changeSignUpMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(signUpModeActive) {
                    signUpModeActive = false;
                    mButton.setText("Log In");
                    changeSignUpMode.setText("Sign Up");
                } else {
                    signUpModeActive = true;
                    mButton.setText("Sign Up");
                    changeSignUpMode.setText("Log In");
                }

                ParseUser.logInInBackground(String.valueOf(mUsername.getText()), String.valueOf(mPassword.getText()), new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if( user != null){
                            Log.i("AppInfo", "LogIn successful");
                        } else {
                            Log.i("AppInfo", "LogIn failed");
                        }
                    }
                });

            }
        });



        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
