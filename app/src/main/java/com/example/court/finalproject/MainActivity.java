package com.example.court.finalproject;

import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.AlarmManager;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.TextView;
import android.content.Context;
import android.view.Menu;

import java.util.ArrayList;
import java.util.Calendar;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final int permissionRequest = 1;
    ListView listView;
    ArrayAdapter<String> adapter;
    ArrayList<String> arrayList;
    AlarmManager alarm_manager;
    TimePicker alarm_timepicker;
    TextView update_text;
    Context context;
    PendingIntent pending_intent;
    int choose_sound;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.context = this;

        //Checking permissions to read external storage
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, permissionRequest);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, permissionRequest);
            }
        } else {
            listMusic();
        }

        alarm_manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarm_timepicker = (TimePicker) findViewById(R.id.timePicker);
        update_text = (TextView) findViewById(R.id.update_text);
        final Calendar calendar = Calendar.getInstance();
        final Intent alarm_receiver = new Intent(this.context, Alarm_Receiver.class);

        // Spinner
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.music_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Set onClickListener to onItemSelected method
        spinner.setOnItemSelectedListener(this);

        // Click 'Set Alarm' button
        Button alarm_on = (Button) this.findViewById(R.id.alarm_on);
        alarm_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set calendar
                calendar.set(Calendar.HOUR_OF_DAY, alarm_timepicker.getHour());
                calendar.set(Calendar.MINUTE, alarm_timepicker.getMinute());

                // Displaying time of alarm
                // int values of hour and minute
                int hour = alarm_timepicker.getHour();
                int minute = alarm_timepicker.getMinute();

                // Convert the int values to Strings
                String hour_string = String.valueOf(hour);
                String minute_string = String.valueOf(minute);

                // Convert 24-hour time to 12-hour time
                if(hour == 0) {
                    hour_string = String.valueOf(12);
                    minute_string = String.valueOf(minute) + " AM";
                } else if (hour > 12) {
                    hour_string = String.valueOf(hour - 12);
                    minute_string = String.valueOf(minute) + " PM";
                } else {
                    minute_string = String.valueOf(minute) + " AM";
                }

                if (minute < 10) {
                    minute_string = "0" + String.valueOf(minute);
                }

                set_alarm_text("Alarm set to: " + hour_string + ":" + minute_string);

                // Tells clock that you pressed "alarm on" button
                alarm_receiver.putExtra("extra", "alarm on");

                // Tells clock which sound you selected from spinner
                alarm_receiver.putExtra("sound_choice", choose_sound);
                Log.e("The sound id is ", String.valueOf(choose_sound));

                // Ringtone will play at set time
                pending_intent = PendingIntent.getBroadcast(MainActivity.this, 0, alarm_receiver,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                alarm_manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pending_intent);
            }
        });

        // Click 'Alarm Off' button
        Button alarm_off = (Button) this.findViewById(R.id.alarm_off);
        alarm_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Display cancellation
                set_alarm_text("Alarm off!");

                // Tells clock that you pressed "alarm off" button
                alarm_receiver.putExtra("extra", "alarm off");

                // Tells clock which sound you selected from spinner
                // Prevents crash from Null Pointer Exception
                alarm_receiver.putExtra("sound_choice", choose_sound);

                // Cancel alarm, stop ringtone
                try {
                    alarm_manager.cancel(pending_intent);
                    sendBroadcast(alarm_receiver);
                } catch (NullPointerException e){
                    Log.e("Alarm has not been set ", "and user pressed alarm off");
                }
            }
        });
    }

    private void set_alarm_text(String output) {
        update_text.setText(output);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // Outputting sound user selects from spinner
        Toast.makeText(parent.getContext(), "the spinner item is " + id, Toast.LENGTH_SHORT).show();
        choose_sound = (int) id;

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    // Takes contents of SD Card and places them as clickable buttons in list view
    public void listMusic(){
        listView = (ListView) findViewById(R.id.listView);
        arrayList = new ArrayList<>();
        getMusic();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, arrayList);
        listView.setAdapter(adapter);

        // Currently non-functional
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView adapterView, View view, int i, long l){
//                //open music player to play desired song
//                if (mediaPlayer != null){
//                    mediaPlayer.release();
//                }
//
//                int resID = getResources().getIdentifier(arrayList.get(i), "sdcard", getPackageName());
//                mediaPlayer = MediaPlayer.create(MainActivity.this, resID);
//                mediaPlayer.start();
//            }
//        });
    }

    //Reads contents on the external SD Card
    public void getMusic() {
        ContentResolver contentResolver = getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = contentResolver.query(songUri, null, null, null, null);

        if (songCursor != null && songCursor.moveToFirst()) {
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);

            do {
                String currentTitle = songCursor.getString(songTitle);
                arrayList.add(currentTitle);
            } while (songCursor.moveToNext());
        }
    }

    //Toasts confirming/denying permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch(requestCode){
            case permissionRequest: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(MainActivity.this,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();

                        listMusic();
                    }
                } else {
                    Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }

    }

}
