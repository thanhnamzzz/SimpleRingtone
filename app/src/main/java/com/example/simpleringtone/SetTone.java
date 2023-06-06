package com.example.simpleringtone;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class SetTone extends AsyncTask<String, String, String> {
    private static final String TAG = "SetTone";
    private Activity _context;
    private File savedFile;

    public SetTone(Activity context) {
        this._context = context;
    }

    public String doInBackground(String... strArr) {
        String filename;
        InputStream fIn = this._context.getResources().openRawResource(CONST.rawArray[CONST.POS]);
        try {
            byte[] buffer = new byte[fIn.available()];
            fIn.read(buffer);
            fIn.close();
            String path = AudioStream.getInstance().getRingtonePath(this._context).getAbsolutePath();
            if (CONST.SELECTED_SHOW_FILE_NAME == null || !CONST.SELECTED_SHOW_FILE_NAME.contains(".mp3")) {
                filename = CONST.SELECTED_SHOW_FILE_NAME + ".mp3";
            } else {
                filename = CONST.SELECTED_SHOW_FILE_NAME;
            }
            if (!new File(path).exists()) {
                new File(path).mkdirs();
            }
            File mFile = new File(path + "/" + filename);
            if (!mFile.exists()) {
                try {
                    FileOutputStream save = new FileOutputStream(mFile.getAbsolutePath());
                    save.write(buffer);
                    save.flush();
                    save.close();
                } catch (FileNotFoundException e) {
                    Log.e(TAG, "FileNotFoundException: " + e.getMessage());
                    return CONST.FAIL_TASK;
                } catch (IOException e2) {
                    Log.e(TAG, "IOException: " + e2.getMessage());
                    return CONST.FAIL_TASK;
                }
            }
            this.savedFile = mFile;
            return CONST.SUCCESS_TASK;
        } catch (IOException e3) {
            return CONST.FAIL_TASK;
        }
    }

    private ContentValues getValues(boolean... value) {
        ContentValues values = new ContentValues();
        long current = System.currentTimeMillis();
        values.put("_data", this.savedFile.getAbsolutePath());
        values.put("title", this.savedFile.getName());
        values.put("date_added", (int) (current / 1000));
        values.put("artist", this._context.getString(R.string.app_name));
        values.put("is_ringtone", value[0]);
        values.put("is_notification", value[1]);
        values.put("is_alarm", value[2]);
        values.put("is_music", (Boolean) false);
        return values;
    }

    private void setMedia(int type, ContentValues value) {
        Activity activity = this._context;
        RingtoneManager.setActualDefaultRingtoneUri(activity, 1, activity.getContentResolver().insert(MediaStore.Audio.Media.getContentUriForPath(this.savedFile.getAbsolutePath()), value));
        this._context.getContentResolver().insert(MediaStore.Audio.Media.getContentUriForPath(this.savedFile.getAbsolutePath()), value);
        String what = "";
        switch (type) {
            case 1:
                what = "Ringtone";
                break;
            case 2:
                what = "Notification";
                break;
            case 4:
                what = "Alarm";
                break;
        }
        Toast.makeText(this._context, what + " Set Success", Toast.LENGTH_SHORT).show();
    }

    public void onPostExecute(String mTask) {
        String str = CONST.SELECTED_SHOW_FILE_NAME;
        switch (CONST.SELECTED_TYPE) {
            case 1:
                setMedia(1, getValues(true, false, false));
                return;
            case 2:
                setMedia(2, getValues(false, true, false));
                return;
            case 4:
                setMedia(4, getValues(false, false, true));
                AudioStream.getInstance().setAlarm(this._context, Uri.parse("android.resource://com.gsbusiness.ringtoneoffline/" + CONST.rawArray[CONST.POS]));
                return;
            case 104:
                this._context.startActivityForResult(new Intent("android.intent.action.PICK", ContactsContract.Contacts.CONTENT_URI), 505);
                return;
            case 105:
                if (mTask.equals(CONST.SUCCESS_TASK)) {
                    Uri mediaUri = FileProvider.getUriForFile(this._context, this._context.getPackageName() + ".provider", this.savedFile);
                    Log.e(TAG, "onPostExecute: mediaUri: " + mediaUri);
                    Intent intent = new Intent("android.intent.action.SEND");
                    intent.setType("audio/mp3");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.putExtra("android.intent.extra.STREAM", mediaUri);
                    Intent chooser = Intent.createChooser(intent, CONST.SHARE_AUDIO_TITLE);
                    for (ResolveInfo resolveInfo : this._context.getPackageManager().queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY)) {
                        this._context.grantUriPermission(resolveInfo.activityInfo.packageName, mediaUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                    this._context.startActivity(chooser);
                    return;
                }
                final Activity activity = this._context;
                activity.runOnUiThread(new Runnable() {

                    public void run() {
                        Toast.makeText(activity, "Something Want Wrong...", Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            default:
                return;
        }
    }
}
