package com.example.robotman.taptest;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.HashMap;


public class OffActivity extends Activity implements CompoundButton.OnCheckedChangeListener
{
    private static final int ADMIN_INTENT = 15;
    private static final String description = "Sample Administrator description";

    private DevicePolicyManager mDevicePolicyManager;
    private ComponentName mComponentName;
    private HashMap<Integer,Boolean> switchStatus;
    private MyService myService;

    //Components
    private  ToggleButton tbtnDown;
    private ToggleButton tbtnService;
    private ToggleButton tbtnAdmin;
    private ToggleButton tbtnShake;
    private EditText editThresh;
    private EditText editAngle;
    private RelativeLayout layout;



    private boolean isBound;
    private SaveSettings settings;
    @Override

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.off);
        initAdmin();

        initLayout();
        initButtons();
        initEditText();
        retrieveSavedSettings();

        initHashMap();

        if(settings.isService())
        {
            tbtnDown.setEnabled(true);
            tbtnShake.setEnabled(true);
            if(tbtnDown.isChecked())
                editAngle.setEnabled(true);
            if(tbtnShake.isChecked())
                editThresh.setEnabled(true);

        }
        else
        {
            tbtnDown.setEnabled(false);
            tbtnShake.setEnabled(false);
            editThresh.setEnabled(false);
            editAngle.setEnabled(false);

        }

        attachListeners();
        if(isMyServiceRunning(MyService.class))
        {
            tbtnDown.setEnabled(true);
            tbtnService.setChecked(true);
            tbtnShake.setEnabled(true);
            if(tbtnDown.isChecked())
                editAngle.setEnabled(true);
            if(tbtnShake.isChecked())
                editThresh.setEnabled(true);

            //Bind the activity if the service is already running
            if(!isBound)
            {
                //Bind the service
                isBound = bindService(new Intent(this, MyService.class), mConnection,0);
            }
        }
        else
        {
            tbtnService.setChecked(false);
        }

    }

    public void initAdmin()
    {
        mDevicePolicyManager = (DevicePolicyManager) getSystemService(
                Context.DEVICE_POLICY_SERVICE);
        mDevicePolicyManager = (DevicePolicyManager) getSystemService(
                Context.DEVICE_POLICY_SERVICE);
        mComponentName = new ComponentName(this, MyAdminReceiver.class);
    }

    public void initLayout()
    {
        layout = (RelativeLayout)findViewById(R.id.relativeLayout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) v.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });
    }

    public void initButtons()
    {
        //Attach buttons to layout
        tbtnAdmin = (ToggleButton)findViewById(R.id.tbtnAdmin);
        tbtnService = (ToggleButton)findViewById(R.id.tbtnService);
        tbtnDown = (ToggleButton)findViewById(R.id.tbtnDown);
        tbtnShake=(ToggleButton)findViewById(R.id.tbtnShake);
    }

    public void initEditText()
    {
        editThresh =(EditText)findViewById(R.id.editThresh);
        final  int MIN_THRESHOLD=500;
        final  int MAX_THRESHOLD=1500;
        editThresh.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (!hasFocus)
                {
                    if (isNumeric(editThresh.getText().toString()))
                    {
                        int value = formatValue(Integer.parseInt(editThresh.getText().toString()),MIN_THRESHOLD,MAX_THRESHOLD );
                        editThresh.setText(value + "");
                        myService.getSensors().setShakeThreshold(value);
                        Toast.makeText(getApplicationContext(), "Threshold set to: " + value, Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        editThresh.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    int value = formatValue(Integer.parseInt(editThresh.getText().toString()),MIN_THRESHOLD,MAX_THRESHOLD );
                    editThresh.setText(value + "");
                    myService.getSensors().setShakeThreshold(value);
                    Toast.makeText(getApplicationContext(), "Threshold set to: " + value, Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        //Angle edit text
        editAngle=(EditText)findViewById(R.id.editAngle);
        final  int MIN_ANGLE = 25;
        final  int MAX_ANGLE = 90;
        editAngle.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (!hasFocus)
                {
                    if (isNumeric(editAngle.getText().toString()))
                    {
                        int value = formatValue(Integer.parseInt(editAngle.getText().toString()),MIN_ANGLE,MAX_ANGLE );
                        editAngle.setText(value + "");
                        Toast.makeText(getApplicationContext(), "Threshold set to: " + value, Toast.LENGTH_SHORT).show();
                        value*=-1;
                        myService.getSensors().setAngle(value);
                    }
                }
            }
        });

        editAngle.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    int value = formatValue(Integer.parseInt(editAngle.getText().toString()), MIN_ANGLE, MAX_ANGLE);
                    editAngle.setText(value + "");
                    Toast.makeText(getApplicationContext(), "Threshold set to: " + value, Toast.LENGTH_SHORT).show();
                    value*=-1;
                    myService.getSensors().setAngle(value);
                }
                return false;
            }
        });
    }

    public void retrieveSavedSettings()
    {
        //Retrieve saved settings
        settings= new SaveSettings(this.getApplicationContext());
        tbtnAdmin.setChecked(settings.isAdmin());
        tbtnService.setChecked(settings.isService());
        tbtnDown.setChecked(settings.isSensorsOn());
        tbtnShake.setChecked(settings.isShakeOn());
        editThresh.setText(settings.getShakeThreshold() + "");
        editAngle.setText(settings.getAngleThreshold()*-1+"");

    }

    public void initHashMap()
    {
        //add to hashmap
        switchStatus= new HashMap<>();
        switchStatus.put(R.id.tbtnAdmin, settings.isAdmin());
        switchStatus.put(R.id.tbtnService, settings.isService());
        switchStatus.put(R.id.tbtnBar, settings.isBarOn());
        switchStatus.put(R.id.tbtnDown, tbtnDown.isChecked());
        switchStatus.put(R.id.tbtnShake, tbtnShake.isChecked());
    }

    public void attachListeners()
    {
        //Attach listeners
        tbtnAdmin.setOnCheckedChangeListener(this);
        tbtnService.setOnCheckedChangeListener(this);
        tbtnDown.setOnCheckedChangeListener(this);
        tbtnShake.setOnCheckedChangeListener(this);
    }

    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //Result of the registering as admin intent
        if (requestCode == ADMIN_INTENT)
        {
            if (resultCode == RESULT_OK)
            {
                Toast.makeText(getApplicationContext(), "Registered As Admin", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Failed to register as Admin", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private ServiceConnection mConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service)
        {
            // Toast.makeText(MainActivity.this, "onServiceConnected called", Toast.LENGTH_SHORT).show();
            // We've binded to LocalService, cast the IBinder and get LocalService instance
            MyService.LocalBinder binder = (MyService.LocalBinder) service;
            myService = binder.getServiceInstance(); //Get instance of your service!
            // Toast.makeText(getApplicationContext(), "bound", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0)
        {
            //Toast.makeText(MainActivity.this, "onServiceDisconnected called", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        settings.setAdmin(tbtnAdmin.isChecked());
        settings.setService(tbtnService.isChecked());
        settings.setSensorsOn(tbtnDown.isChecked());
        settings.setShakeOn(tbtnShake.isChecked());
        if(isNumeric(editThresh.getText().toString()))
            settings.setShakeThreshold(Integer.parseInt(editThresh.getText().toString()) );
        if(isNumeric(editAngle.getText().toString()))
            settings.setAngleThreshold((Integer.parseInt(editAngle.getText().toString()))*-1);
    }

    @Override
    public void onDestroy()
    {

        if(isBound)
            unbindService(mConnection);

        super.onDestroy();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {

        switch (buttonView.getId())
        {
            case R.id.tbtnAdmin:
                if(!switchStatus.get(R.id.tbtnAdmin))
                {
                    //Enable admin
                    Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                    intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mComponentName);
                    intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, description);
                    startActivityForResult(intent, ADMIN_INTENT);
                    switchStatus.put(R.id.tbtnAdmin,true);
                }
                else
                {
                    //Disable admin
                    switchStatus.put(R.id.tbtnAdmin,false);
                    mDevicePolicyManager.removeActiveAdmin(mComponentName);
                    Toast.makeText(getApplicationContext(), "Admin registration removed", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.tbtnService:
                if(!switchStatus.get(R.id.tbtnService))
                {
                    Toast.makeText(getApplicationContext(), "service started", Toast.LENGTH_SHORT).show();
                    //Start the service
                    Intent serviceIntent = new Intent(this, MyService.class);
                    //Add user preferences to the service
                    serviceIntent.putExtra("sensor", tbtnDown.isChecked());
                    //Start the service
                    startService(serviceIntent);

                    //Bind the service
                    isBound= bindService(serviceIntent, mConnection,0);

                    switchStatus.put(R.id.tbtnService, true);
                    //Enable the buttons
                    tbtnDown.setEnabled(true);
                    tbtnShake.setEnabled(true);
                    if(tbtnDown.isChecked())
                        editAngle.setEnabled(true);
                    if(tbtnShake.isChecked())
                        editThresh.setEnabled(true);

                } else {
                    Toast.makeText(getApplicationContext(), "service stopped", Toast.LENGTH_SHORT).show();

                    switchStatus.put(R.id.tbtnService, false);

                    //unbind
                    if(myService!=null)
                    {
                        myService.onDestroy();
                    }
                    //Stop the service
                    if(isMyServiceRunning(MyService.class))
                    {
                        stopService(new Intent(this, MyService.class));
                    }
                    //Disable the buttons
                    tbtnDown.setEnabled(false);
                    tbtnShake.setEnabled(false);
                    editThresh.setEnabled(false);
                    editAngle.setEnabled(false);

                }
                break;


            case R.id.tbtnDown:
                if(!switchStatus.get(R.id.tbtnDown))
                {
                    myService.getSensors().setRotateOn(true);
                    if(!myService.getSensors().isSensorsOn())
                    {
                        myService.getSensors().startSensors();
                        myService.getSensors().resume();
                    }
                    switchStatus.put(R.id.tbtnDown, true);
                    editAngle.setEnabled(true);

                }
                else
                {
                    if(!switchStatus.get(R.id.tbtnShake))
                        myService.getSensors().destroy();
                    switchStatus.put(R.id.tbtnDown, false);
                    myService.getSensors().setRotateOn(false);
                    editAngle.setEnabled(false);

                }
                break;
            case R.id.tbtnShake:
                if(!switchStatus.get(R.id.tbtnShake) )
                {
                    myService.getSensors().setShakeOn(true);
                    if(!myService.getSensors().isSensorsOn())
                    {
                        myService.getSensors().startSensors();
                        myService.getSensors().resume();
                    }
                    editThresh.setEnabled(true);
                    switchStatus.put(R.id.tbtnShake, true);
                }
                else
                {
                    if(!switchStatus.get(R.id.tbtnDown))
                        myService.getSensors().destroy();
                    switchStatus.put(R.id.tbtnShake, false);
                    myService.getSensors().setShakeOn(false);
                    editThresh.setEnabled(false);
                }
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass)
    {
        //Checks if a service is running
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    public static int formatValue(int val,int min,int max )
    {

            if (val < min)
                val = min;
            else if (val > max)
                val = max;
            return val;
    }
    public static boolean isNumeric(String s)
    {
        return s.matches("[-+]?\\d*\\.?\\d+");
    }

}