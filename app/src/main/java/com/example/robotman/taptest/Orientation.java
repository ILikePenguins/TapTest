package com.example.robotman.taptest;


import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

public class Orientation
{
    private MyService service;

    public Orientation(MyService service)
    {
        this.service=service;
    }

    public int getOrientation()
    {
        Display display = ((WindowManager) service.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int orientation = display.getRotation();

        if (orientation == 1) {
            /* The device is rotated to the left. */
            Log.v("Left", "Rotated Left");
        } else if (orientation == 3) {
            /* The device is rotated to the right. */
            Log.v("Right", "Rotated Right");
        }
        else if(orientation == 2)
        {
            Log.v("2", "Rotated ");
        }
        else if(orientation == 4)
        {
            Log.v("4", "Rotated ");
        }

        return orientation;
    }
}
