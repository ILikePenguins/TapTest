package com.example.robotman.taptest;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.HashMap;


public class MainActivity extends Activity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener,AdapterView.OnItemSelectedListener {

    private static final int ADMIN_INTENT = 15;
    private static final String description = "Sample Administrator description";
    private DevicePolicyManager mDevicePolicyManager;
    private ComponentName mComponentName;
    private HashMap<Integer,Boolean> switchStatus;
    private MyService myService;
    private  ToggleButton tbtnBar;
    private  ToggleButton tbtnOrientation;
    private  ToggleButton tbtnDown;
    private boolean isBound;
    private Intent serviceIntent;
    @Override

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        setContentView(R.layout.options);
        mDevicePolicyManager = (DevicePolicyManager) getSystemService(
                Context.DEVICE_POLICY_SERVICE);
        mDevicePolicyManager = (DevicePolicyManager) getSystemService(
                Context.DEVICE_POLICY_SERVICE);
        mComponentName = new ComponentName(this, MyAdminReceiver.class);

        //Button listeners
        ToggleButton tbtnAdmin = (ToggleButton)findViewById(R.id.tbtnAdmin);
        ToggleButton tbtnService = (ToggleButton)findViewById(R.id.tbtnService);
        tbtnBar = (ToggleButton)findViewById(R.id.tbtnBar);
        tbtnOrientation = (ToggleButton)findViewById(R.id.tbtnOrientation);
        tbtnDown = (ToggleButton)findViewById(R.id.tbtnDown);

        tbtnAdmin.setOnCheckedChangeListener(this);
        tbtnService.setOnCheckedChangeListener(this);
        tbtnBar.setOnCheckedChangeListener(this);
        tbtnOrientation.setOnCheckedChangeListener(this);
        tbtnDown.setOnCheckedChangeListener(this);

        //add to hashmap
        switchStatus= new HashMap<>();

        switchStatus.put(R.id.tbtnAdmin, tbtnAdmin.isChecked());
        switchStatus.put(R.id.tbtnService, tbtnService.isChecked());
        switchStatus.put(R.id.tbtnBar, tbtnBar.isChecked());
        switchStatus.put(R.id.tbtnOrientation, tbtnOrientation.isChecked());
        switchStatus.put(R.id.tbtnDown, tbtnDown.isChecked());

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.color_choices, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        //Set the listener
        spinner.setOnItemSelectedListener(this);
//        //button listeners
//        Button btnEnableAdmin = (Button) findViewById(R.id.btnEnable);
//        Button btnDisableAdmin = (Button) findViewById(R.id.btnDisable);
//        Button btnLock = (Button) findViewById(R.id.btnLock);
//        Button btnStart = (Button) findViewById(R.id.btnStart);
//        Button btnStop = (Button) findViewById(R.id.btnStop);
//        btnEnableAdmin.setOnClickListener(this);
//        btnDisableAdmin.setOnClickListener(this);
//        btnLock.setOnClickListener(this);
//        btnStart.setOnClickListener(this);
//        btnStop.setOnClickListener(this);


    }
    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.btnEnable:
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mComponentName);
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,description);
                startActivityForResult(intent, ADMIN_INTENT);
                break;

            case R.id.btnDisable:
                mDevicePolicyManager.removeActiveAdmin(mComponentName);
                Toast.makeText(getApplicationContext(), "Admin registration removed", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btnLock:
                boolean isAdmin = mDevicePolicyManager.isAdminActive(mComponentName);
                if (isAdmin) {
                    mDevicePolicyManager.lockNow();
                }else{
                    Toast.makeText(getApplicationContext(), "Not Registered as admin", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btnStart:
                //Toast.makeText(getApplicationContext(), "clicked", Toast.LENGTH_LONG).show();
                startService(new Intent(this, MyService.class));
                break;

            case R.id.btnStop:
                //Toast.makeText(getApplicationContext(), "stop clicked", Toast.LENGTH_LONG).show();
                stopService(new Intent(this, MyService.class));
                break;
        }
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
            Toast.makeText(MainActivity.this, "onServiceConnected called", Toast.LENGTH_SHORT).show();
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
                    serviceIntent.putExtra("panel", tbtnBar.isChecked());
                    serviceIntent.putExtra("sensor", tbtnDown.isChecked());
                    //Start the service
                    startService(serviceIntent);

                    //Bind the service
                    isBound= bindService(serviceIntent, mConnection,0);

                    switchStatus.put(R.id.tbtnService, true);
                    //Enable the buttons
                    tbtnBar.setEnabled(true);
                    tbtnDown.setEnabled(true);
                    tbtnOrientation.setEnabled(true);

                } else {
                    Toast.makeText(getApplicationContext(), "service stopped", Toast.LENGTH_SHORT).show();

                    switchStatus.put(R.id.tbtnService, false);
                    //Stop the service
                    myService.onDestroy();
                    stopService(new Intent(this, MyService.class));
//                    if(isBound)
//                        unbindService(mConnection);
                    //Disable the buttons
                    tbtnBar.setEnabled(false);
                    tbtnDown.setEnabled(false);
                    tbtnOrientation.setEnabled(false);

                }
                break;
            case R.id.tbtnBar:
                if(!switchStatus.get(R.id.tbtnBar) )
                {
                    //Turn the bar on
                    if(!myService.getPanel().isCreated())
                    {

                        switchStatus.put(R.id.tbtnBar, true);
                        myService.getPanel().createPanel();
                        tbtnOrientation.setEnabled(true);
                    }
                }
                else
                {
                    //Turn bar off
                    if(myService.getPanel().isCreated())
                    {
                        switchStatus.put(R.id.tbtnBar, false);
                        myService.getPanel().destroy();
                        tbtnOrientation.setEnabled(false);
                    }
                }
                break;
            case R.id.tbtnOrientation:
                if(!switchStatus.get(R.id.tbtnOrientation) )
                {
                    myService.getPanel().setBarOrientation("right");
                    myService.getPanel().updateVIew();
                    switchStatus.put(R.id.tbtnOrientation, true);
                }
                else
                {
                    myService.getPanel().setBarOrientation("left");
                    switchStatus.put(R.id.tbtnOrientation, false);
                    myService.getPanel().updateVIew();
                }
                break;
            case R.id.tbtnDown:
                if(!switchStatus.get(R.id.tbtnDown) )
                {
                    myService.getSensors().startSensors();
                    myService.getSensors().resume();
                    switchStatus.put(R.id.tbtnDown, true);
                }
                else {
                    myService.getSensors().destroy();

                    switchStatus.put(R.id.tbtnDown, false);
                }
                break;
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        //Set the side bar to the chosen color
        if(myService!=null && myService.getPanel().isCreated())
            myService.getPanel().setColor((String)parent.getItemAtPosition(position));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


}
