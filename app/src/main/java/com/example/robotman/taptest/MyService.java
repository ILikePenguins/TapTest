package com.example.robotman.taptest;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;

public class MyService extends Service //implements View.OnTouchListener
{
    private String TAG = this.getClass().getSimpleName();
    private BroadcastReceiver screenoffReceiver;
    private LockScreen lock;
    private Sensors sensors;
    private Panel panel;
    private final IBinder mBinder = new LocalBinder();
    private boolean registered;
   // private static boolean barOn=true;
   // private static boolean tiltOn=true;
    private SaveSettings settings;

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
        settings= new SaveSettings(getApplicationContext());
       // panel.createPanel();
    }


//    @Override
//        public void onStart(Intent intent, int startid)
//        {
//            Toast.makeText(this, "on start", Toast.LENGTH_SHORT).show();
//            //sensors.resume();
//            detectScreenState();
//            panel.createPanel();
//            Bundle extras = intent.getExtras();
//            if(extras!=null)
//            {
//                if (intent.getBooleanExtra("panel", false)||barOn) {
//                    //Panel is set to true
//                    Toast.makeText(this, "creating panel :", Toast.LENGTH_SHORT).show();
//                    panel.createPanel();
//                    barOn=true;
//                }
//                if (intent.getBooleanExtra("sensor", false)||tiltOn) {
//                    //Sensor set to true
//                    sensors.resume();
//                    tiltOn=true;
//                }
//            }
//        }

//    @Override
//    public void onNewIntent(Intent newIntent) {
//        this.setIntent(newIntent);
//
//        // Now getIntent() returns the updated Intent
//        isNextWeek = getIntent().getBooleanExtra(IS_NEXT_WEEK, false);
//    }
    @Override
    public void onTaskRemoved(Intent rootIntent)
    {
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(this, 0, restartServiceIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartServicePendingIntent);

        super.onTaskRemoved(rootIntent);
    }
     @Override
     public int onStartCommand(Intent intent, int flags, int startId)
    {
        if(!registered)
            detectScreenState();
        //Toast.makeText(this, "commmaaannddd", Toast.LENGTH_SHORT).show();

        Bundle extras = intent.getExtras();
        if(extras!=null)
        {
            if (intent.getBooleanExtra("panel", false)||settings.isBarOn()) {
                //Panel is set to true
                //Toast.makeText(this, "creating panel", Toast.LENGTH_SHORT).show();
                panel.createPanel();
            }
            if (intent.getBooleanExtra("sensor", false)||settings.isSensorsOn()||settings.isShakeOn()) {
                //Sensor set to true
                sensors.resume();
            }
        }
        return START_STICKY; //START_NOT_STICKY
    }

    @Override
    public void onDestroy()
    {
        //Toast.makeText(this, "boooooooooop", Toast.LENGTH_LONG).show();
        panel.destroy();
        sensors.destroy();
        if(registered) {
            this.unregisterReceiver(this.screenoffReceiver);
            registered=false;
        }

        super.onDestroy();
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
        registered=true;
    }

    public Panel getPanel() {
        return panel;
    }

    public Sensors getSensors() {
        return sensors;
    }
}