package com.example.robotman.taptest;


import android.graphics.Color;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Spinner;

import java.util.ArrayList;

public class Panel implements View.OnTouchListener, View.OnClickListener
{
    private static String TAG = "Panel";
    private WindowManager mWindowManager;
    // linear layout will use to detect touch event
    private LinearLayout touchLayout;
    private MyService service;
    private WindowManager.LayoutParams mParams;
    private int startWidth;
    private LockScreen lock;

    private ArrayList<Button> buttons;
    private boolean isCreated;
    private boolean left;
    private SaveSettings settings;

    public Panel(MyService service, LockScreen lock)
    {
        this.service = service;
        this.lock = lock;
        buttons = new ArrayList<Button>();
        settings=new SaveSettings(service.getApplicationContext());
    }

    public void createPanel()
    {
        touchLayout = new LinearLayout(service);
        //set layout params
        LayoutParams lp = new LayoutParams(40, LayoutParams.MATCH_PARENT);
        touchLayout.setLayoutParams(lp);
        touchLayout.requestLayout();
        touchLayout.setOrientation(LinearLayout.VERTICAL);

        // set on touch listener
        touchLayout.setOnTouchListener(this);

        // fetch window manager object
        mWindowManager = (WindowManager) service.getSystemService(service.WINDOW_SERVICE);
        // set layout parameter of window manager
        startWidth = 40;
        if(!settings.isBarLeft())
        {
            left=true;
        }
        else {
            left=false;
        }
        setSize(startWidth);
        Log.i(TAG, "add View");
        addButton("Lock", 0);
        addButton("Reboot", 1);
        //setBarOrientation("left");
        mWindowManager.addView(touchLayout, mParams);
        //Set Color
        if(settings.getColor().equals("")|| settings.getColor()==null)
            touchLayout.setBackgroundColor(Color.RED);
        else {
            Log.i(TAG, "colorrrr "+ settings.getColor());
            setColor(settings.getColor());
        }

        isCreated=true;
    }
    public void setColor(String color)
    {
        Log.i(TAG, "colorrrrrrrrrrr "+color);

        switch (color)
        {
            case "Black":
                color="#000000";
                break;
            case "White":
                color="#ffffff";
                break;
            case "Red":
                color="#ffff0000";
                break;
            case "Blue":
                color="#0000ff";
                break;
            case "Translucent":
                color="#50FFFFFF";
                break;
            case "Transparent":
                color="#00FFFFFF";
                break;
        }
        touchLayout.setBackgroundColor(Color.parseColor(color));
        updateVIew();
    }


    public void destroy()
    {
        Log.i(TAG, "destroy got called");
        if (isCreated()&& mWindowManager != null && touchLayout != null)
        {
            Log.i(TAG, "destroyyyyyyyed");
            mWindowManager.removeView(touchLayout);
            isCreated=false;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        switch (event.getAction() & MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN:
                if (mParams.width == startWidth)
                {
                    //Increase panel size and show buttons
                    showButtons();
                    updateSize(250);
                }
                else
                {
                    //Decrease panel size and hide buttons
                    hideButtons();
                    updateSize(startWidth);
                }
                break;
        }

        return false;
    }

    public void setSize(int width)
    {
        mParams = new WindowManager.LayoutParams(
                //30, // width of layout 30 px
                width,
                WindowManager.LayoutParams.MATCH_PARENT, // height is equal to full screen
                WindowManager.LayoutParams.TYPE_PHONE, // Type Phone, These are non-application windows providing user interaction with the phone (in particular incoming calls).
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, // this window won't ever get key input focus
                PixelFormat.TRANSLUCENT);
        if(left)
            setBarOrientation("left");
        else
            setBarOrientation("right");
    }

    public void hideButtons()
    {
        for(Button button:buttons)
        {
            button.setVisibility(View.GONE);
        }
    }

    public void showButtons()
    {
        for(Button button:buttons)
        {
            button.setVisibility(View.VISIBLE);
        }
    }
    public void setBarOrientation(String orientation)
    {
        switch(orientation)
        {
            case "left":
                mParams.gravity = Gravity.LEFT | Gravity.TOP;
                left=true;
                break;
            case "right":
                mParams.gravity = Gravity.RIGHT | Gravity.TOP;
                left=false;
                break;
        }
    }

    public void updateSize(int width)
    {
        setSize(width);
        mWindowManager.updateViewLayout(touchLayout,mParams);

    }
    public void updateVIew()
    {

        mWindowManager.updateViewLayout(touchLayout,mParams);
    }
    public void addButton(String text,int id)
    {
        Button button = new Button(service);
        button.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        button.setText(text);
        button.setId(id);
        button.setVisibility(View.GONE);
        button.setOnClickListener(this);
        touchLayout.addView(button);
        buttons.add(button);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case 0:
                Log.i(TAG, "off clicckeded");
                lock.lockScreen();
                break;
            case 1:
                Log.i(TAG, "reboot clicckeded");
                lock.reboot();
            case 2:
                Log.i(TAG,"shutdown");
        }
    }

    public boolean isCreated() {
        return isCreated;
    }
}
