package com.example.robotman.taptest;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class Sensors implements SensorEventListener
{
    private static String TAG="Sensors";
    //Variables needed to record orientation
    float[] gData = new float[3];           // Gravity or accelerometer
    float[] mData = new float[3];           // Magnetometer
    float[] orientation = new float[3];
    float[] Rmat = new float[9];
    float[] R2 = new float[9];
    float[] Imat = new float[9];
    boolean haveGrav = false;
    boolean haveAccel = false;
    boolean haveMag = false;
    private int roll;

    //Sensors
    private SensorManager sensorManager;
    private Sensor gsensor;
    private Sensor asensor;
    private Sensor msensor;
    private LockScreen lock;
    private MyService service;

    public  Sensors(MyService service, LockScreen lock)
    {
        this.lock=lock;
        this.service=service;
        startSensors();
    }

    public void startSensors()
    {
        sensorManager = (SensorManager)service.getSystemService(Context.SENSOR_SERVICE);
        //get the types of sensors that will be used
        gsensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        asensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        msensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        float[] data;
        switch( event.sensor.getType() )
        {
            case Sensor.TYPE_GRAVITY:
                gData[0] = event.values[0];
                gData[1] = event.values[1];
                gData[2] = event.values[2];
                haveGrav = true;
                break;
            case Sensor.TYPE_ACCELEROMETER:
                if (haveGrav) break;    // don't need it, we have better
                gData[0] = event.values[0];
                gData[1] = event.values[1];
                gData[2] = event.values[2];
                haveAccel = true;
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                mData[0] = event.values[0];
                mData[1] = event.values[1];
                mData[2] = event.values[2];
                haveMag = true;
                break;
            default:
                return;
        }

        if ((haveGrav || haveAccel) && haveMag)
        {
            SensorManager.getRotationMatrix(Rmat, Imat, gData, mData);
            SensorManager.remapCoordinateSystem(Rmat,
                    SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, R2);

            // Orientation
            SensorManager.getOrientation(R2, orientation);
            //float incl = SensorManager.getInclination(Imat);

           // int pitch=(int)(Math.toDegrees(orientation[1]));
            roll = (int)(Math.toDegrees(orientation[2]));
            //int yaw= (int)(Math.toDegrees(orientation[0]));
            if(roll <-40)
            {
                lock.lockScreen();
            }

            // Log.d(TAG, "roll: " + (int) (Math.toDegrees(orientation[2])));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void destroy()
    {
        roll=0;
        if(sensorManager!=null)
        {
            //unregister all the listeners
            sensorManager.unregisterListener(this, gsensor);
            sensorManager.unregisterListener(this, msensor);
            sensorManager.unregisterListener(this, asensor);
        }
    }

    public void resume()
    {
        //attach listeners
        sensorManager.registerListener(this, msensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, gsensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, asensor, SensorManager.SENSOR_DELAY_GAME);

        //clear previous values, prevents screen from turning off right away
        roll=0;
        gData = new float[3];           // Gravity or accelerometer
        mData = new float[3];           // Magnetometer
        orientation = new float[3];
        Rmat = new float[9];
        R2 = new float[9];
        Imat = new float[9];
    }
}
