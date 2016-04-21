package thecompleteandroiddeveloper.whatstheweather;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends Activity {

    EditText mEditText;
    TextView mTextView2;

    public void findWeather(View view){

        Log.i("City Name : ", mEditText.getText().toString());

        try {
            String encodedCityName = URLEncoder.encode(mEditText.getText().toString(), "UTF-8");
            DownloadTask downloadTask = new DownloadTask();
            downloadTask.execute("http://api.openweathermap.org/data/2.5/weather?q=" + encodedCityName + "&APPID=" + getResources().getString(R.string.api_key));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //Hide the keyboard once the button is pressed
        InputMethodManager methodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        methodManager.hideSoftInputFromInputMethod(mEditText.getWindowToken(), 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditText = (EditText) findViewById(R.id.editText);
        mTextView2 = (TextView) findViewById(R.id.textView2);


    }

    public class DownloadTask extends AsyncTask<String , Void, String >{
        String result = "";
        @Override
        protected String doInBackground(String... urls) {
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                int data = inputStreamReader.read();
                while(data != -1){
                    char c = (char) data;
                    result += c;
                    data = inputStreamReader.read();
                }
                return result;
            } catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                String message = "";
                JSONObject jsonObject = new JSONObject(result);
                Log.i("Weather Info : ", jsonObject.getString("weather"));

                String weatherInfo = jsonObject.getString("weather");
                JSONArray jsonArray = new JSONArray(weatherInfo);
                for(int i = 0 ; i < jsonArray.length(); i++){
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    String main = "";
                    String description = "";


                    main = jsonObject1.getString("main");
                    description = jsonObject1.getString("description");

                    if(main != null && description != null){
                        message += main + ": "+ description + "\r\n";
                    } else{
                        Toast.makeText(getApplicationContext(), "Cannot find weather", Toast.LENGTH_LONG).show();
                    }

                    Log.i("Main : " , jsonObject1.getString("main"));
                    Log.i("Description : ", jsonObject1.getString("description"));
                }

                if(message != null){
                    mTextView2.setText(message);
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Cannot find weather", Toast.LENGTH_LONG).show();
            }

        }
    }

}
