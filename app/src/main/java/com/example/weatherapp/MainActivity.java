package com.example.weatherapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private String JSON = "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=e4ee20ff6ea4ff694a53790f5083cfef&lang=ru";

    private EditText editTextCity;
    private static TextView textViewWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextCity = findViewById(R.id.editTextCityName);
        textViewWeather = findViewById(R.id.сityName);

    }

    public void onClickGetInfo(View view) {
        String cityname = editTextCity.getText().toString().trim();
        if (!cityname.isEmpty()){
        String url = String.format(JSON,cityname);
        GetWeatherInfo getWeatherInfo = new GetWeatherInfo();
        getWeatherInfo.execute(url);
        }
    }

    public static class  GetWeatherInfo extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {
            URL url = null;
            HttpURLConnection urlConnection = null;
            StringBuilder stringBuilder = new StringBuilder();
            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream is = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(is);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line = bufferedReader.readLine();
                while (line!=null){
                    stringBuilder.append(line);
                    line = bufferedReader.readLine();
                }
                return stringBuilder.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if (urlConnection!=null){
                    urlConnection.disconnect();
                }
            }
            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                String name = jsonObject.getString("name");
                JSONArray weather = jsonObject.getJSONArray("weather");
                JSONObject jsonObjectWeather = weather.getJSONObject(0);
                String desc = jsonObjectWeather.getString("description");
                JSONObject main = jsonObject.getJSONObject("main");
                double orgTemp = main.getDouble("temp");
                double tempC = orgTemp-273.15;
                int temp = (int) tempC;
                String allDescWeather = String.format("%s \nТемпература: %s\nНа улице: %s",name,temp,desc);
                textViewWeather.setText(allDescWeather);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}