package com.abc.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Load_PDF_from_Assets_with_file_provider extends AppCompatActivity {
    private static final String TAG = "Load_PDF_from_Assets_wi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.load_pdf_from_assets_with_file_provider);

        AssetManager assetManager = getAssets();
        String[] files = null;
        String file_name = "adhesive.pdf";
        try {
            files = assetManager.list("");
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        for (int i = 0; i < files.length; i++) {
            String temp_file = files[i];

            Log.i(TAG, "temp_file : " + temp_file);
            if (temp_file.equalsIgnoreCase(file_name)) {
                File file = new File(getCacheDir().getPath() + "/" + file_name);
                if (!file.exists())
                    try {
                        Log.i(TAG, "file.exists() : " + file.exists());
                        InputStream is = assetManager.open(file_name);
                        FileOutputStream fos = new FileOutputStream(file);
                        byte[] buffer = new byte[1024];
                        int read;
                        while ((read = is.read(buffer)) != -1) {
                            fos.write(buffer, 0, read);
                        }
                        is.close();
                        fos.flush();
                        fos.close();
                        Log.d(TAG, "file.getAbsolutePath() : " + file.getAbsolutePath());

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

// generate URI, I defined authority as the application ID in the Manifest, the last param is file I want to open
                Uri file_uri = null;
                try {
                    file_uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID, file);
                } catch (Exception e) {
                    e.printStackTrace();
                }

// create new Intent
                Intent intent = new Intent(Intent.ACTION_VIEW);

// set flag to give temporary permission to external app to use your FileProvider
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

// I am opening a PDF file so I give it a valid MIME type
                intent.setDataAndType(file_uri, "application/pdf");

// validate that the device can open your File!
                PackageManager pm = getPackageManager();
                Log.i(TAG, "intent.resolveActivity(pm) : " + intent.resolveActivity(pm));
                try {
                    if (intent.resolveActivity(pm) != null) {
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }


        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            trimCache(this);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void trimCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // The directory is now empty so delete it
        return dir != null && dir.delete();
    }
}
