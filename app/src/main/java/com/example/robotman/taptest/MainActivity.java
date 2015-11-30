//package com.example.robotman.taptest;
//
//import android.app.Activity;
//import android.app.ActivityManager;
//import android.app.admin.DevicePolicyManager;
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.Intent;
//import android.content.ServiceConnection;
//import android.os.Bundle;
//import android.os.IBinder;
//import android.util.Log;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.CompoundButton;
//import android.widget.Spinner;
//import android.widget.Toast;
//import android.widget.ToggleButton;
//
//import java.util.HashMap;
//
//
//public class MainActivity extends Activity implements CompoundButton.OnCheckedChangeListener,AdapterView.OnItemSelectedListener
//{
//    private static final int ADMIN_INTENT = 15;
//    private static final String description = "Sample Administrator description";
//
//    private DevicePolicyManager mDevicePolicyManager;
//    private ComponentName mComponentName;
//    private HashMap<Integer,Boolean> switchStatus;
//    private MyService myService;
//    //Components
//    private  ToggleButton tbtnBar;
//    private  ToggleButton tbtnOrientation;
//    private  ToggleButton tbtnDown;
//    private ToggleButton tbtnService;
//    private ToggleButton tbtnAdmin;
//    private ToggleButton tbtnShake;
//    private Spinner spinner;
//
//    private boolean isBound;
//    private Intent serviceIntent;
//    private SaveSettings settings;
//    @Override
//
//    protected void onCreate(Bundle savedInstanceState)
//    {
//        super.onCreate(savedInstanceState);
////        setContentView(R.layout.activity_main);
//        setContentView(R.layout.options);
//        initAdmin();
//
//        initButtons();
//        retrieveSavedSettings();
//
//       initHashMap();
//       initSpinner();
//
//        if(settings.isService())
//        {
//            tbtnBar.setEnabled(true);
//            tbtnDown.setEnabled(true);
//            tbtnOrientation.setEnabled(true);
//            spinner.setEnabled(true);
//            tbtnShake.setEnabled(true);
//
//        }
//        else
//        {
//            tbtnBar.setEnabled(false);
//            tbtnDown.setEnabled(false);
//            tbtnOrientation.setEnabled(false);
//            spinner.setEnabled(false);
//            tbtnShake.setEnabled(false);
//        }
//        if(!settings.isBarOn())
//        {
//            tbtnDown.setEnabled(false);
//            tbtnOrientation.setEnabled(false);
//            spinner.setEnabled(false);
//        }
//
//        attachListeners();
//        if(isMyServiceRunning(MyService.class))
//        {
//            tbtnBar.setEnabled(true);
//            tbtnDown.setEnabled(true);
//            tbtnOrientation.setEnabled(true);
//            spinner.setEnabled(true);
//            tbtnService.setChecked(true);
//            tbtnShake.setEnabled(true);
//            //Bind the activity if the service is already running
//            if(!isBound)
//            {
//                //Bind the service
//                isBound = bindService(new Intent(this, MyService.class), mConnection,0);
//            }
//        }
//        else
//        {
//            tbtnService.setChecked(false);
//        }
//
//    }
//    public void initAdmin()
//    {
//        mDevicePolicyManager = (DevicePolicyManager) getSystemService(
//                Context.DEVICE_POLICY_SERVICE);
//        mDevicePolicyManager = (DevicePolicyManager) getSystemService(
//                Context.DEVICE_POLICY_SERVICE);
//        mComponentName = new ComponentName(this, MyAdminReceiver.class);
//    }
//    public void initButtons()
//    {
//        //Attach buttons to layout
//        tbtnAdmin = (ToggleButton)findViewById(R.id.tbtnAdmin);
//        tbtnService = (ToggleButton)findViewById(R.id.tbtnService);
//        tbtnBar = (ToggleButton)findViewById(R.id.tbtnBar);
//        tbtnOrientation = (ToggleButton)findViewById(R.id.tbtnOrientation);
//        tbtnDown = (ToggleButton)findViewById(R.id.tbtnDown);
//        tbtnShake=(ToggleButton)findViewById(R.id.tbtnShake);
//    }
//    public void retrieveSavedSettings()
//    {
//        //Retrieve saved settings
//        settings= new SaveSettings(this.getApplicationContext());
//        tbtnAdmin.setChecked(settings.isAdmin());
//        tbtnService.setChecked(settings.isService());
//        tbtnBar.setChecked(settings.isBarOn());
//        tbtnOrientation.setChecked(settings.isBarLeft());
//        tbtnDown.setChecked(settings.isSensorsOn());
//        tbtnShake.setChecked(settings.isShakeOn());
//
//    }
//    public void initHashMap()
//    {
//        //add to hashmap
//        switchStatus= new HashMap<>();
//        switchStatus.put(R.id.tbtnAdmin, settings.isAdmin());
//        switchStatus.put(R.id.tbtnService, settings.isService());
//        switchStatus.put(R.id.tbtnBar, settings.isBarOn());
//        switchStatus.put(R.id.tbtnOrientation, tbtnOrientation.isChecked());
//        switchStatus.put(R.id.tbtnDown, tbtnDown.isChecked());
//        switchStatus.put(R.id.tbtnShake,tbtnShake.isChecked());
//
//    }
//
//    public void initSpinner()
//    {
//        //Setup spinner
//        spinner = (Spinner) findViewById(R.id.spinner);
//        // Create an ArrayAdapter using the string array and a default spinner layout
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
//                R.array.color_choices, android.R.layout.simple_spinner_item);
//        // Specify the layout to use when the list of choices appears
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        // Apply the adapter to the spinner
//        spinner.setAdapter(adapter);
//        //Set the position of the spinner
//        spinner.setSelection(settings.getSpinnerPos());
//    }
//    public void attachListeners()
//    {
//        //Attach listeners
//        tbtnAdmin.setOnCheckedChangeListener(this);
//        tbtnService.setOnCheckedChangeListener(this);
//        tbtnBar.setOnCheckedChangeListener(this);
//        tbtnOrientation.setOnCheckedChangeListener(this);
//        tbtnDown.setOnCheckedChangeListener(this);
//        tbtnShake.setOnCheckedChangeListener(this);
//        //Set the listener
//        spinner.setOnItemSelectedListener(this);
//    }
//    protected void onResume() {
//        super.onResume();
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data)
//    {
//        //Result of the registering as admin intent
//        if (requestCode == ADMIN_INTENT)
//        {
//            if (resultCode == RESULT_OK)
//            {
//                Toast.makeText(getApplicationContext(), "Registered As Admin", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(getApplicationContext(), "Failed to register as Admin", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    private ServiceConnection mConnection = new ServiceConnection()
//    {
//        @Override
//        public void onServiceConnected(ComponentName className,
//                                       IBinder service)
//        {
//            // Toast.makeText(MainActivity.this, "onServiceConnected called", Toast.LENGTH_SHORT).show();
//            // We've binded to LocalService, cast the IBinder and get LocalService instance
//            MyService.LocalBinder binder = (MyService.LocalBinder) service;
//            myService = binder.getServiceInstance(); //Get instance of your service!
//            // Toast.makeText(getApplicationContext(), "bound", Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName arg0)
//        {
//            //Toast.makeText(MainActivity.this, "onServiceDisconnected called", Toast.LENGTH_SHORT).show();
//
//        }
//    };
//
//    @Override
//    public void onSaveInstanceState(Bundle savedInstanceState)
//    {
//        super.onSaveInstanceState(savedInstanceState);
//        settings.setAdmin(tbtnAdmin.isChecked());
//        settings.setService(tbtnService.isChecked());
//        settings.setBarOn(tbtnBar.isChecked());
//        settings.setBarLeft(tbtnOrientation.isChecked());
//        settings.setSensorsOn(tbtnDown.isChecked());
//        settings.setShakeOn(tbtnShake.isChecked());
//    }
//
//    @Override
//    public void onDestroy()
//    {
//
//        if(isBound)
//            unbindService(mConnection);
//
//        super.onDestroy();
//    }
//    @Override
//    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
//    {
//
//        switch (buttonView.getId())
//        {
//            case R.id.tbtnAdmin:
//                if(!switchStatus.get(R.id.tbtnAdmin))
//                {
//                    //Enable admin
//                    Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
//                    intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mComponentName);
//                    intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, description);
//                    startActivityForResult(intent, ADMIN_INTENT);
//                    switchStatus.put(R.id.tbtnAdmin,true);
//                }
//                else
//                {
//                    //Disable admin
//                    switchStatus.put(R.id.tbtnAdmin,false);
//                    mDevicePolicyManager.removeActiveAdmin(mComponentName);
//                    Toast.makeText(getApplicationContext(), "Admin registration removed", Toast.LENGTH_SHORT).show();
//                }
//                break;
//
//            case R.id.tbtnService:
//                if(!switchStatus.get(R.id.tbtnService))
//                {
//                    Toast.makeText(getApplicationContext(), "service started", Toast.LENGTH_SHORT).show();
//                    //Start the service
//                    Intent serviceIntent = new Intent(this, MyService.class);
//                    //Add user preferences to the service
//                    serviceIntent.putExtra("panel", tbtnBar.isChecked());
//                    serviceIntent.putExtra("sensor", tbtnDown.isChecked());
//                    //Start the service
//                    startService(serviceIntent);
//
//                    //Bind the service
//                    isBound= bindService(serviceIntent, mConnection,0);
//
//                    switchStatus.put(R.id.tbtnService, true);
//                    //Enable the buttons
//                    tbtnBar.setEnabled(true);
//                    tbtnDown.setEnabled(true);
//                    tbtnShake.setEnabled(true);
//                    tbtnOrientation.setEnabled(true);
//                    spinner.setEnabled(true);
//
//                } else {
//                    Toast.makeText(getApplicationContext(), "service stopped", Toast.LENGTH_SHORT).show();
//
//                    switchStatus.put(R.id.tbtnService, false);
//
//                    //unbind
//                    if(myService!=null)
//                    {
//                        myService.onDestroy();
//                    }
//                    //Stop the service
//                    if(isMyServiceRunning(MyService.class))
//                    {
//                        stopService(new Intent(this, MyService.class));
//                    }
////                    if(isBound)
////                        unbindService(mConnection);
//                    //Disable the buttons
//                    tbtnBar.setEnabled(false);
//                    tbtnDown.setEnabled(false);
//                    tbtnOrientation.setEnabled(false);
//                    spinner.setEnabled(false);
//                    tbtnShake.setEnabled(false);
//
//                }
//                break;
//
//            case R.id.tbtnBar:
//                if(!switchStatus.get(R.id.tbtnBar) )
//                {
//                    //Turn the bar on
//                    if(!myService.getPanel().isCreated())
//                    {
//                        Toast.makeText(getApplicationContext(), "bar on ", Toast.LENGTH_SHORT).show();
//                        switchStatus.put(R.id.tbtnBar, true);
//                        myService.getPanel().createPanel();
//                        tbtnOrientation.setEnabled(true);
//                        spinner.setEnabled(true);
//                    }
//                }
//                else
//                {
//                    //Turn bar off
//                    if(myService.getPanel().isCreated())
//                    {
//                        Toast.makeText(getApplicationContext(), "bar off", Toast.LENGTH_SHORT).show();
//                        switchStatus.put(R.id.tbtnBar, false);
//                        myService.getPanel().destroy();
//                        tbtnOrientation.setEnabled(false);
//                        spinner.setEnabled(false);
//                    }
//                }
//
//                break;
//
//            case R.id.tbtnOrientation:
//                if(!switchStatus.get(R.id.tbtnOrientation) )
//                {
//                    myService.getPanel().setBarOrientation("right");
//                    myService.getPanel().updateVIew();
//                    switchStatus.put(R.id.tbtnOrientation, true);
//                }
//                else
//                {
//                    myService.getPanel().setBarOrientation("left");
//                    switchStatus.put(R.id.tbtnOrientation, false);
//                    myService.getPanel().updateVIew();
//                }
//                break;
//
//            case R.id.tbtnDown:
//
//
//                if(!switchStatus.get(R.id.tbtnDown))
//                {
//                    myService.getSensors().setRotateOn(true);
//                    if(!myService.getSensors().isSensorsOn())
//                    {
//                        myService.getSensors().startSensors();
//                        myService.getSensors().resume();
//                    }
//                    switchStatus.put(R.id.tbtnDown, true);
//                    Toast.makeText(getApplicationContext(), "down if "+switchStatus.get(R.id.tbtnDown), Toast.LENGTH_SHORT).show();
//                }
//                else
//                {
//                    if(!switchStatus.get(R.id.tbtnShake))
//                        myService.getSensors().destroy();
//                    switchStatus.put(R.id.tbtnDown, false);
//                    myService.getSensors().setRotateOn(false);
//                    Toast.makeText(getApplicationContext(), "down else " + switchStatus.get(R.id.tbtnDown), Toast.LENGTH_SHORT).show();
//                }
//                break;
//            case R.id.tbtnShake:
//                if(!switchStatus.get(R.id.tbtnShake) )
//                {
//                    myService.getSensors().setShakeOn(true);
//                    if(!myService.getSensors().isSensorsOn())
//                    {
//                        myService.getSensors().startSensors();
//                        myService.getSensors().resume();
//                    }
//                    switchStatus.put(R.id.tbtnShake, true);
//                }
//                else
//                {
//                    if(!switchStatus.get(R.id.tbtnDown))
//                        myService.getSensors().destroy();
//                    switchStatus.put(R.id.tbtnShake, false);
//                    myService.getSensors().setShakeOn(false);
//                }
//        }
//    }
//
//
//    @Override
//    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
//    {
//        //Set the side bar to the chosen color
//        if(myService != null && myService.getPanel().isCreated())
//        {
//            String color=(String) parent.getItemAtPosition(position);
//            myService.getPanel().setColor(color);
//            settings.setSpinnerPos(position);
//            settings.setColor(color);
//        }
//    }
//
//    @Override
//    public void onNothingSelected(AdapterView<?> parent) {
//
//    }
//    private boolean isMyServiceRunning(Class<?> serviceClass)
//    {
//        //Checks if a service is running
//        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
//            if (serviceClass.getName().equals(service.service.getClassName())) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//}