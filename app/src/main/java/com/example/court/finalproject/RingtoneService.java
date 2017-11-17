package com.example.court.finalproject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.media.MediaPlayer;
import android.util.Log;

import java.util.Random;

/**
 * Created by court on 10/21/2017.
 */

public class RingtoneService extends Service {
    MediaPlayer media_song;
    int startId;
    boolean isRunning;

    @Nullable
    @Override
    public IBinder onBind (Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("LocalService", "Received start id " + startId + ": " + intent);

        // Fetch alarm on/off values
        String state = intent.getExtras().getString("extra");

        // Fetch sound choice
        Integer sound_choice = intent.getExtras().getInt("sound_choice");

        Log.e("Ringtone extra is ", state);
        Log.e("Sound choice is ", sound_choice.toString());

        // Notification.Builder is deprecated, fix for Android version
        /*
        // Notifications
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent_main_activity = new Intent(this.getApplicationContext(), MainActivity.class);
        PendingIntent pending_intent_main_activity = PendingIntent.getActivity(this, 0, intent_main_activity, 0);

        Notification notification_popup = new Notification.Builder(this)
                .setContentTitle("Your alarm is going off!")
                .setContentText("Click me!")
                .setContentIntent(pending_intent_main_activity)
                .setAutoCancel(true)
                .build();
        */

        assert state != null;
        switch (state) {
            case "alarm on":
                startId = 1;
                break;
            case "alarm off":
                startId = 0;
                Log.e("Start ID is ", state);
                break;
            default:
                startId = 0;
                break;
        }

        if(!this.isRunning && startId == 1) {
            Log.e("There is no music, ", "and user pressed alarm on");

            // Play sound depending on passed sound id
            if (sound_choice == 0) {
                // Play random audio file
                int min = 1;
                int max = 10;

                Random random_number = new Random();
                int random_sound_choice = random_number.nextInt(max + min);
                Log.e("random number is ",  String.valueOf(random_sound_choice));
                playMusic(random_sound_choice);

            } else {
                // Play audio file selected from spinner
                playMusic(sound_choice);
            }

            this.isRunning = true;
            this.startId = 0;

            // Deprecated, fix
            // Start command for notification
            // notificationManager.notify(0, notification_popup);

        } else if(this.isRunning && startId == 0) {
            Log.e("There is music, ", "and user pressed alarm off");
            media_song.stop();
            media_song.reset();

            this.isRunning = false;
            this.startId = 0;

        } else if(!this.isRunning && startId == 0) {
            Log.e("There is no music, ", "and user pressed alarm off");
            this.isRunning = false;
            this.startId = 0;

        } else if (this.isRunning && startId == 1) {
            Log.e("There is music, ", "and user pressed alarm on");
            this.isRunning = true;
            this.startId = 1;
        } else {
            Log.e("else statement ", " how did you reach this");
        }

        return START_NOT_STICKY;
    }

    private void playMusic(int sound_number) {
        Log.e("In playMusic() ", "test");
        if(sound_number == 1){
            media_song = MediaPlayer.create(this, R.raw.ac_1pm);
            media_song.start();
        }  else if (sound_number == 2) {
            media_song = MediaPlayer.create(this, R.raw.ac_8pm);
            media_song.start();
        } else if (sound_number == 3) {
            media_song = MediaPlayer.create(this, R.raw.ac_save);
            media_song.start();
        } else if (sound_number == 4) {
            media_song = MediaPlayer.create(this, R.raw.kdl_greengreens);
            media_song.start();
        } else if (sound_number == 5) {
            media_song = MediaPlayer.create(this, R.raw.ktam_rainbowroute);
            media_song.start();
        } else if (sound_number == 6) {
            media_song = MediaPlayer.create(this, R.raw.ktam_stageclear);
            media_song.start();
        } else if (sound_number == 7) {
            media_song = MediaPlayer.create(this, R.raw.lozminish_fairy);
            media_song.start();
        } else if (sound_number == 8) {
            media_song = MediaPlayer.create(this, R.raw.mother3_love);
            media_song.start();
        } else if (sound_number == 9) {
            media_song = MediaPlayer.create(this, R.raw.pkm_dive);
            media_song.start();
        } else if (sound_number == 10) {
            media_song = MediaPlayer.create(this, R.raw.sms_delfino);
            media_song.start();
        } else {
            // Not supposed to get here, just play Delfino
            media_song = MediaPlayer.create(this, R.raw.sms_delfino);
            media_song.start();
        }
    }

    @Override
    public void onDestroy() {
        Log.e("onDestroy called ", "app closed");
    }
}
