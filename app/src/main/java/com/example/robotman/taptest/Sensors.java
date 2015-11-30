package com.example.robotman.taptest;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

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
    private int angle=-40;
    //Sensors
    private SensorManager sensorManager;
    private Sensor gsensor;
    private Sensor asensor;
    private Sensor msensor;
    private LockScreen lock;
    private MyService service;

    //Shake
    private int shakeThreshold = 800;
    private float last_x;
    private float last_y;
    private float last_z;
    private long lastUpdate;
    private float speed;

    private boolean shakeOn;
    private boolean rotateOn;
    private boolean sensorsOn;

    private SaveSettings settings;

    public  Sensors(MyService service, LockScreen lock)
    {
        this.lock=lock;
        this.service=service;
        settings=new SaveSettings(service.getApplicationContext());
        //Retrieve saved settings
        shakeThreshold=settings.getShakeThreshold();
        angle=settings.getAngleThreshold();
        shakeOn=settings.isShakeOn();
        rotateOn=settings.isSensorsOn();

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
                detectShake();

                break;
            case Sensor.TYPE_ACCELEROMETER:
                if (haveGrav) break;    // don't need it, we have better
                gData[0] = event.values[0];
                gData[1] = event.values[1];
                gData[2] = event.values[2];
                haveAccel = true;
                detectShake();
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

        if ( rotateOn&&((haveGrav || haveAccel) && haveMag) )
        {
            SensorManager.getRotationMatrix(Rmat, Imat, gData, mData);
            SensorManager.remapCoordinateSystem(Rmat,
                    SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, R2);

            // Orientation
            SensorManager.getOrientation(R2, orientation);
            //float incl = SensorManager.getInclination(Imat);

           // int pitch=(int)(Math.toDegrees(orientation[1]));
            roll = (int)(Math.toDegrees(orientation[2]));
            int yaw= (int)(Math.toDegrees(orientation[0]));
            if(roll <angle)
            {
                lock.lockScreen();
            }

             //Log.d(TAG, "roll: " + (int) (Math.toDegrees(orientation[2])));
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
        sensorsOn=false;
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

        speed=0;
        sensorsOn=true;
    }

    public void detectShake()
    {
        if(shakeOn)
        {
            long curTime = System.currentTimeMillis();
            // only allow one update every 100ms.
            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                speed = Math.abs(gData[0] + gData[1] + gData[2] - last_x - last_y - last_z) / diffTime * 10000;


                if (speed > shakeThreshold) {
                    lock.lockScreen();
                   // Log.d("sensor", "shake detected w/ speed: " + speed);
                }
                last_x = gData[0];
                last_y = gData[1];
                last_z = gData[2];
            }
        }
    }

    public void setShakeThreshold(int shakeThreshold)
    {
        this.shakeThreshold = shakeThreshold;

    }

    public void setShakeOn(boolean shakeOn) {
        this.shakeOn = shakeOn;
    }

    public void setRotateOn(boolean rotateOn) {
        this.rotateOn = rotateOn;
    }

    public boolean isSensorsOn() {
        return sensorsOn;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }
}
