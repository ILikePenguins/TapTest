package com.example.robotman.taptest;

import android.content.Context;
import android.content.SharedPreferences;


public class SaveSettings
{
    public static final String PREFS_NAME = "MyPrefsFile";
    private SharedPreferences settings;
    private boolean barOn;
    private boolean barLeft=true;
    private boolean sensorsOn;
    private boolean admin;
    private boolean service;
    private String color;
    private int spinnerPos;
    private boolean shakeOn;

    public SaveSettings(Context context)
    {
        settings = context.getSharedPreferences(PREFS_NAME, 0);
        color="";
    }



    public void saveBoolean(String key, boolean value)
    {
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);

        // Commit the edits!
        editor.commit();
    }
    public void saveString(String key, String value)
    {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);

        // Commit the edits!
        editor.commit();
    }

    public void saveInt(String key, int value)
    {
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, value);

        // Commit the edits!
        editor.commit();
    }
    public boolean isShakeOn()
    {
        if(settings.contains("shakeOn"))
        {
            return settings.getBoolean("shakeOn",false);
        }
        return shakeOn;
    }

    public void setShakeOn(boolean shakeOn) {
        this.shakeOn = shakeOn;
        saveBoolean("shakeOn",shakeOn);

    }
    public void setBarOn(boolean barOn) {
        this.barOn = barOn;
        saveBoolean("barOn",barOn);
    }

    public void setSensorsOn(boolean sensorsOn) {
        this.sensorsOn = sensorsOn;
        saveBoolean("sensorsOn",sensorsOn);
    }

    public void setBarLeft(boolean barLeft) {
        this.barLeft = barLeft;
        saveBoolean("barLeft",barLeft);
    }

    public boolean isSensorsOn()
    {
       if(settings.contains("sensorsOn"))
       {
           return settings.getBoolean("sensorsOn",false);
       }
        return sensorsOn;
    }

    public boolean isBarOn() {
        if(settings.contains("barOn"))
        {
            return settings.getBoolean("barOn",false);
        }
        return barOn;
    }

    public boolean isBarLeft() {
        if(settings.contains("barLeft"))
        {
            return settings.getBoolean("barLeft",false);
        }
        return barLeft;
    }

    public boolean isAdmin() {
        if(settings.contains("admin"))
        {
            return settings.getBoolean("admin",false);
        }
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
        saveBoolean("admin",admin);
    }

    public boolean isService() {
        if(settings.contains("service"))
        {
            return settings.getBoolean("service",false);
        }
        return service;
    }

    public void setService(boolean service) {
        this.service = service;
        saveBoolean("service",service);
    }

    public String getColor() {
        if(settings.contains("color"))
        {
            return settings.getString("color", "");
        }
        return color;
    }

    public void setColor(String color) {
        this.color = color;
        saveString("color",color);
    }

    public int getSpinnerPos() {
        if(settings.contains("spinnerPos"))
        {
            return settings.getInt("spinnerPos", 0);
        }
        return spinnerPos;
    }

    public void setSpinnerPos(int spinnerPos) {
        this.spinnerPos = spinnerPos;
        saveInt("spinnerPos",spinnerPos);
    }
}
