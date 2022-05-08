package com.example.imu;

import android.widget.Button;

import java.util.ArrayList;

public class Constants {
    static Button startButton;
    static Button stopButton;
    static boolean start=false;
    static ArrayList<Float> accx;
    static ArrayList<Float> accy;
    static ArrayList<Float> cumligyrox;
    static ArrayList<Float> cumligyroy;
    static ArrayList<Float> accz;
    static ArrayList<Float> gravx;
    static ArrayList<Float> gravy;
    static ArrayList<Float> gravz;
    static float dt = (float) 0.05;
    static ArrayList<Float> tilt_angle_degrees;
    static ArrayList<Float> tilt_angle_radians;
    static ArrayList<String> calculation_log;
}
