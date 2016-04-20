package com.example.sureshbabu.capstonetest;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Locale;
/*
 * @author : Suresh Babu Jothilingam
 */

public class MainActivity extends AppCompatActivity {

    //variables used
    ImageButton connect;
    String server_ip = "129.21.105.107";
    SensorManager sensorManager;
    Sensor sensor;
    boolean isClicked;
    TextToSpeech textToSpeech;
    Vibrator vibrator;
    SensorListenerAccelerometer listener;
    HashMap<String,Double> activities_summary;
    long startTime, endTime;
    TextView walking_summary, sitting_summary, standing_summary;
    PowerManager powerManager;
    WakeLock wakeLock;
    DecimalFormat format;

    //init function to instantiate all the widgets
    void init(){
        try {

            wakeLock.acquire();

            activities_summary.put("standing", 0.0); activities_summary.put("sitting", 0.0); activities_summary.put("walking", 0.0);

            sensorManager = (SensorManager) getSystemService(getApplicationContext().SENSOR_SERVICE);
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    textToSpeech.setLanguage(Locale.US);
                }
            });

            connect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (!isClicked) {

                            isClicked = true;
                            connect.setImageDrawable(getResources().getDrawable(R.drawable.on));

                            new ConnectToServer().execute();

                            textToSpeech.speak("Connected to Server. You have 5 seconds", TextToSpeech.QUEUE_FLUSH, null);

                            new TimerThread().start();
                        } else {

                            connect.setImageDrawable(getResources().getDrawable(R.drawable.off));
                            listener.closeOpenConnections();
                            sensorManager.unregisterListener(listener);
                        }
                    } catch (Exception e) {
                        displayError("ButtonClick",e);
                    }
                }
            });
        }catch (Exception e){
            displayError("SensorInit",e);
        }
    }

    /*
     * Timer class to start data collection in 5 seconds
     */
    class TimerThread extends Thread{
        @Override
        public void run() {
            super.run();
            //to alter view
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int count = 5;
                    while (count > 1) {
                        count--;
                        try {
                            sleep(1500);
                        } catch (Exception e) {
                            displayError("counter",e);
                        }
                    }
                    vibrator.vibrate(500);
                    new RegisterToSensor().execute();
                }

            });
        }
    }


    //Register to Listener
    class RegisterToSensor extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                listener = new SensorListenerAccelerometer(server_ip);

                new RegisterAccelerometerListener(listener).run();

            }catch(Exception e){
                displayError("RegisterToSenor",e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }

    //Server to receive activity
    class Server extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            ServerSocket server = null;
            Socket socket = null;
            ObjectInputStream input = null;
            try {
                Log.d("Server","Server Started");
                server = new ServerSocket(5555);
                socket = server.accept();
                input = new ObjectInputStream(socket.getInputStream());
                String activity = "";

                startTime = System.currentTimeMillis();
                boolean isStart = true;

                while(isClicked){
                    String result = input.readObject().toString();

                    if(result.equals("stop")){
                        isClicked = false;
                        continue;
                    }
                    if (isStart){
                        activity = result;
                        isStart = false;
                        textToSpeech.speak("You are " + result, TextToSpeech.QUEUE_FLUSH, null);
                        publishProgress();
                    }
                    else if(!result.equals(activity) && !result.equals("none")) {

                        endTime = System.currentTimeMillis();
                        activities_summary.put(activity, activities_summary.get(activity) + Double.valueOf(endTime - startTime));
                        //Log.d("Updated", activity + " " + activities_summary.get(activity) + " " + endTime + " " + startTime + " " + (endTime - startTime));
                        startTime = endTime;
                        //update UI
                        publishProgress();

                        textToSpeech.speak("You are " + result, TextToSpeech.QUEUE_FLUSH, null);
                        activity = result;
                        vibrator.vibrate(150);
                    }
                }
                input.close();
                socket.close();
                server.close();

            }catch (Exception e){
                displayError("Server",e);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            sitting_summary.setText(((activities_summary.get("sitting")>=Double.valueOf(60000))? format.format(activities_summary.get("sitting")/60000).toString()+" minutes" : format.format(activities_summary.get("sitting")/1000).toString()+" seconds"));
            standing_summary.setText(((activities_summary.get("standing") >= Double.valueOf(60000)) ? format.format(activities_summary.get("standing") / 60000).toString()+ " minutes" : format.format(activities_summary.get("standing") / 1000).toString() +" seconds"));
            walking_summary.setText(((activities_summary.get("walking") >= Double.valueOf(60000)) ? format.format(activities_summary.get("walking") / 60000).toString()+ " minutes" : format.format(activities_summary.get("walking") / 1000).toString() +" seconds"));
        }

    }

    //Register and run the accelerometer in a thread
    class RegisterAccelerometerListener extends Thread{
        SensorListenerAccelerometer listener;
        RegisterAccelerometerListener(SensorListenerAccelerometer listener){
            this.listener = listener;
        }
        @Override
        public void run() {
            super.run();
            //sensorManager.registerListener(this.listener, sensor, SensorManager.SENSOR_DELAY_NORMAL);

            sensorManager.registerListener(this.listener, sensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    /*
    * Set up initial connection with server
     */
    class ConnectToServer extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... params) {
            try {
                Socket socket = new Socket(server_ip, 5566);
                ObjectOutputStream write = new ObjectOutputStream(socket.getOutputStream());
                write.writeObject("Hello");
                write.close();
                socket.close();
            } catch(Exception e){
                displayError("ErrorConnectToServer", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            new Server().execute();
        }
    }


    public void displayError(String from,Exception e){
        Log.d(from, e.toString());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connect = (ImageButton)findViewById(R.id.imageButton_connect);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        isClicked = false;

        activities_summary = new HashMap<String,Double>();

        walking_summary = (TextView) findViewById(R.id.walking_summary);
        sitting_summary = (TextView) findViewById(R.id.sitting_summary);
        standing_summary = (TextView) findViewById(R.id.standing_summary);

        powerManager = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Lock");

        format = new DecimalFormat("#.##");
        init();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //wakeLock.release();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //wakeLock.release();
    }
}
