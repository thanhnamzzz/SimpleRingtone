package com.example.simpleringtone;

import android.app.Activity;
import android.content.ContentValues;
import android.content.ContextWrapper;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

public class AudioStream {
    private static AudioStream audioStream;

    public static synchronized AudioStream getInstance() {
        AudioStream audioStream2;
        synchronized (AudioStream.class) {
            if (audioStream == null) {
                audioStream = new AudioStream();
            }
            audioStream2 = audioStream;
        }
        return audioStream2;
    }

    public static Object getCurrentRingtone(Activity activity) {
        return RingtoneManager.getRingtone(activity, RingtoneManager.getActualDefaultRingtoneUri(activity.getApplicationContext(), 1)).getTitle(activity);
    }

    public static void listRingtones(Activity activity) {
        try {
            RingtoneManager.getRingtone(activity, RingtoneManager.getActualDefaultRingtoneUri(activity.getApplicationContext(), 1));
        } catch (Exception e) {
            Log.e("TAG", "listRingtones: " + e.getMessage());
        }
    }

    public File getWrapperRingtonePath(Activity activity) {
        try {
            File file = new ContextWrapper(activity).getExternalMediaDirs()[0];
            if (!file.exists()) {
                file.mkdirs();
            }
            return file;
        } catch (Exception e) {
            File file2 = new File(Environment.getExternalStorageDirectory(), activity.getString(R.string.app_name));
            if (!file2.exists()) {
                file2.mkdirs();
            }
            return file2;
        }
    }

    public File getRingtonePath(Activity activity) {
        File file;
        if (Build.VERSION.SDK_INT >= 29) {
            file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES), activity.getString(R.string.app_name));
        } else {
            file = new File(Environment.getExternalStorageDirectory(), activity.getString(R.string.app_name));
        }
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public void assignRingtoneToContact(Activity activity, String contactId, String contactName) {
        Uri uri = Uri.parse("android.resource://com.example.simpleringtone/" + CONST.rawArray[CONST.POS]);
        ContentValues values = new ContentValues();
        values.put("custom_ringtone", uri.toString());
        activity.getContentResolver().update(ContactsContract.Contacts.CONTENT_URI, values, "_id=" + contactId, null);

        Toast.makeText(activity, "Ringtone set on " + contactName, Toast.LENGTH_SHORT).show();
    }

    public void setRingtone(Activity activity, Uri uri) {
        RingtoneManager.setActualDefaultRingtoneUri(activity, 1, uri);

        Toast.makeText(activity, "Ringtone set Success", Toast.LENGTH_SHORT).show();
    }

    public void setNotification(Activity activity, Uri uri) {
        RingtoneManager.setActualDefaultRingtoneUri(activity, 2, uri);
        Toast.makeText(activity, "Notification set Success", Toast.LENGTH_SHORT).show();
    }

    public void setAlarm(Activity activity, Uri uri) {
        RingtoneManager.setActualDefaultRingtoneUri(activity, 4, uri);
        Toast.makeText(activity, "Alarm set Success", Toast.LENGTH_SHORT).show();

    }



    public static boolean isExternalStorageWritable() {
        if ("mounted".equals(Environment.getExternalStorageState())) {
            return true;
        }
        return false;
    }
}
