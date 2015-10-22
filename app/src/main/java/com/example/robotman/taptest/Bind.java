package com.example.robotman.taptest;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * Created by Robotman on 22-Oct-15.
 */
public class Bind
{
    private  MyService myService;

    private ServiceConnection mConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service)
        {
            // We've binded to LocalService, cast the IBinder and get LocalService instance
            MyService.LocalBinder binder = (MyService.LocalBinder) service;
            myService = binder.getServiceInstance(); //Get instance of your service!
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0)
        {
            //Toast.makeText(MainActivity.this, "onServiceDisconnected called", Toast.LENGTH_SHORT).show();
        }
    };

    public MyService getMyService() {
        return myService;
    }

    public ServiceConnection getmConnection() {
        return mConnection;
    }
}
