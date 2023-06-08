package com.example.simpleringtone;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private Button btn_setup;
    private EditText edt;
    private File selectFile;
    private ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean result) {
            request(result);
        }
    });

    public void request(Boolean isGranted) {
        if (isGranted) {
            Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Please allow permission", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestNewSDK();

        btn_setup = findViewById(R.id.btn_setup);
        edt = findViewById(R.id.edt);

        btn_setup.setOnClickListener(v -> {

            try {
                int position = Integer.parseInt(edt.getText().toString());
                if (position < 1 || position > 10){
                    Toast.makeText(MainActivity.this, "Vui lòng chọn từ 1 đến 10" ,Toast.LENGTH_SHORT).show();
                    return;
                }
                switch (position){

                    case 1:
                        setRingtone(R.raw.funny_animal_1);
                        break;
                    case 2:
                        setRingtone(R.raw.funny_animal_2);
                        break;
                    case 3:
                        setRingtone(R.raw.funny_animal_3);
                        break;
                    case 4:
                        setRingtone(R.raw.funny_animal_4);
                        break;
                    case 5:
                        setRingtone(R.raw.funny_animal_5);
                        break;
                    case 6:
                        setRingtone(R.raw.funny_animal_6);
                        break;
                    case 7:
                        setRingtone(R.raw.funny_animal_7);
                        break;
                    case 8:
                        setRingtone(R.raw.funny_animal_8);
                        break;
                    case 9:
                        setRingtone(R.raw.funny_animal_9);
                        break;
                    case 10:
                        setRingtone(R.raw.funny_animal_10);
                        break;

                }



            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void refreshStorage(Context mContext, String filePath) {
        if (SDK_INT < 19) {
            mContext.sendBroadcast(new Intent("android.intent.action.MEDIA_MOUNTED", Uri.parse("file://" + Environment.getExternalStorageDirectory())));
            return;
        }
        MediaScannerConnection.scanFile(mContext, new String[]{filePath}, null, new MediaScannerConnection.OnScanCompletedListener() {


            public void onScanCompleted(String path, Uri uri) {
                Log.i("ExternalStorage", "Scanned " + path + ":");
                Log.i("ExternalStorage", "-> uri=" + uri);
            }
        });
    }

    private void requestNewSDK() {
        if (isStoragePermissionGranted()) {
            Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
            startPermission(this, "android.permission.READ_MEDIA_AUDIO", 101);
        }
        if (!Settings.System.canWrite(this)) {
            String settingType = Settings.ACTION_MANAGE_WRITE_SETTINGS;
            Intent intent = new Intent();
            intent.setAction(settingType);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivityForResult(intent, 30);
        }
    }

    private Uri addToRingtoneList(int resourceId){
        try {
            // lay file tu raw luu vao cache
            InputStream in = getResources().openRawResource(resourceId);
            TypedValue value = new TypedValue();
            getResources().getValue(resourceId, value, true);
            String fileName = value.string.toString().replace("res/raw/", "");
            //cache
            File file = new File(getExternalCacheDir() + "/" + fileName);
            FileOutputStream out = null;
            Log.e("aaa0", file.getPath());
            out = new FileOutputStream(file.getPath());

            byte[] buff = new byte[1024];
            int read = 0;

            try {
                while ((read = in.read(buff)) > 0) {
                    out.write(buff, 0, read);
                }
            } finally {
                in.close();
                out.close();
            }

//day file tu cache vao nhac chuong
            ContentResolver mCr = this.getContentResolver();
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());
//            values.put(MediaStore.MediaColumns.TITLE, "Ringtone Animal");
            values.put(MediaStore.MediaColumns.TITLE, file.getName());
//            values.put(MediaStore.MediaColumns.DISPLAY_NAME,"Ringtone Animal");
            values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mpeg");
            values.put(MediaStore.MediaColumns.SIZE, file.length());
            values.put(MediaStore.Audio.Media.ARTIST, R.string.app_name);
            values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
            values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
            values.put(MediaStore.Audio.Media.IS_ALARM, true);
            values.put(MediaStore.Audio.Media.IS_MUSIC, false);

//            Uri uri = MediaStore.Audio.Media.getContentUriForPath(file.getAbsolutePath());
            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            uri = Uri.parse(Environment.getExternalStorageDirectory() + "/Music/nc/" + file.getName());
            Log.e("aaa9", uri.getPath());
            Uri newUri = mCr.insert(uri, values);
            OutputStream out2 = mCr.openOutputStream(newUri);
            byte[] buff2 = new byte[1024];
            int read2 = 0;
            InputStream in2 = getResources().openRawResource(resourceId);
            try {
                while ((read2 = in2.read(buff2)) > 0) {
                    out2.write(buff2, 0, read2);
                }
            } finally {
                in.close();
                out.close();
            }
            Log.e("aaa1", newUri.toString());
            Log.e("aaa2", newUri.getPath());
//luu vao danh sach nhac chuong va chon lam nhac chuong
            RingtoneManager.setActualDefaultRingtoneUri(MainActivity.this, RingtoneManager.TYPE_RINGTONE, newUri);
//            Settings.System.putString(mCr, Settings.System.RINGTONE, newUri.toString());
            return newUri;
        } catch (Exception e) {
            e.printStackTrace();
        }
        //bug thi return null
        return null;
    }

    private void setRingtone(int resourceId){
        try {

//            requestPermission();
            Uri uri = addToRingtoneList(resourceId);
            Log.e("aaa", "URI: " + uri.getPath());
            Log.e("aaa", "URI222: " + uri.toString());
            return;
            //duoi nay thua`


//            File dir = new File(Environment.getExternalStorageDirectory().getPath() + "/Music");
//            if (!dir.exists()) dir.mkdir();
//            InputStream in = getResources().openRawResource(resourceId);
//            TypedValue value = new TypedValue();
//            getResources().getValue(resourceId, value, true);
//            String fileName = value.string.toString().replace("res/raw/", "");
//            File file = new File(getExternalCacheDir() + "/" + fileName);
//            FileOutputStream out = new FileOutputStream(file.getPath());
//            byte[] buff = new byte[1024];
//            int read = 0;
//
//            try {
//                while ((read = in.read(buff)) > 0) {
//                    out.write(buff, 0, read);
//                }
//            } finally {
//                out.flush();
//                in.close();
//                out.close();
//            }
//            ContentResolver mCr = this.getContentResolver();
//
//
//
//            try {
//                File file1 = new File(createFile(file, mCr, resourceId).getPath());
////                Uri uri = Uri.parse(file1.getPath());
////                RingtoneManager.setActualDefaultRingtoneUri(MainActivity.this,
////                        RingtoneManager.TYPE_RINGTONE, uri);
////                Settings.System.putString(mCr, Settings.System.RINGTONE,
////                        uri.toString());
//
//
//
//                final Handler handler = new Handler(Looper.getMainLooper());
//
//                Uri uri2 = addToRingtone(file1, mCr, resourceId);
//                uri2 = Uri.fromFile(file1);
//                uri2 = Uri.parse("content://com.android.externalstorage.documents/document/primary%3Abb.mp3");//                uri2 = MediaStore.Audio.Media.getContentUriForPath(file1.getPath());
////                uri2 = Uri.parse("content://media/external_primary/audio/media/1000000274");
//                Log.e("aaa", new File(uri2.getPath()).length() + " NEW URI: " + uri2.getPath());
//                RingtoneManager.setActualDefaultRingtoneUri(MainActivity.this,
//                        RingtoneManager.TYPE_RINGTONE, uri2);
//                Settings.System.putString(mCr, Settings.System.RINGTONE,
//                        uri2.toString());
//
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//
//
//
//                    }
//                }, 10000);
//
//
//            } catch (Throwable t) {
//                t.printStackTrace();
//            }
//
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

//    private Uri createFile(File file, ContentResolver mCr, int resourceId) {
//        String newName = "" + file.getName();
//        ContentValues values = new ContentValues();
//        values.put(MediaStore.MediaColumns.DISPLAY_NAME, newName);
//        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mpeg");
//        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_MUSIC);
//        values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
//        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
//        values.put(MediaStore.Audio.Media.IS_ALARM, true);
//        values.put(MediaStore.Audio.Media.IS_MUSIC, true);
//        Log.e("aaa", MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.getPath());
//        Uri newUri = mCr.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
//
//
//
//        try {
//            OutputStream out = mCr.openOutputStream(newUri);
//            byte[] buff = new byte[1024];
//            int read = 0;
//            InputStream in = getResources().openRawResource(resourceId);
//            try {
//                while ((read = in.read(buff)) > 0) {
//                    out.write(buff, 0, read);
//                }
//            } finally {
//                out.flush();
//                in.close();
//                out.close();
//            }
//            return Uri.parse(Environment.getExternalStorageDirectory().getPath() + "/Music/" +  newName);
//
////            return newUri;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//
//    }
//
//    private Uri addToRingtone(File file,ContentResolver mCr, int resourceId){
//        ContentValues values = new ContentValues();
//        values.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());
//        values.put(MediaStore.MediaColumns.TITLE, file.getName());
//        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mpeg");
//
//        values.put(MediaStore.MediaColumns.SIZE, file.length());
//        values.put(MediaStore.Audio.Media.ARTIST, R.string.app_name);
//        values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
//        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
//        values.put(MediaStore.Audio.Media.IS_ALARM, true);
//        values.put(MediaStore.Audio.Media.IS_MUSIC, true);
//
//        Uri uri = MediaStore.Audio.Media.getContentUriForPath(file.getAbsolutePath());
//        return mCr.insert(uri, values);
//    }
//    private void requestPermission() {
//        if (SDK_INT >= Build.VERSION_CODES.R) {
//            if (Environment.isExternalStorageManager()) {
//                return;
//            }
//            try {
//                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
//                intent.addCategory("android.intent.category.DEFAULT");
//                intent.setData(Uri.parse(String.format("package:%s",getApplicationContext().getPackageName())));
//                startActivityForResult(intent, 2296);
//            } catch (Exception e) {
//                Intent intent = new Intent();
//                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
//                startActivityForResult(intent, 2296);
//            }
//        } else {
//            //below android 11
//            ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, 0);
//        }
//    }




//    private void setRingtone() throws IOException {
//
//       // File f = new File(this.getExternalFilesDir("myRingtonFolder"),"funny_animal_9" + ".mp3");
//        File f = new File(this.getFilesDir(), "funny_animal_9" + ".mp3");
//
//        if (!f.exists()) {
//            f.createNewFile();
//        }
//        Uri mUri=Uri.parse("android.resource://"+getPackageName()+"/raw/funny_animal_9");
//
//
//        ContentResolver mCr = this.getContentResolver();
//        AssetFileDescriptor soundFile;
//        try {
//            soundFile = mCr.openAssetFileDescriptor(mUri, "r");
//        } catch (FileNotFoundException e) {
//            soundFile = null;
//        }
//
//        try {
//            byte[] readData = new byte[1024];
//            FileInputStream fis = soundFile.createInputStream();
//            FileOutputStream fos = new FileOutputStream(f);
//            int i = fis.read(readData);
//
//            while (i != -1) {
//                fos.write(readData, 0, i);
//                i = fis.read(readData);
//            }
//
//            fos.close();
//        } catch (IOException io) {
//            io.printStackTrace();
//        }
//        ContentValues values = new ContentValues();
//        values.put(MediaStore.MediaColumns.DATA, "content://com.android.externalstorage.documents/document/primary%3Angua.mp3");
////        values.put(MediaStore.MediaColumns.DATA, f.getAbsolutePath());
//        values.put(MediaStore.MediaColumns.TITLE, "name");
//        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
//        values.put(MediaStore.MediaColumns.SIZE, f.length());
//        values.put(MediaStore.Audio.Media.ARTIST, R.string.app_name);
//        values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
//        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
//        values.put(MediaStore.Audio.Media.IS_ALARM, true);
//        values.put(MediaStore.Audio.Media.IS_MUSIC, true);
//
////        Uri uri = MediaStore.Audio.Media.getContentUriForPath(f.getAbsolutePath());
////        Uri uri = MediaStore.Audio.Media.getContentUriForPath("content://com.android.externalstorage.documents/document/primary%3Angua.mp3");
//        Uri uri = Uri.parse("content://com.android.externalstorage.documents/document/primary%3Angua.mp3");
//
//        Log.e("aaa", uri.toString());
//        Log.e("aaa", values.toString());
//        //Uri newUri = mCr.insert(uri, values);
//
//        try {
//            RingtoneManager.setActualDefaultRingtoneUri(getApplicationContext(), RingtoneManager.TYPE_NOTIFICATION, uri);
//
////            RingtoneManager.setActualDefaultRingtoneUri(this,
////                    RingtoneManager.TYPE_RINGTONE, uri);
////            Settings.System.putString(mCr, Settings.System.RINGTONE,
////                    uri.toString());
//        } catch (Throwable t) {
//            t.printStackTrace();
//        }
//    }


    private void startPermission(MainActivity mainActivity, String permission, int i) {
        this.requestPermissionLauncher.launch(permission);
    }

    private boolean isStoragePermissionGranted() {
        return checkCallingOrSelfPermission("android.permission.READ_MEDIA_AUDIO") == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 30 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        }
    }
}