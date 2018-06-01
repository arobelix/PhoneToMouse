package aaron.phonetomouse;

import android.content.Context;
import android.hardware.SensorEventListener;
import android.hardware.SensorEvent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.view.MenuItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private SensorManager senSensorManager;
    private Sensor accelerometer;
    private long lastUpdate;
    private TextView dataValues;
    private TextView messages;
    private Button rightClick;
    private Button leftClick;
    float benchX = 0;
    float benchY = 0;
    float benchZ = 0;
    float sensitivity = .1f;
    String message = null;
    //Called when activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        messages = (TextView) findViewById(R.id.textView2);
        dataValues = (TextView) findViewById(R.id.textView3);
        leftClick = (Button) findViewById(R.id.button);
        rightClick = (Button) findViewById(R.id.button2);

        rightClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new InternetData().execute();
            }
        });
        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
        @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;
        float x = sensorEvent.values[0];
        float y = sensorEvent.values[1];
        float z = sensorEvent.values[2];

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long curTime = System.currentTimeMillis();
            if(curTime - lastUpdate > 100) {
                lastUpdate = curTime;
                if(Math.abs(x - benchX) > sensitivity || Math.abs(y - benchY) > sensitivity ||Math.abs(z - benchZ) > sensitivity) {
                    dataValues.setText(String.format("%.5f", x - benchX) + "  " + String.format("%.5f", y - benchY) + "  " + String.format("%.5f", z - benchZ));
                    benchX = x;
                    benchY = y;
                    benchZ = z;
                }
            }
        }


            long curTime = System.currentTimeMillis();
            if(curTime - lastUpdate > 100) {
                lastUpdate = curTime;

            }
        }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    protected void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(this);
    }
    protected void onResume() {
        super.onResume();
        senSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private class InternetData extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void...params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL("http://192.168.1.151:80/test");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

               InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader((new InputStreamReader(inputStream)));
                String line;
                while((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if(buffer.length() == 0) {
                    return null;
                }

                message = buffer.toString();
                return message;
            }
            catch(IOException e) {
                    Log.d("e","error1");
                    return null;
                }
            finally {
                if(urlConnection != null) {
                    urlConnection.disconnect();
                }
                if(reader != null) {
                    try {
                        reader.close();
                    }
                    catch(final IOException e) {
                        Log.d("e","error2");
                    }
                }
            }

        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            messages.setText(s);    
        }
    }
}
