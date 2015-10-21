package com.example.robotman.taptest;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.widget.Toast;

import java.io.IOException;

public class LockScreen
{
    //admin
    private DevicePolicyManager mDevicePolicyManager;
    private ComponentName mComponentName;
    private MyService service;
    public LockScreen(MyService service)
    {
        this.service=service;
        //Register admin privileges
        mDevicePolicyManager = (DevicePolicyManager)service.getSystemService(
                Context.DEVICE_POLICY_SERVICE);
        mDevicePolicyManager = (DevicePolicyManager)service.getSystemService(
                Context.DEVICE_POLICY_SERVICE);
        mComponentName = new ComponentName(service, MyAdminReceiver.class);
    }

    public boolean isAdmin()
    {
        //Checks if admin privileges are invoked
        return mDevicePolicyManager.isAdminActive(mComponentName);
    }
    public void lockScreen()
    {
        if(isAdmin())
        {
            //lock the screen
            mDevicePolicyManager.lockNow();
        }
        else
        {
            //Admin privileges are not invoked, inform user
            Toast.makeText(service.getApplicationContext(), "Not Registered as admin", Toast.LENGTH_SHORT).show();
        }
    }

    public void reboot()
    {
        try {
            Runtime.getRuntime().exec(new String[]{"/system/bin/su","-c","reboot now"});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void unlockScreen()
    {
//       Window window = this.getWindow();
//        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
//        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
//        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }
}
