package com.example.sureshbabu.capstonetest;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

/*
 * @author : Suresh Babu Jothilingam
 */
/*
   * SensorEventListener class to capture the raw accelerometer data
 */
public class SensorListenerAccelerometer implements SensorEventListener {
    String serverIp;
    ArrayList<String> data;
    Socket socket;
    ObjectOutputStream write;

    SensorListenerAccelerometer(String serverIp) throws Exception{
        this.serverIp = serverIp;
        this.data = new ArrayList<String>();
        this.socket = new Socket(this.serverIp, 6666);
        this.write = new ObjectOutputStream(socket.getOutputStream());
    }


    //code to send data to server
    @Override
    public void onSensorChanged(SensorEvent event) {
        //Log.d("SensorMessage", String.valueOf(event.values[0])+","+ String.valueOf(event.values[1])+","+ String.valueOf(event.values[2]));
        this.data.add(String.valueOf(event.values[0])+","+ String.valueOf(event.values[1])+","+ String.valueOf(event.values[2]));
        //for every 150 instance send it to server
        if(this.data.size() == 150){
            //Log.d("SensorMessageToSend", this.data.get(0)+","+this.data.get(1)+","+this.data.get(2));
            new SendDataToServer(this.serverIp,new ArrayList<String>(this.data), this.write).start();
            this.data.clear();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    //close all the connection at server side
    public void closeOpenConnections() throws Exception{
        new SendStopToServer(this.serverIp,this.data, this.write).start();
    }
}

/*
 * Send a window of data to the server
 */
class SendDataToServer extends Thread{
    String serverIp;
    ArrayList<String> data;
    ObjectOutputStream write;

    SendDataToServer(String serverIp, ArrayList<String> data, ObjectOutputStream write){
        this.serverIp = serverIp;
        this.data = new ArrayList<String>(data);
        this.write = write;
    }
    @Override
    public void run() {
        super.run();
        try{
            this.write.writeObject("data");
            //Log.d("SensorMessageSent", this.data.get(0) + "," + this.data.get(1) + "," + this.data.get(2));
            this.write.writeObject(this.data);
        } catch(Exception e){
            Log.d("SensorError", e.toString());
        }
    }
}

/*
 * Once the user stops the application this class sends stop signal to the server
 */
class SendStopToServer extends Thread{
    String serverIp;
    ObjectOutputStream write;

    SendStopToServer(String serverIp, ArrayList<String> data, ObjectOutputStream write){
        this.serverIp = serverIp;
        this.write = write;
    }
    @Override
    public void run() {
        super.run();
        try{
            this.write.writeObject("stop");
        } catch(Exception e){
            Log.d("SensorError", e.toString());
        }
    }
}
