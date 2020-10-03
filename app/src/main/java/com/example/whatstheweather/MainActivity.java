package com.example.whatstheweather;

import androidx.appcompat.app.AppCompatActivity;

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
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    TextView resultTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        resultTextView = findViewById(R.id.resultTextView);

    }

    public void getWeather (View view){

        try{
            DownloadTask task = new DownloadTask();
            String encodeCityName = URLEncoder.encode(editText.getText().toString(), "UTF-8");

            task.execute("http://api.openweathermap.org/data/2.5/weather?q=" + encodeCityName + "&appid=be8e9cc2fbf4ea2b7b21e6e96bc02f42");

            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Could not find weather !",Toast.LENGTH_SHORT).show();

        }
    }
    public class DownloadTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url ;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while ( data != -1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Could not find weather !",Toast.LENGTH_SHORT).show();

                return null;
            }
        }

    @Override
        protected void  onPostExecute(String s) {
        super.onPostExecute(s);

        try {
            JSONObject jsonObject = new JSONObject(s);
            String weatherInfo = jsonObject.getString("weather");
            Log.i("Weather content", weatherInfo);
            JSONArray array = new JSONArray(weatherInfo);
            String message = "";

            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonpart = array.getJSONObject(i);

                String main = jsonpart.getString("main");
                String description = jsonpart.getString("description");

                if (!main.equals("") && !description.equals("")) {
                    message += main + ": " + description + "\r\n";

                }
            }
            if (!message.equals("")) {
                resultTextView.setText(message);

            } else {
                Toast.makeText(getApplicationContext(), "Could not find weather !", Toast.LENGTH_SHORT).show();

            }

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Could not find weather !", Toast.LENGTH_SHORT).show();

            e.printStackTrace();
        }

    }
    }
}