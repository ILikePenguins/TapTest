package com.example.robotman.taptest;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;


public class GestureListener extends GestureDetector.SimpleOnGestureListener
{
    static String currentGestureDetected;

//    @Override
//    public boolean onDown(MotionEvent e) {
//        currentGestureDetected="down";
//
//        return true;
//    }

    @Override
    public void onShowPress(MotionEvent e) {
        currentGestureDetected=e.toString();

    }

//    @Override
//    public boolean onSingleTapUp(MotionEvent e) {
//        //  Toast.makeText(getApplicationContext(), "single", Toast.LENGTH_SHORT).show();
//        currentGestureDetected="tap";
//        return true;
//    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        //Toast.makeText(getApplicationContext(), "scroll", Toast.LENGTH_SHORT).show();
        currentGestureDetected="scroll";
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
       // Toast.makeText(getApplicationContext(), "long", Toast.LENGTH_SHORT).show();
        currentGestureDetected="long";

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
       // Toast.makeText(getApplicationContext(), "fling", Toast.LENGTH_SHORT).show();
        currentGestureDetected="fling";
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e)
    {
       // Toast.makeText(getApplicationContext(), "tap tap", Toast.LENGTH_SHORT).show();
        currentGestureDetected="tap tap";
;
        return true;
    }
}
