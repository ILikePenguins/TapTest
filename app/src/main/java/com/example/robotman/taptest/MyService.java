package com.example.robotman.taptest;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

public class MyService extends Service //implements View.OnTouchListener
{
    private String TAG = this.getClass().getSimpleName();
    private BroadcastReceiver screenoffReceiver;
    private LockScreen lock;
    private Sensors sensors;
    private Panel panel;
    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder
    {
        public MyService getServiceInstance(){
            return MyService.this;
        }
    }
    @Override
    public IBinder onBind(Intent intent)
    {
        return mBinder;
    }

    @Override
    public void onCreate()
    {
        //Toast.makeText(this, "on create", Toast.LENGTH_LONG).show();
        detectScreenState();
        lock= new LockScreen(this);
        sensors= new Sensors(this, lock);
        panel = new Panel(this,lock);
       // panel.createPanel();
    }

    public void detectScreenState()
    {
        //Detects if the screen is on or off
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        screenoffReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF))
                {
                    //Toast.makeText(MyService.this, "screen off", Toast.LENGTH_LONG).show();

                    sensors.destroy();
                }

                else if(intent.getAction().equals(Intent.ACTION_SCREEN_ON))
                {
                   // Toast.makeText(MyService.this, "screen on", Toast.LENGTH_LONG).show();
                    sensors.resume();
                }
                return;
            }
        };
        registerReceiver(screenoffReceiver, filter);
    }

    @Override
    public void onDestroy()
    {
        //Toast.makeText(this, "boooooooooop", Toast.LENGTH_LONG).show();
        panel.destroy();
        sensors.destroy();
        this.unregisterReceiver(this.screenoffReceiver);

        super.onDestroy();
    }

    @Override
    public void onStart(Intent intent, int startid)
    {
        Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
        //sensors.resume();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        detectScreenState();
       // lock= new LockScreen(this);
//        panel.createPanel();
        Bundle extras = intent.getExtras();
        if(extras!=null)
        {
            if (intent.getBooleanExtra("panel", false)) {
                //Panel is set to true
                Toast.makeText(this, "creating panel :", Toast.LENGTH_SHORT).show();
                panel.createPanel();
            }
            if (intent.getBooleanExtra("sensor", false)) {
                //Sensor set to true
                sensors.resume();
            }
        }

        //Do what you need in onStartCommand when service has been started
        return START_NOT_STICKY;
    }

    public Panel getPanel() {
        return panel;
    }

    public Sensors getSensors() {
        return sensors;
    }
}