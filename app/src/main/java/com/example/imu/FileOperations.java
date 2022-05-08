package com.example.imu;

import android.app.Activity;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class FileOperations {

    public static void writetofile(Activity av, String fname) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String dir = av.getExternalFilesDir(null).toString();
                    File path = new File(dir);
                    if (!path.exists()) {
                        path.mkdirs();
                    }
                    File file = new File(dir, fname+"-comp_tilt.txt");
                    BufferedWriter outfile = new BufferedWriter(new FileWriter(file,false));
                    for (int i = 0; i < Constants.tilt_angle_degrees.size(); i++) {
                        //outfile.append(Constants.accx.get(i)+","+Constants.accy.get(i)+","+Constants.accz.get(i));
                        //outfile.append(Constants.cumligyrox.get(i)+","+Constants.cumligyroy.get(i));
                        outfile.append("" + Constants.tilt_angle_degrees.get(i));
                        outfile.newLine();
                    }
                    outfile.flush();
                    outfile.close();

                    file = new File(dir, fname+"-comp_tilt_calculation.txt");
                    outfile = new BufferedWriter(new FileWriter(file,false));
                    for (int i = 0; i < Constants.calculation_log.size(); i++) {
                        //outfile.append(Constants.accx.get(i)+","+Constants.accy.get(i)+","+Constants.accz.get(i));
                        //outfile.append(Constants.cumligyrox.get(i)+","+Constants.cumligyroy.get(i));
                        //outfile.append("" + Constants.tilt_angle_degrees.get(i));
                        outfile.append(Constants.calculation_log.get(i));
                        outfile.newLine();
                    }
                    outfile.flush();
                    outfile.close();

                    file = new File(dir, fname+"-grav.txt");
                    outfile = new BufferedWriter(new FileWriter(file,false));
                    for (int i = 0; i < Constants.gravx.size(); i++) {
                        outfile.append(Constants.gravx.get(i)+","+Constants.gravy.get(i)+","+Constants.gravz.get(i));
                        outfile.newLine();
                    }
                    outfile.flush();
                    outfile.close();
                } catch(Exception e) {
                    Log.e("ex", "writeRecToDisk");
                    Log.e("ex", e.getMessage());
                }
            }
        }).run();
    }
}
