package com.parse.starter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserList extends AppCompatActivity {

    ArrayList<String> usernames;
    ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        usernames = new ArrayList<String>();
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, usernames);

        final ListView userList = (ListView) findViewById(R.id.userList);

        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent i = new Intent(getApplicationContext(), UserFeed.class);
                i.putExtra("username", usernames.get(position));
                startActivity(i);
            }
        });


        //Display the list of users except the currently logged in user
        ParseQuery<ParseUser> query = ParseUser.getQuery();

        //Do not display the currently logged in user
        query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());

        //Sort in alphabetical order
        query.addAscendingOrder("username");

        query.findInBackground(new FindCallback<ParseUser>() {

            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        for( ParseUser user : objects){
                            usernames.add(user.getUsername());
                        }
                        userList.setAdapter(arrayAdapter);
                    }
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id == R.id.share){
            //Picking images
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, 1);
        } else if(id == R.id.logout){
            ParseUser.logOut();
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK && data != null){
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                Log.i("AppInfo", "Image Received.");

                //Convert the image to byte array in order to save it to Parse as a Parse File
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                ParseFile parseFile = new ParseFile("image1.png", byteArray);

                //Save the image to Parse
                ParseObject object = new ParseObject("Images");
                object.put("username", ParseUser.getCurrentUser().getUsername());
                object.put("image", parseFile);

                //Set Public Read Access on file using ParseACL
                ParseACL parseACL = new ParseACL();
                parseACL.setPublicReadAccess(true);

                //Set the ACL on the object
                object.setACL(parseACL);

                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if( e == null){
                            Toast.makeText(getApplication().getBaseContext(), "Your images was posted!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplication().getBaseContext(), "There was an error-please try again", Toast.LENGTH_LONG).show();
                        }
                    }
                });


            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplication().getBaseContext(), "There was an error-please try again", Toast.LENGTH_LONG).show();
            }
        }
    }
}
