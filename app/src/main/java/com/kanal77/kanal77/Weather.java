package com.kanal77.kanal77;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kanal77.Volley.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Weather extends AppCompatActivity {

    Context context;

    //Getting weather informations
    static String Kanal77_openweathermap_API_key = "4e10680de9753eecac3a1bfed1d13059";

    //API call for 20 macedonian cities
    /*      ID	City
        785842	Skopje
        792578	Bitola
        786735	Prilep
        785387	Struga
        787487	Ohrid
        785380	Strumica
        785482	Shtip
        790295	Gostivar
        787716	Negotino
        789541	Kavadarci
        788886	Kumanovo
        789527	Kicevo
        789403	Kochani
        791606	Debar
        791559	Delchevo
        792656	Berovo
        788961	Krushevo
        785092	Teovo
        789091	Kratovo
        791542	Demir Kapija
    */
    static String Openweathermap_API_URL = "http://api.openweathermap.org/data/2.5/group?id=785842,792578,786735,785387,787487,785380,785482,790295,787716,789541,788886,789527,789403,791606,791559,792656,788961,785092,789091,791542&units=metric&appid=" + Kanal77_openweathermap_API_key;

    ArrayList<CityWeather> weather = new ArrayList<>();
    Spinner citySpinner;
    List<String> cities = new ArrayList<String>();

    TextView weatherDesc;
    TextView temperature;
    ImageView weatherImage;
    ProgressBar progressBar;

    String savedResponse;

    long lastDataDownloadTime;
    long currentTime;

    long weatherCheckInterval = 30*60*1000; //30 minutes in milliseconds

    SharedPreferences mPrefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wheater);

        context = this;
        mPrefs = PreferenceManager.getDefaultSharedPreferences(Weather.this);

        weatherDesc = (TextView)findViewById(R.id.weather_desc);
        temperature = (TextView)findViewById(R.id.temperature);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimaryDark), android.graphics.PorterDuff.Mode.MULTIPLY);
        weatherImage = (ImageView) findViewById(R.id.weather_image);
        //Deafult rounded weatherImage
        weatherImage.setImageBitmap(getRoundedCornerBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.sunny)));



        //Initializing city spinner list
        citySpinner = (Spinner) findViewById(R.id.city_spinner);

        savedResponse = mPrefs.getString("SAVED_RESPONSE", null);

        lastDataDownloadTime = mPrefs.getLong("LAST_DATA_DOWNLOAD_TIME", 0);
        currentTime = System.currentTimeMillis();

        compareTime(weatherCheckInterval, lastDataDownloadTime, currentTime);


    }

    private void compareTime(long checkInterval, long lastDataDownloadTime, long weatherCheckInterval) {
        if(currentTime-weatherCheckInterval >= lastDataDownloadTime ){
            //Get weather information because last value is very old
            downloadWeatherData(Openweathermap_API_URL);
        }
        else {
            try{
                JSONObject oldWeatherData = new JSONObject(savedResponse);
                processResponse(oldWeatherData);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            catch (NullPointerException e){
                e.printStackTrace();
                weatherDesc.setText(getResources().getString(R.string.reading_saved_weather_info_error));
            }
        }
    }

    private void downloadWeatherData(String openweathermap_api_url) {

        JsonObjectRequest req = new JsonObjectRequest(openweathermap_api_url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d("Response ",response.toString());

                        processResponse(response);


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                weatherDesc.setText(getResources().getString(R.string.getting_weather_info_error));
            }


        });

        //Increasing volley post timeout to 10 seconds
        req.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(req);

    }

    private void processResponse(JSONObject response) {

        //Save response value and response time
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString("SAVED_RESPONSE", response.toString());
        editor.putLong("LAST_DATA_DOWNLOAD_TIME", System.currentTimeMillis());
        editor.apply();

        int city_id;
        String city_name;
        int weather_id;
        String weather_main;
        String weather_desc;
        int temperature;

        CityWeather cityWeather = null;

        try {
            JSONArray weather_conditions = response.getJSONArray("list");

            for(int i=0; i<weather_conditions.length(); i++){

                JSONObject city_json = weather_conditions.getJSONObject(i);

                //Get City info
                city_id = city_json.getInt("id");
                city_name = city_json.getString("name");
                cities.add(city_name);

                //Get Weather info
                JSONArray weather_array = city_json.getJSONArray("weather");
                JSONObject weather_json = weather_array.getJSONObject(0);

                weather_id = weather_json.getInt("id");
                weather_main = weather_json.getString("main");
                weather_desc = weather_json.getString("description");

                //Get Temperature info
                JSONObject temperature_json = city_json.getJSONObject("main");
                temperature = temperature_json.getInt("temp");

                //Create CityWeather object and add it to weather ArrayList
                cityWeather = new CityWeather(city_id, city_name, weather_id, weather_main, weather_desc, temperature);

                weather.add(cityWeather);
            }

            addItemsToCitySpinner();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void addItemsToCitySpinner() {

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.weather_spinner_item, cities);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.weather_spinner_dropdown_item);
        progressBar.setVisibility(View.INVISIBLE);

        // attaching data adapter to spinner
        citySpinner.setAdapter(dataAdapter);

        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCity = citySpinner.getItemAtPosition(position).toString();
                weatherDesc.setText(weather.get(position).getWeather_main() + ", " + weather.get(position).getWeather_desc());
                temperature.setText(Integer.toString(weather.get(position).getTemperature())+ (char) 0x00B0 );

                setWeatherBackground(weather.get(position).getWeather_id());


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                weatherDesc.setText(getString(R.string.getting_weather_info));
            }
        });

    }

    private void setWeatherBackground(int weatherID) {
        //Set weather picture based on the main weather id
        if(weatherID/100 == 2){
            //Weather is Thunderstorm
            weatherImage.setImageBitmap(getRoundedCornerBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.storm)));
        }
        else if(weatherID/100==3){
            //Weather is Drizzle
            weatherImage.setImageBitmap(getRoundedCornerBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.drizzle)));
        }
        else if(weatherID/100==5){
            //Weather is Rain
            weatherImage.setImageBitmap(getRoundedCornerBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.rainy)));
        }
        else if(weatherID/100==6){
            //Weather is Snow
            weatherImage.setImageBitmap(getRoundedCornerBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.snow)));
        }
        else if(weatherID/100==7){
            //Weather is Atmosphere
            weatherImage.setImageBitmap(getRoundedCornerBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.atmosphere)));
        }
        else if(weatherID/100==8){
            if(weatherID==800){
                //Weather is Clear
                weatherImage.setImageBitmap(getRoundedCornerBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.sunny)));
            }
            else{
                //Weather is Clouds
                weatherImage.setImageBitmap(getRoundedCornerBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.cloudy)));
            }
        }
        else if(weatherID/100==9){
            //Weather is Additional 9xx or Extreme 90x
            weatherImage.setImageBitmap(getRoundedCornerBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.additional)));
        }
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = 100;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
