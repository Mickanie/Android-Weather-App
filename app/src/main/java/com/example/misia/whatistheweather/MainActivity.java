package com.example.misia.whatistheweather;

import android.content.Context;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.misia.whatistheweather.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
    EditText editText;
    TextView weatherTextView;
    ImageView backgroundImageView;


    public class DownloadTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {

                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();
                while (data != -1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();

                }
                return result;

            }catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) { //here we convert the data into JSON Object
            super.onPostExecute(s);  //s == result from the backgroud method


            try {
                JSONObject jsonObject = new JSONObject(s);
                String weatherInfo = jsonObject.getString("weather");
                String temperatureInfo = "["+ jsonObject.getString("main")+"]";
                Log.i("Weather content", weatherInfo);
                Log.i("Temperature", temperatureInfo);
                JSONArray arr = new JSONArray(weatherInfo);

                String message = "";

                for(int i = 0; i< arr.length(); i++) {
                    JSONObject jsonPart = arr.getJSONObject(i);

                    String main = jsonPart.getString(("main"));
                    String description = jsonPart.getString("description");




                    if (!main.equals("") && !description.equals("")) {
                        message = main + ": " + description + "\r\n";
                        switch(main) {
                            case "Clear":
                                backgroundImageView.setImageResource(R.drawable.sunny1);
                                break;
                            case "Rain":
                                backgroundImageView.setImageResource(R.drawable.rainy);
                                break;
                            case "Snow":
                                backgroundImageView.setImageResource(R.drawable.snowy);
                                break;
                            case "Mist":
                                backgroundImageView.setImageResource(R.drawable.misty);
                                break;
                            case "Storm":
                                backgroundImageView.setImageResource(R.drawable.stormy);
                                break;
                            case "Clouds":
                                backgroundImageView.setImageResource(R.drawable.cloudy);
                                break;
                            default:
                                backgroundImageView.setImageResource(R.drawable.wallpaper);
                        }

                    }

                }
                JSONArray arr2 = new JSONArray((temperatureInfo));
                String message2 = "";
                for(int i = 0; i< arr.length(); i++) {
                    JSONObject jsonPart = arr2.getJSONObject(i);

                    String temperature = jsonPart.getString(("temp"));


                    if (!temperature.equals("")) {
                        message2 = "\r\n" + temperature + "°C" + "\r\n";
                        Log.i("Temperature", temperature);
                    }

                }
                if (!message.equals("")) {
                    weatherTextView.setText(message + message2);

                } else {
                    Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_SHORT).show();
                }


            } catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        weatherTextView = findViewById(R.id.weatherTextView);
        backgroundImageView = findViewById(R.id.backgroundImageView);


    }
    public void getWeather(View view) {
        try {
            DownloadTask task = new DownloadTask();
            String encodedCityName = URLEncoder.encode(editText.getText().toString(), "UTF-8");             //turn spaces into %20 - url style
            task.execute("https://openweathermap.org/data/2.5/weather?q=" + encodedCityName + "&appid=b6907d289e10d714a6e88b30761fae22");
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(editText.getWindowToken(), 0);//so that clicking the button hides the virtual keyboar
        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_SHORT).show();

        }

    }

}

