/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class MainActivity extends AppCompatActivity {

  EditText mUsername;
  EditText mPassword;
  TextView changeSignUpMode;
  Button mButton;

  Boolean signUpModeActive;

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
          } else {
            Log.i("AppInfo", "Login failed");
            Toast.makeText(getApplicationContext(),e.getMessage().substring(e.getMessage().indexOf(" ")), Toast.LENGTH_LONG).show();
          }
        }
      });
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    signUpModeActive = true;

    mButton = (Button) findViewById(R.id.signUpButton);
    mUsername = (EditText) findViewById(R.id.username);
    mPassword = (EditText) findViewById(R.id.password);

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
