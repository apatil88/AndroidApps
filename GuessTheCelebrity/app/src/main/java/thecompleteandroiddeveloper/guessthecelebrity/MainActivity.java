package thecompleteandroiddeveloper.guessthecelebrity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends Activity {

    ImageView mImageView;
    Button mButton1;
    Button mButton2;
    Button mButton3;
    Button mButton4;

    ArrayList<String> celebURLs = new ArrayList<>();
    ArrayList<String> celebNames = new ArrayList<>();

    int chosenCeleb = 0;
    int locationOfCorrectAnswer = 0;
    String[] answer = new String[4];

    public void celebChosen(View view){
        if(view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswer))){
            Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Wrong! It is " + celebNames.get(chosenCeleb), Toast.LENGTH_LONG).show();
        }
        createNewQuestion();
    }

    public class DownloadImage extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {

            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                return bitmap;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public class DownloadTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection connection = null;
            try {
                url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();

                InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                int data = inputStreamReader.read();
                while(data != -1){
                    char c = (char) data;
                    result = result + c;
                    data = inputStreamReader.read();
                }
                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Find the views
        mImageView = (ImageView) findViewById(R.id.imageView);
        mButton1 = (Button) findViewById(R.id.button1);
        mButton2 = (Button) findViewById(R.id.button2);
        mButton3 = (Button) findViewById(R.id.button3);
        mButton4 = (Button) findViewById(R.id.button4);

        DownloadTask downloadTask = new DownloadTask();
        String result = null;
        try {
            result = downloadTask.execute("http://www.posh24.com/celebrities").get();

            String[] splitResult = result.split("<div class=\"sidebarContainer\">");

            //Regex to extract image URL
            Pattern pattern = Pattern.compile("<img src=\"(.*?)\"");
            Matcher matcher = pattern.matcher(splitResult[0]);

            while(matcher.find()){
                celebURLs.add(matcher.group(1));
            }

            //Regex to extract celebrity name
            Pattern pattern1 = Pattern.compile("alt=\"(.*?)\"");
            Matcher matcher1 = pattern1.matcher(splitResult[0]);

            while (matcher1.find()){
                celebNames.add(matcher1.group(1));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        createNewQuestion();
    }

    public void createNewQuestion(){
        Random random = new Random();
        chosenCeleb = random.nextInt(celebURLs.size());

        DownloadImage downloadImage = new DownloadImage();
        try {
            Bitmap celebImage = downloadImage.execute(celebURLs.get(chosenCeleb)).get();

            mImageView.setImageBitmap(celebImage);

            locationOfCorrectAnswer = random.nextInt(4);

            int incorrectAnswerLocation;

            for(int i = 0; i < answer.length; i++){
                if( i == locationOfCorrectAnswer){
                    answer[i] = celebNames.get(chosenCeleb);
                } else{
                    incorrectAnswerLocation = random.nextInt(celebURLs.size());
                    //Make sure that we do not select the correct answer twice
                    while(incorrectAnswerLocation == chosenCeleb) {
                        incorrectAnswerLocation = random.nextInt(celebURLs.size());
                    }
                    answer[i] = celebNames.get(incorrectAnswerLocation);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        mButton1.setText(answer[0]);
        mButton2.setText(answer[1]);
        mButton3.setText(answer[2]);
        mButton4.setText(answer[3]);
    }

}
