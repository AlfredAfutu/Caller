package com.ticket.gemroc.caller;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;

import java.io.File;
import java.io.IOException;

/**
 * Created by cted on 5/31/15.
 */
public class MainActivity extends Activity implements View.OnClickListener, AdapterView.OnItemSelectedListener{
    private String TAG = getClass().getSimpleName();
    private Spinner timeIntervalSpinner;
    private BootstrapEditText phoneNumberEditText;
    private BootstrapButton startCallButton, stopCallButton;
    private String phoneNumber;
    private int timeInterval = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();
        setListeners();
        ArrayAdapter<CharSequence> timeIntervalSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.time_interval, android.R.layout.simple_spinner_item);
        timeIntervalSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeIntervalSpinner.setAdapter(timeIntervalSpinnerAdapter);

        File callLogFile = new File(Environment.getExternalStorageDirectory(), "calltimelogs.txt");
       // if(!callLogFile.exists()){
            try {
                if(callLogFile.createNewFile()){
                    Log.i(TAG, "File created");
                }else{
                    Log.i(TAG, "File Already exists");
                }
            } catch (IOException e) {
                Log.i(TAG, "Create New File exception >> " + e);
                e.printStackTrace();
            }
       // }
        sendBroadcast((new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)).setData(Uri.fromFile(callLogFile)));



    }

    private void initializeViews(){
        timeIntervalSpinner = (Spinner) findViewById(R.id.time_interval_spinner);
        phoneNumberEditText = (BootstrapEditText) findViewById(R.id.phone_number);
        startCallButton = (BootstrapButton) findViewById(R.id.start_button);
        stopCallButton = (BootstrapButton) findViewById(R.id.stop_button);
    }

    private void setListeners(){
        startCallButton.setOnClickListener(this);
        stopCallButton.setOnClickListener(this);
        timeIntervalSpinner.setOnItemSelectedListener(this);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.start_button:
                phoneNumber = phoneNumberEditText.getText().toString();
                Log.i(TAG, "Time Interval >> " + timeInterval);
                Log.i(TAG, "Phone Number >> " + phoneNumber);
                try {
                    if (timeInterval != 0 && (!phoneNumber.equalsIgnoreCase("") || phoneNumber != null )) {
                        long timeIntervalMilliSeconds = (timeInterval * 60) * 1000;
                        Log.i(TAG, "Interval in Milliseconds >> " + timeIntervalMilliSeconds);
                        Intent phoneServiceIntent = new Intent(this, PhoneCallService.class);
                        phoneServiceIntent.putExtra("TimeInterval", timeIntervalMilliSeconds).putExtra("PhoneNumber", phoneNumber);
                        startService(phoneServiceIntent);
                    }else{
                        return;
                    }
                }catch(Exception e){
                    Log.e(TAG, "Call Start Error >> " + e);
                }
                break;
            case R.id.stop_button:
                if(isMyServiceRunning(PhoneCallService.class)) {
                    stopService(new Intent(getApplicationContext(), PhoneCallService.class));
                    PhoneCallService.callTimer.cancel();
                    Toast.makeText(this, "Calls Stopped", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
           //String itemValue = timeIntervalSpinner.getSelectedItem().toString();
           String itemValue = (String) parent.getItemAtPosition(position);
           Log.i(TAG, "Item Value is >> " + itemValue);
           timeInterval = Integer.parseInt(itemValue);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
