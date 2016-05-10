package com.ticket.gemroc.caller;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by cted on 5/31/15.
 */
public class PhoneCallService extends Service {
    private String TAG = getClass().getSimpleName();
    public static Timer callTimer = new Timer();
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "In Start Command");
        long timeInterval = intent.getExtras().getLong("TimeInterval");
        String phoneNumber = intent.getExtras().getString("PhoneNumber");
        Log.i(TAG, "Time Interval >> " + timeInterval);
        Log.i(TAG, "Phone Number >> " + phoneNumber);
        makePhoneCalls(phoneNumber, timeInterval);
        return super.onStartCommand(intent, flags, startId);
    }

    private void makePhoneCalls(final String phoneNumber, long timeInterval){
        Log.i(TAG, "In Make Phone Calls");
        try {
            callTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Date currentDate = new Date();
                    String currentDateString = DateFormat.getDateTimeInstance().format(currentDate);
                    Log.i(TAG, "Current Date time >> " + currentDateString);
                    OutputStreamWriter outputStream = null;
                    try{
                        outputStream = new OutputStreamWriter(new FileOutputStream(Environment.getExternalStorageDirectory() + File.separator+"calltimelogs.txt", true));
                        CharSequence charSequence = (currentDateString+"\n");
                        outputStream.append(charSequence);


                    }catch(FileNotFoundException exception){
                        Log.i(TAG, "File Not Found Exception >> " + exception);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally{
                         if(outputStream!=null){
                             try {
                                 outputStream.close();
                             } catch (IOException e) {
                                 e.printStackTrace();
                             }
                         }
                    }
                    Intent phoneCallIntent = new Intent(Intent.ACTION_CALL);
                    phoneCallIntent.setData(Uri.parse("tel:" + phoneNumber));
                    startActivity(phoneCallIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            }, new Date(), timeInterval);
        }catch(Exception e){
            Log.i(TAG, "Call Timer Exception >> " + e);
        }
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "In On Create");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
