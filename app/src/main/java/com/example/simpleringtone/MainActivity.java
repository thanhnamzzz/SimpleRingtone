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
            /*
//            for (int position = 0; position < CONST.rawArray.length; position++) {
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES).getAbsolutePath();
//                String name = "Ringtones " + position;
            String name = "Ringtones";
            File mFile = new File(path + "/" + name + ".mp3");
            if (!mFile.exists()) {
                InputStream fIn = this.getResources().openRawResource(CONST.rawArray[7]);
                try {
                    byte[] buffer = new byte[fIn.available()];
                    fIn.read(buffer);
                    fIn.close();
                    if (!new File(path).exists()) {
                        new File(path).mkdirs();
                    }
                    if (!mFile.exists()) {
                        try {
                            FileOutputStream save = new FileOutputStream(mFile.getAbsolutePath());
                            save.write(buffer);
                            save.flush();
                            save.close();
                            refreshStorage(this, mFile.getAbsolutePath());
                        } catch (FileNotFoundException e) {
                            Log.e(TAG, "FileNotFoundException: " + e.getMessage());
                        } catch (IOException e2) {
                            Log.e(TAG, "IOException: " + e2.getMessage());
                        }
                    }
                } catch (IOException e3) {
                    throw new RuntimeException(e3);
                }
            }
            ContentValues values = new ContentValues();
            long current = System.currentTimeMillis();
            values.put("_data", mFile.getAbsolutePath());
            values.put("title", "Ringtone Animal: Check " + edt.getText());
            values.put("date_added", (int) (current / 1000));
            values.put("artist", mFile.getName());
            values.put("is_ringtone", true);
            values.put("is_notification", false);
            values.put("is_alarm", false);
            values.put("is_music", false);

//            }
//            String fileName;
//            InputStream fIn = getResources().openRawResource(CONST.rawArray[7]);
//            File newFile = null;
//            try {
//                byte[] buffer = new byte[fIn.available()];
//                fIn.read(buffer);
//                fIn.close();
//                String path = AudioStream.getInstance().getRingtonePath(this).getAbsolutePath();
//                fileName = "funny_animal_8";
//                if (!new File(path).exists()) {
//                    new File(path).mkdirs();
//                }
//                newFile = new File(path + "/" + fileName);
//                if (!newFile.exists()) {
//                    try {
//                        FileOutputStream save = new FileOutputStream(newFile.getAbsolutePath());
//                        save.write(buffer);
//                        save.flush();
//                        save.close();
//                    } catch (FileNotFoundException e) {
//                        Log.e("Gambi", "FileNotFoundException: " + e.getMessage());
//                    } catch (IOException e2) {
//                        Log.e("Gambi", "IOException: " + e2.getMessage());
//                    }
//                }
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }

//            AssetFileDescriptor openAssetFile;
//            File file;
//            if (Build.VERSION.SDK_INT >= 29) {
//                file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES) + "/funny_animal_9.mp3");
//            } else {
//                file = new File(Environment.getExternalStorageDirectory(), "funny_animal_9.mp3");
//            }
//            if (!file.getParentFile().exists()) {
//                file.getParentFile().mkdirs();
//            }
//            if (!file.exists()) {
//                try {
//                    file.createNewFile();
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//
//            String parth = "android.resource://" + getPackageName() + "/" + R.raw.funny_animal_9;
//            Uri uri = Uri.parse(parth);
//            File fileinApp = new File("android.resource://" + getPackageName() + "/raw/funny_animal_9.mp3");
//            Uri uri = Uri.parse(Environment.getExternalStorageDirectory() + "/Ringtones/funny_animal_8");

//            ContentResolver contentResolver = getContentResolver();
//            try {
//                openAssetFile = contentResolver.openAssetFileDescriptor(uri, "r");
//            } catch (FileNotFoundException e) {
//                openAssetFile = null;
//                throw new RuntimeException(e);
//            }
//
//            try {
//                byte[] readData = new byte[1024];
//                FileInputStream fis = openAssetFile.createInputStream();
//                FileOutputStream fos = new FileOutputStream(file);
//                int i = fis.read(readData);
//
//                while (i != -1) {
//                    fos.write(readData, 0, i);
//                    i = fis.read(readData);
//
//                }
//                fos.close();
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }

//            byte[] bArr = new byte[1024];
//            try {
//                FileInputStream createInputStream = openAssetFile.createInputStream();
//                FileOutputStream fileOutputStream = new FileOutputStream(file);
//                for (int read = createInputStream.read(bArr); read != -1; read = createInputStream.read(bArr)) {
//                    fileOutputStream.write(bArr, 0, read);
//                }
//                fileOutputStream.close();
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//            Uri uri1 = Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES) + "/funny_animal_9.mp3");
//            String storagePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES).getPath() + "/funny_animal_9.mp3";
//            String storagePath = Environment.getExternalStorageDirectory() + "/Ringtones/funny_animal_9.mp3";
//            Log.d("TAG", "onCreate: storagePath " + storagePath);

//            File newFile = new File(Environment.getExternalStorageDirectory(), "/Ringtones/funny_animal_9.mp3");
//            ContentValues values = new ContentValues();
//            long current = System.currentTimeMillis();
//            values.put("_data", newFile.getAbsolutePath());
//            values.put("title", "Ringtone Animal: Funny Animal " + edt.getText());
//            values.put("date_added", (int) (current / 1000));
//            values.put("artist", newFile.getName());
//            values.put("is_ringtone", true);
//            values.put("is_notification", false);
//            values.put("is_alarm", false);
//            values.put("is_music", false);
//            values.put(MediaStore.MediaColumns.DATA, newFile.getAbsolutePath());
//            values.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());
//            values.put(MediaStore.MediaColumns.DATA, uri.getPath());
//            values.put(MediaStore.MediaColumns.DISPLAY_NAME, "Ringtone Animal: Funny Animal " + edt.getText());
//            values.put(MediaStore.MediaColumns.TITLE, "Ringtone Animal: Funny Animal " + edt.getText());
//            values.put(MediaStore.MediaColumns.ARTIST, "Ringtone Animal: Funny Animal " + edt.getText());
//            values.put(MediaStore.MediaColumns.SIZE, newFile.length());
//            values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/ogg");
//            values.put(MediaStore.MediaColumns.RELATIVE_PATH, storagePath);
//            values.put(MediaStore.Audio.Media.RELATIVE_PATH, storagePath);
//            values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
//            values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
//            values.put(MediaStore.Audio.Media.IS_ALARM, false);
//            values.put(MediaStore.Audio.Media.IS_MUSIC, false);

//            String mimeTypeFile = null;
//            String fileExtensionFile = MimeTypeMap.getFileExtensionFromUrl(file.getAbsolutePath());
//            if (fileExtensionFile != null) {
//                mimeTypeFile = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtensionFile);
//            }
//            Log.d("TAG", "onCreate: " + file.getAbsolutePath());
//            Log.d("TAG", "onCreate: mimeTypeFile " + mimeTypeFile);

            try {
//                Uri uriFile = MediaStore.Audio.Media.getContentUriForPath(newFile.getAbsolutePath());
//                getContentResolver().delete(uriFile, MediaStore.MediaColumns.DATA + "=\"" + newFile.getAbsolutePath() + "\"", null);
                Uri newUri = getContentResolver().insert(MediaStore.Audio.Media.getContentUriForPath(mFile.getAbsolutePath()), values);
//                Uri uriFile = MediaStore.Audio.Media.getContentUriForPath(newFile.getAbsolutePath());
//                Uri newUri = getContentResolver().insert(uriFile, values);
//                Log.d("TAG", "onCreate: file.getAbsolutePath(): " + file.getAbsolutePath());
//                Uri newUri = getContentResolver().insert(MediaStore.Audio.Media.getContentUriForPath(MediaStore.VOLUME_EXTERNAL_PRIMARY), values);
                RingtoneManager.setActualDefaultRingtoneUri(MainActivity.this, RingtoneManager.TYPE_RINGTONE, newUri);
                Toast.makeText(MainActivity.this, "Added success ringtone", Toast.LENGTH_SHORT).show();
            } catch (Throwable throwable) {
                Toast.makeText(MainActivity.this, "Set sound failed", Toast.LENGTH_SHORT).show();
            }*/
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

    private String setRingtone(int resourceId){
        try {

//            requestPermission();
            File dir = new File(Environment.getExternalStorageDirectory().getPath() + "/Music");
            if (!dir.exists()) dir.mkdir();
            InputStream in = getResources().openRawResource(resourceId);
            TypedValue value = new TypedValue();
            getResources().getValue(resourceId, value, true);
            String fileName = value.string.toString().replace("res/raw/", "");
            File file = new File(getExternalCacheDir() + "/" + fileName);
            FileOutputStream out = new FileOutputStream(file.getPath());
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
            ContentResolver mCr = this.getContentResolver();



            try {
                File file1 = new File(createFile(file, mCr, resourceId).getPath());
                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        Uri uri =  addToRingtone(file1, mCr);
                        Uri uri = Uri.parse(file1.getPath());
                        RingtoneManager.setActualDefaultRingtoneUri(MainActivity.this,
                                RingtoneManager.TYPE_RINGTONE, uri);
                        Settings.System.putString(mCr, Settings.System.RINGTONE,
                                uri.toString());
                    }
                }, 5000);


            } catch (Throwable t) {
                t.printStackTrace();
            }

        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return null;
    }

    private Uri createFile(File file, ContentResolver mCr, int resourceId) {
        String newName = "" + file.getName();
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, newName);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mpeg");
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_MUSIC);
        values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
        values.put(MediaStore.Audio.Media.IS_ALARM, true);
        values.put(MediaStore.Audio.Media.IS_MUSIC, true);
        Log.e("aaa", MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.getPath());
        Uri newUri = mCr.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);



        try {
            OutputStream out = mCr.openOutputStream(newUri);
            byte[] buff = new byte[1024];
            int read = 0;
            InputStream in = getResources().openRawResource(resourceId);
            try {
                while ((read = in.read(buff)) > 0) {
                    out.write(buff, 0, read);
                }
            } finally {
                in.close();
                out.close();
            }
            return Uri.parse(Environment.getExternalStorageDirectory().getPath() + "/Music/" +  newName);

//            return newUri;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    private Uri addToRingtone(File file,ContentResolver mCr){
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());
        values.put(MediaStore.MediaColumns.TITLE, file.getName());
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mpeg");
        values.put(MediaStore.MediaColumns.SIZE, file.length());
        values.put(MediaStore.Audio.Media.ARTIST, R.string.app_name);
        values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
        values.put(MediaStore.Audio.Media.IS_ALARM, true);
        values.put(MediaStore.Audio.Media.IS_MUSIC, true);

        Uri uri = MediaStore.Audio.Media.getContentUriForPath(file.getAbsolutePath());
        return mCr.insert(uri, values);
    }
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