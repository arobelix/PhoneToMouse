package aaron.phonetomouse;

import android.content.Context;
import android.hardware.GeomagneticField;
import android.hardware.SensorEventListener;
import android.hardware.SensorEvent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.view.MenuItem;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private SensorManager senSensorManager;
    private Sensor accelerometer;
    private Sensor magnet;
    private long lastUpdate;
    private boolean magOn = false;
    private boolean accelerOn = true;;
    private static final int SHAKE_THRESHOLD = 600;
    private TextView timeValues;
    private TextView dataValues;
    private Button rightClick;
    private Button leftClick;
    private GeomagneticField geo;
    private Location location;

    //Called when activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnet = senSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        geo = new GeomagneticField((float) location.getLatitude(), (float) location.getLongitude(), (float) location.getAltitude(), System.currentTimeMillis());
        senSensorManager.registerListener(this, magnet, SensorManager.SENSOR_DELAY_NORMAL);
        senSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        timeValues = (TextView) findViewById(R.id.textView2);
        dataValues = (TextView) findViewById(R.id.textView3);
        leftClick = (Button) findViewById(R.id.button);
        rightClick = (Button) findViewById(R.id.button2);
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
        leftClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accelerOn = !accelerOn;
                magOn = !magOn;
            }
        });
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;
        float x = sensorEvent.values[0];
        float y = sensorEvent.values[1];
        float z = sensorEvent.values[2];

        if (accelerOn && mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long curTime = System.currentTimeMillis();
            if(curTime - lastUpdate > 100) {
                lastUpdate = curTime;
                dataValues.setText(String.format("%.5f", x) + "  " + String.format("%.5f", y) + "  " + String.format("%.5f", z));

            }
        }

        if (magOn && mySensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            long curTime = System.currentTimeMillis();
            if(curTime - lastUpdate > 100) {
                lastUpdate = curTime;
                dataValues.setText(String.format("%.5f", geo.getDeclination()));

            }
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
        senSensorManager.registerListener(this, magnet, SensorManager.SENSOR_DELAY_NORMAL);
    }
    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    */
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
