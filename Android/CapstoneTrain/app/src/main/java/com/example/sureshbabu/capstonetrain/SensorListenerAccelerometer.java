package com.example.sureshbabu.capstonetrain;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Environment;
import android.util.Log;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * Created by Suresh Babu on 2/13/2016.
 */
public class SensorListenerAccelerometer implements SensorEventListener {
    Long start = System.currentTimeMillis();
    String activityName;
    String fileName;
    File fileDir, file;
    PrintWriter write;
    int count;
    SensorListenerAccelerometer(String activityName){
        try {
            this.count = 0;
            this.activityName = activityName;
            fileDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + activityName);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
            fileName = activityName + start;
            file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + activityName + File.separator + fileName + ".csv");

            if(!file.exists()){
                file.createNewFile();
            }
            write = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            //Log.d("SensorMessage",activityName);

        } catch(Exception e){
            Log.d("SensorError",e.toString());
        }
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        //Log.d("SensorMessage", String.valueOf(event.values[0])+","+ String.valueOf(event.values[1])+","+ String.valueOf(event.values[2]));
        write.println(String.valueOf(event.values[0])+","+ String.valueOf(event.values[1])+","+ String.valueOf(event.values[2]));
        count++;
        //for every 3 seconds write in a new file
        //if((int)(System.currentTimeMillis() - start)/1000 == 3){
            //start = System.currentTimeMillis();
            //to indicate 3 seconds break
            //write.println("#,#,#");
            //Log.d("SensorMessage","SUCCESS");
        //}
        //Log.d("SensorCount",String.valueOf(this.count));
        //for every 150 instance
        if(this.count == 150){
            start = System.currentTimeMillis();
            //to indicate 150 instance break
            write.println("#,#,#");
            Log.d("SensorMessage","Inside 150");
            this.count = 0;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void closePrintWriter(){
        write.close();
    }
}
