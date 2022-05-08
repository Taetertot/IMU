package com.example.imu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor gravitySensor;
    private TextView textView;
    private Sensor MagnetometerSensor;
    private Sensor accelerometerSensor;
    private Sensor gyroSensor;
    private LineChart lineChart;
    private int grantResults[];
    List<Entry> lineDataX;
    List<Entry> lineDataY;
    List<Entry> lineDataZ;
    int counter=0;
    int lim=500;
    Activity av;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        av = this;

        // get permissions
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        onRequestPermissionsResult(1,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},grantResults);



        // ui logic
        Constants.startButton = (Button)findViewById(R.id.button);
        Constants.stopButton = (Button)findViewById(R.id.button2);
        lineChart = (LineChart)findViewById(R.id.linechart);
        textView = (TextView)findViewById(R.id.textView);

        Constants.startButton.setEnabled(true);
        Constants.stopButton.setEnabled(false);

        // defining sensors
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER_UNCALIBRATED);
        gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED);
        MagnetometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        //sensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        //sensorManager.registerListener(this, MagnetometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyroSensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);

        // on click listeners
        Constants.startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.accx=new ArrayList<>();
                Constants.accy=new ArrayList<>();
                Constants.cumligyrox=new ArrayList<>();
                Constants.cumligyroy=new ArrayList<>();
                Constants.accz=new ArrayList<>();
                Constants.gravx=new ArrayList<>();
                Constants.gravy=new ArrayList<>();
                Constants.gravz=new ArrayList<>();
                Constants.startButton.setEnabled(false);
                Constants.stopButton.setEnabled(true);
                Constants.tilt_angle_degrees=new ArrayList<>();
                Constants.tilt_angle_degrees.add( (float) 0.0);
                Constants.tilt_angle_radians=new ArrayList<>();
                Constants.tilt_angle_radians.add( (float) 0.0);
                Constants.calculation_log=new ArrayList<>();

                lineDataX=new ArrayList<>();
                lineDataY=new ArrayList<>();
                lineDataZ=new ArrayList<>();
                counter=0;
                Constants.start=true;
            }
        });
        Constants.stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.startButton.setEnabled(true);
                Constants.stopButton.setEnabled(false);
                Constants.start=false;
                String fname = System.currentTimeMillis()+"";
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(fname);
                    }
                });
                FileOperations.writetofile(av,fname);
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (Constants.start) {
            if (sensorEvent.sensor.equals(accelerometerSensor)) {
                Constants.accx.add(sensorEvent.values[0]);
                Constants.accy.add(sensorEvent.values[1]);
                Constants.accz.add(sensorEvent.values[2]);

                Constants.calculation_log.add("accel " + sensorEvent.values[0] + ", " + sensorEvent.values[1] + ", '" + sensorEvent.values[2]);

            } else if ( sensorEvent.sensor.equals(gyroSensor)) {
                //if (Constants.cumligyrox.size()==0){
                    Constants.cumligyrox.add(sensorEvent.values[0]);
                    Constants.cumligyroy.add(sensorEvent.values[1]);

                    Constants.calculation_log.add("gyro " + sensorEvent.values[0] + ", " + sensorEvent.values[1]);

                /*}
                else{
                    Constants.cumligyrox.add(sensorEvent.values[0]+Constants.cumligyrox.get(Constants.cumligyrox.size()-1));
                    Constants.cumligyroy.add(sensorEvent.values[1]+Constants.cumligyroy.get(Constants.cumligyroy.size()-1));
                }*/
                //graphing logic
                //graphData(sensorEvent.values);
            }
           /* else {
                Constants.gravx.add(sensorEvent.values[0]);
                Constants.gravy.add(sensorEvent.values[1]);
                Constants.gravz.add(sensorEvent.values[2]);
            }
            */


            //angle = .98 * (angle + gyrData*dt) + .02 * accel

            float accel_tilt_radians;

            if(Constants.accx.size() == 0 || Constants.accx.get(Constants.accx.size() - 1) == 0) {
                    accel_tilt_radians = 0;
            } else {

                float accel_x = Constants.accx.get(Constants.accx.size() - 1);
                float accel_y = Constants.accy.get(Constants.accy.size() - 1);
                float accel_z = Constants.accz.get(Constants.accz.size() - 1);

                accel_tilt_radians = accel_z / (float) Math.pow( accel_x*accel_x + accel_y*accel_y + accel_z*accel_z, .5);
                accel_tilt_radians = (float) Math.acos(accel_tilt_radians);
            }

            float gyro_tilt_radians;

            if(Constants.cumligyrox.size() == 0) {
                    gyro_tilt_radians = 0;
            } else {
                 float cumliX = Constants.cumligyrox.get(Constants.cumligyrox.size() - 1);
                 float cumliY = Constants.cumligyroy.get(Constants.cumligyroy.size() - 1);

                gyro_tilt_radians = (float) Math.pow( cumliX*cumliX + cumliY*cumliY, .5 );
            }

            float angle_radians = (float).98 * (Constants.tilt_angle_radians.get(Constants.tilt_angle_radians.size() - 1) + gyro_tilt_radians*Constants.dt) + (float).02 * accel_tilt_radians;


            float angle_degrees = (float) Math.toDegrees(angle_radians);

            Constants.tilt_angle_radians.add(angle_radians);
            Constants.tilt_angle_degrees.add(angle_degrees);

            Constants.calculation_log.add("Calculation " + "accel_tilt = " + accel_tilt_radians + "; gyro_til = " + gyro_tilt_radians + "; gyro_tilt_times_dt = " + (gyro_tilt_radians*Constants.dt) + "; previous = " + Constants.tilt_angle_radians.get(Constants.tilt_angle_radians.size() - 1) + "; final = " + angle_degrees);

            graphDataOne(angle_degrees);




            //Log.e("log",String.format("%s %.2f %.2f %.2f",sensorEvent.sensor.getName(),sensorEvent.values[0],sensorEvent.values[1],sensorEvent.values[2]));
            //Log.e("log",String.format("%s %.2f %.2f","cumliGyro",cumliGyroX,cumliGyroY));
        }
    }

    public void graphDataOne(float value) {
        lineDataX.add(new Entry(counter, value));

        if(lineDataX.size() > lim) {
                lineDataX.remove(0);
        }
        counter+=1;

        LineDataSet data1 = new LineDataSet(lineDataX, "tilt");
        data1.setDrawCircles(false);

        data1.setColor(((MainActivity)this).getResources().getColor(R.color.red));
        List<ILineDataSet> data = new ArrayList<>();
        data.add(data1);

        LineData lineData = new LineData(data);
        lineChart.setData(lineData);
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();

    }
    public void graphData(float[] values) {
        lineDataX.add(new Entry(counter,values[0]));
        lineDataY.add(new Entry(counter,values[1]));
        lineDataZ.add(new Entry(counter,values[2]));
        if (lineDataX.size()>lim) {
            lineDataX.remove(0);
            lineDataY.remove(0);
            lineDataZ.remove(0);
        }
        counter+=1;

        LineDataSet data1 = new LineDataSet(lineDataX, "x");
        LineDataSet data2 = new LineDataSet(lineDataY, "y");
        LineDataSet data3 = new LineDataSet(lineDataZ, "z");
        data1.setDrawCircles(false);
        data2.setDrawCircles(false);
        data3.setDrawCircles(false);
        data1.setColor(((MainActivity)this).getResources().getColor(R.color.red));
        data2.setColor(((MainActivity)this).getResources().getColor(R.color.green));
        data3.setColor(((MainActivity)this).getResources().getColor(R.color.blue));
        List<ILineDataSet> data = new ArrayList<>();
        data.add(data1);
        data.add(data2);
        data.add(data3);

        LineData lineData = new LineData(data);
        lineChart.setData(lineData);
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }

    protected void onResume() {
        super.onResume();
        //sensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, gyroSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}