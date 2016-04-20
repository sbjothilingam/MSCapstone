package com.example.sureshbabu.capstonetrain;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    ImageButton imageButton_connect;
    String server_ip = "129.21.107.130";
    String activityName;
    SensorManager sensorManager;
    Sensor sensor;

    PowerManager powerManager;
    PowerManager.WakeLock wakeLock;

    public void init(){

        wakeLock.acquire();

        sensorManager = (SensorManager) getSystemService(getApplicationContext().SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        imageButton_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new ConnectToServer().execute();
                } catch (Exception e){
                    displayError(e);
                }
            }
        });

    }


    //start collecting data for the activity and store it in a file
    class Server extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... params) {
            try {
                Log.d("MessageServer", "Server Started");
                ServerSocket serverSocket = new ServerSocket(5555);
                SensorListenerAccelerometer listener = null;
                Socket socket = serverSocket.accept();
                BufferedReader read = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                //toggleButton(false);
                while(true) {
                    String message = read.readLine();
                    //Log.d("MessageServer",message);
                    if (message.equals("activity")) {
                        message = read.readLine();
                        activityName = message;
                        Log.d("MessageServer", activityName);
                        listener = new SensorListenerAccelerometer(activityName);
                        new RegisterAccelerometerListener(listener).run();
                    } else {
                        //toggleButton(true);
                        listener.closePrintWriter();
                        sensorManager.unregisterListener(listener);
                    }
                }
            } catch (Exception e){
                Log.d("ErrorServer", e.toString());
            }
            return null;
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
            sensorManager.registerListener(this.listener, sensor, SensorManager.SENSOR_DELAY_FASTEST);
            //sensorManager.registerListener(this.listener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    class ConnectToServer extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... params) {
            try {
                Socket socket = new Socket(server_ip, 6666);
                PrintWriter write = new PrintWriter(socket.getOutputStream(), true);
                write.println("Hello");
                //start Server
                //new Server().start();
                write.close();
                socket.close();
            } catch(Exception e){
                Log.d("ErrorConnectToServer", e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            new Server().execute();
        }
    }


    public void displayError(Exception e){
        Log.d("Error", e.toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageButton_connect = (ImageButton) findViewById(R.id.imageButton_connect);

        powerManager = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Lock");

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
