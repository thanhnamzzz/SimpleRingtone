package com.example.simpleringtone;

import static android.Manifest.permission.MANAGE_MEDIA;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_MEDIA_AUDIO;
import static android.Manifest.permission.READ_MEDIA_VIDEO;
import static android.Manifest.permission.WRITE_CONTACTS;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_SETTINGS;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
public class MainActivity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private Button btn_setup, btn_setup2;
    private EditText edt;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestNewSDK();

        btn_setup = findViewById(R.id.btn_setup);
        edt = findViewById(R.id.edt);

        btn_setup.setOnClickListener(v -> {
            AssetFileDescriptor openAssetFile;
            File file;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES) + "/funny_animal_9.mp3");
            } else {
                file = new File(Environment.getExternalStorageDirectory(), "funny_animal_9.mp3");
            }
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            String parth = "android.resource://" + getPackageName() + "/" + R.raw.funny_animal_9;
            Uri uri = Uri.parse(parth);
            File fileinApp = new File("android.resource://" + getPackageName() + "/raw/funny_animal_9.mp3");
//            Uri uri = Uri.parse(Environment.getExternalStorageDirectory() + "/Ringtones/funny_animal_8");

            ContentResolver contentResolver = getContentResolver();
            try {
                openAssetFile = contentResolver.openAssetFileDescriptor(uri, "r");
            } catch (FileNotFoundException e) {
                openAssetFile = null;
                throw new RuntimeException(e);
            }

            try {
                byte[] readData = new byte[1024];
                FileInputStream fis = openAssetFile.createInputStream();
                FileOutputStream fos = new FileOutputStream(file);
                int i = fis.read(readData);

                while (i != -1) {
                    fos.write(readData, 0, i);
                    i = fis.read(readData);

                }
                fos.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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
            Uri uri1 = Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES) + "/funny_animal_9.mp3");
//            String storagePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES).getPath() + "/funny_animal_9.mp3";
            String storagePath = Environment.getExternalStorageDirectory() + "/Ringtones/funny_animal_9.mp3";
            Log.d("TAG", "onCreate: storagePath " + storagePath);

            File newFile = new File(Environment.getExternalStorageDirectory(), "/Ringtones/funny_animal_9.mp3");
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DATA, newFile.getAbsolutePath());
//            values.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());
//            values.put(MediaStore.MediaColumns.DATA, uri.getPath());
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, "Ringtone Animal: Funny Animal " + edt.getText());
            values.put(MediaStore.MediaColumns.TITLE, "Ringtone Animal: Funny Animal " + edt.getText());
            values.put(MediaStore.MediaColumns.ARTIST, "Ringtone Animal: Funny Animal " + edt.getText());
            values.put(MediaStore.MediaColumns.SIZE, newFile.length());
            values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/ogg");
//            values.put(MediaStore.MediaColumns.RELATIVE_PATH, storagePath);
//            values.put(MediaStore.Audio.Media.RELATIVE_PATH, storagePath);
            values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
            values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
            values.put(MediaStore.Audio.Media.IS_ALARM, false);
            values.put(MediaStore.Audio.Media.IS_MUSIC, false);

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
//                Uri newUri = getContentResolver().insert(MediaStore.Audio.Media.getContentUri(uri1.getPath()), values);
                Uri uriFile = MediaStore.Audio.Media.getContentUriForPath(newFile.getAbsolutePath());
                Uri newUri = contentResolver.insert(uriFile, values);
//                Log.d("TAG", "onCreate: file.getAbsolutePath(): " + file.getAbsolutePath());
//                Uri newUri = getContentResolver().insert(MediaStore.Audio.Media.getContentUriForPath(MediaStore.VOLUME_EXTERNAL_PRIMARY), values);
                RingtoneManager.setActualDefaultRingtoneUri(MainActivity.this, RingtoneManager.TYPE_RINGTONE, newUri);
                Toast.makeText(MainActivity.this, "Added success ringtone", Toast.LENGTH_SHORT).show();
            } catch (Throwable throwable) {
                Toast.makeText(MainActivity.this, "Set sound failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void requestNewSDK() {
        if (!Settings.System.canWrite(this)) {
            String settingType = Settings.ACTION_MANAGE_WRITE_SETTINGS;
            Intent intent = new Intent();
            intent.setAction(settingType);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivityForResult(intent, 30);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(MainActivity.this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(MainActivity.this, READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            String[] permission = {WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE, READ_MEDIA_AUDIO};
            ActivityCompat.requestPermissions(this, permission, 30);
        }
//        if (!Environment.isExternalStorageManager()){
//            try {
//                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
//                intent.addCategory("android.intent.category.DEFAULT");
//                intent.setData(Uri.parse(String.format("package:%s", getApplicationContext(), getPackageName())));
//                startActivityForResult(intent, 30);
//            } catch (Exception e) {
//                Intent intent = new Intent();
//                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
//                startActivityForResult(intent, 30);
//            }
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 30 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        }
    }
}